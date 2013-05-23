package cn.halen.controller.formbean;

public class GoodsBaseValidator extends BaseValidator {
	private GoodsBase goodsBase;

	public GoodsBaseValidator(GoodsBase goodsBase) {
		this.goodsBase = goodsBase;
	}
	public void doValidate() {
		validNotEmpty(goodsBase.getHid(), "商品编号不能为空!");
		validNotEmpty(goodsBase.getColor(), "颜色不能为空!");
		validBigger0(goodsBase.getPrice(), "价格不能小于或等于0!");
		validBigger0(goodsBase.getWeight(), "重量不能小于或等于0!");
	}
}
