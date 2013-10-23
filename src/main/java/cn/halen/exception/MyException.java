package cn.halen.exception;

/**
 * Created with IntelliJ IDEA.
 * User: hzhang
 * Date: 10/23/13
 * Time: 7:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyException extends Exception {

    private String errorInfo;

    public MyException(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getErrorInfo() {
        return errorInfo;
    }
}
