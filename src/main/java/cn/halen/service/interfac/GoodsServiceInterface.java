package cn.halen.service.interfac;

import java.util.List;

import cn.halen.controller.formbean.GoodsBase;
import cn.halen.controller.formbean.GoodsStore;
import cn.halen.data.pojo.Goods;
import cn.halen.service.ResultInfo;

public interface GoodsServiceInterface {
	ResultInfo updateGoodsBase(GoodsBase goodsBase);
	ResultInfo updateGoodsStore(GoodsStore goodsStore);
	List<Goods> list();
	Goods getById(long id);
}
