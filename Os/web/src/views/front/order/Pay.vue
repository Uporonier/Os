
<template>
<div style="margin-top: 10px;width: 50%;margin: 10px auto;background-color: white;text-align: center">
  <table style="margin: 20px auto">
    <tr>
      <th>订单号</th>
      <td>{{orderNo}}</td>
    </tr>
    <tr>
      <th>总数目</th>
      <td>{{count}} 件</td>
    </tr>
    <tr>
      <th>总金额</th>
      <td>{{money}} 元</td>
    </tr>

  </table>
  <hr style="width: 280px" />
  <div>
    <span>支付方式：</span>
  </div>
  <br>
<!--  <img src="../../../resource/img/微信支付.png" style="width: 45px;height: 45px;margin-right: 60px;cursor:pointer;" @click="pay">-->
  <img src="../../../resource/img/支付宝.png" style="width: 50px;height: 50px;cursor:pointer;" @click="pay">
</div>
</template>

<script>
export default {
  name: "Pay",
  data(){
    return{
      userId: 0,
      count:0,
      orderNo: '',
    }
  },
  created() {
    this.money = parseFloat(this.$route.query.money).toFixed(2);
    this.orderNo = this.$route.query.orderNo;
    this.count= this.$route.query.count;
    console.log(this.count)
    console.log(this.money)
  },

  methods:{
    pay(){
      // window.location.href = `http://localhost:9191/alipay/pay?orderId=${this.orderNo}
      // &price=${this.money}&goodsName=${encodeURIComponent("商品")}`;
      // let orderNo = res.data;
      // console.log(this.money)
      // console.log(this.count)

      //跳转到支付页面
      window.location.href = `http://localhost:9191/alipay/pay?orderId=${this.orderNo}&price=${this.money}&num=${this.count}&goodsName=${encodeURIComponent("商品")}`;

    }
  }

}
</script>

<style scoped>
tr{
  line-height:40px;
}

</style>