package top.wefor.giftrainpractice.giftrain;


import java.util.Random;

import top.wefor.giftrainpractice.R;

/**
 * Created on 2018/1/26.
 * 红包雨动画涉及到的资源。
 *
 * @author ice
 */

public class RedPacketRes {

    private static final Random RANDOM = new Random();

    /*普通红包（无序）*/
    public static final int[] NORMAL_LIST = {
            R.mipmap.img_red_packet_1,
            R.mipmap.img_red_packet_2,
            R.mipmap.img_red_packet_3,
    };

    /*无表情红包*/
    public static final int NO_EMOTION = R.mipmap.img_red_packet;

    /*彩带（无序）*/
    public static final int[] RIBBON_LIST = {
            R.mipmap.img_red_packet_ribbon_1,
            R.mipmap.img_red_packet_ribbon_2,
            R.mipmap.img_red_packet_ribbon_3,
            R.mipmap.img_red_packet_ribbon_4,
            R.mipmap.img_red_packet_ribbon_5,
            R.mipmap.img_red_packet_ribbon_6,
            R.mipmap.img_red_packet_ribbon_7,
            R.mipmap.img_red_packet_ribbon_8,
            R.mipmap.img_red_packet_ribbon_9,
    };

    /*爆炸列表*/
    public static final int[] BOOM_LIST = {
//            R.mipmap.ic_red_packet,
            R.mipmap.img_red_packet_boom_1,
            R.mipmap.img_red_packet_boom_2,
            R.mipmap.img_red_packet_boom_3,
            R.mipmap.img_red_packet_boom_4,
            R.mipmap.img_red_packet_boom_5,
    };

    /*礼物开启*/
    public static final int[] GIFT_LIST = {
            R.mipmap.img_red_packet_gift_00,
            R.mipmap.img_red_packet_gift_02,
            R.mipmap.img_red_packet_gift_04,
            R.mipmap.img_red_packet_gift_06,
            R.mipmap.img_red_packet_gift_08,
            R.mipmap.img_red_packet_gift_10,
            R.mipmap.img_red_packet_gift_12,
            R.mipmap.img_red_packet_gift_14,
            R.mipmap.img_red_packet_gift_16,
    };

    /*礼物开启后光圈*/
    public static final int[] GIFT_DONE_LIST = {
            R.mipmap.img_red_packet_gift_18,
            R.mipmap.img_red_packet_gift_20,
            R.mipmap.img_red_packet_gift_22,
            R.mipmap.img_red_packet_gift_24,
            R.mipmap.img_red_packet_gift_26,
            R.mipmap.img_red_packet_gift_28,
            R.mipmap.img_red_packet_gift_30,
            R.mipmap.img_red_packet_gift_32,
    };


    /*获取一个随机下落红包*/
    public static int getPacket() {
        return NORMAL_LIST[RANDOM.nextInt(NORMAL_LIST.length)];
    }

    /*获取一个随机下落彩带*/
    public static int getRibbon() {
        return RIBBON_LIST[RANDOM.nextInt(RIBBON_LIST.length)];
    }

    public static boolean isLastBoom(int imageRes) {
        return imageRes == BOOM_LIST[BOOM_LIST.length - 1];
    }

    public static boolean isGiftDone(int imageRes) {
        for (int i = GIFT_LIST.length / 2; i < GIFT_LIST.length; i++) {
            if (GIFT_LIST[i] == imageRes) {
                return true;
            }
        }
        for (int doneRes : GIFT_DONE_LIST) {
            if (doneRes == imageRes) {
                return true;
            }
        }
        return false;
    }

}
