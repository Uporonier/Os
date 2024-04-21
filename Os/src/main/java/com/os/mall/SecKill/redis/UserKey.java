package com.os.mall.SecKill.redis;

public class UserKey extends BasePrefix{

    public static final int TOKEN_BEFORESECONDS=3600*24*7;//cookie的有效期
    private UserKey(int befoSeconds,String Prefixx) {
        super(befoSeconds,Prefixx);
    }

    public static UserKey token=new UserKey(TOKEN_BEFORESECONDS,"tk");  //前缀是tk
    public static UserKey getById =new UserKey(0,"id");//对象级缓存  有效期设置为0   0对应永久有效
    public static UserKey getByName =new UserKey(TOKEN_BEFORESECONDS,"name");
}
