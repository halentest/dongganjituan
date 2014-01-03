package cn.halen.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.halen.data.mapper.MyTradeMapper;
import cn.halen.data.pojo.Goods;
import cn.halen.data.pojo.Shop;
import cn.halen.service.excel.GoodsRow;
import cn.halen.service.top.TopConfig;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Sku;
import com.taobao.api.request.SkusCustomGetRequest;
import com.taobao.api.response.SkusCustomGetResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.MySku;
import cn.halen.service.top.ItemClient;

import com.taobao.api.ApiException;
import com.taobao.api.domain.Item;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoodsService {
	private static final Logger logger = LoggerFactory.getLogger(GoodsService.class);
	
	@Autowired
	private MySkuMapper skuMapper;
	
	@Autowired
	private GoodsMapper goodsMapper;
	
	@Autowired
	private ItemClient itemClient;

    @Autowired
    private TopConfig topConfig;

    @Autowired
    private MyTradeMapper tradeMapper;
	
	/**
	 * @throws ApiException
	 */
	public void updateSkuQuantity(List<Long> skuIdList, Shop shop) throws ApiException {

        for(long skuId : skuIdList) {
            MySku mySku = skuMapper.select(skuId);
            if(null == mySku) {
                continue;
            }
            String outerSkuId = mySku.getGoods_id() + mySku.getColor_id() + mySku.getSize();
            TaobaoClient client = topConfig.getRetryClient();
            SkusCustomGetRequest req = new SkusCustomGetRequest();
            req.setOuterId(outerSkuId);
            req.setFields("sku_id, num_iid");
            SkusCustomGetResponse response = client.execute(req , shop.getToken());
            if(!response.isSuccess()) {
                continue;
            }
            List<Sku> skuList = response.getSkus();
            if(skuList==null || skuList.size()==0) {
                logger.info("query shop : {}, skuId : {} , outerSkuId ： {}， have no item!", shop.getSeller_nick(), skuId,
                        outerSkuId);
            } else {
                logger.debug("query shop : {}, skuId : {}, outerSkuId : {}, skuList from top size is {}", shop.getSeller_nick(),
                        skuId, outerSkuId, skuList.size());
            }
            if(skuList != null && skuList.size() > 0) {
                long onlineQuantity = Math.round((mySku.getQuantity() - mySku.getLock_quantity()
                        - mySku.getManaual_lock_quantity()) * shop.getRate());
                for(Sku sku : skuList) {
                    itemClient.updateSkuQuantity(sku.getNumIid(), sku.getSkuId(), onlineQuantity, shop.getToken());
                }
            }
        }
	}

    /**
     * @throws ApiException
     */
    public String updateSkuQuantity(MySku mySku, Shop shop) {

        String outerSkuId = mySku.getGoods_id() + mySku.getColor_id() + mySku.getSize();
        TaobaoClient client = topConfig.getRetryClient();
        SkusCustomGetRequest req = new SkusCustomGetRequest();
        req.setOuterId(outerSkuId);
        req.setFields("sku_id, num_iid");
        SkusCustomGetResponse response = null;
        try {
            response = client.execute(req , shop.getToken());
        } catch (ApiException e) {
            logger.error("query sku error", e);
            return "query sku ApiException";
        }
        if(!response.isSuccess()) {
            return "查询sku失败";
        }
        List<Sku> skuList = response.getSkus();
        if(skuList==null || skuList.size()==0) {
            logger.info("query shop : {}, skuId : {} , outerSkuId ： {}， have no item!", shop.getSeller_nick(), mySku.getId(),
                    outerSkuId);
        } else {
            logger.debug("query shop : {}, skuId : {}, outerSkuId : {}, skuList from top size is {}", shop.getSeller_nick(),
                    mySku.getColor_id(), outerSkuId, skuList.size());
        }
        if(skuList != null && skuList.size() > 0) {
            long onlineQuantity = Math.round((mySku.getQuantity() - mySku.getLock_quantity()
                    - mySku.getManaual_lock_quantity()) * shop.getRate());
            for(Sku sku : skuList) {
                try {
                    itemClient.updateSkuQuantity(sku.getNumIid(), sku.getSkuId(), onlineQuantity, shop.getToken());
                } catch (ApiException e) {
                    return "update sku ApiException";
                }
            }
        }
        return null;
    }

	/**
	 * @param item 
	 * @return color+size -> sku_id
	 */
	private Map<String, Long> getTaoSku(Item item) {
		Map<String, Long> result = new HashMap<String, Long>();
		Map<String, String> alias = new HashMap<String, String>();
		String propertyAlias = item.getPropertyAlias(); // 1627207:3232480:杏色;1627207:3232480:杏色 
		if(StringUtils.isNotBlank(propertyAlias)) {
			String[] aliasArray = propertyAlias.split(";");
			for(String aliasArrayItem : aliasArray) {
				String[] itemArray = aliasArrayItem.split(":");
				alias.put(itemArray[0] + ":" + itemArray[1], itemArray[2]); // 1627207:3232480 -> 杏色
			} 
		}
		
		for(com.taobao.api.domain.Sku sku : item.getSkus()) {
			String color = null;
			String size = null;
			String propsName = sku.getPropertiesName();
			String[] propsNameArray = propsName.split(";");
			for(String propsNameArrayItem : propsNameArray) {
				String[] itemArray = propsNameArrayItem.split(":");
				if(itemArray[0].equals("1627207")) {
					color = alias.get(itemArray[0] + ":" + itemArray[1]);
					if(color==null) {
						color = itemArray[3];
					}
				}
				if(itemArray[0].equals("21921") || itemArray[0].equals("20518") ) {
					size = alias.get(itemArray[0] + ":" + itemArray[1]);
					if(size==null) {
						size = itemArray[3];
					}
				}
			}
			result.put(color+size, sku.getSkuId());
		}	
		return result;
	}

    public String checkRow(List<GoodsRow> rows) {
        for(GoodsRow row : rows) {
            List<MySku> skuList = skuMapper.selectByGoodsId(row.getGoodsId());
            if(skuList.size()==0 && (row.getColors().size()==0 || row.getSizes().size()==0)) {
                return "(" + row.getGoodsId() + ")颜色和尺码都不能为空!";
            }
            Map<String, Boolean> colorMap = new HashMap<String, Boolean>();
            Map<String, Boolean> sizeMap = new HashMap<String, Boolean>();
            for(MySku sku : skuList) {
                colorMap.put(sku.getColor(), true);
                sizeMap.put(sku.getSize(), true);
            }
            List<String> colors = row.getColors();
            List<String> sizes = row.getSizes();
            for(String color : colors) {
                if(colorMap.containsKey(color)) {
                    return "(" + row.getGoodsId() + "," + color + ")已经存在，不能重复添加!";
                }
            }
            for(String size : sizes) {
                if(sizeMap.containsKey(size)) {
                    return "(" + row.getGoodsId() + "," + size + ")已经存在，不能重复添加!";
                }
            }
        }
        return null;
    }

    /**
     * @param rows
     * @param isDelete  true or false, if false then add
     */
    @Transactional(rollbackFor=Exception.class)
    public void execRow(List<GoodsRow> rows, boolean isDelete) {
        if(isDelete) {
            for(GoodsRow row : rows) {
                String goodsId = row.getGoodsId();
                List<String> colors = row.getColors();
                List<String> sizes = row.getSizes();

                for(String color : colors) {
                    List<MySku> list = skuMapper.selectByGoodsIdColor(goodsId, color);
                    if(null==list || list.size()==0) {
                        continue;
                    }
                    List<Long> idList = new ArrayList<Long>(list.size());
                    for(MySku sku : list) {
                        idList.add(sku.getId());
                    }
                    //检查有没有相关的订单，如果有就不能删除
                    boolean skuHasOrder = tradeMapper.checkSkuHasOrder(idList);
                    if(skuHasOrder) {
                        continue;
                    }
                    //删除sku
                    skuMapper.deleteBatch(idList);
                }

                for(String size : sizes) {
                    List<MySku> list = skuMapper.selectByGoodsIdSize(goodsId, size);
                    if(null == list || list.size()==0) {
                        continue;
                    }
                    List<Long> idList = new ArrayList<Long>(list.size());
                    for(MySku sku : list) {
                        idList.add(sku.getId());
                    }
                    //检查有没有相关的订单，如果有就不能删除
                    boolean skuHasOrder = tradeMapper.checkSkuHasOrder(idList);
                    if(skuHasOrder) {
                        continue;
                    }
                    //删除sku
                    skuMapper.deleteBatch(idList);
                }
                //检查此商品是否还有sku，如果没有就删除它
                List<MySku> list = skuMapper.selectByGoodsId(goodsId);
                if(null==list || list.size()==0) {
                    goodsMapper.deleteByHid(goodsId);
                }
            }
            return;
        }

        for(GoodsRow row : rows) {
            Goods goods = goodsMapper.getByHid(row.getGoodsId());
            List<String> colorIds = row.getColorIds();
            List<String> colors = row.getColors();
            if(null == goods) {
                goods = new Goods();
                goods.setHid(row.getGoodsId());
                goods.setTitle(row.getName());
                goods.setPrice(row.getPrice());
                goods.setStatus(1);
                goods.setTemplate("默认模板");
                goodsMapper.insert(goods);
                for(int i=0; i<colors.size(); i++) {
                    String color = colors.get(i);
                    for(String size : row.getSizes()) {
                        MySku sku = new MySku();
                        sku.setGoods_id(row.getGoodsId());
                        sku.setColor(color);
                        sku.setSize(size);
                        sku.setColor_id(colorIds.get(i));
                        sku.setQuantity(0);
                        skuMapper.insert(sku);
                    }
                }
            } else {
                List<MySku> skuList = skuMapper.selectByGoodsId(row.getGoodsId());
                Map<String, String> colorMap = new HashMap<String, String>();
                Set<String> sizeSet = new HashSet<String>();
                for(MySku sku : skuList) {
                    colorMap.put(sku.getColor(), sku.getColor_id());
                    sizeSet.add(sku.getSize());
                }
                for(int i=0; i<colors.size(); i++) {
                    colorMap.put(colors.get(i), colorIds.get(i));
                }
                for(String size : row.getSizes()) {
                    sizeSet.add(size);
                }

                for(Map.Entry<String, String> entry : colorMap.entrySet()) {
                    for(String size : sizeSet) {
                        MySku sku = skuMapper.select(row.getGoodsId(), entry.getKey(), size);
                        if(null == sku) {
                            sku = new MySku();
                            sku.setGoods_id(row.getGoodsId());
                            sku.setColor(entry.getKey());
                            sku.setSize(size);
                            sku.setColor_id(entry.getValue());
                            sku.setQuantity(0);
                            skuMapper.insert(sku);
                        }
                    }
                }
            }
        }
    }
	
}
