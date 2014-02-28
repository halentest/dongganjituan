package cn.halen.service.yougou;

import cn.halen.data.pojo.MySku;
import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.Shop;
import cn.halen.service.top.TopConfig;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * User: hzhang
 * Date: 12/28/13
 * Time: 10:51 AM
 */
@Service
public class YougouService {
    private static final String yougouUrl = "http://183.62.162.119:80/mms/api.sc";
    private static final String yougouAppKey = "SP20120412312125";
    private static final String yougouAppSecret = "0123456789";

    public static final String METHOD_UPDATE_INVENTORY = "yougou.inventory.update";
    public static final String SING_METHOD = "MD5";
    public static final String FORMAT = "xml";
    public static final String VERSION = "1.0";

    @Autowired
    private TopConfig topConfig;

    private Logger log = LoggerFactory.getLogger(YougouService.class);

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
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(topConfig.getYougouUrl())
                .append("?method=").append(METHOD_UPDATE_INVENTORY)
                .append("&timestamp=").append(URLEncoder.encode(timestamp, "UTF8"))
                .append("&app_key=").append(shop.getAppkey())
                .append("&sign_method=").append(SING_METHOD)
                .append("&app_version=").append(VERSION)
                .append("&format=").append(FORMAT)
                .append("&third_party_code=").append(thirdPartyCode)
                .append("&update_type=0")
                .append("&quantity=").append(quantity);
        list.add(new BasicNameValuePair("method", METHOD_UPDATE_INVENTORY));
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
                reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF8"));
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

        //get();
        post();
    }

    public static void get() throws UnsupportedEncodingException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String timestamp = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(timestamp);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(yougouUrl)
                .append("?method=yougou.inventory.query")
                .append("&timestamp=").append(URLEncoder.encode(timestamp, "UTF8"))
                .append("&app_key=").append(yougouAppKey)
                .append("&sign_method=MD5")
                .append("&app_version=1.0")
                .append("&format=xml")
                .append("&page_index=1")
                .append("&page_size=5");
        list.add(new BasicNameValuePair("method", "yougou.inventory.query"));
        list.add(new BasicNameValuePair("timestamp", timestamp));
        list.add(new BasicNameValuePair("app_key", yougouAppKey));
        list.add(new BasicNameValuePair("sign_method", "MD5"));
        list.add(new BasicNameValuePair("app_version", "1.0"));
        list.add(new BasicNameValuePair("format", "xml"));
        list.add(new BasicNameValuePair("page_index", "1"));
        list.add(new BasicNameValuePair("page_size", "5"));
        Collections.sort(list, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair o1, NameValuePair o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        StringBuilder sourceBuilder = new StringBuilder(yougouAppSecret);
        for(NameValuePair p : list) {
            sourceBuilder.append(p.getName()).append(p.getValue());
        }
        String md5 = DigestUtils.md5DigestAsHex(sourceBuilder.toString().getBytes());
        urlBuilder.append("&sign=").append(md5);
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

    public static void post() throws UnsupportedEncodingException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String timestamp = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(timestamp);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(yougouUrl)
                .append("?method=yougou.inventory.update")
                .append("&timestamp=").append(URLEncoder.encode(timestamp, "UTF8"))
                .append("&app_key=").append(yougouAppKey)
                .append("&sign_method=MD5")
                .append("&app_version=1.0")
                .append("&format=xml")
                .append("&third_party_code=353555531")
                .append("&update_type=0")
                .append("&quantity=22");
        list.add(new BasicNameValuePair("method", "yougou.inventory.update"));
        list.add(new BasicNameValuePair("timestamp", timestamp));
        list.add(new BasicNameValuePair("app_key", yougouAppKey));
        list.add(new BasicNameValuePair("sign_method", "MD5"));
        list.add(new BasicNameValuePair("app_version", "1.0"));
        list.add(new BasicNameValuePair("format", "xml"));
        list.add(new BasicNameValuePair("third_party_code", "353555531"));
        list.add(new BasicNameValuePair("update_type", "0"));
        list.add(new BasicNameValuePair("quantity", "22"));


        Collections.sort(list, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair o1, NameValuePair o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        StringBuilder sourceBuilder = new StringBuilder(yougouAppSecret);
        for(NameValuePair p : list) {
            sourceBuilder.append(p.getName()).append(p.getValue());
        }
        String md5 = DigestUtils.md5DigestAsHex(sourceBuilder.toString().getBytes());
        urlBuilder.append("&sign=").append(md5);
        HttpPost get = new HttpPost(urlBuilder.toString());
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
}
