package cn.halen.service.dangdang;

import cn.halen.data.mapper.*;
import cn.halen.data.pojo.*;
import cn.halen.service.TradeService;
import cn.halen.service.top.TopConfig;
import cn.halen.service.top.domain.TaoTradeStatus;
import cn.halen.service.top.util.MoneyUtils;
import cn.halen.util.Constants;
import com.taobao.api.ApiException;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;
import freemarker.template.SimpleDate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public static final String SING_METHOD = "md5";
    public static final String FORMAT = "xml";
    public static final String VERSION = "1.0";

    private FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

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

    public String updateInventory(MySku sku, Shop shop) throws UnsupportedEncodingException {

        String thirdPartyCode = sku.getGoods_id() + sku.getColor_id() + sku.getSize();
        long quantity = sku.getQuantity() - sku.getLock_quantity() - sku.getManaual_lock_quantity();
        if(shop.getBase_quantity() > 0) {
            if(quantity > shop.getBase_quantity()) {
                quantity = quantity - shop.getBase_quantity();
            } else {
                quantity = 0;
            }
        } else if(shop.getRate() != 1.00) {
            quantity = Math.round(quantity * shop.getRate());
        }

        CloseableHttpClient httpClient = HttpClients.createDefault();
        String timestamp = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(timestamp);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(topConfig.getYougouUrl())
                .append("?method=").append(method)
                .append("&timestamp=").append(URLEncoder.encode(timestamp, "UTF8"))
                .append("&app_key=").append(shop.getAppkey())
                .append("&sign_method=").append(SING_METHOD)
                .append("&app_version=").append(VERSION)
                .append("&format=").append(FORMAT)
                .append("&third_party_code=").append(thirdPartyCode)
                .append("&update_type=0")
                .append("&quantity=").append(quantity);
        list.add(new BasicNameValuePair("method", method));
        list.add(new BasicNameValuePair("timestamp", timestamp));
        list.add(new BasicNameValuePair("app_key", shop.getAppkey()));
        list.add(new BasicNameValuePair("sign_method", SING_METHOD));
        list.add(new BasicNameValuePair("app_version", VERSION));
        list.add(new BasicNameValuePair("format", FORMAT));
        list.add(new BasicNameValuePair("third_party_code", thirdPartyCode));
        list.add(new BasicNameValuePair("update_type", "0"));
        list.add(new BasicNameValuePair("quantity", String.valueOf(quantity)));


        Collections.sort(list, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair o1, NameValuePair o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        StringBuilder sourceBuilder = new StringBuilder(shop.getAppsecret());
        for(NameValuePair p : list) {
            sourceBuilder.append(p.getName()).append(p.getValue());
        }
        String md5 = DigestUtils.md5DigestAsHex(sourceBuilder.toString().getBytes());
        urlBuilder.append("&sign=").append(md5);
        HttpPost get = new HttpPost(urlBuilder.toString());
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            CloseableHttpResponse resp = httpClient.execute(get);

            HttpEntity entity = resp.getEntity();
            if(null != entity) {
                reader = new BufferedReader(new InputStreamReader(entity.getContent()));
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
        log.debug("sku id {}, {}", sku.getId(), builder.toString());
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
            doc = docBuilder.parse(new ByteArrayInputStream(builder.toString().getBytes()));
        } catch (SAXException e) {
            log.error("", e);
        } catch (IOException e) {
            log.error("", e);
        }
        Node codeNode = doc.getElementsByTagName("code").item(0);
        String code = codeNode.getTextContent();
        if("200".equals(code)) {
            return null;
        } else {
            return code;
        }
    }

    public static void main(String[] args) throws MalformedURLException, UnsupportedEncodingException {

        get();
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
            String url = urlBuilder.toString() + "&p=" + totalInfo.getCurrentPage() + 1;
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

    //查询订单详情
    private OrderInfo queryDetail(OrderInfo orderInfo, Shop shop) {
        StringBuilder urlBuilder = baseUrl(method_order_detail_get, shop);
        urlBuilder.append("&o=").append(orderInfo.getOrderID());

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
        System.out.println(timestamp);

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
        urlBuilder.append("&o=7486353144");
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

            Goods goods = goodsMapper.getByHid(sku.getGoods_id());

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
        myTrade.setBuyer_nick(orderInfo.getDangdangAccountID());
        myTrade.setCome_from(Constants.DANGDANG);
        myTrade.setModified(new Date());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            myTrade.setCreated(format.parse(orderInfo.getPaymentDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        myTrade.setMyOrderList(myOrderList);
        myTrade.setGoods_count(goodsCount);
        myTrade.setPay_type(Constants.PAY_TYPE_ONLINE); //目前只支持淘宝的在线支付订单
        MyLogisticsCompany mc = logisticsMapper.select(1);
        myTrade.setDelivery(mc.getName());

        return myTrade;
    }

}
