package com.os.mall.SecKill.util;

public class UUID  {
    public static String uuid(){
//        System.out.println(java.util.UUID.randomUUID().toString());
        return java.util.UUID.randomUUID().toString().replace("-","");//去掉横杠

    }

}
