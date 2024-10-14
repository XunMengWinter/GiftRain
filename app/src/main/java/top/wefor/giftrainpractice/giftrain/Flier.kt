package top.wefor.giftrainpractice.giftrain

/**
 * 飞行物(设计待完善，需要考虑到某些飞行物可以转换（比如红包boom会变成爆炸物，也可能变成礼包）。
 *
 *
 * Created on 2018/2/1.
 *
 * @author ice
 */
@Deprecated("")
interface Flier {
    fun nextX(dx: Int): Int

    fun nextY(dy: Int): Int

    /*判断某个点是否在区域内*/
    fun isInArea(x: Int, y: Int): Boolean

    val imageRes: Int

    val isClickable: Boolean

    val type: Int

    fun addTypeIndex(addIndex: Int): Int
}