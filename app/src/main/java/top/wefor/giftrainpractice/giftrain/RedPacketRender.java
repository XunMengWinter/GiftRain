package top.wefor.giftrainpractice.giftrain;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import top.wefor.giftrainpractice.R;
import top.wefor.giftrainpractice.model.BoxPrizeBean;


/**
 * <p>
 * Handles Canvas rendering and SurfaceTexture callbacks.
 * <p>
 * We don't create a Looper, so the SurfaceTexture-by-way-of-TextureView callbacks
 * happen on the UI thread.
 * <p>
 * Created on 2018/1/23.
 * 红包雨渲染器。参考自[TextureViewCanvasActivity](https://github.com/google/grafika)
 *
 * @author ice
 */
public class RedPacketRender extends Thread implements TextureView.SurfaceTextureListener {

    public interface OnStateChangeListener {
        void onRun();

        void onHalt();
    }

    private OnStateChangeListener mOnStateChangeListener;

    private static final String TAG = "xyz RedPacketRender";

    private final Object mLock = new Object();        // guards mSurfaceTexture, mDone
    private SurfaceTexture mSurfaceTexture;
    private volatile boolean mDone;
    private final int mCount;
    private Map<Integer, Bitmap> mBitmapMap = new ConcurrentHashMap<>();

    private int mWidth;     // from SurfaceTexture
    private int mHeight;

    private Resources mResources;

    public RedPacketRender(Resources resources, int count) {
        super("TextureViewCanvas Renderer");
        mResources = resources;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 抗锯齿画笔
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 抗锯齿画笔
        mStandardBitmap = BitmapFactory.decodeResource(mResources, R.mipmap.img_red_packet);
        mCount = count;
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        mOnStateChangeListener = onStateChangeListener;
    }

    @Override
    public void run() {
        if (mOnStateChangeListener != null) {
            mOnStateChangeListener.onRun();
        }
        mDone = false;
        while (!mDone) {
            SurfaceTexture surfaceTexture = null;

            // Latch the SurfaceTexture when it becomes available.  We have to wait for
            // the TextureView to create it.
            synchronized (mLock) {
                while (!mDone && (surfaceTexture = mSurfaceTexture) == null) {
                    try {
                        mLock.wait();
                    } catch (InterruptedException ie) {
                        throw new RuntimeException(ie);     // not expected
                    }
                }
                if (mDone) {
                    break;
                }
            }
            Log.d(TAG, "Got surfaceTexture=" + surfaceTexture);

            // Render frames until we're told to stop or the SurfaceTexture is destroyed.
            doAnimation();
        }

        Log.d(TAG, "Renderer thread exiting");
    }

    /**
     * Draws updates as fast as the system will allow.
     * <p>
     * In 4.4, with the synchronous buffer queue queue, the frame rate will be limited.
     * In previous (and future) releases, with the async queue, many of the frames we
     * render may be dropped.
     * <p>
     * The correct thing to do here is use Choreographer to schedule frame updates off
     * of vsync, but that's not nearly as much fun.
     */

    private List<RedPacket> mRedPackets = new CopyOnWriteArrayList<>();
    private Bitmap mStandardBitmap;
    private Random mRandom = new Random();
    private Paint mPaint;
    private Paint mTextPaint;
    private static final int INVISIBLE_Y = 5000; //不可见的y坐标（用于防误判，可以拉回）。
    private static final int SLEEP_TIME = 10; //多少毫秒一帧（请根据设备性能权衡） TODO 待添加双重校准
    private static final float BOOM_PER_TIME = 80 / SLEEP_TIME; //爆炸物多少帧刷新一次（UI给的动画是80ms一帧，所以需要拿 80/每帧时长）。
    private static int BLOCK_SPEED = 20; //红包每一帧的移动距离（在xxhdpi基准下采用 耗时/1.3f）
    private RedPacket mLastDrawRedPacket; //最后绘制的红包--礼物的那个。
    private BoxPrizeBean mBoxPrizeBean; //红包信息
    private boolean mRaining;

    private void doAnimation() {
        // Create a Surface for the SurfaceTexture.
        Surface surface = null;
        synchronized (mLock) {
            SurfaceTexture surfaceTexture = mSurfaceTexture;
            if (surfaceTexture == null) {
                Log.d(TAG, "ST null on entry");
                return;
            }
            surface = new Surface(surfaceTexture);
        }

        long lastNano = 0;
        mRedPackets.clear();

        BLOCK_SPEED = (int) (SLEEP_TIME * mStandardBitmap.getHeight() / (250 * 1.3f));

        //礼物的像素为750*1400。
        final int giftWidth = mStandardBitmap.getWidth() * 750 / 230;
        final int giftHeight = mStandardBitmap.getHeight() * 1400 / 250;
        //用于标准红包到礼物大红包的位置校准
        final int giftDx = -(giftWidth - mStandardBitmap.getWidth()) / 2;
        final int giftDy = -(giftHeight - mStandardBitmap.getHeight()) / 2;
        //礼物大红包的最终位置
        final int giftX = (mWidth - giftWidth) / 2;
        final int giftY = (mHeight - giftHeight) / 2;
        final float density = mResources.getDisplayMetrics().density;

        int xLength = mWidth - mStandardBitmap.getWidth();
        int ribbonXLength = mWidth - mStandardBitmap.getWidth() * 35 / 230;

        int centerX = xLength * 16 / 30;
        int leftX = xLength * 7 / 30;
        int rightX = xLength * 5 / 6;

        int visibleY = -mStandardBitmap.getHeight();
        //第一个红包的位置
        int firstY = mStandardBitmap.getHeight() * 7 / 10;
        int yLength = mHeight;

        int boomWidth = mStandardBitmap.getWidth() * 368 / 230;
        int boomHeight = mStandardBitmap.getHeight() * 400 / 250;

        int boomDx = (boomWidth - mStandardBitmap.getWidth()) / 2;
        int boomDy = (boomHeight - mStandardBitmap.getHeight()) / 2;


        int diff = 0;
        int maxDiff = Math.max(0, (xLength - mStandardBitmap.getWidth() * 3) / 6);
        for (int i = 0; i < mCount; i++) {
            RedPacket redPacket;
            if (i >= 3) {
                diff = mRandom.nextInt(maxDiff * 2 + 1) - maxDiff;
            }
            switch (i % 3) {
                //右
                case 1:
                    redPacket = (new RedPacket(rightX + diff, firstY - yLength * i / 10 + diff));
                    break;
                //左
                case 2:
                    redPacket = (new RedPacket(leftX + diff, firstY - yLength * i / 10 + yLength / 9 + diff));
                    break;
                //中间
                default:
                    redPacket = (new RedPacket(centerX + diff, firstY - yLength * i / 10 + diff));
                    break;
            }
            redPacket.setImageRes(RedPacketRes.getPacket());
            redPacket.setIndex(i);
            mRedPackets.add(redPacket);

            //生成彩带
            RedPacket ribbon = new RedPacket((int) (ribbonXLength * mRandom.nextFloat()),
                    firstY - yLength * i / 10 - mRandom.nextInt(100));
            ribbon.setImageRes(RedPacketRes.getRibbon());
            ribbon.setType(RedPacket.TYPE_RIBBON);
            mRedPackets.add(ribbon);
        }

        while (!mDone) {
            final long startNano = System.nanoTime();
            Canvas canvas = null;
            try {
                canvas = surface.lockCanvas(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (canvas == null) {
                Log.d(TAG, "lockCanvas() failed");
                break;
            }
            try {
                // just curious
                if (canvas.getWidth() != mWidth || canvas.getHeight() != mHeight) {
                    Log.d(TAG, "WEIRD: width/height mismatch");
                }
                // Draw the entire window.  If the dirty rect is set we should actually
                // just be drawing into the area covered by it -- the system lets us draw
                // whatever we want, then overwrites the areas outside the dirty rect with
                // the previous contents.  So we've got a lot of overdraw here.
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                boolean hasShowRedPacket = false;
                long nano = System.nanoTime();
                int dirY;
                if (lastNano == 0) {
                    dirY = BLOCK_SPEED;
                } else {
                    float dirMills = (nano - lastNano) / 1000_000F;
                    dirY = Math.round(dirMills * BLOCK_SPEED / SLEEP_TIME);
//                    Log.i("xyz", dirY + " y " + dirMills);
                }
                lastNano = nano;
//                int giftPerTime = (int) (BOOM_PER_TIME * 1.5f);//礼物的慢一倍。
                final float giftPerTime = BOOM_PER_TIME;
                for (RedPacket redPacket : mRedPackets) {
                    int y = redPacket.nextY(dirY);
                    int x = redPacket.nextX(0);
                    if (redPacket.getType() == RedPacket.TYPE_RIBBON) {
                        //彩带飘落得快一点
                        y = redPacket.nextY((int) (dirY * 0.31f));
                    }

                    if (y > visibleY && y < mHeight) {
                        //爆炸!
                        final int typeIndex = redPacket.addTypeIndex(1) - 1;
                        switch (redPacket.getType()) {
                            case RedPacket.TYPE_BOOM:
                                final int boomIndex = (int) (typeIndex / BOOM_PER_TIME);
                                if (boomIndex < RedPacketRes.BOOM_LIST.length) {
                                    redPacket.setImageRes(RedPacketRes.BOOM_LIST[boomIndex]);
                                    if (typeIndex == 0) {
                                        //校准位置
                                        x = redPacket.nextX(-boomDx);
                                        y = redPacket.nextY(-boomDy);
                                    }
                                } else {
                                    redPacket.nextY(INVISIBLE_Y);
                                }
                                break;
                            case RedPacket.TYPE_GIFT:
                                y = redPacket.nextY(-dirY);//位置复原
                                int frame = (int) (typeIndex / giftPerTime);
                                if (frame < RedPacketRes.GIFT_LIST.length) {
                                    redPacket.setImageRes(RedPacketRes.GIFT_LIST[frame]);
                                    if (typeIndex == 0) {
                                        // 校准位置
                                        x = redPacket.nextX(giftDx);
                                        y = redPacket.nextY(giftDy);
                                    } else {
                                        int allTimes = (int) ((RedPacketRes.GIFT_LIST.length - 2) * giftPerTime);
                                        float percent = Math.min(1f, typeIndex * 1f / allTimes);
                                        int dx = (int) ((giftX - x) * percent);
                                        int dy = (int) ((giftY - y) * percent);
                                        x = redPacket.nextX(dx);
                                        y = redPacket.nextY(dy);
                                    }
                                } else {
                                    int doneIndex = frame - RedPacketRes.GIFT_LIST.length;
                                    redPacket.setImageRes(RedPacketRes.GIFT_DONE_LIST[doneIndex % RedPacketRes.GIFT_DONE_LIST.length]);
                                }

                                mLastDrawRedPacket = redPacket;
//                                5秒且等红包雨下完。
//                                if (!mRaining && typeIndex > RedPacketRes.GIFT_LIST.length * giftPerTime + 5_000 / SLEEP_TIME) {
//                                显示完成5秒后消失
                                if (typeIndex > RedPacketRes.GIFT_LIST.length * giftPerTime + 5_000 / SLEEP_TIME) {
                                    redPacket.nextY(INVISIBLE_Y);
                                    mLastDrawRedPacket = null;
                                    mBoxPrizeBean = null;
                                }
                                mRaining = false;
                                continue;//特别注意, 礼物红包需最后绘制。
//                                break;
                            case RedPacket.TYPE_PACKET_OPEN:
                                if (typeIndex == 0) {
                                    redPacket.setImageRes(RedPacketRes.NO_EMOTION);
                                }
                                // 600ms后自动爆炸。
                                if (typeIndex > 600 / SLEEP_TIME) {
                                    redPacket.setType(RedPacket.TYPE_BOOM);
                                }
                                break;
                        }
                        canvas.drawBitmap(getBitmapFromRes(redPacket.getImageRes()), x, y, mPaint);
                        hasShowRedPacket = true;
                    }
                }
                mRaining = hasShowRedPacket;
                if (mLastDrawRedPacket != null) {
                    hasShowRedPacket = true;
                    int x = mLastDrawRedPacket.nextX(0);
                    int y = mLastDrawRedPacket.nextY(0);
                    canvas.drawBitmap(getBitmapFromRes(mLastDrawRedPacket.getImageRes()),
                            x, y, mPaint);
                    /*绘制文字*/
                    if (RedPacketRes.isGiftFullOpen(mLastDrawRedPacket.getImageRes()) && mBoxPrizeBean != null) {
                        int textCenterX = x + giftWidth / 2;
                        int textCenterY = y + giftHeight / 4;
                        String upText = "获得";
                        String belowLeftText = mBoxPrizeBean.prizeName;
                        if (belowLeftText == null) {
                            belowLeftText = "";
                        }
                        String belowRightText = " ×" + mBoxPrizeBean.amount;

                        mTextPaint.setColor(0xFFee91aa);
                        mTextPaint.setTextSize(density * 22);
                        canvas.drawText(upText,
                                textCenterX - mTextPaint.measureText(upText) / 2,
                                textCenterY,
                                mTextPaint);

                        mTextPaint.setColor(0xFFfef5ae);
                        mTextPaint.setTextSize(density * 28);
                        canvas.drawText(belowLeftText,
                                textCenterX - mTextPaint.measureText(belowLeftText),
                                textCenterY + 1f * (mTextPaint.descent() - mTextPaint.ascent()),
                                mTextPaint);

                        mTextPaint.setColor(0xFFffffff);
                        mTextPaint.setTextSize(density * 28);
                        canvas.drawText(belowRightText,
                                textCenterX,
                                textCenterY + 1f * (mTextPaint.descent() - mTextPaint.ascent()),
                                mTextPaint);
                    }
                }
                if (!hasShowRedPacket) {
                    mRedPackets.clear();
                    halt();
                }
            } finally {
                // Publish the frame.  If we overrun the consumer, frames will be dropped,
                // so on a sufficiently fast device the animation will run at faster than
                // the display refresh rate.
                //
                // If the SurfaceTexture has been destroyed, this will throw an exception.
                try {
                    surface.unlockCanvasAndPost(canvas);
                } catch (IllegalArgumentException iae) {
                    Log.d(TAG, "unlockCanvasAndPost failed: " + iae.getMessage());
                    break;
                }
            }
            long costNano = System.nanoTime() - startNano;
            long sleepMills = SLEEP_TIME - costNano / 1000_000;
            if (sleepMills > 0) {
                SystemClock.sleep(sleepMills);
            }
        }

        surface.release();
        mRedPackets.clear();
        mBitmapMap.clear();
    }

    private Bitmap getBitmapFromRes(int imageRes) {
        Bitmap bitmap;
        //缓存策略
        if (mBitmapMap.containsKey(imageRes)) {
            bitmap = mBitmapMap.get(imageRes);
        } else {
            bitmap = BitmapFactory.decodeResource(mResources, imageRes);
            mBitmapMap.put(imageRes, bitmap);
        }
        return bitmap;
    }


    /**
     * 红包雨点击事件，返回点击的item.
     *
     * @param x 点击的x坐标
     * @param y 点击的y坐标
     * @return 返回点中的红包position。（若未点中则返回-1）
     */
    public int getClickPosition(int x, int y) {
        if (!mDone && mStandardBitmap != null && mRedPackets.size() > 0) {
            for (RedPacket redPacket : mRedPackets) {
                if (redPacket.isClickable()
                        && redPacket.isInArea(x, y, mStandardBitmap.getWidth(), mStandardBitmap.getHeight())) {
                    redPacket.setType(RedPacket.TYPE_PACKET_OPEN);
                    return redPacket.getIndex();
                }
            }
        }
        return -1;
    }

    /**
     * 炸掉红包
     */
    public void openBoom(int index) {
        RedPacket redPacket = findRedPacket(index);
        if (redPacket != null) {
            redPacket.setType(RedPacket.TYPE_BOOM);
        }
    }

    /**
     * 得到礼物
     */
    public void openGift(int index, BoxPrizeBean boxPrizeBean) {
        RedPacket redPacket = findRedPacket(index);
        if (redPacket != null) {
            mBoxPrizeBean = boxPrizeBean;
            redPacket.setType(RedPacket.TYPE_GIFT);
            //位置校准
            if (redPacket.nextY(0) >= mHeight) {
                redPacket.setXY(mWidth / 2, mHeight / 2);
            }
        }
    }

    private RedPacket findRedPacket(int index) {
        //0,2,4,6...
        int pos = index * 2;
        RedPacket redPacket = null;
        if (mRedPackets.size() > pos) {
            redPacket = mRedPackets.get(pos);
        }

        if (redPacket == null || redPacket.getIndex() != index) {
            //实在没拿对，那只能遍历了。
            for (RedPacket packet : mRedPackets) {
                if (packet.getIndex() == index) {
                    return packet;
                }
            }
        }
        return redPacket;
    }

    /**
     * Tells the thread to stop running.
     */
    public void halt() {
        synchronized (mLock) {
            mDone = true;
            mLock.notify();
        }
        if (mOnStateChangeListener != null) {
            mOnStateChangeListener.onHalt();
        }
    }

    @Override   // will be called on UI thread
    public void onSurfaceTextureAvailable(SurfaceTexture st, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable(" + width + "x" + height + ")");
        mWidth = width;
        mHeight = height;
        synchronized (mLock) {
            mSurfaceTexture = st;
            mLock.notify();
        }
    }

    @Override   // will be called on UI thread
    public void onSurfaceTextureSizeChanged(SurfaceTexture st, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged(" + width + "x" + height + ")");
        mWidth = width;
        mHeight = height;
    }

    @Override   // will be called on UI thread
    public boolean onSurfaceTextureDestroyed(SurfaceTexture st) {
        Log.d(TAG, "onSurfaceTextureDestroyed");

        synchronized (mLock) {
            mSurfaceTexture = null;
        }
        return true;
    }

    @Override   // will be called on UI thread
    public void onSurfaceTextureUpdated(SurfaceTexture st) {
        //Log.d(TAG, "onSurfaceTextureUpdated");
    }
}
