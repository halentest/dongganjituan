package cn.halen.service.jd;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.domain.order.OrderInfo;
import com.jd.open.api.sdk.domain.order.OrderResult;
import com.jd.open.api.sdk.domain.order.OrderSearchInfo;
import com.jd.open.api.sdk.request.order.OrderSearchRequest;
import com.jd.open.api.sdk.request.order.OrderSopOutstorageRequest;
import com.jd.open.api.sdk.response.order.OrderSearchResponse;
import com.jd.open.api.sdk.response.order.OrderSopOutstorageResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hzhang
 * Date: 7/30/13
 * Time: 10:24 PM
 * To change this template use File | Settings | File Templates.
 * 负责调用京东商城接口
 */
@Service
public class JdTradeClient {

    @Autowired
    private JdConfig config;

    private Logger log = LoggerFactory.getLogger(JdTradeClient.class);

    private static final String PAGE_SIZE = "50";

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

    /**
     * 查询所有待发货的订单
     * @param startDate
     * @param endDate
     * @return
     * @throws JdException
     */
    public List<OrderSearchInfo> queryOrder(String startDate, String endDate) throws JdException {
        List<OrderSearchInfo> result = new ArrayList<OrderSearchInfo>();

        OrderSearchRequest request = new OrderSearchRequest();
        request.setOrderState("WAIT_SELLER_STOCK_OUT");
        if(StringUtils.isNotBlank(startDate)) {
            request.setStartDate(startDate);
        }
        if(StringUtils.isNotBlank(endDate)) {
            request.setEndDate(endDate);
        }
        request.setPage("1");
        request.setPageSize(PAGE_SIZE);
        request.setOptionalFields("vender_id,order_id,pay_type,order_payment,order_seller_price,seller_discount,freight_price,invoice_info,order_remark," +
                "order_start_time,consignee_info,item_info_list");

        OrderSearchResponse response = config.getClient().execute(request);
        int total = 0;  //系统返回的所有满足条件的订单数量
        int queryTotal = 0; //以及查询的出的订单的总数量
        if(response.getCode().equals("0")) {      //调用成功
            OrderResult orderResult = response.getOrderInfoResult();
            List<OrderSearchInfo> orderInfoList = orderResult.getOrderInfoList();
            result.addAll(orderInfoList);
            total = orderResult.getOrderTotal();
            log.debug("total size is {}", total);
            queryTotal += orderInfoList.size();
        } else {
            log.error("Error while query jd orders, code : {}, msg : {}", response.getCode(), response.getMsg());
        }

        //如果total > page size , 则循环调用接口，查询出所有满足条件的订单
        int page = 2; //第一页已经查询过
        while(queryTotal < total) {
            request.setPage(String.valueOf(page++));
            response = config.getClient().execute(request);
            if(response.getCode().equals("0")) {      //调用成功
                OrderResult orderResult = response.getOrderInfoResult();
                List<OrderSearchInfo> orderInfoList = orderResult.getOrderInfoList();
                result.addAll(orderInfoList);
                queryTotal += orderInfoList.size();
            } else {
                log.error("Error while query jd orders, code : {}, msg : {}", response.getCode(), response.getMsg());
            }
        }
        log.debug("query total size is {}", queryTotal);
        return result;
    }
}
