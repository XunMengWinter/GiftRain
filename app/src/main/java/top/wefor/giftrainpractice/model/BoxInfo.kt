package top.wefor.giftrainpractice.model

/**
 * Created on 2018/10/3.
 * 红包
 *
 * @author ice
 */
class BoxInfo {
    //红包ID（拿这个去问服务器是否中奖）
    var awardId: Int = 0
        private set

    //红包校验
    var voucher: String? = null
        private set

    fun setAwardId(awardId: Int): BoxInfo {
        this.awardId = awardId
        return this
    }

    fun setVoucher(voucher: String?): BoxInfo {
        this.voucher = voucher
        return this
    }
}
