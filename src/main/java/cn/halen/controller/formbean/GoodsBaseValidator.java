package cn.halen.controller.formbean;

public class GoodsBaseValidator extends BaseValidator {
	private GoodsBase goodsBase;

	public GoodsBaseValidator(GoodsBase goodsBase) {
		this.goodsBase = goodsBase;
	}
	public void doValidate() {
//		validNotEmpty(goodsBase.getHid(), "��Ʒ��Ų���Ϊ��!");
//		validNotEmpty(goodsBase.getColor(), "��ɫ����Ϊ��!");
//		validBigger0(goodsBase.getPrice(), "�۸���С�ڻ����0!");
//		validBigger0(goodsBase.getWeight(), "��������С�ڻ����0!");
	}
}
