package cn.halen.service.dangdang;

import cn.halen.data.pojo.MySku;
import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.Shop;
import cn.halen.data.pojo.TradeStatus;
import cn.halen.service.top.TopConfig;
import cn.halen.util.Constants;
import com.taobao.api.ApiException;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;
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
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.ParseException;
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

    private static final String ACCESS_TOKEN = "11AAB4792A76EBCCA9AE01E0BFCE74F37BEC216B29A314B1D48AA75FCB4BC67E";
    public static final String method = "dangdang.items.list.get";
    public static final String method_order_list_get = "dangdang.orders.list.get";
    public static final String method_order_detail_get = "dangdang.order.details.get";
    public static final String SING_METHOD = "md5";
    public static final String FORMAT = "xml";
    public static final String VERSION = "1.0";

    private FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private TopConfig topConfig;

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
        StringBuilder urlBuilder = baseUrl(method_order_list_get, shop);

        urlBuilder.append("&osd=").append(dateFormat.format(startDate));
        urlBuilder.append("&oed=").append(dateFormat.format(endDate));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(urlBuilder.toString());
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
            log.error("", e);
        } finally {
            if(null != reader) {
                IOUtils.closeQuietly(reader);
            }
            HttpClientUtils.closeQuietly(httpClient);
        }

        return null;
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
        urlBuilder.append("&o=7485770369");
        HttpGet get = new HttpGet(urlBuilder.toString());
        System.out.println(urlBuilder.toString());
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
        System.out.println(builder.toString());
    }

    public Map<String, Object> syncTrade(Shop shop, Date startDate, Date endDate) {
        Map<String, Object> counter = new HashMap<String, Object>();
        List<Trade> tradeList = Collections.EMPTY_LIST;
        try {
            tradeList = tradeClient.queryTradeList(tokenList, startDate, endDate);
        } catch (ParseException e) {
            log.error("", e);
        } catch (ApiException e) {
            log.error("", e);
        }
        counter.put("Paid", tradeList.size()); //已付款订单数量

        int success = 0;
        int existCount = 0;
        List<String> fail = new ArrayList<String>();
        for(Trade trade : tradeList) {
            //check trade if exists
            boolean exist = tradeMapper.checkTidExist(String.valueOf(trade.getTid()));
            if(exist) {
                existCount++;
                continue;
            }
            Trade tradeDetail = null;
            try {
                tradeDetail = tradeClient.getTradeFullInfo(trade.getTid(), topConfig.getToken(trade.getSellerNick()));
            } catch (ApiException e) {
                log.error("", e);
            }

            //检查订单是否退款
            Iterator<Order> it = tradeDetail.getOrders().iterator();
            while(it.hasNext()) {
                Order o = it.next();
                if(!"NO_REFUND".equals(o.getRefundStatus())) {
                    it.remove();
                }
            }
            if(tradeDetail.getOrders().size() == 0) {
                tradeDetail = null;
            }

            if(null == tradeDetail) {
                fail.add(String.valueOf(trade.getTid()));
                continue;
            }
            MyTrade myTrade = null;
            try {
                myTrade = tradeService.toMyTrade(tradeDetail);
            } catch (ApiException e) {
                log.error("", e);
            }
            if(null == myTrade || !myTrade.isSuccess()) {
                fail.add(String.valueOf(trade.getTid()));
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

}
