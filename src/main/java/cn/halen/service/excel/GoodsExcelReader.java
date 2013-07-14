package cn.halen.service.excel;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hzhang
 * Date: 7/6/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoodsExcelReader {

    private File file;

    private Workbook book;

    private Sheet sheet;

    private int rowCount;

    public List<GoodsRow> getData() {
        List<GoodsRow> list = new ArrayList<GoodsRow>();
        int rows = sheet.getRows();
        for(int i=1; i<rows; i++) {
            Cell[] cells = sheet.getRow(i);
            String cell0 = cells[0].getContents();
            String cell1 = cells[1].getContents();
            String cell2 = cells[2].getContents();
            String cell3 = cells[3].getContents();
            String cell4 = cells[4].getContents();
            String cell5 = cells[5].getContents();

            List<String> colorIds = new ArrayList<String>();
            for(String s : cell3.replaceAll("；", ";").split(";")) {
                if(StringUtils.isNotBlank(s)) {
                    colorIds.add(s.trim());
                }
            }
            List<String> colors = new ArrayList<String>();
            for(String s : cell4.replaceAll("；", ";").split(";")) {
                if(StringUtils.isNotBlank(s)) {
                    colors.add(s.trim());
                }
            }

            List<String> sizes = new ArrayList<String>();
            for(String s : cell5.replaceAll("；", ";").split(";")) {
                if(StringUtils.isNotBlank(s)) {
                    sizes.add(s.trim());
                }
            }
            GoodsRow row = new GoodsRow(cell0, cell1, Integer.parseInt(cell2)*100, colorIds, colors, sizes);
            list.add(row);
        }
        return list;
    }

    public GoodsExcelReader(String filePath) throws IOException, BiffException {
        this.file = new File(filePath);
        this.book = Workbook.getWorkbook(file);
        this.sheet = book.getSheet(0);
    }

    public GoodsExcelReader(File file) throws IOException, BiffException {
        this.file = file;
        this.book = Workbook.getWorkbook(file);
        this.sheet = book.getSheet(0);
    }

    public boolean checkColumn() {
        Cell[] cells = sheet.getRow(0);
        if(cells.length < 6) {
            return false;
        }
        String c0 = cells[0].getContents();
        String c1 = cells[1].getContents();
        String c2 = cells[2].getContents();
        String c3 = cells[3].getContents();
        String c4 = cells[4].getContents();
        String c5 = cells[5].getContents();
        if(!"货号".equals(c0) || !"名称".equals(c1) || !"价格".equals(c2) ||
                !"颜色代码".equals(c3) || !"颜色".equals(c4) || !"尺码".equals(c5)) {
            return false;
        }
        return true;
    }

    public void destroy() {
        book.close();
    }

    public int checkData() {
        boolean result = true;
        int rows = sheet.getRows();
        for(int row=1; row<rows; row++) {
            Cell[] cells = sheet.getRow(row);
            if (cells.length < 5) {
                return row;
            }
            String cell0 = cells[0].getContents();
            String cell1 = cells[1].getContents();
            String cell2 = cells[2].getContents();
            String cell3 = cells[3].getContents();
            String cell4 = cells[4].getContents();
            String cell5 = cells[5].getContents();
            if (StringUtils.isBlank(cell0) || StringUtils.isBlank(cell1) || StringUtils.isBlank(cell2) ||
                    (StringUtils.isBlank(cell4) && StringUtils.isBlank(cell5))) {
                return row;
            }
            List<String> colorIds = new ArrayList<String>();
            for(String s : cell3.replaceAll("；", ";").split(";")) {
                if(StringUtils.isNotBlank(s)) {
                    colorIds.add(s);
                }
            }
            List<String> colors = new ArrayList<String>();
            for(String s : cell4.replaceAll("；", ";").split(";")) {
                if(StringUtils.isNotBlank(s)) {
                    colors.add(s);
                }
            }
            if(colorIds.size() != colors.size()) {
                return row;
            }
            try {
                Integer.parseInt(cell2);
            } catch (Exception e) {
                return row;
            }
        }
        rowCount = rows - 1;
        return 0;
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
