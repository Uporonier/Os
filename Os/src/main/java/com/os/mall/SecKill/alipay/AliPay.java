package com.os.mall.SecKill.alipay;
import lombok.Data;
@Data
public class AliPay {
    private String traceNo;
    private double totalAmount;
    private String subject;
    private String alipayTraceNo;
}