package cn.halen.service.dangdang;

import cn.halen.data.mapper.*;
import cn.halen.data.pojo.*;
import cn.halen.service.TradeService;
import cn.halen.service.top.TopConfig;
import cn.halen.util.Constants;
import com.businessrefinery.barcode.Barcode;
import com.taobao.api.ApiException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * User: hzhang
 * Date: 12/28/13
 * Time: 10:51 AM
 */
@Service
public class DangdangService {
    private static final String url = "http://api.open.dangdang.com/openapi/rest?v=1.0";
    private static final String appKey = "2100001705";
    private static final String appSecret = "F97DCB9E1E1B4BEFD640653FFE2CE4B1";

    private static final String ACCESS_TOKEN = "11AAB4792A76EBCC51FFFC6FDAC939125D1F7CB2E029933E1C8F2E274A2D9675";
    public static final String method = "dangdang.items.list.get";
    public static final String method_order_list_get = "dangdang.orders.list.get";
    public static final String method_order_detail_get = "dangdang.order.details.get";
    public static final String method_stock_update = "dangdang.items.stock.update";
    public static final String method_print = "dangdang.order.receipt.details.list";
    public static final String SING_METHOD = "md5";
    public static final String FORMAT = "xml";
    public static final String VERSION = "1.0";

    private FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private FastDateFormat dateFormat2 = FastDateFormat.getInstance("yyyyMMddHHmmss");

    @Autowired
    private TopConfig topConfig;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private MyTradeMapper tradeMapper;

    @Autowired
    private MySkuMapper mySkuMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private MyLogisticsCompanyMapper logisticsMapper;

    private Logger log = LoggerFactory.getLogger(DangdangService.class);

    public File createRequestXml(List<MySku> skuList, Shop shop) {
        DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.error("", e);
        }
        Document document = builder.newDocument();
        Element request = document.createElement("request");
        document.appendChild(request);

        Element functionId = document.createElement("functionId");
        functionId.appendChild(document.createTextNode("dangdang.items.stock.update"));
        request.appendChild(functionId);

        Date date = new Date();
        String time = dateFormat.format(date);
        String time2 = dateFormat2.format(date);
        Element timeEle = document.createElement("time");
        timeEle.appendChild(document.createTextNode(time));
        request.appendChild(timeEle);

        Element itemsList = document.createElement("ItemsList");
        request.appendChild(itemsList);

        for(MySku sku : skuList) {
            Element itemUpadteInfo = document.createElement("ItemUpadteInfo");

            Element outerItemID = document.createElement("outerItemID");
            String outId = sku.getGoods_id() + sku.getColor_id() + sku.getSize();
            outerItemID.appendChild(document.createTextNode(outId));
            itemUpadteInfo.appendChild(outerItemID);

            Element stockCount = document.createElement("stockCount");
            long avaiQuantity = sku.getQuantity() - sku.getManaual_lock_quantity() - sku.getLock_quantity();
            if(avaiQuantity < 0) {
                avaiQuantity = 0;
            }
            stockCount.appendChild(document.createTextNode(String.valueOf(avaiQuantity)));
            itemUpadteInfo.appendChild(stockCount);

            itemsList.appendChild(itemUpadteInfo);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        String fileName = shop.getSeller_nick() + time2 + ".xml";
        try {
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(document);
            transformer.setOutputProperty(OutputKeys.ENCODING, "gb2312");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
            StreamResult result = new StreamResult(pw);
            transformer.transform(source, result);
        } catch (Exception e) {
            log.error("", e);
        }

        return new File(fileName);
    }

    public StringBuilder updateInventory(List<Goods> goodsList, Shop shop) {
        int totalSuccess = 0;
        StringBuilder builder = new StringBuilder();
        List<MySku> skuList = new ArrayList<MySku>();
        for(Goods goods : goodsList) {
            skuList.addAll(goods.getSkuList());
        }
        int size = skuList.size();
        int batchSize = 40;
        int time = size%batchSize==0?size/batchSize : size/batchSize + 1;
        for(int i=1; i<=time; i++) {
            List<MySku> subList = null;
            if(i==time) {
                subList = skuList.subList((i-1)*batchSize, skuList.size());
            } else {
                subList = skuList.subList((i-1)*batchSize, i*batchSize);
            }
            File file = this.createRequestXml(subList, shop);
            try {
                String resp = this.updateInventory(file, shop);
                //解析结果
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = null;
                try {
                    docBuilder = docFactory.newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                    log.error("", e);
                }
                Document doc = null;
                try {
                    doc = docBuilder.parse(new ByteArrayInputStream(resp.getBytes("gbk")));
                } catch (SAXException e) {
                    log.error("", e);
                } catch (IOException e) {
                    log.error("", e);
                }
                NodeList errorNode = doc.getElementsByTagName("Error");
                if(null != errorNode && errorNode.getLength() > 0) {
                    String operCode = doc.getElementsByTagName("operCode").item(0).getTextContent();
                    String operation = doc.getElementsByTagName("operation").item(0).getTextContent();
                    builder.append("operCode: ").append(operCode).append(", operation: ").append(operation);
                } else {
                    Node itemsIDList = doc.getElementsByTagName("ItemsIDList").item(0);
                    NodeList children = itemsIDList.getChildNodes();
                    for(int j=0; j<children.getLength(); j++) {
                        Node n = children.item(j);
                        if(!(n instanceof Element)) {
                            continue;
                        }
                        String operCode = ((Element) n).getElementsByTagName("operCode").item(0).getTextContent();
                        if("0".equals(operCode)) {
                            totalSuccess++;
                            continue;
                        }
                        String operation = ((Element) n).getElementsByTagName("operation").item(0).getTextContent();
                        String outerItemID = ((Element) n).getElementsByTagName("outerItemID").item(0).getTextContent();
                        builder.append(outerItemID).append("更新库存失败，原因：").append(operation).append("<br>");
                    }
                }
            } catch (UnsupportedEncodingException e) {
                log.error("", e);
            }
        }
        StringBuilder newBuilder = new StringBuilder();
        newBuilder.append("<font color='red'>更新成功").append(totalSuccess).append("个sku</font><br>");
        newBuilder.append(builder.toString());
        return newBuilder;
    }

    protected String updateInventory(File file, Shop shop) throws UnsupportedEncodingException {

        StringBuilder urlBuilder = baseUrl(method_stock_update, shop);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        FileBody fileBody = new FileBody(file);

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        //解析参数
        String url = urlBuilder.toString();
        String[] params = url.split("\\?")[1].split("&");
        for(String param : params) {
            String[] kv = param.split("=");
            if(!"timestamp".equals(kv[0])) {
                multipartEntityBuilder.addTextBody(kv[0], kv[1]);
            }
        }
        multipartEntityBuilder.addTextBody("timestamp", dateFormat.format(new Date()));

        HttpEntity reqEntity = multipartEntityBuilder.addPart("multiItemsStock", fileBody).build();
        post.setEntity(reqEntity);
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            CloseableHttpResponse resp = httpClient.execute(post);

            HttpEntity entity = resp.getEntity();
            if(null != entity) {
                reader = new BufferedReader(new InputStreamReader(entity.getContent(), "gbk"));
                try {
                    for(String s=reader.readLine(); s!=null; s=reader.readLine()) {
                        builder.append(s);
                    }
                } finally {
                    reader.close();
                }
            }
        } catch (IOException e) {
            log.error("", e);
        } finally {
            if(null != reader) {
                IOUtils.closeQuietly(reader);
            }
            HttpClientUtils.closeQuietly(httpClient);
        }
        String resp = builder.toString(); //服务器返回的结果字符串
        return resp;
    }

    public static void main(String[] args) throws MalformedURLException, UnsupportedEncodingException {

        Shop shop = new Shop();
        shop.setSeller_nick("kekeshop");
        shop.setToken("11AAB4792A76EBCC51FFFC6FDAC939125D1F7CB2E029933E1C8F2E274A2D9675");
//        System.out.println(new DangdangService().getPrintXml(Arrays.asList("7527633587", "752759960", "7523179365"), shop));
//        get();
        //post();
    }

    //查询待发货的订单列表
    private List<OrderInfo> queryList(Shop shop, Date startDate, Date endDate) {
        List<OrderInfo> result = new ArrayList<OrderInfo>();
        StringBuilder urlBuilder = baseUrl(method_order_list_get, shop);

        try {
            urlBuilder.append("&osd=").append(URLEncoder.encode(dateFormat.format(startDate), "gbk"));
            urlBuilder.append("&oed=").append(URLEncoder.encode(dateFormat.format(endDate), "gbk"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String resp = urlRequest(urlBuilder.toString());
        //解析结果
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.error("", e);
        }
        Document doc = null;
        try {
            doc = docBuilder.parse(new ByteArrayInputStream(resp.getBytes("gbk")));
        } catch (SAXException e) {
            log.error("", e);
        } catch (IOException e) {
            log.error("", e);
        }
        List<OrderInfo> orderInfos = parseOrderInfo(doc);
        result.addAll(orderInfos);
        TotalInfo totalInfo = parseTotalInfo(doc);
        while(totalInfo.getCurrentPage() < totalInfo.getPageTotal()) {
            String url = urlBuilder.toString() + "&p=" + (totalInfo.getCurrentPage() + 1);
            resp = urlRequest(url);
            try {
                doc = docBuilder.parse(new ByteArrayInputStream(resp.getBytes("gbk")));
            } catch (SAXException e) {
                log.error("", e);
            } catch (IOException e) {
                log.error("", e);
            }
            orderInfos = parseOrderInfo(doc);
            result.addAll(orderInfos);
            totalInfo = parseTotalInfo(doc);
        }

        return result;
    }

    private Document getDoc(String resp) {
        //解析结果
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.error("", e);
        }
        Document doc = null;
        try {
            doc = docBuilder.parse(new ByteArrayInputStream(resp.getBytes("gbk")));
        } catch (SAXException e) {
            log.error("", e);
        } catch (IOException e) {
            log.error("", e);
        }
        return doc;
    }

    //查询订单详情
    private OrderInfo queryDetail(OrderInfo orderInfo, Shop shop) {
        StringBuilder urlBuilder = baseUrl(method_order_detail_get, shop);
        urlBuilder.append("&o=").append(orderInfo.getOrderID());

        String resp = urlRequest(urlBuilder.toString());

        Document doc = getDoc(resp);
        Node orderStateNode = doc.getElementsByTagName("orderState").item(0);
        orderInfo.setOrderState(Integer.parseInt(orderStateNode.getTextContent()));
        Node messageNode = doc.getElementsByTagName("message").item(0);
        orderInfo.setMessage(messageNode.getTextContent());
        Node remarkNode = doc.getElementsByTagName("remark").item(0);
        orderInfo.setRemark(remarkNode.getTextContent());
        Node paymentDateNode = doc.getElementsByTagName("paymentDate").item(0);
        orderInfo.setPaymentDate(paymentDateNode.getTextContent());
        Node orderModeNode = doc.getElementsByTagName("orderMode").item(0);
        orderInfo.setOrderMode(Integer.parseInt(orderModeNode.getTextContent()));
        Node buyerPayModeNode = doc.getElementsByTagName("buyerPayMode").item(0);
        orderInfo.setBuyerPayMode(buyerPayModeNode.getTextContent());
        Node goodsMoneyNode = doc.getElementsByTagName("goodsMoney").item(0);
        orderInfo.setGoodsMoney(Float.parseFloat(goodsMoneyNode.getTextContent()));
        Node realPaidAmountNode = doc.getElementsByTagName("realPaidAmount").item(0);
        orderInfo.setRealPaidAmount(Float.parseFloat(realPaidAmountNode.getTextContent()));
        Node postageNode = doc.getElementsByTagName("postage").item(0);
        orderInfo.setPostage(Float.parseFloat(postageNode.getTextContent()));

        Node sendGoodsInfoNode = doc.getElementsByTagName("sendGoodsInfo").item(0);
        for(int i=0; i<sendGoodsInfoNode.getChildNodes().getLength(); i++) {
            String nodeName = sendGoodsInfoNode.getChildNodes().item(i).getNodeName();
            String nodeValue = sendGoodsInfoNode.getChildNodes().item(i).getTextContent();
            if("dangdangAccountID".equals(nodeName)) {
                orderInfo.setDangdangAccountID(nodeValue);
            } else if("consigneeName".equals(nodeName)) {
                orderInfo.setConsigneeName(nodeValue);
            } else if("consigneeAddr".equals(nodeName)) {
                orderInfo.setConsigneeAddr(nodeValue);
            } else if("consigneeAddr_State".equals(nodeName)) {
                orderInfo.setConsigneeAddr_State(nodeValue);
            } else if("consigneeAddr_Province".equals(nodeName)) {
                orderInfo.setConsigneeAddr_Province(nodeValue);
            } else if("consigneeAddr_City".equals(nodeName)) {
                orderInfo.setConsigneeAddr_City(nodeValue);
            } else if("consigneeAddr_Area".equals(nodeName)) {
                orderInfo.setConsigneeAddr_Area(nodeValue);
            } else if("consigneePostcode".equals(nodeName)) {
                orderInfo.setConsigneePostcode(nodeValue);
            } else if("consigneeTel".equals(nodeName)) {
                orderInfo.setConsigneeTel(nodeValue);
            } else if("consigneeMobileTel".equals(nodeName)) {
                orderInfo.setConsigneeMobileTel(nodeValue);
            } else if("sendGoodsMode".equals(nodeName)) {
                orderInfo.setSendGoodsMode(nodeValue);
            } else if("sendCompany".equals(nodeName)) {
                orderInfo.setSendCompany(nodeValue);
            }
        }

        Node itemListNode = doc.getElementsByTagName("ItemsList").item(0);
        NodeList itemList = itemListNode.getChildNodes();
        for(int i=0; i<itemList.getLength(); i++) {
            Node itemInfoNode = itemList.item(i);
            if(!(itemInfoNode instanceof Element)) {
                continue;
            }

            ItemInfo itemInfo = new ItemInfo();
            NodeList paramList = itemInfoNode.getChildNodes();
            for(int j=0; j<paramList.getLength(); j++) {
                Node c = paramList.item(j);
                if(!(c instanceof Element)) {
                    continue;
                }
                String nodeName = c.getNodeName();
                String nodeValue = c.getTextContent();
                if("itemID".equals(nodeName)) {
                    itemInfo.setItemID(nodeValue);
                } else if("outerItemID".equals(nodeName)) {
                    itemInfo.setOuterItemID(nodeValue);
                } else if("itemName".equals(nodeName)) {
                    itemInfo.setItemName(nodeValue);
                } else if("itemType".equals(nodeName)) {
                    itemInfo.setItemType(Integer.parseInt(nodeValue));
                } else if("specialAttribute".equals(nodeName)) {
                    itemInfo.setSpecialAttribute(nodeValue);
                } else if("marketPrice".equals(nodeName)) {
                    itemInfo.setMarketPrice(Float.parseFloat(nodeValue));
                } else if("unitPrice".equals(nodeName)) {
                    itemInfo.setUnitPrice(Float.parseFloat(nodeValue));
                } else if("orderCount".equals(nodeName)) {
                    itemInfo.setOrderCount(Integer.parseInt(nodeValue));
                }
            }
            orderInfo.getItems().add(itemInfo);
        }

        return orderInfo;
    }

    private String urlRequest(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            CloseableHttpResponse resp = httpClient.execute(get);

            HttpEntity entity = resp.getEntity();
            if(null != entity) {
                reader = new BufferedReader(new InputStreamReader(entity.getContent(), "gbk"));
                try {
                    for(String s=reader.readLine(); s!=null; s=reader.readLine()) {
                        builder.append(s);
                    }
                } finally {
                    reader.close();
                }
            }
        } catch (IOException e) {
            log.error("", e);
        } finally {
            if(null != reader) {
                IOUtils.closeQuietly(reader);
            }
            HttpClientUtils.closeQuietly(httpClient);
        }
        String resp = builder.toString(); //服务器返回的结果字符串
        return resp;
    }

    private List<OrderInfo> parseOrderInfo(Document doc) {
        List<OrderInfo> list = new ArrayList<OrderInfo>();
        Node ordersListNode = doc.getElementsByTagName("OrdersList").item(0);
        NodeList nodeList = ordersListNode.getChildNodes();
        for(int i=0; i<nodeList.getLength(); i++) {
            Node orderInfoNode = nodeList.item(i);
            if(!(orderInfoNode instanceof Element)) {
                continue;
            }
            OrderInfo orderInfo = new OrderInfo();
            NodeList itemNodeList = orderInfoNode.getChildNodes();
            for(int j=0; j<itemNodeList.getLength(); j++) {
                Node c = itemNodeList.item(j);
                if(!(c instanceof Element)) {
                    continue;
                }
                String nodeName = c.getNodeName();
                String nodeValue = c.getTextContent();
                if("orderID".equals(nodeName)) {
                    orderInfo.setOrderID(nodeValue);
                }
            }
            list.add(orderInfo);
        }
        return list;
    }

    private TotalInfo parseTotalInfo(Document doc) {
        //解析totalInfo
        TotalInfo totalInfo = new TotalInfo();
        Node totalInfoNode = doc.getElementsByTagName("totalInfo").item(0);
        NodeList nodeList = totalInfoNode.getChildNodes();
        for(int i=0; i<nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if(!(n instanceof Element)) {
                continue;
            }
            if(n.getNodeName().equals("orderCount")) {
                totalInfo.setOrderCount(Integer.parseInt(n.getTextContent()));
            } else if(n.getNodeName().equals("pageSize")) {
                totalInfo.setPageSize(Integer.parseInt(n.getTextContent()));
            } else if(n.getNodeName().equals("pageTotal")) {
                totalInfo.setPageTotal(Integer.parseInt(n.getTextContent()));
            } else if(n.getNodeName().equals("currentPage")) {
                totalInfo.setCurrentPage(Integer.parseInt(n.getTextContent()));
            }
        }
        return totalInfo;
    }

    //组装系统参数
    public StringBuilder baseUrl(String method, Shop shop) {
        String timestamp = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date());
        StringBuilder urlBuilder = new StringBuilder();
        try {
            urlBuilder.append(url)
                    .append("&method=").append(method)
                    .append("&timestamp=").append(URLEncoder.encode(timestamp, "gbk"))
                    .append("&format=").append(FORMAT)
                    .append("&app_key=").append(appKey)
                    .append("&sign_method=").append(SING_METHOD)
                    .append("&session=").append(shop.getToken());
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }

        //添加签名的参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("method", method));
        list.add(new BasicNameValuePair("timestamp", timestamp));
        list.add(new BasicNameValuePair("format", FORMAT));
        list.add(new BasicNameValuePair("app_key", appKey));
        list.add(new BasicNameValuePair("v", VERSION));
        list.add(new BasicNameValuePair("sign_method", SING_METHOD));
        list.add(new BasicNameValuePair("session", shop.getToken()));
        Collections.sort(list, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair o1, NameValuePair o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        //secret需要参加签名
        StringBuilder sourceBuilder = new StringBuilder(appSecret);
        for(NameValuePair p : list) {
            sourceBuilder.append(p.getName()).append(p.getValue());
        }
        sourceBuilder.append(appSecret);
        //其他业务参数不需要参加签名
        String md5 = DigestUtils.md5DigestAsHex(sourceBuilder.toString().getBytes());
        String sign = StringUtils.upperCase(md5);

        urlBuilder.append("&sign=").append(sign);
        return urlBuilder;
    }

    public static void get() throws UnsupportedEncodingException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String timestamp = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(timestamp);

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(url)
                .append("&method=").append(method_order_detail_get)
                .append("&timestamp=").append(URLEncoder.encode(timestamp, "gbk"))
                .append("&format=").append(FORMAT)
                .append("&app_key=").append(appKey)
                .append("&sign_method=").append(SING_METHOD)
                .append("&session=").append(ACCESS_TOKEN);

        //添加签名的参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("method", method_order_detail_get));
        list.add(new BasicNameValuePair("timestamp", timestamp));
        list.add(new BasicNameValuePair("format", FORMAT));
        list.add(new BasicNameValuePair("app_key", appKey));
        list.add(new BasicNameValuePair("v", VERSION));
        list.add(new BasicNameValuePair("sign_method", SING_METHOD));
        list.add(new BasicNameValuePair("session", ACCESS_TOKEN));
        Collections.sort(list, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair o1, NameValuePair o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        //secret需要参加签名
        StringBuilder sourceBuilder = new StringBuilder(appSecret);
        for(NameValuePair p : list) {
            sourceBuilder.append(p.getName()).append(p.getValue());
        }
        sourceBuilder.append(appSecret);
        //其他业务参数不需要参加签名
        String md5 = DigestUtils.md5DigestAsHex(sourceBuilder.toString().getBytes());
        String sign = StringUtils.upperCase(md5);

        urlBuilder.append("&sign=").append(sign);
        urlBuilder.append("&o=7495517442");
        HttpGet get = new HttpGet(urlBuilder.toString());
        System.out.println(urlBuilder.toString());
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            CloseableHttpResponse resp = httpClient.execute(get);

            HttpEntity entity = resp.getEntity();
            if(null != entity) {
                reader = new BufferedReader(new InputStreamReader(entity.getContent(), "gbk"));
                try {
                    for(String s=reader.readLine(); s!=null; s=reader.readLine()) {
                        builder.append(s);
                    }
                } finally {
                    reader.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != reader) {
                IOUtils.closeQuietly(reader);
            }
            HttpClientUtils.closeQuietly(httpClient);
        }
        String result = builder.toString(); //服务器返回的结果字符串
        //解析结果
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
        }
        Document doc = null;
        try {
            doc = docBuilder.parse(new ByteArrayInputStream(result.getBytes("gbk")));
        } catch (SAXException e) {
        } catch (IOException e) {
        }
        System.out.println(result);
//        TotalInfo totalInfo = new DangdangService().parseTotalInfo(doc);
//        System.out.println(totalInfo.getCurrentPage());
//        List<OrderInfo> orderInfos = new DangdangService().parseOrderInfo(doc);
//        System.out.println(orderInfos.size());
    }

    public Map<String, Object> syncTrade(Shop shop, Date startDate, Date endDate) {
        Map<String, Object> counter = new HashMap<String, Object>();
        List<OrderInfo> orderInfos = queryList(shop, startDate, endDate);
        counter.put("Paid", orderInfos.size()); //已付款订单数量

        int success = 0;
        int existCount = 0;
        List<String> fail = new ArrayList<String>();
        for(OrderInfo orderInfo : orderInfos) {
            //check trade if exists
            boolean exist = tradeMapper.checkTidExist(orderInfo.getOrderID());
            if(exist) {
                existCount++;
                continue;
            }
            orderInfo = queryDetail(orderInfo, shop);

            MyTrade myTrade = toMyTrade(orderInfo, shop);
            if(null == myTrade || !myTrade.isSuccess()) {
                fail.add(orderInfo.getOrderID());
            }
            if(null == myTrade) {
                continue;
            }
            myTrade.setStatus(TradeStatus.UnSubmit.getStatus());
            int count = 0;
            try {
                count = tradeService.insertMyTrade(myTrade, Constants.LOCK_QUANTITY, null);
            } catch (ApiException e) {
                log.error("", e);
            }
            success += count;
        }
        counter.put("Success", success); //导入成功的订单
        counter.put("Exist", existCount);
        counter.put("Fail", fail);
        log.info("fail is {}", fail.toString());

        return counter;
    }

    public MyTrade toMyTrade(OrderInfo orderInfo, Shop shop) {

        MyTrade myTrade = new MyTrade();
        String id = tradeMapper.generateId();
        myTrade.setId(id);
        List<ItemInfo> itemInfos = orderInfo.getItems();
        int goodsCount = 0;
        List<MyOrder> myOrderList = new ArrayList<MyOrder>();
        String sellerNick = shop.getSeller_nick();
        Distributor d = adminMapper.selectShopMapBySellerNick(sellerNick).getD();
        boolean isSelf = d.getSelf() == Constants.DISTRIBUTOR_SELF_YES;
        float discount = d.getDiscount();
        int totalPayment = 0;
        boolean first = true; //the first order
        for(ItemInfo itemInfo : itemInfos) {
            MySku sku = mySkuMapper.selectByHid(itemInfo.getOuterItemID());

            if(null == sku) {  //检查sku是否存在
                log.info("This sku {} not exist!", itemInfo.getOuterItemID());
//                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date now = new Date();
//                String memo = (StringUtils.isEmpty(trade.getSellerMemo())?"":trade.getSellerMemo()) + "同步失败2";
//                tradeClient.updateMemo(trade.getTid(), trade.getSellerNick(), memo);
                myTrade.setSuccess(false);
                continue;
            }

            goodsCount += itemInfo.getOrderCount();
            MyOrder myOrder = new MyOrder();
            myOrder.setTid(id);
            myOrder.setColor(sku.getColor());
            myOrder.setSize(sku.getSize());
            myOrder.setSku_id(sku.getId());

            log.info("sku.id is {}", sku.getId());
            Goods goods = goodsMapper.getByHid(sku.getGoods_id(), true);

            //set order info to trade so as to be able to order by it.
            if(first) {
                myTrade.setSku_id(sku.getId());
                myTrade.setGoods_id(goods.getId());
                first = false;
            }

            myOrder.setGoods_id(goods.getHid());
            myOrder.setTitle(itemInfo.getItemName());
            myOrder.setPic_path(null);
            myOrder.setQuantity(itemInfo.getOrderCount());
            myOrder.setPayment((int) (itemInfo.getUnitPrice() * 100));
            myOrder.setPrice((int) (itemInfo.getMarketPrice() * 100));
            myOrderList.add(myOrder);
        }
        if(myOrderList.size() == 0) {
            return null;
        }

        myTrade.setTid(orderInfo.getOrderID());
        myTrade.setName(orderInfo.getConsigneeName());
        myTrade.setPhone(orderInfo.getConsigneeTel());
        myTrade.setMobile(orderInfo.getConsigneeMobileTel());
        myTrade.setState(orderInfo.getConsigneeAddr_Province());
        myTrade.setCity(orderInfo.getConsigneeAddr_City());
        myTrade.setDistrict(orderInfo.getConsigneeAddr_Area());
        myTrade.setAddress(orderInfo.getConsigneeAddr());
        myTrade.setPostcode(orderInfo.getConsigneePostcode());
        myTrade.setDistributor_id(1);
        myTrade.setSeller_memo(orderInfo.getRemark());
        myTrade.setBuyer_message(orderInfo.getMessage());
        myTrade.setSeller_nick(shop.getSeller_nick());
        myTrade.setDistrib(d.getName());
        myTrade.setBuyer_nick(orderInfo.getDangdangAccountID());
        myTrade.setCome_from(Constants.DANGDANG);
        myTrade.setModified(new Date());
        myTrade.setPayment((int) (orderInfo.getGoodsMoney() * 100));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            myTrade.setCreated(format.parse(orderInfo.getPaymentDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        myTrade.setMyOrderList(myOrderList);
        myTrade.setGoods_count(goodsCount);
        if("货到付款".equals(orderInfo.getBuyerPayMode())) {
            myTrade.setPay_type(Constants.PAY_TYPE_AFTER_RECEIVE);
            myTrade.setDelivery("当当物流");
        } else {
            myTrade.setPay_type(Constants.PAY_TYPE_ONLINE); //目前只支持淘宝的在线支付订单
            MyLogisticsCompany mc = logisticsMapper.select(1);
            myTrade.setDelivery(mc.getName());
        }

        return myTrade;
    }

    public boolean writeDangdang(String path, CourierReceiptDetail detail, MyTrade trade) {
        int width = 1435;
        int height = 1000;
        try {
            File file = new File(path);

            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = (Graphics2D)bi.getGraphics();
            g2.setBackground(Color.WHITE);
            g2.clearRect(0, 0, width, height);
            g2.setPaint(Color.black);
            g2.setStroke(new BasicStroke(3));
            hf(0, 0, g2, detail, trade);
            hf(700, 0, g2, detail, trade);
            barcode(0, 0, g2, detail);
            barcode(700, 0, g2, detail);

            ImageIO.write(bi, "jpg", file);
        } catch (Exception e) {
            log.error("write dangdang error", e);
            return false;
        }
        return true;
    }

    private void hf(int x, int y, Graphics2D g2, CourierReceiptDetail detail, MyTrade trade) throws Exception {
        g2.drawRect(x + 50, y + 50, 600, 850); //外边框
        g2.setFont(new Font("楷体", Font.BOLD, 20));
        String s = detail.getReceiptTitle() + "/派送仓 - " + detail.getSendGoodsWarehouse() + "仓";
        g2.drawString(s, x + 150, y + 80);

        String s2 = "应收款：" + detail.getTotalBarginPrice();
        g2.drawString(s2, x + 400, y + 200);

        g2.drawRect(x + 60, y + 210, 580, 240);

        g2.drawRect(x + 60, y + 210, 110, 40);
        g2.drawString("订 单 号", x + 75, y + 238);
        g2.drawRect(x + 170, y + 210, 160, 40);
        g2.drawString(detail.getOrderID(), x + 185, y + 238);
        g2.drawRect(x + 330, y + 210, 110, 40);
        g2.drawString("订购日期", x + 345, y + 238);
        g2.drawRect(x + 440, y + 210, 200, 40);
        g2.drawString(detail.getOrderCreateTime(), x + 445, y + 238);

        g2.drawRect(x + 60, y + 250, 110, 40);
        g2.drawString("收 货 人", x + 75, y + 278);
        g2.drawRect(x + 170, y + 250, 160, 40);
        g2.drawString(detail.getConsigneeName(), x + 185, y + 278);
        g2.drawRect(x + 330, y + 250, 110, 40);
        g2.drawString("联系电话", x + 345, y + 278);
        g2.drawRect(x + 440, y + 250, 200, 40);
        g2.drawString(detail.getConsigneeMobileTel(), x + 445, y + 278);

        g2.drawRect(x + 60, y + 290, 110, 120);
        g2.drawString("地址", x + 100, y + 355);
        g2.drawRect(x + 170, y + 290, 470, 120);
        String addr = detail.getConsigneeAddr() + "," + detail.getConsigneePostcode();
        String[] lines = getAllLineStr(addr, 470, 120, g2.getFontMetrics());
        int h = 320;
        for(String line : lines) {
            g2.drawString(line, x + 175, y + h);
            h += 20;
        }

        g2.drawRect(x + 60, y + 410, 110, 40);
        g2.drawString("派送要求", x + 75, y + 435);
        g2.drawRect(x + 170, y + 410, 470, 40);
        g2.drawString(detail.getSendGoodsTime(), x + 175, y + 435);

        List<MyOrder> orderList = trade.getMyOrderList();
        h = 475;
        for(MyOrder order : orderList) {
            String goods = order.getGoods_id() + ", " + order.getSku().getColor() + " " + order.getSku().getSize() + " X " + order.getQuantity();
            g2.drawString(goods, x + 70, y + h);
            h += 20;
        }


        g2.drawString("签收", x + 80, y + 720);
        g2.drawLine(x + 120, y + 720, x + 300, y + 720);
        g2.drawString("日期", x + 320, y + 720);
        g2.drawLine(x + 360, y + 720, x + 540, y + 720);

        g2.drawRect(x + 60, y + 730, 580, 160);

        g2.drawRect(x + 60, y + 730, 110, 40);
        g2.drawString("发货商家", x + 70, y + 758);
        g2.drawRect(x + 170, y + 730, 470, 40);
        g2.drawString("当当网", x + 175, y + 758);

        g2.drawRect(x + 60, y + 770, 110, 40);
        g2.drawString("商家编号", x + 70, y + 798);
        g2.drawRect(x + 170, y + 770, 160, 40);
        g2.drawRect(x + 330, y + 770, 110, 40);
        g2.drawString("取货日期", x + 340, y + 798);
        g2.drawRect(x + 440, y + 770, 200, 40);

        g2.drawRect(x + 60, y + 810, 110, 80);
        g2.drawString("发货人", x + 80, y + 855);
        g2.drawRect(x + 170, y + 810, 160, 80);
        String sender = detail.getShopName();
        lines = getAllLineStr(sender, 160, 80, g2.getFontMetrics());
        h = 840;
        for(String line : lines) {
            g2.drawString(line, x + 175, y + h);
            h +=20;
        }
        g2.drawRect(x + 330, y + 810, 110, 80);
        g2.drawString("联系电话", x + 340, y + 855);
        g2.drawRect(x + 440, y + 810, 200, 80);
        g2.drawString("0595-86093983", x + 450, y + 855);
    }

    private void barcode(int x, int y, Graphics2D g2, CourierReceiptDetail detail) throws Exception {
        Barcode barcode = new Barcode();
        barcode.setSymbology(Barcode.CODE128);
        barcode.setCode(detail.getOrderID() + "x"); //barcode会把最后一位丢掉，所以随便加上个值
        Rectangle2D rectangle2D = new Rectangle2D.Double(x + 150, y + 100, 100, 30);
        barcode.drawOnGraphics(g2, rectangle2D);
    }



    /**
     * add by zkl.  2007-07-12.
     * @param data        需要显示的文本内容.
     * @param width       显示区域宽度.
     * @param height      显示区域高度
     * @param metrics     字体的字形对象.
     * @return String[]   返回每行要显示的内容.
     */
    protected static String[] getAllLineStr(String data, int width, int height, FontMetrics metrics)
    {
        int lineH=metrics.getHeight();
        int charH=lineH-metrics.getLeading()-metrics.getDescent();
        String[] dest=new String[1+(int)Math.ceil(height/lineH)];
        char[] chs=data.toCharArray();
        int widthLeft=width;
        int chWidth;
        int lineCount=0;
        int lastIndex=0;
        for(int i=0;i<chs.length;i++)
        {
            chWidth=metrics.charWidth(chs[i]);
            if(chWidth>width||charH>height)
            {
                return new String[0];
            }

   /*
    * 此处提供了对手动换行的支持,再要显示的字符串中加\\n来换行.
    */
            if(chs[i]=='\\'&&chs[i+1]=='n')
            {
                continue;
            }
            if(chs[i]=='n'&&chs[i-1]=='\\')
            {
                dest[lineCount]=new String(chs,lastIndex,i-lastIndex-1);
                if("".equals(dest[lineCount]))
                {
                }else
                {
                    lineCount++;
                }
                widthLeft=width;
                lastIndex=i+1;
            }
   /* 换行处理, the end. */
            else
            {

                widthLeft-=chWidth;
                if(i<chs.length-1&&widthLeft<metrics.charWidth(chs[i+1])){
                    dest[lineCount]=new String(chs,lastIndex,i-lastIndex+1);
                    widthLeft=width;
                    lineCount++;
                    lastIndex=i+1;
                }

                if(height<lineCount*lineH+charH)
                {
                    break;
                }
            }

        }
        if(lastIndex<=chs.length-1&&height>=lineCount*lineH+charH)
        {
            dest[lineCount]=new String(chs,lastIndex,chs.length-lastIndex);
            lineCount++;
        }

        String[] result=new String[lineCount];
        System.arraycopy(dest, 0, result, 0, lineCount);
        return result;
    }

    public List<CourierReceiptDetail> getPrintDetail(List<MyTrade> tradeList, Shop shop) {
        List<CourierReceiptDetail> list = new ArrayList<CourierReceiptDetail>();
        Document doc = getDoc(getPrintXml(tradeList, shop));
        Node p = doc.getElementsByTagName("orderCourierReceiptDetails").item(0);
        NodeList nl = p.getChildNodes();
        for(int i=0; i<nl.getLength(); i++) {
            Node detail = nl.item(i);
            if(!(detail instanceof Element)) {
                continue;
            }
            CourierReceiptDetail d = new CourierReceiptDetail();
            NodeList operCodeNodeList = ((Element) detail).getElementsByTagName("operCode");
            if(null != operCodeNodeList && operCodeNodeList.getLength()>0) {
                d.setSuccess(false);
                d.setOperCode(getTextContent((Element) detail, "operCode"));
                d.setOperation(getTextContent((Element) detail, "operation"));
                d.setOrderID(getTextContent((Element) detail, "orderID"));
            } else {
                d.setReceiptTitle(getTextContent((Element) detail, "receiptTitle"));
                d.setShopWarehouse(getTextContent((Element) detail, "shopWarehouse"));
                d.setSendGoodsWarehouse(getTextContent((Element) detail, "sendGoodsWarehouse"));
                d.setRejectWarehouse(getTextContent((Element) detail, "rejectWarehouse"));
                d.setOrderID(getTextContent((Element) detail, "orderID"));
                d.setOrderCreateTime(getTextContent((Element) detail, "orderCreateTime"));
                d.setConsigneeName(getTextContent((Element) detail, "consigneeName"));
                d.setConsigneeAddr(getTextContent((Element) detail, "consigneeAddr"));
                d.setConsigneeAddr_State(getTextContent((Element) detail, "consigneeAddr_State"));
                d.setConsigneeAddr_Province(getTextContent((Element) detail, "consigneeAddr_Province"));
                d.setConsigneeAddr_City(getTextContent((Element) detail, "consigneeAddr_City"));
                d.setConsigneeAddr_Area(getTextContent((Element) detail, "consigneeAddr_Area"));
                d.setConsigneePostcode(getTextContent((Element) detail, "consigneePostcode"));
                d.setConsigneeTel(getTextContent((Element) detail, "consigneeTel"));
                d.setConsigneeMobileTel(getTextContent((Element) detail, "consigneeMobileTel"));
                d.setShopName(getTextContent((Element) detail, "shopName"));
                d.setShopID(getTextContent((Element) detail, "shopID"));
                d.setConsignerName(getTextContent((Element) detail, "consignerName"));
                d.setConsignerTel(getTextContent((Element) detail, "consignerTel"));
                d.setConsignerAddr(getTextContent((Element) detail, "consignerAddr"));
                d.setTotalBarginPrice(getTextContent((Element) detail, "totalBarginPrice"));
                d.setSendGoodsTime(getTextContent((Element) detail, "sendGoodsTime"));
                d.setExpressCompany(getTextContent((Element) detail, "expressCompany"));
            }
            list.add(d);
        }
        return list;
    }

    private String getTextContent(Element e, String nodeName) {
        NodeList nl = e.getElementsByTagName(nodeName);
        if(nl != null && nl.getLength() > 0) {
            return nl.item(0).getTextContent();
        }
        return null;
    }

    private String getPrintXml(List<MyTrade> tradeList, Shop shop) {
        StringBuilder orderIdBuilder = new StringBuilder();
        for(MyTrade trade : tradeList) {
            orderIdBuilder.append(trade.getTid()).append(",");
        }
        orderIdBuilder.setLength(orderIdBuilder.length() - 1);
        StringBuilder urlBuilder = baseUrl(method_print, shop);
        urlBuilder.append("&o=").append(orderIdBuilder.toString());
        log.debug(urlBuilder.toString());
        System.out.println(urlBuilder.toString());
        String resp = urlRequest(urlBuilder.toString());
        return resp;
    }

}
