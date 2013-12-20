package com.sf.integration.expressservice.service;

import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.SellerInfo;
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

/**
 * User: hzhang
 * Date: 12/19/13
 * Time: 7:36 PM
 */
public class RequestXmlBuilder {

    private static final String CUSTOMER_ID = "5953106803";

    private static final String CHECKWORD = "lPW+DZilSUJ9Vfgs";


    public static DocumentBuilder getBuilder() throws ParserConfigurationException {
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
    public static String orderRequest(MyTrade trade, SellerInfo sellerInfo) throws Exception {
        DocumentBuilder docBuilder = getBuilder();

        //root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Request");
        doc.appendChild(rootElement);

        // set attribute
        rootElement.setAttribute("service", "OrderService");
        rootElement.setAttribute("lang", "zh-CN");

        Element head = doc.createElement("Head");
        head.appendChild(doc.createTextNode(CUSTOMER_ID + "," + CHECKWORD));
        rootElement.appendChild(head);

        Element body = doc.createElement("Body");
        rootElement.appendChild(body);

        Element order = doc.createElement("Order");
        order.setAttribute("orderid", "XJFS" + trade.getId());
        order.setAttribute("express_type", "3");
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
        order.setAttribute("d_address", trade.getAddress());
        order.setAttribute("parcel_quantity", "1");
        order.setAttribute("pay_method", "1");
        body.appendChild(order);

//        Element orderOption = doc.createElement("OrderOption");
//        orderOption.setAttribute("custid", CUSTOMER_ID);
//        orderOption.setAttribute("cargo", "鞋子");
//        orderOption.setAttribute("cargo_count", "1");
//        orderOption.setAttribute("cargo_unit", "双");
//        orderOption.setAttribute("cargo_weight", "1.5");
//        orderOption.setAttribute("cargo_amount", "1500");
//        orderOption.setAttribute("cargo_total_weight", "55.56");
//        orderOption.setAttribute("insurance_amount", "52.15");
//        orderOption.setAttribute("sendstarttime", "2013-11-11 10:24:44");
//        orderOption.setAttribute("remark", "备注");
//        order.appendChild(orderOption);
//
//        Element added1 = doc.createElement("AddedService");
//        added1.setAttribute("name", "COD");
//        added1.setAttribute("value", "2000");
//        added1.setAttribute("value1", "5953106803");
//        added1.setAttribute("value2", "");
//        added1.setAttribute("value3", "");
//        orderOption.appendChild(added1);
//
//        Element added2 = doc.createElement("AddedService");
//        added2.setAttribute("name", "INSURE");
//        added2.setAttribute("value", "2000");
//        added2.setAttribute("value1", "");
//        added2.setAttribute("value2", "");
//        added2.setAttribute("value3", "");
//        orderOption.appendChild(added2);

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
