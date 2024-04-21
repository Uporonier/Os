package com.os.mall.SecKill.Result;

public class Result1<T> {
    private int code;
    private String msg;
    private T data;
    public static <T> Result1<T> suc(T data){
        return new Result1<T>(data);
    }
    public static <T> Result1<T> error(CodeMessage cm){
        return new Result1<T>(cm);
    }

    private Result1(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    private Result1(CodeMessage cm) {
        if(cm == null) {
            return;
        }
        this.code = cm.getCode();
        this.msg = cm.getMsg();
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
    public T getData() {
        return data;
    }
}
