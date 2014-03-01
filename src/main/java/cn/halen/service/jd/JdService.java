package cn.halen.service.jd;

import cn.halen.data.pojo.MySku;
import cn.halen.data.pojo.Shop;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.request.ware.WareSkuStockUpdateRequest;
import com.jd.open.api.sdk.response.ware.WareSkuStockUpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by apple on 14-3-1.
 */
@Service
public class JdService {

    @Autowired
    private JdConfig jdConfig;

    private Logger log = LoggerFactory.getLogger(JdService.class);

    public String updateInventory(MySku sku, Shop shop) throws JdException {
        JdClient client = new DefaultJdClient(jdConfig.getUrl(), shop.getToken(), jdConfig.getAppKey(), jdConfig.getAppSecret());
        WareSkuStockUpdateRequest request = new WareSkuStockUpdateRequest();
        //request.setSkuId("1100051093");
        String outerId = sku.getGoods_id() + sku.getColor_id() + sku.getSize();
        long q = sku.getQuantity() - sku.getManaual_lock_quantity() - sku.getLock_quantity();
        request.setOuterId(outerId);
        request.setQuantity(String.valueOf(q));
        WareSkuStockUpdateResponse res = client.execute(request);
        if("0".equals(res.getCode())) {
            return null;
        }
        return res.getCode() + res.getMsg();
    }
}
