  <template>
    <div>
      <div class="main-box">
        <div style="margin: 20px auto">
          <h2 style="color: #2b669a;font-size: 24px;">
            <i class="el-icon-star-on"  ></i>
            下面是您可能喜欢的商品
          </h2>

          <hr />
          <el-row :gutter="20">
            <el-col
                :span="6"
                v-for="good in good"
                :key="good.id"
                style="margin-bottom: 20px"
            >
              <!--            商品格子-->
              <router-link :to="'goodview/' + good.id">
                <el-card :body-style="{ padding: '0px', background: '#e3f5f4' }">
                  <img
                      :src="baseApi + good.imgs"
                      style="width: 100%; height: 300px"
                  />
                  <div style="padding: 5px 10px">
                    <span style="font-size: 18px">{{ good.name }}</span
                    ><br />
<!--                    <span style="color: red; font-size: 15px"-->
<!--                    >￥{{ good.price }}</span-->
<!--                    >-->
                  </div>
                </el-card>
              </router-link>
            </el-col>
          </el-row>
        </div>
        <!--      分页-->
        <div class="block" style="text-align: center">
          <el-pagination
              background
              :hide-on-single-page="false"
              :current-page="currentPage"
              :page-size="pageSize"
              layout="total, prev, pager, next"
              :total="total"
              @current-change="handleCurrentPage"
          >
          </el-pagination>
        </div>
      </div>
    </div>
  </template>

  <script>
  import search from "../../../components/Search";
  import API from "@/utils/request";
  export default {
    name: "GoodList",
    data() {
      return {
        //分类icon，每个icon包含id、value、categories对象数组.category：id，name
        icons: [],
        total: 0,
        pageSize: 9,
        currentPage: 1,
        //选择的分类
        categoryId: Number,
        //搜索的内容
        searchText: "",
        good: [],
        baseApi: this.$store.state.baseApi,
      };
    },
    components: {
      search,
    },
    created() {
      //二者一般不同时存在
      this.searchText = this.$route.query.searchText;
      this.categoryId = this.$route.query.categoryId;

      // this.loadCategories();
      this.load();
    },
    methods: {
      loadCategories() {
        this.request.get("/api/icon").then((res) => {
          if (res.code === "200") {
            this.icons = res.data;
          }
        });
      },
      handleCurrentPage(currentPage) {
        this.currentPage = currentPage;
        this.load();
      },
      handleSearch(text) {
        this.searchText = text;
        this.load();
      },
      load() {

        // 假设 userId 可以从 Vuex、localStorage 或 Vue Router 中获取
        var userId = 0; // 仅作为示例，请根据实际情况替换

        // 从localStorage中获取用户信息字符串
        var userStr = localStorage.getItem("user");

  // 将字符串转换为JavaScript对象
        var user = JSON.parse(userStr);
        // 替换原有的 API 路径和参数
        this.request.get(`/recommend/user/${user.id}`, { withCredentials: true })

            .then((res) => {
              console.log(res)
              if (res.code === "200") {
                // 假设后端返回的数据结构是 { code: "200", data: [商品列表] }
                // 你可能需要根据实际返回的数据结构进行调整
                console.log(res)
                this.good = res.data;
                // 注意：如果推荐接口不支持分页，则可能需要移除或调整分页逻辑
              }
            });
      },
  //     load() {
  //       // 从localStorage中获取用户信息字符串
  //       var userStr = localStorage.getItem("user");
  //       const user = JSON.parse(userStr); // 将字符串转换为JavaScript对象
  //
  //       // 调整API请求路径以匹配新的分页推荐接口，并传递分页参数
  //       API.get(`/recommend/page/${user.id}?page=${this.currentPage-1}&size=${this.pageSize-1}`, { withCredentials: true })
  //           .then((response) => {
  //             const res = response.data;
  //             if (res.code === "200") {
  //               this.good = res.data.content; // 使用content数组更新商品列表
  //               this.total = res.data.totalElements; // 使用totalElements更新总商品数
  //               // 更新当前页码（如果需要）和页面大小
  //               this.currentPage = res.data.number + 1; // 后端分页可能从0开始，而前端从1开始
  //               this.pageSize = res.data.size;
  //             }
  //           })
  //           .catch((error) => {
  //             console.error("请求推荐商品失败：", error);
  //           });
  //     },


    },

  };
  </script>

  <style scoped>
  .main-box {
    background-color: white;
    border: white 2px solid;
    border-radius: 40px;
    padding: 20px 40px;
    margin: 5px auto;
  }

  .black {
    color: black;
  }

  .grey {
    color: grey;
  }
  </style>