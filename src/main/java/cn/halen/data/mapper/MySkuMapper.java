package cn.halen.data.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.halen.util.Paging;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import cn.halen.data.pojo.MySku;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySkuMapper extends SqlSessionDaoSupport {

    private Logger log = LoggerFactory.getLogger(MySkuMapper.class);

    /**
     * 依据商家自定义sku id查询sku
     * @param hid
     * @return
     */
    public MySku selectByHid(String hid) {
        return getSqlSession().selectOne("cn.halen.data.mapper.SkuMapper.selectByHid", hid);
    }

    public List<MySku> selectManaualLock(Paging paging, String goodsId) {
        Map<Object, Object> param = new HashMap<Object, Object>();
        param.put("goodsId", goodsId);
        param.put("start", paging.getStart());
        param.put("pageSize", paging.getPageSize());
        return getSqlSession().selectList("cn.halen.data.mapper.SkuMapper.selectManaualLock", param);
    }

    public int countManaualLock(String goodsId) {
        Map<Object, Object> param = new HashMap<Object, Object>();
        param.put("goodsId", goodsId);
        return getSqlSession().selectOne("cn.halen.data.mapper.SkuMapper.countManaualLock", param);
    }

	public int insert(MySku sku) {
        MySku exist = this.select(sku.getGoods_id(), sku.getColor(), sku.getSize());
        if(null != exist) {
            log.debug("sku ({}, {}, {}) has exists, will not insert again.", sku.getGoods_id(),
                    sku.getColor(), sku.getSize());
            return 0;
        }
		int count = getSqlSession().insert("cn.halen.data.mapper.SkuMapper.insert", sku);
		return count;
	}
	
	public int delete(int id) {
		int count = getSqlSession().delete("cn.halen.data.mapper.SkuMapper.delete", id);
		return count;
	}

    public long sumQuantity() {
        long result = 0;
        try{
            result = getSqlSession().selectOne("cn.halen.data.mapper.SkuMapper.sumQuantity");
        } catch (Exception e) {
        //当表为空时，会抛出Null异常，这里不做异常处理，返回数量为0
        }
        return result;
    }

    public long sumLockQuantity() {
        long result = 0;
        try {
            result = getSqlSession().selectOne("cn.halen.data.mapper.SkuMapper.sumLockQuantity");
        } catch (Exception e) {
            //当表为空时，会抛出Null异常，这里不做异常处理，返回数量为0
        }
        return result;
    }

	public int update(MySku sku) {
		int count = getSqlSession().update("cn.halen.data.mapper.SkuMapper.update", sku);
		return count;
	}
	
	public MySku select(String goodsId, String color, String size) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("goods_id", goodsId);
		param.put("color", color);
		param.put("size1", size);
		return getSqlSession().selectOne("cn.halen.data.mapper.SkuMapper.select", param);
	}
	
	public MySku select(long skuId) {
		return getSqlSession().selectOne("cn.halen.data.mapper.SkuMapper.selectBySkuId", skuId);
	}

    public List<MySku> selectByGoodsId(String goodsId) {
        return getSqlSession().selectList("cn.halen.data.mapper.SkuMapper.selectByGoodsId", goodsId);
    }

    public List<MySku> selectByGoodsIdColor(String goodsId, String color) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("goodsId", goodsId);
        param.put("color", color);
        return getSqlSession().selectList("cn.halen.data.mapper.SkuMapper.selectByGoodsIdColor", param);
    }

    public List<MySku> selectByGoodsIdSize(String goodsId, String size) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("goodsId", goodsId);
        param.put("size1", size);
        return getSqlSession().selectList("cn.halen.data.mapper.SkuMapper.selectByGoodsIdSize", param);
    }

    public int updateColorByGoodsIdColor(String goodsId, String color, String newColor) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("goodsId", goodsId);
        param.put("color", color);
        param.put("newColor", newColor);
        return getSqlSession().update("cn.halen.data.mapper.SkuMapper.updateColorByGoodsIdColor", param);
    }

    public int updateSizeByGoodsIdSize(String goodsId, String size, String newSize) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("goodsId", goodsId);
        param.put("size1", size);
        param.put("newSize", newSize);
        return getSqlSession().update("cn.halen.data.mapper.SkuMapper.updateSizeByGoodsIdSize", param);
    }
}
