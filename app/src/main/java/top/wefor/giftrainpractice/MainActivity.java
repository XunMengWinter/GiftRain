package top.wefor.giftrainpractice;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import top.wefor.giftrainpractice.giftrain.RedPacketViewHelper;
import top.wefor.giftrainpractice.model.BoxInfo;
import top.wefor.giftrainpractice.model.BoxPrizeBean;

public class MainActivity extends AppCompatActivity {

    RedPacketViewHelper mRedPacketViewHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRedPacketViewHelper = new RedPacketViewHelper(this);
    }

    public void rain(View view) {
        view.setEnabled(false);
        mRedPacketViewHelper.endGiftRain();
        getWindow().getDecorView().postDelayed(() -> {
            List<BoxInfo> boxInfos = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                BoxInfo boxInfo = new BoxInfo();
                boxInfo.setAwardId(i);
                boxInfo.setVoucher("ice " + i);
                boxInfos.add(boxInfo);
            }
            Point point = new Point();
            getWindow().getWindowManager().getDefaultDisplay().getSize(point);
            mRedPacketViewHelper.launchGiftRainRocket(0, boxInfos, point.x, point.x, new RedPacketViewHelper.GiftRainListener() {
                @Override
                public void startLaunch() {

                }

                @Override
                public void startRain() {

                }

                @Override
                public void openGift(BoxPrizeBean boxPrizeBean) {

                }

                @Override
                public void endRain() {
                    view.setEnabled(true);
                }
            });
        }, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRedPacketViewHelper.endGiftRain();
    }
}
