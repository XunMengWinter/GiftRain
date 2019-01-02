package top.wefor.giftrainpractice.model;

/**
 * Created on 2018/10/3.
 *
 * @author ice
 */
public class BoxInfo {
    private int awardId;
    private String voucher;

    public int getAwardId() {
        return awardId;
    }

    public BoxInfo setAwardId(int awardId) {
        this.awardId = awardId;
        return this;
    }

    public String getVoucher() {
        return voucher;
    }

    public BoxInfo setVoucher(String voucher) {
        this.voucher = voucher;
        return this;
    }
}
