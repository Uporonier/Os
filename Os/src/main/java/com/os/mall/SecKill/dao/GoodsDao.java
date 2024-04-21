package com.os.mall.SecKill.dao;

import com.os.mall.SecKill.entity.SecKillGoods;
import com.os.mall.SecKill.recParam.GoodsRP;
import com.os.mall.SecKill.recParam.SecKillGoodsRP;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GoodsDao {
    @Select("select sg.*,sgs.stock_count,sgs.start_date,sgs.end_date ,sgs.seckill_price from sk_goods_seckill sgs left join sk_goods sg  " +
            "on sgs.goods_id =sg.id")
    public List<GoodsRP> listGoodsRP();


    @Select("select sg.*,sgs.stock_count,sgs.start_date,sgs.end_date ,sgs.seckill_price from sk_goods_seckill sgs left join sk_goods sg  " +
            "on sgs.goods_id =sg.id where sg.id=#{goodsId}")
    GoodsRP listGoodsRPByGoodsId(@Param("goodsId") long goodsId);

    @Update("update sk_goods_seckill set stock_count= stock_count-1 where goods_id=#{goodsId} and stock_count>0")
    public int reduceStock(SecKillGoods goods1);  //stock_count>0 方式多个进程一块

    @Update("update sk_goods_seckill set stock_count= stock_count+1 where goods_id=#{goodsId} ")
    public int addStock(SecKillGoods goods1);  //stock_count>0 方式多个进程一块

    @Insert("INSERT INTO sk_goods_seckill (goods_id, seckill_price, stock_count, start_date, end_date) " +
            "VALUES (#{goodsId}, #{seckillPrice}, #{stockCount}, #{startDate}, #{endDate})")
    void addSecGoods(SecKillGoods secKillGoods);;

    @Delete("DELETE FROM sk_goods_seckill WHERE goods_id = #{goodsId}")
    void delSecGoods(Long goods_id);

    @Select("select sg.*,sgs.stock_count,sgs.start_date,sgs.end_date ,sgs.seckill_price from sk_goods_seckill sgs left join good sg  " +
            "on sgs.goods_id =sg.id")
    List<SecKillGoodsRP> listSecKillGoodsRP();


    @Select("select sg.*,sgs.stock_count,sgs.start_date,sgs.end_date ,sgs.seckill_price from sk_goods_seckill sgs left join good sg  " +
            "on sgs.goods_id =sg.id where sg.id=#{goodsId}")
    SecKillGoodsRP listSecKillGoodsRPByGoodsId(@Param("goodsId") long goodsId);

}
