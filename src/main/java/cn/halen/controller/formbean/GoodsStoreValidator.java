package cn.halen.controller.formbean;

public class GoodsStoreValidator extends BaseValidator {
	private GoodsStore goodsStore;

	public GoodsStoreValidator(GoodsStore goodsStore) {
		this.goodsStore = goodsStore;
	}
	public void doValidate() {
		validNotNegative(goodsStore.getThity_eight(), "38的值不能小于0");
		validNotNegative(goodsStore.getThity_nine(), "39的值不能小于0");
		validNotNegative(goodsStore.getForty(), "40的值不能小于0");
		validNotNegative(goodsStore.getForty_one(), "41的值不能小于0");
		validNotNegative(goodsStore.getForty_two(), "42的值不能小于0");
		validNotNegative(goodsStore.getForty_three(), "43的值不能小于0");
		validNotNegative(goodsStore.getForty_four(), "44的值不能小于0");
	}
}
