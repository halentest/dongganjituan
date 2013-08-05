package cn.halen.service.jd;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.request.order.OrderSearchRequest;
import com.jd.open.api.sdk.request.order.OrderSopOutstorageRequest;
import com.jd.open.api.sdk.response.order.OrderSearchResponse;
import com.jd.open.api.sdk.response.order.OrderSopOutstorageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: hzhang
 * Date: 7/30/13
 * Time: 10:24 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class JdTradeClient {

    @Autowired
    private JdConfig config;

    public void test() throws JdException {
        JdClient client = new DefaultJdClient(config.getUrl(), config.getToken(), config.getAppKey(), config.getAppSecret());
        OrderSopOutstorageRequest request = new OrderSopOutstorageRequest();
        request.setOrderId("500015539");
        request.setLogisticsId("463");
        request.setWaybill("1828047314");
        request.setTradeNo("400f33234423");
        OrderSopOutstorageResponse response = client.execute(request);
        System.out.println(response.getMsg());
    }

    public void queryOrder() throws JdException {
        OrderSearchRequest request = new OrderSearchRequest();
        request.setOrderState("WAIT_SELLER_STOCK_OUT");
        request.setPage("1");
        request.setPageSize("100");
        request.setOptionalFields("vender_id,order_id,pay_type,order_payment,seller_discount,invoice_info,order_remark," +
                "order_start_time,consignee_info,item_info_list");
        OrderSearchResponse response = config.getClient().execute(request);
        System.out.println(response.getMsg());
    }
}
