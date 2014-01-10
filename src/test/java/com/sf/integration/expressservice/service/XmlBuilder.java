package com.sf.integration.expressservice.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * User: hzhang
 * Date: 12/17/13
 * Time: 5:10 PM
 */
public class XmlBuilder {

    public static void main(String[] args) throws Exception {
        System.out.println(get());
    }

    public static String get() throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        //root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Request");
        doc.appendChild(rootElement);

        // set attribute
        rootElement.setAttribute("service", "OrderService");
        rootElement.setAttribute("lang", "zh-CN");

        Element head = doc.createElement("Head");
        head.appendChild(doc.createTextNode("5953106803,lPW+DZilSUJ9Vfgs"));
        rootElement.appendChild(head);

        Element body = doc.createElement("Body");
        rootElement.appendChild(body);

        Element order = doc.createElement("Order");
        order.setAttribute("orderid", "XJFS1306170001112");
        order.setAttribute("express_type", "3");
        order.setAttribute("j_province", "北京");
        order.setAttribute("j_city", "北京市");
        order.setAttribute("j_company", "xx公司");
        order.setAttribute("j_contact", "客服");
        order.setAttribute("j_tel", "025-10106699");
        order.setAttribute("j_address", "北京市海淀区科学园科健路328号");
        order.setAttribute("d_province", "广东省");
        order.setAttribute("d_city", "深圳市");
        order.setAttribute("d_company", "顺丰速运");
        order.setAttribute("d_contact", "小顺");
        order.setAttribute("d_tel", "0755-33992159");
        order.setAttribute("d_address", "广东省深圳市福田区新洲十一街万基商务大厦");
        order.setAttribute("parcel_quantity", "1");
        order.setAttribute("pay_method", "1");
        body.appendChild(order);

        Element orderOption = doc.createElement("OrderOption");
        orderOption.setAttribute("custid", "5953106803");
        orderOption.setAttribute("cargo", "服装");
        orderOption.setAttribute("cargo_count", "1");
        orderOption.setAttribute("cargo_unit", "个");
        orderOption.setAttribute("cargo_weight", "1.5");
        orderOption.setAttribute("cargo_amount", "1500");
        orderOption.setAttribute("cargo_total_weight", "55.56");
        orderOption.setAttribute("insurance_amount", "52.15");
        orderOption.setAttribute("sendstarttime", "2013-11-11 10:24:44");
        orderOption.setAttribute("remark", "备注");
        //order.appendChild(orderOption);

        Element added1 = doc.createElement("AddedService");
        added1.setAttribute("name", "COD");
        added1.setAttribute("value", "2000");
        added1.setAttribute("value1", "5953106803");
        added1.setAttribute("value2", "");
        added1.setAttribute("value3", "");
        orderOption.appendChild(added1);

        Element added2 = doc.createElement("AddedService");
        added2.setAttribute("name", "INSURE");
        added2.setAttribute("value", "2000");
//        added2.setAttribute("value1", "");
//        added2.setAttribute("value2", "");
//        added2.setAttribute("value3", "");
        orderOption.appendChild(added2);

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

    public static void main2(String[] args) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        //root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Request");
        doc.appendChild(rootElement);

        // set attribute
        rootElement.setAttribute("service", "OrderService");
        rootElement.setAttribute("lang", "zh-CN");

        Element head = doc.createElement("Head");
        head.appendChild(doc.createTextNode("5953106803,lPW+DZilSUJ9Vfgs"));
        rootElement.appendChild(head);

        Element body = doc.createElement("Body");
        rootElement.appendChild(body);

        Element order = doc.createElement("Order");
        order.setAttribute("orderid", "XJFS1306170001");
        order.setAttribute("express_type", "3");
        order.setAttribute("j_province", "北京");
        order.setAttribute("j_city", "北京市");
        order.setAttribute("j_company", "xx公司");
        order.setAttribute("j_contact", "客服");
        order.setAttribute("j_tel", "025-10106699");
        order.setAttribute("j_address", "北京市海淀区科学园科健路328号");
        order.setAttribute("d_province", "广东省");
        order.setAttribute("d_city", "深圳市");
        order.setAttribute("d_company", "顺丰速运");
        order.setAttribute("d_contact", "小顺");
        order.setAttribute("d_tel", "0755-33992159");
        order.setAttribute("d_address", "广东省深圳市福田区新洲十一街万基商务大厦");
        order.setAttribute("parcel_quantity", "1");
        order.setAttribute("pay_method", "1");
        body.appendChild(order);

        Element orderOption = doc.createElement("OrderOption");
        orderOption.setAttribute("custid", "5953106803");
        orderOption.setAttribute("cargo", "服装");
        orderOption.setAttribute("cargo_count", "1");
        orderOption.setAttribute("cargo_unit", "个");
        orderOption.setAttribute("cargo_weight", "1.5");
        orderOption.setAttribute("cargo_amount", "1500");
        orderOption.setAttribute("cargo_total_weight", "55.56");
        orderOption.setAttribute("insurance_amount", "52.15");
        orderOption.setAttribute("sendstarttime", "2013-11-11 10:24:44");
        orderOption.setAttribute("remark", "备注");
        order.appendChild(orderOption);

        Element added1 = doc.createElement("AddedService");
        added1.setAttribute("name", "COD");
        added1.setAttribute("value", "2000");
        added1.setAttribute("value1", "5953106803");
        added1.setAttribute("value2", "");
        added1.setAttribute("value3", "");
        orderOption.appendChild(added1);

        Element added2 = doc.createElement("AddedService");
        added2.setAttribute("name", "INSURE");
        added2.setAttribute("value", "2000");
        added2.setAttribute("value1", "");
        added2.setAttribute("value2", "");
        added2.setAttribute("value3", "");
        orderOption.appendChild(added2);

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(System.out);

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(source, result);
    }
}
