package com.os.mall.SecKill.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileVerify {
    /**
     * 判断字符串是否是合法的手机号码
     *
     * @param src 待检查的字符串
     * @return 如果是合法的手机号码返回true，否则返回false
     */
    public static boolean isMobile(String src) {
        // 手机号码的正则表达式
        String mobileRegex = "^(?:(?:\\+|00)86)?1[3-9]\\d{9}$";

        // 创建正则表达式的 Pattern 对象
        Pattern pattern = Pattern.compile(mobileRegex);

        // 创建匹配器对象
        Matcher matcher = pattern.matcher(src);

        // 判断输入字符串是否符合手机号码的格式
        return matcher.matches();
    }
    public static void main(String[]args){
        String str="18769808523";
        if(isMobile(str)) System.out.println(str+"  是手机号");
        else System.out.println(str+"  不是手机号");

    }
}
