package com.os.mall.SecKill.redis;

public abstract class BasePrefix implements KeyPrefix{
    private int befSeconds;
    private String Prefix;
    public BasePrefix(int Seconds,String Prefixx){
        this.Prefix=Prefixx;
        this.befSeconds=Seconds;
    }

    BasePrefix(String prefix){
        this(0,prefix);
    }

    @Override
    public int befSeconds() {
        return befSeconds; //默认0是不过期的
    }

    @Override
    public String getPrefix() {//获取前缀
        String strname = getClass().getSimpleName();
        System.out.println(strname+":"+Prefix);
        return strname+":"+Prefix;
    }
}
