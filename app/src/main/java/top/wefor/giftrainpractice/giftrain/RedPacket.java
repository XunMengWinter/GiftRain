package top.wefor.giftrainpractice.giftrain;

/**
 * Created on 2018/1/23.
 * 飘落对象。
 * 统一看待红包、飘带、爆炸物、礼物。
 *
 * @author ice
 */

public class RedPacket {

    public static final int TYPE_PACKET = 1; //只有这个类型可以点击
    public static final int TYPE_PACKET_OPEN = 3; //开启后
    public static final int TYPE_BOOM = 5; //爆炸
    public static final int TYPE_GIFT = 7; //礼物

    public static final int TYPE_RIBBON = 12; //彩带


    private int mIndex = -1; //红包在红包流中的位置（未包含彩带）。

    private int mX, mY; //飘落物此刻的坐标。

    private int mImageRes; //飘落物的图片资源。

    private int mType = TYPE_PACKET; //飘落物体的类型。

    private int mTypeIndex; //飘落物当前类型的索引（每帧自增）。

    public RedPacket() {

    }

    public RedPacket(int x, int y) {
        mX = x;
        mY = y;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int nextX(int dx) {
        mX = mX + dx;
        return mX;
    }

    public int nextY(int dy) {
        mY = mY + dy;
        return mY;
    }

    public void setXY(int x, int y) {
        mX = x;
        mY = y;
    }

    public boolean isInArea(int x, int y) {
        return false;
    }

    public int getImageRes() {
        return mImageRes;
    }

    public void setImageRes(int imageRes) {
        mImageRes = imageRes;
    }

    public boolean isClickable() {
        return mType == TYPE_PACKET;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        if (mType != type) {
            mTypeIndex = 0;
        }
        mType = type;
    }

    public void setTypeIndex(int typeIndex) {
        mTypeIndex = typeIndex;
    }

    public int addTypeIndex(int addIndex) {
        mTypeIndex += addIndex;
        return mTypeIndex;
    }

    /*判断某个点是否在区域内*/
    public boolean isInArea(int x, int y, int width, int height) {
        boolean inX = x > mX && x < mX + width;
        boolean inY = y > mY && y < mY + height;
        return inX && inY;
    }

}
