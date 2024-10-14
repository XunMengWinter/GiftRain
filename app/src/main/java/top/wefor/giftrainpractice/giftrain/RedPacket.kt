package top.wefor.giftrainpractice.giftrain

/**
 * Created on 2018/1/23.
 * 飘落对象。
 * 统一看待红包、飘带、爆炸物、礼物。
 *
 * @author ice
 */
class RedPacket {
    var index: Int = -1 //红包在红包流中的位置（未包含彩带）。

    private var mX = 0
    private var mY = 0 //飘落物此刻的坐标。

    var imageRes: Int = 0 //飘落物的图片资源。

    private var mType = TYPE_PACKET //飘落物体的类型。

    private var mTypeIndex = 0 //飘落物当前类型的索引（每帧自增）。

    constructor()

    constructor(x: Int, y: Int) {
        mX = x
        mY = y
    }

    fun nextX(dx: Int): Int {
        mX = mX + dx
        return mX
    }

    fun nextY(dy: Int): Int {
        mY = mY + dy
        return mY
    }

    fun setXY(x: Int, y: Int) {
        mX = x
        mY = y
    }

    fun isInArea(x: Int, y: Int): Boolean {
        return false
    }

    val isClickable: Boolean
        get() = mType == TYPE_PACKET

    var type: Int
        get() = mType
        set(type) {
            if (mType != type) {
                mTypeIndex = 0
            }
            mType = type
        }

    fun setTypeIndex(typeIndex: Int) {
        mTypeIndex = typeIndex
    }

    fun addTypeIndex(addIndex: Int): Int {
        mTypeIndex += addIndex
        return mTypeIndex
    }

    /*判断某个点是否在区域内*/
    fun isInArea(x: Int, y: Int, width: Int, height: Int): Boolean {
        val inX = x > mX && x < mX + width
        val inY = y > mY && y < mY + height
        return inX && inY
    }

    companion object {
        const val TYPE_PACKET: Int = 1 //只有这个类型可以点击
        const val TYPE_PACKET_OPEN: Int = 3 //开启后
        const val TYPE_BOOM: Int = 5 //爆炸
        const val TYPE_GIFT: Int = 7 //礼物

        const val TYPE_RIBBON: Int = 12 //彩带
    }
}
