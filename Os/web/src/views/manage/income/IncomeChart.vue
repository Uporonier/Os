<template>
  <div>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-card
          v-if="activeName !== 'hourly' && activeName !== 'salesAdvice'"
      style="
      display: inline-block;
      margin-left: 50px;
      margin-top: 30px;
      font-weight: bold;
      font-size: 22px;
      color: #ffb02a;
      "
      >￥总计：{{ total | numFilter }}</el-card>

      <!--      柱状图-->
      <el-tab-pane label="各类收入柱状图" name="bar">
        <div
          id="bar"
          style="width: 1200px; height: 500px; margin: auto auto"
        ></div>
      </el-tab-pane>
      <!--      饼图-->
      <el-tab-pane label="各类收入饼图" name="pie">
        <div
          id="pie"
          style="width: 600px; height: 600px; margin: 10px auto"
        ></div>
      </el-tab-pane>
      <!--  本周收入折线图-->
      <el-tab-pane label="本周收入" name="line1">
        <div
          id="weekLine"
          style="width: 900px; height: 500px; margin: 10px auto"
        ></div>
      </el-tab-pane>
      <!-- 本月收入折线图-->
      <el-tab-pane label="本月收入" name="line2">
        <div
          id="monthLine"
          style="width: 1500px; height: 500px; margin: 10px auto"
        ></div>
      </el-tab-pane>

      <el-tab-pane label="流量统计" name="hourly">
        <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="选择日期"
            style="margin-bottom: 20px;"
            @change="fetchHourlyOrders"
        ></el-date-picker>
        <div id="hourlyOrdersChart" style="width: 1000px; height: 500px; margin: auto;"></div>
      </el-tab-pane>

      <el-tab-pane label="销售建议" name="salesAdvice">
        <div style="margin: 20px;">
          <div v-html="salesAdviceHtml"></div> <!-- 使用 v-html 指令展示 HTML 内容 -->
        </div>
      </el-tab-pane>





    </el-tabs>
  </div>
</template>

<script>
import * as echarts from "echarts";
import axios from "axios";
export default {
  name: "IncomeChart",
  data() {
    return {
      sumIncome: 0,
      categoryIncomes: [],
      categoryNames: [],
      incomes: [],
      activeName: "bar",
      totalAll: 0,
      totalWeek: 0,
      totalMonth: 0,
      total: 0,
      // 其他已定义的数据...
      selectedDate: new Date(), // 默认选择当前日期
      hourlyOrders: [],
      salesAdviceHtml: '', // 用来存储从服务器获取的销售建议HTML内容
    };
  },
  methods: {
    fetchSalesAdvice() {
      axios.get('http://localhost:9191/saleadvice/marketing-suggestions').then(response => {
        this.salesAdviceHtml = response.data;
      }).catch(error => console.error('Failed to fetch sales advice:', error));

    },
    fetchHourlyOrders() {
      // 手动格式化日期
      let year = this.selectedDate.getFullYear();
      let month = this.selectedDate.getMonth() + 1;  // getMonth() 返回 0-11，表示1-12月，所以需要+1
      let day = this.selectedDate.getDate();

      // 确保月份和日期始终是两位数
      let formattedMonth = month < 10 ? `0${month}` : month;
      let formattedDay = day < 10 ? `0${day}` : day;

      let dateStr = `${year}-${formattedMonth}-${formattedDay}`; // 格式化日期为 YYYY-MM-DD
      console.log(dateStr);

      this.request.get(`/api/income/day?date=${dateStr}`).then((res) => {
        if (res.code === "200") {
          console.log(res.data)
          this.hourlyOrders = res.data;
          this.updateHourlyOrdersChart();
        }
      });
    },
    updateHourlyOrdersChart() {
      let hourlyChart = echarts.init(document.getElementById('hourlyOrdersChart'));
      let option = {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: ['0点', '1点', '2点', '3点', '4点', '5点', '6点', '7点', '8点', '9点', '10点', '11点', '12点', '13点', '14点', '15点', '16点', '17点', '18点', '19点', '20点', '21点', '22点', '23点']
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          data: this.hourlyOrders,
          type: 'line',
          smooth: true // 使线条平滑
        }]
      };
      hourlyChart.setOption(option);
    },

    handleClick(tab) {
      switch (tab.name) {
        case "bar":
          this.total = this.totalAll;
          break;
        case "pie":
          this.total = this.totalAll;
          break;
        case "line1":
          this.total = this.totalWeek;
          break;
        case "line2":
          this.total = this.totalMonth;
          break;
        case "salesAdvice":  // 当用户点击销售建议标签时，调用方法
          this.fetchSalesAdvice();
          break;

      }
    },
  },

  mounted() {



    var barChart = echarts.init(document.getElementById("bar"));
    var pieChart = echarts.init(document.getElementById("pie"));
    var lineChart1 = echarts.init(document.getElementById("weekLine"));
    var lineChart2 = echarts.init(document.getElementById("monthLine"));

    var barOption = {
      tooltip: {
        trigger: "item",
      },
      title: {
        text: "收入统计柱状图",
        x: "center",
      },
      label: {
        show: true, //是否显示
        position: "top",
      },
      xAxis: {
        type: "category",
        data: [],
      },
      yAxis: {
        type: "value",
      },
      series: [
        {
          data: [],
          type: "bar",
        },
      ],
    };
    var pieOption = {
      tooltip: {
        trigger: "item",
      },

      title: {
        text: "收入统计饼图",
        x: "center",
      },
      series: [
        {
          type: "pie",
          data: [],
        },
      ],
    };
    var lineOption1 = {
      tooltip: {
        trigger: "item",
      },
      label: {
        show: true, //是否显示
      },
      title: {
        text: "本周收入",
        x: "center",
      },
      xAxis: {
        type: "category",
        data: ["周一", "周二", "周三", "周四", "周五", "周六", "周日"],
      },
      yAxis: {
        type: "value",
      },
      series: [
        {
          data: [],
          type: "line",
        },
      ],
    };
    var lineOption2 = {
      tooltip: {
        trigger: "item",
      },
      label: {
        show: true, //是否显示
      },
      title: {
        text: "本月收入",
        x: "center",
      },
      xAxis: {
        type: "category",
        data: [],
      },
      yAxis: {
        type: "value",
      },
      series: [
        {
          data: [],
          type: "line",
        },
      ],
    };
    //渲染柱状图和饼图

    this.request.get("/api/income/chart").then((res) => {
      if (res.code === "200") {
        let categoryIncomes = res.data.categoryIncomes;
        let categoryNames = categoryIncomes.map((item) => {
          return item.categoryName;
        });
        let incomes = categoryIncomes.map((item) => {
          return item.categoryIncome;
        });
        barOption.xAxis.data = categoryNames;
        barOption.series[0].data = incomes;
        barChart.setOption(barOption);

        for (let i = 0; i < categoryNames.length; i++) {
          let item = { value: incomes[i], name: categoryNames[i] };
          pieOption.series[0].data.push(item);
        }
        pieChart.setOption(pieOption);
        //计算总和
        let sum = 0;
        incomes.forEach((item) => {
          sum += item;
        });
        this.total = sum;
        this.totalAll = sum;
      }
    });
    //渲染本周折线图
    this.request.get("/api/income/week").then((res) => {
      if (res.code === "200") {
        // lineOption1.xAxis.data = res.data.weekDays;
        let weekIncome = res.data.weekIncome;
        lineOption1.series[0].data = weekIncome;
        lineChart1.setOption(lineOption1);
        //计算本周总营收
        let sum = 0;
        weekIncome.forEach((item) => {
          sum += item;
        });
        this.totalWeek = sum;
      }
    });
    //渲染本月折线图
    this.request.get("/api/income/month").then((res) => {
      if (res.code === "200") {
        lineOption2.xAxis.data = res.data.monthDays;
        let monthIncome = res.data.monthIncome;
        lineOption2.series[0].data = monthIncome;
        lineChart2.setOption(lineOption2);
        //计算本月总营收
        let sum = 0;
        monthIncome.forEach((item) => {
          sum += item;
        });
        this.totalMonth = sum;
      }
    });

    this.fetchHourlyOrders(); // 初始化时加载当前日期的小时订单数据

  },
  filters: {
    numFilter(value) {
      // 截取当前数据到小数点后两位

      let realVal = Number(value).toFixed(2);

      // num.toFixed(2)获取的是字符串

      return Number(realVal);
    },
  },
};
</script>

<style scoped>

</style>