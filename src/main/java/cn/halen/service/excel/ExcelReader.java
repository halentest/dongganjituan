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
public class ExcelReader {

    private File file;

    private Workbook book;

    private Sheet sheet;

    private int rowCount;

    public List<Row> getData() {
        List<Row> list = new ArrayList<Row>();
        int rows = sheet.getRows();
        for(int i=1; i<rows; i++) {
            Cell[] cells = sheet.getRow(i);
            String cell0 = cells[0].getContents();
            String cell1 = cells[1].getContents();
            String cell2 = cells[2].getContents();
            String cell3 = cells[3].getContents();
            Row row = new Row(cell0.trim(), cell1.trim(), cell2.trim(), Integer.parseInt(cell3.trim()));
            list.add(row);
        }
        return list;
    }

    public ExcelReader(String filePath) throws IOException, BiffException {
        this.file = new File(filePath);
        this.book = Workbook.getWorkbook(file);
        this.sheet = book.getSheet(0);
    }

    public ExcelReader(File file) throws IOException, BiffException {
        this.file = file;
        this.book = Workbook.getWorkbook(file);
        this.sheet = book.getSheet(0);
    }

    public static void main(String[] args) throws IOException, BiffException {
        String filePath = "C:\\Users\\hzhang\\Desktop\\进货单1.xls";
        ExcelReader reader = new ExcelReader(filePath);
        boolean checkColumn = reader.checkColumn();
        int checkData = reader.checkData();
        System.out.println("checkColumn is " + checkColumn);
        System.out.println("checkData is " + checkData);
        System.out.println("rowCount is " + reader.rowCount);
    }

    public boolean checkColumn() {
        Cell[] cells = sheet.getRow(0);
        String c0 = cells[0].getContents();
        String c1 = cells[1].getContents();
        String c2 = cells[2].getContents();
        String c3 = cells[3].getContents();
        if(!"货号".equals(c0) || !"颜色".equals(c1) || !"尺码".equals(c2) ||
                !"数量".equals(c3)) {
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
            if (cells.length < 4) {
                return row;
            }
            String cell0 = cells[0].getContents();
            System.out.print("cell0 is " + cell0 + "; ");
            String cell1 = cells[1].getContents();
            System.out.print("cell1 is " + cell1 + "; ");
            String cell2 = cells[2].getContents();
            System.out.print("cell2 is " + cell2 + "; ");
            String cell3 = cells[3].getContents();
            System.out.print("cell3 is " + cell3 + "; ");
            System.out.println();
            if (StringUtils.isBlank(cell0) || StringUtils.isBlank(cell1) || StringUtils.isBlank(cell2) ||
                    StringUtils.isBlank(cell3)) {
                return row;
            }
            try {
                Integer.parseInt(cell3);
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
