package top.wefor.giftrainpractice.giftrain;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Random;

import top.wefor.giftrainpractice.model.BoxInfo;
import top.wefor.giftrainpractice.model.BoxPrizeBean;

/**
 * 红包雨动画帮助类。
 * 业务代码，保持直播间整洁而抽离。
 * <p>
 * Created on 2018/1/30.
 *
 * @author ice
 */

public class RedPacketViewHelper {

    private static final boolean IS_DEBUG = false;

    public interface GiftRainListener {
        void startLaunch(); //开始发射

        void startRain(); //开始红包雨

        void openGift(BoxPrizeBean boxPrizeBean); //打开并获得了礼物

        void endRain(); //红包雨最后一帧结束
    }


    private Activity mActivity;

    private TextureView mGiftRainView; //红包雨承载控件（为保持扩展性，未对该View进行自定义）。
    private RedPacketRender mRedPacketRender; //红包雨渲染器。
    private boolean mIsGiftRaining; //是否在下红包雨（用于规避同时下多场红包雨）。

    private int mBoxId; //宝箱ID
    private GiftRainListener mGiftRainListener; //红包雨监听器。

    public RedPacketViewHelper(Activity activity) {
        mActivity = activity;
    }

    /*发射红包雨火箭*/
    public boolean launchGiftRainRocket(int boxId, List<BoxInfo> boxInfoList,
                                        int rocketWidth, int rocketHeight,
                                        GiftRainListener giftRainListener) {
        if (mIsGiftRaining || boxInfoList.isEmpty()) {
            return false;
        }
        mIsGiftRaining = true;
        mBoxId = boxId;
        mGiftRainListener = giftRainListener;
        mGiftRainListener.startLaunch();
        giftRain(boxInfoList);
        return true;
    }

    private void openGift(int pos, BoxPrizeBean boxPrizeBean) {
        if (mGiftRainView == null || mRedPacketRender == null) {
            return;
        }
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }

//        mGiftRainView.setOnTouchListener(null);//出礼物后不处理点击。
        mRedPacketRender.openGift(pos, boxPrizeBean);
        mGiftRainListener.openGift(boxPrizeBean);
    }

    private void openBoom(int pos) {
//        if (IS_DEBUG) {
//            if (new Random().nextInt(10) > 6) {
//                ///红包测试代码 TODO删除测试代码
//                BoxPrizeBean response = new BoxPrizeBean();
//                response.prizeName = "星星";
//                response.amount = 77;
//                openGift(pos, response);
//                return;
//            }
//        }

        if (mRedPacketRender == null) {
            return;
        }
        mRedPacketRender.openBoom(pos);
    }

    /**
     * 红包雨
     */
    private void giftRain(@NonNull List<BoxInfo> boxInfoList) {
        Log.i("xyz","gift rain create textureView");
        mGiftRainView = new TextureView(mActivity);
        mGiftRainView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int pos = mRedPacketRender.click((int) event.getRawX(), (int) event.getRawY());
                if (pos >= 0) {
                    Random random = new Random();
                    if (random.nextInt(10) > 7) {
                        BoxInfo boxInfo = boxInfoList.get(pos);
                        BoxPrizeBean boxPrizeBean = new BoxPrizeBean();
                        boxPrizeBean.amount = 5;
                        boxPrizeBean.prizeName = "喵🐱";
                        openGift(pos, boxPrizeBean);
                    } else {
                        openBoom(pos);
                    }
                    return true;
                }
                return false;
            }
            return true;
        });
        mGiftRainView.setOpaque(false);
        final ViewGroup viewGroup = (ViewGroup) mActivity.getWindow().getDecorView();
        viewGroup.addView(mGiftRainView);

        mRedPacketRender = new RedPacketRender(mActivity.getResources(), boxInfoList.size());
        mRedPacketRender.setOnStateChangeListener(new RedPacketRender.OnStateChangeListener() {
            @Override
            public void onRun() {
                if (mGiftRainView == null || mActivity == null || mActivity.isFinishing()) {
                    return;
                }
                mActivity.runOnUiThread(() -> {
                    mGiftRainView.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onHalt() {
                if (mActivity == null || mActivity.isFinishing()) {
                    return;
                }
                mActivity.runOnUiThread(() -> {
                    mGiftRainListener.endRain();
                    if (mGiftRainView != null) {
                        mGiftRainView.setVisibility(View.GONE);
                        mGiftRainView.setSurfaceTextureListener(null);
//                        mGiftRainView.setOnTouchListener(null);
                        viewGroup.removeView(mGiftRainView);
                        mGiftRainView = null;
                        mRedPacketRender = null;
                        //在所有红包雨的引用断开后，才置为false。
                        mIsGiftRaining = false;
                        Log.i("xyz","gift rain remove textureView");
                    }
                });
            }
        });
        mGiftRainView.setSurfaceTextureListener(mRedPacketRender);
        mRedPacketRender.start();
    }

    /**
     * 结束红包雨.
     */
    public void endGiftRain() {
        if (mRedPacketRender != null) {
            mRedPacketRender.halt();
        }
    }

}
