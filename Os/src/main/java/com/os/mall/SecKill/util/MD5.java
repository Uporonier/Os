package com.os.mall.SecKill.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5 {
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }
    private static final String salt="LyandXxy";
    public static String inputToForm(String inputPassword){
        String str=""+salt.charAt(0)+salt.charAt(2)+inputPassword+salt.charAt(1)+salt.charAt(3);
        System.out.println(str);
        return md5(str);
    }

    public static String formToDB(String formPassword,String salt){
        String str=salt.charAt(0)+salt.charAt(2)+formPassword+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }
    public static String inputToDB(String inputPassword,String saltDB){ //明文转数据库密码
        String formPassword=inputToForm(inputPassword);
        return formToDB(formPassword,saltDB);

    }



    public static void main(String[]args){
//        System.out.println(inputToForm("passwordInput"));
        System.out.println(formToDB("33bc963888cf469b848a946d86dede68","dB18c1FGzE"));
//        System.out.println(inputToDB("passwordInput","dB18c1FGzE"));
        System.out.println(inputToDB("123456","ly+xxy"));
//        System.out.println(formToDB("02f65418b3d741e192eff8dcf0c157d0","zsrZIHtRra"));
    }
}
