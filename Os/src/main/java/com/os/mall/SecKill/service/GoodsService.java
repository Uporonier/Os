package com.os.mall.SecKill.service;

import com.os.mall.SecKill.dao.GoodsDao;
import com.os.mall.SecKill.entity.SecKillGoods;
import com.os.mall.SecKill.recParam.GoodsRP;
import com.os.mall.SecKill.recParam.SecKillGoodsRP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;
    public List<GoodsRP> listGoodsRP(){
        return goodsDao.listGoodsRP();
    }
    public GoodsRP getGoodsRPByGoodsId(long goodsId) {
        return goodsDao.listGoodsRPByGoodsId(goodsId);
    }


    public boolean reduceStock(GoodsRP goods) {
        SecKillGoods goods1=new SecKillGoods();
        goods1.setGoodsId((goods.getId()));
        return goodsDao.reduceStock(goods1)>0;
    }
    public boolean reduceStock1(SecKillGoodsRP goods) {
        SecKillGoods goods1=new SecKillGoods();
        goods1.setGoodsId((goods.getId()));
        return goodsDao.reduceStock(goods1)>0;
    }
    public void addStock1(Long goodsId) {
        SecKillGoods goods1=new SecKillGoods();
        goods1.setGoodsId(goodsId);
        goodsDao.addStock(goods1);
    }
    public void addSecGoods(SecKillGoods secKillGoods) {
        goodsDao.addSecGoods(secKillGoods);
    }
    public void delSecGoods(Long goods_id) {
        goodsDao.delSecGoods(goods_id);
    }

    public List<SecKillGoodsRP> listSecKillGoodsRP() {
        return goodsDao.listSecKillGoodsRP();
    }

    public SecKillGoodsRP getSecKillGoodsRPByGoodsId(long goodsId) {
        return  goodsDao.listSecKillGoodsRPByGoodsId(goodsId);
    }
    public  float getSecPriceBySecGoodsId(int goodsId){
        SecKillGoodsRP secKillGoodsRP=goodsDao.listSecKillGoodsRPByGoodsId(goodsId);
        System.out.println("秒杀商品的价格是"+secKillGoodsRP.getSeckillPrice());
        float price=secKillGoodsRP.getSeckillPrice();
        return price;
    }
}
