package com.os.mall.SecKill.Result;

public class CodeMessage {

    private int code;
    private String msg;
    public String token_tmp;

    //通用异常
    public static CodeMessage SUCCESS = new CodeMessage(0, "成功");
    public static CodeMessage SERVER_ERROR = new CodeMessage(500100, "服务端异常");
    //登录模块 5002XX
    public static CodeMessage LoginPass_ERROR = new CodeMessage(500101, "登录密码不能为空");
    public static CodeMessage Pass_ERROR = new CodeMessage(500101, "登录密码错误");
    public static CodeMessage LoginMobile_ERROR = new CodeMessage(500102, "请输入合法的手机号");
    public static CodeMessage LoginMobile_EMPTY = new CodeMessage(500103, "账号不存在！");

    public static CodeMessage SESSION_FAIL =new CodeMessage(500104, "session信息失效") ;

    //商品模块 5003XX

    //订单模块 5004XX
    public static CodeMessage  OrderNULL=new CodeMessage(500401, "订单为空") ;
    //秒杀模块 5005XX

    public static CodeMessage STOCK_EMPTY = new CodeMessage(500501, "商品已经没有库存");
    public static CodeMessage SECKILL_REPEAT = new CodeMessage(500502, "商品不可重复秒杀");
    public static CodeMessage SECKILL_OVER =  new CodeMessage(500502, "秒杀已经结束");
    public static CodeMessage STOCK_NULL =  new CodeMessage(500502, "已全部售罄");

    public CodeMessage(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CodeMessage(String token_tmp,int code) {

        this.code = code;
        this.token_tmp = token_tmp;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}

