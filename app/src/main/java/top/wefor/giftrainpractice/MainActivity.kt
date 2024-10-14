package top.wefor.giftrainpractice

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import top.wefor.giftrainpractice.giftrain.RedPacketViewHelper
import top.wefor.giftrainpractice.giftrain.RedPacketViewHelper.GiftRainListener
import top.wefor.giftrainpractice.model.BoxInfo
import top.wefor.giftrainpractice.model.BoxPrizeBean

class MainActivity : AppCompatActivity() {
    var mRedPacketViewHelper: RedPacketViewHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRedPacketViewHelper = RedPacketViewHelper(this)
    }

    fun rain(view: View) {
        view.isEnabled = false
        mRedPacketViewHelper!!.endGiftRain()
        window.decorView.postDelayed({
            val boxInfos: MutableList<BoxInfo> = ArrayList()
            for (i in 0..31) {
                val boxInfo = BoxInfo()
                boxInfo.setAwardId(i)
                boxInfo.setVoucher("ice $i")
                boxInfos.add(boxInfo)
            }
            mRedPacketViewHelper!!.launchGiftRainRocket(0, boxInfos, object : GiftRainListener {
                override fun startLaunch() {
                }

                override fun startRain() {
                }

                override fun openGift(boxPrizeBean: BoxPrizeBean) {
                }

                override fun endRain() {
                    view.isEnabled = true
                }
            })
        }, 500)
    }

    override fun onDestroy() {
        super.onDestroy()
        mRedPacketViewHelper!!.endGiftRain()
    }
}
