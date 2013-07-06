package cn.halen.service;

import cn.halen.data.DataConfig;
import cn.halen.exception.InsufficientStockException;
import cn.halen.service.excel.ExcelReader;
import cn.halen.service.excel.Row;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class, ServiceConfig.class})
public class SkuServiceTest {

	@Autowired
	private SkuService skuService;
	
	@Test
	public void test_updateSkuAndInsertRefund() {
        String filePath = "C:\\Users\\hzhang\\Desktop\\进货单1.xls";
        ExcelReader reader = null;
        try {
            reader = new ExcelReader(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean checkColumn = reader.checkColumn();
        if(!checkColumn) {
            System.out.println("格式不正确，必须有 编号、颜色、尺码、数量 这几列!");
            return;
        }
        int checkData = reader.checkData();
        if(checkData != 0) {
            System.out.println("第 " + (checkData + 1) + " 行数据格式不正确，请修改后重试!");
            return;
        }
        List<Row> rows = reader.getData();
        Row row = skuService.checkRow(rows);
        if(null != row) {
            System.out.println("这个商品(" + row.getGoodsId() + "," + row.getColor() + "," + row.getSize() + ")不存在，请检查是否存在错误或者在系统中添加该商品之后重试!");
            return;
        }
        try {
            skuService.execRow(rows, "refund");
        } catch (InsufficientStockException e) {
            System.out.println("这个商品(" + e.getGoodsHid() + ")库存不足，更新失败!");
        } catch (Exception e) {
            System.out.println("系统异常，请重试!");
        }
    }
}
