package cn.halen.service.excel;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hzhang
 * Date: 7/6/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradeExcelReader {

    private File file;

    private Workbook book;

    private Sheet sheet;

    private int rowCount;

    public List<TradeRow> getData() {
        List<TradeRow> list = new ArrayList<TradeRow>();
        int rows = sheet.getRows();
        for(int i=1; i<rows; i++) {
            Cell[] cells = sheet.getRow(i);
            String shopName = cells[1].getContents();
            String tradeId = cells[3].getContents();
            String goodsId = cells[8].getContents();
            String color = cells[9].getContents();
            String size = cells[10].getContents();
            String num = cells[11].getContents();
            String name = cells[19].getContents();
            String mobile = cells[20].getContents();
            String phone = cells[21].getContents();
            String address = cells[22].getContents();
            String delivery = cells[25].getContents();
            String comment = cells[28].getContents();
            String title = cells[7].getContents();
            int price = (int) (100 * Double.parseDouble(cells[15].getContents()));

            TradeRow row = new TradeRow(shopName, tradeId, goodsId, color, size, Integer.parseInt(num), name,
                    mobile, phone, address, comment, delivery, title, price);
            list.add(row);
        }
        return list;
    }

    public TradeExcelReader(String filePath) throws IOException, BiffException {
        this.file = new File(filePath);
        this.book = Workbook.getWorkbook(file);
        this.sheet = book.getSheet(0);
    }

    public TradeExcelReader(File file) throws IOException, BiffException {
        this.file = file;
        this.book = Workbook.getWorkbook(file);
        this.sheet = book.getSheet(0);
    }

    public boolean checkColumn() {
        Cell[] cells = sheet.getRow(0);
        if(cells.length != 33) {
            return false;
        }
        return true;
    }

    public void destroy() {
        book.close();
    }

    public File getFile() {
        return file;
    }

    public Workbook getBook() {
        return book;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public int getRowCount() {
        return rowCount;
    }
}
