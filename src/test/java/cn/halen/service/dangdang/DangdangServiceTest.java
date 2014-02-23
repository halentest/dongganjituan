package cn.halen.service.dangdang;

import cn.halen.data.pojo.MySku;
import cn.halen.data.pojo.Shop;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 14-2-23.
 */
public class DangdangServiceTest {
    private List<MySku> skuList() {
        List<MySku> skuList = new ArrayList<MySku>(2);
        MySku sku = new MySku();
        sku.setGoods_id("C82506116");
        sku.setColor_id("81");
        sku.setSize("38");
        sku.setQuantity(0);
        skuList.add(sku);

        sku = new MySku();
        sku.setGoods_id("C82506116");
        sku.setColor_id("81");
        sku.setSize("39");
        sku.setQuantity(0);
        skuList.add(sku);
        return skuList;
    }

    private Shop createShop() {
        Shop shop = new Shop();
        shop.setSeller_nick("kekeshop");
        shop.setToken("11AAB4792A76EBCC51FFFC6FDAC939125D1F7CB2E029933E1C8F2E274A2D9675");
        return shop;
    }

    @Test
    public void testCreateRequestXml() throws IOException {

        List<MySku> skuList = skuList();
        DangdangService service = new DangdangService();
        Shop shop = createShop();
        File file = service.createRequestXml(skuList, shop);

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line = null;
        while((line=reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    @Test
    public void testUpdateInventory() throws UnsupportedEncodingException {

        List<MySku> skuList = skuList();
        Shop shop = createShop();
        DangdangService service = new DangdangService();
        File file = service.createRequestXml(skuList, shop);
        String result = service.updateInventory(file, shop);
        System.out.println(result);
    }
}
