package cn.halen.controller;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.MigrationMapper;
import cn.halen.data.mapper.MyTradeMapper;
import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.TradeStatus;
import cn.halen.data.pojo.migration.Order1;
import cn.halen.data.pojo.migration.Order2;
import cn.halen.data.pojo.migration.Trade1;
import cn.halen.data.pojo.migration.Trade2;
import cn.halen.exception.InsufficientBalanceException;
import cn.halen.exception.InsufficientStockException;
import cn.halen.service.ResultInfo;
import cn.halen.service.TradeService;
import cn.halen.service.top.*;
import cn.halen.util.Constants;
import com.sf.integration.expressservice.service.CommonServiceService;
import com.sf.integration.expressservice.service.RequestXmlBuilder;
import com.taobao.api.ApiException;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 对接顺丰快递
 */
@Controller
public class SFController {

    @Autowired
    private MyTradeMapper tradeMapper;

    @Autowired
    private AdminMapper adminMapper;

	private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value="/trade/sf/order")
    public @ResponseBody ResultInfo order(@RequestParam String id) {
        ResultInfo ri = new ResultInfo();
        MyTrade trade = tradeMapper.selectTradeMap(id);

        if(!trade.getStatus().equals(TradeStatus.WaitFind.getStatus())) {
            ri.setSuccess(false);
            ri.setErrorInfo("当前状态不是待拣货, 不能下单!");
            return ri;
        }

        if(!"顺丰速运".equals(trade.getDelivery())) {
            ri.setSuccess(false);
            ri.setErrorInfo("非顺丰快递订单, 不能下单!");
            return ri;
        }

        if(StringUtils.isNotBlank(trade.getDelivery_number())) {
            ri.setSuccess(false);
            ri.setErrorInfo("不能重复下单!");
            return ri;
        }

        CommonServiceService service = new CommonServiceService();

        String resp = null;
        try {
            resp = service.getCommonServicePort().sfexpressService(RequestXmlBuilder.orderRequest(trade, adminMapper.selectSellerInfo()));
        } catch(Exception e) {
            log.error("error while make sf order, trade id is {}, {}", id, e);
            ri.setSuccess(false);
            ri.setErrorInfo("下单失败!");
            return ri;
        }
        log.debug("resp for id {} is {}", id, resp);
        try {
            DocumentBuilder builder = RequestXmlBuilder.getBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(resp.getBytes()));
            Node head = doc.getElementsByTagName("Head").item(0);
            String result = head.getTextContent();
            if("OK".equals(result)) {
                Node orderResp = doc.getElementsByTagName("OrderResponse").item(0);
                NamedNodeMap map = orderResp.getAttributes();
                String filterResult = map.getNamedItem("filter_result").getTextContent();
                if(!"2".equals(filterResult)) {
                    ri.setSuccess(false);
                    ri.setErrorInfo("参数有误!");
                    return ri;
                }
                String mailno = map.getNamedItem("mailno").getTextContent();
                String destcode = map.getNamedItem("destcode").getTextContent();
                String origincode = map.getNamedItem("origincode").getTextContent();
                trade.setDelivery_number(mailno);
                trade.setDestcode(destcode);
                trade.setOrigincode(origincode);
                tradeMapper.updateMyTrade(trade);
            } else {
                Node errorNode = doc.getElementsByTagName("ERROR").item(0);
                String errorCode = errorNode.getAttributes().getNamedItem("code").getTextContent();
                String errorMsg = errorNode.getTextContent();
                log.error("error while order sf for id {}, error code is {}, error msg is {}", id, errorCode, errorMsg);
                ri.setSuccess(false);
                ri.setErrorInfo("下单失败, " + errorMsg);
            }
        } catch (Exception e) {
            log.error("parse error,", e);
            ri.setSuccess(false);
            ri.setErrorInfo("下单失败!");
        }
        return ri;
    }
}