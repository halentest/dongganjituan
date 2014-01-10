package cn.halen.service;

import cn.halen.data.mapper.ConfigurationMapper;
import cn.halen.data.pojo.Configuration;
import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.SellerInfo;
import cn.halen.service.top.TopConfig;
import cn.halen.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Date;

/**
 * User: hzhang
 * Date: 12/19/13
 * Time: 7:36 PM
 */
@Service
public class RequestXmlBuilder {

    @Autowired
    private TopConfig topConfig;

    @Autowired
    private ConfigurationMapper configurationMapper;

    public DocumentBuilder getBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder;
    }

    /**
     * 构建下单请求xml字符串
     * @param trade
     * @return
     * @throws Exception
     */
    public String orderRequest(MyTrade trade, SellerInfo sellerInfo) throws Exception {
        DocumentBuilder docBuilder = getBuilder();

        String cargo = configurationMapper.selectByKey1("default", "cargo", "鞋子").getValue();
        String checkWord = configurationMapper.selectByKey1("default", "checkword", "lPW+DZilSUJ9Vfgs").getValue();
        String customId = configurationMapper.selectByKey1("default", "custom_id", "5953106803").getValue();
        String yuejie = configurationMapper.selectByKey1("default", "yuejie", customId).getValue();
        int insureValue = Integer.parseInt(configurationMapper.selectByKey1("default", "insure_value", "20000").getValue());
        int isInsure = Integer.parseInt(configurationMapper.selectByKey1("default", "is_insure", "0").getValue());
        String expressType = configurationMapper.selectByKey1("default", "express_type", "3").getValue();

        //root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Request");
        doc.appendChild(rootElement);

        // set attribute
        rootElement.setAttribute("service", "OrderService");
        rootElement.setAttribute("lang", "zh-CN");

        Element head = doc.createElement("Head");
        head.appendChild(doc.createTextNode(customId + "," + checkWord));
        rootElement.appendChild(head);

        Element body = doc.createElement("Body");
        rootElement.appendChild(body);

        Element order = doc.createElement("Order");
        String postfix = FastDateFormat.getInstance("mmss").format(new Date());
        order.setAttribute("orderid", "XJFS" + trade.getId().substring(14) + postfix);
        order.setAttribute("express_type", expressType);
        order.setAttribute("j_province", sellerInfo.getFrom_state());
        order.setAttribute("j_city", sellerInfo.getFrom_city());
        order.setAttribute("j_company", sellerInfo.getFrom_company());
        order.setAttribute("j_contact", sellerInfo.getSender());
        order.setAttribute("j_tel", sellerInfo.getMobile());
        order.setAttribute("j_address", sellerInfo.getFrom_address());
        order.setAttribute("d_province", trade.getState());
        order.setAttribute("d_city", trade.getCity());
        order.setAttribute("d_company", trade.getBuyer_nick());
        order.setAttribute("d_contact", trade.getName());
        order.setAttribute("d_tel", trade.getMobile());
        order.setAttribute("d_address", trade.getState() + trade.getCity() + trade.getDistrict() + trade.getAddress());
        order.setAttribute("parcel_quantity", String.valueOf(trade.getParcel_quantity()));
        order.setAttribute("pay_method", String.valueOf(trade.getPay_method()));
        body.appendChild(order);

        Element orderOption = doc.createElement("OrderOption");
        orderOption.setAttribute("custid", customId);
        orderOption.setAttribute("cargo", StringUtils.isBlank(trade.getCargo())?cargo : trade.getCargo());
//        orderOption.setAttribute("cargo_count", "1");
//        orderOption.setAttribute("cargo_unit", "双");
//        orderOption.setAttribute("cargo_weight", "1.5");
//        orderOption.setAttribute("cargo_amount", "1500");
//        orderOption.setAttribute("cargo_total_weight", "55.56");
//        orderOption.setAttribute("insurance_amount", "52.15");
//        orderOption.setAttribute("sendstarttime", "2013-11-11 10:24:44");
//        orderOption.setAttribute("remark", "备注");
        order.appendChild(orderOption);

        boolean cod = trade.getPay_type() == Constants.PAY_TYPE_AFTER_RECEIVE;
        if(cod) {
            int payment = (trade.getPayment() + trade.getDelivery_money())/100;  //代收货款等于快递费加上货物费用
            Element added1 = doc.createElement("AddedService");
            added1.setAttribute("name", "COD");
            added1.setAttribute("value", String.valueOf(payment));
            added1.setAttribute("value1", yuejie);
//            added1.setAttribute("value2", "");
//            added1.setAttribute("value3", "");
            orderOption.appendChild(added1);

        }

        boolean bIsInsure = trade.getIs_insure()==-1?(isInsure==1?true:false) : (trade.getIs_insure()==1?true:false);
        if(bIsInsure) {
            int value = trade.getInsure_value()==-1?insureValue/100 : trade.getInsure_value()/100;
            Element added2 = doc.createElement("AddedService");
            added2.setAttribute("name", "INSURE");
            added2.setAttribute("value", String.valueOf(value));
//            added2.setAttribute("value1", "");
//            added2.setAttribute("value2", "");
//            added2.setAttribute("value3", "");
            orderOption.appendChild(added2);
        }

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(source, result);
        return sw.toString();
    }

}
