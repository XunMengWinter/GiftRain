package top.wefor.giftrainpractice.giftrain

import top.wefor.giftrainpractice.R
import java.util.Random


/**
 * Created on 2018/1/26.
 * 红包雨动画涉及到的资源。
 *
 * @author ice
 */
object RedPacketRes {
    private val RANDOM = Random()

    /*普通红包（无序）*/
    val NORMAL_LIST: IntArray = intArrayOf(
        R.mipmap.img_red_packet_1,
        R.mipmap.img_red_packet_2,
        R.mipmap.img_red_packet_3,
    )

    /*无表情红包*/
    @JvmField
    val NO_EMOTION: Int = R.mipmap.img_red_packet

    /*彩带（无序）*/
    val RIBBON_LIST: IntArray = intArrayOf(
        R.mipmap.img_red_packet_ribbon_1,
        R.mipmap.img_red_packet_ribbon_2,
        R.mipmap.img_red_packet_ribbon_3,
        R.mipmap.img_red_packet_ribbon_4,
        R.mipmap.img_red_packet_ribbon_5,
        R.mipmap.img_red_packet_ribbon_6,
        R.mipmap.img_red_packet_ribbon_7,
        R.mipmap.img_red_packet_ribbon_8,
        R.mipmap.img_red_packet_ribbon_9,
    )

    /*爆炸列表*/
    @JvmField
    val BOOM_LIST: IntArray = intArrayOf(
        //            R.mipmap.ic_red_packet,
        R.mipmap.img_red_packet_boom_1,
        R.mipmap.img_red_packet_boom_2,
        R.mipmap.img_red_packet_boom_3,
        R.mipmap.img_red_packet_boom_4,
        R.mipmap.img_red_packet_boom_5,
    )

    /*礼物开启*/
    @JvmField
    val GIFT_LIST: IntArray = intArrayOf(
        R.mipmap.img_red_packet_gift_00,
        R.mipmap.img_red_packet_gift_02,
        R.mipmap.img_red_packet_gift_04,
        R.mipmap.img_red_packet_gift_06,
        R.mipmap.img_red_packet_gift_08,
        R.mipmap.img_red_packet_gift_10,
        R.mipmap.img_red_packet_gift_12,
        R.mipmap.img_red_packet_gift_14,
        R.mipmap.img_red_packet_gift_16,
    )

    /*礼物开启后光圈*/
    @JvmField
    val GIFT_DONE_LIST: IntArray = intArrayOf(
        R.mipmap.img_red_packet_gift_18,
        R.mipmap.img_red_packet_gift_20,
        R.mipmap.img_red_packet_gift_22,
        R.mipmap.img_red_packet_gift_24,
        R.mipmap.img_red_packet_gift_26,
        R.mipmap.img_red_packet_gift_28,
        R.mipmap.img_red_packet_gift_30,
        R.mipmap.img_red_packet_gift_32,
    )


    @JvmStatic
    val packet: Int
        /*获取一个随机下落红包*/
        get() = NORMAL_LIST[RANDOM.nextInt(NORMAL_LIST.size)]

    @JvmStatic
    val ribbon: Int
        /*获取一个随机下落彩带*/
        get() = RIBBON_LIST[RANDOM.nextInt(RIBBON_LIST.size)]

    fun isLastBoom(imageRes: Int): Boolean {
        return imageRes == BOOM_LIST[BOOM_LIST.size - 1]
    }

    /**
     * 礼物是否已完全展现
     */
    @JvmStatic
    fun isGiftFullOpen(imageRes: Int): Boolean {
        for (i in GIFT_LIST.size / 2 until GIFT_LIST.size) {
            if (GIFT_LIST[i] == imageRes) {
                return true
            }
        }
        for (doneRes in GIFT_DONE_LIST) {
            if (doneRes == imageRes) {
                return true
            }
        }
        return false
    }
}
