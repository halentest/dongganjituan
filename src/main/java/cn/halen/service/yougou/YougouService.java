package cn.halen.service.yougou;

import cn.halen.data.pojo.MyTrade;
import cn.halen.service.top.TopConfig;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: hzhang
 * Date: 12/28/13
 * Time: 10:51 AM
 */
@Service
public class YougouService {
    private static final String yougouUrl = "http://183.62.162.119:80/mms/api.sc";
    private static final String yougouAppKey = "SP20120412312125";

    @Autowired
    private TopConfig topConfig;

    public boolean updateInventory(MyTrade trade) {

        return true;
    }

    public static void main(String[] args) throws MalformedURLException, UnsupportedEncodingException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String timestamp = FastDateFormat.getInstance("yyyyMMdd HH:mm:ss").format(new Date());
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(yougouUrl)
                .append("?method=yougou.inventory.query")
                .append("&timestamp=").append(URLEncoder.encode(timestamp, "UTF8"))
                .append("&app_key=").append(yougouAppKey)
                .append("&sign_method=MD5")
                .append("&app_version=1.0")
                .append("&format=xml");
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
