
<template>
  <div class="navagation">
    <el-row>
      <el-col :span="2">
        <div style="font-size: 20px; font-weight: bold; text-align: center">
          <a href="/"><i class="el-icon-a-011"></i> 在线商城</a>
        </div>
      </el-col>
      <el-col :span="19">
        <el-menu
          :default-active="activeIndex"
          class="el-menu-demo"
          mode="horizontal"
          router
        >
          <el-menu-item index="/" class="menu-item">商城首页</el-menu-item>
          <el-menu-item index="/recommend" class="menu-item">猜你喜欢</el-menu-item>
<!--          <el-menu-item index="/goodList" class="menu-item">商品分类</el-menu-item>-->

          <el-menu-item index="" class="menu-item" @click="getGoodsList">秒杀活动</el-menu-item>


          <el-menu-item index="/cart" class="menu-item">我的购物车</el-menu-item>
          <el-menu-item index="/orderlist" class="menu-item">我的订单</el-menu-item>
          <el-menu-item index="/manage" class="menu-item" v-if="role === 'admin'">后台管理</el-menu-item>
<!--          <el-menu-item index="/manage" class="menu-item">后台管理</el-menu-item>-->
        </el-menu>
      </el-col>
      <el-col :span="3">
        <!--         右上角个人信息-->
        <el-dropdown style="cursor: pointer; float: right; margin-right: 0px">
          <span class="el-dropdown-link">
            <div style="display: inline-block">
              <img
                v-if="user.avatarUrl != null"
                :src="baseApi + user.avatarUrl"
                class="avatar"
              />
              {{ user.nickname }}
              <i
                class="el-icon-arrow-down el-icon--right"
                style="margin-right: 5px"
              ></i>
            </div>
          </span>
          <!--          下拉菜单-->
          <el-dropdown-menu slot="dropdown" style="text-align: center">
            <el-dropdown-item>
              <!--              传给前端，登录后跳转页面的path为 "/"-->
              <div
                @click="$router.push({ path: '/login', query: { to: '/' } })"
                v-show="!loginStatus"
              >
                登录
              </div>
            </el-dropdown-item>
            <el-dropdown-item v-show="loginStatus">
              <div @click="$router.push('/person')">个人信息</div>
            </el-dropdown-item>
            <el-dropdown-item v-show="loginStatus">
              <div @click="logout">退出</div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-col>
    </el-row>
  </div>
</template>


<script>
export default {
  name: "Navagation",
  props: {
    user: Object,
    loginStatus: Boolean,
    role: String,
  },
  data() {
    return {
      activeIndex: "1",
      activeIndex2: "1",
      baseApi: this.$store.state.baseApi,
    };
  },
  methods: {
    getGoodsList() {
      // // 创建一个包含withCredentials的配置对象
      // const requestOptions = {
      //   withCredentials: true, // 允许跨域请求携带 Cookie
      // };
      //
      // // 在新窗口中打开跨域页面，并携带 Cookie
      // window.open('http://localhost:9191/goods1/toList', '_blank', requestOptions);

      // 使用document.cookie获取当前页面的Cookie字符串
      const cookies = document.cookie;

      // 创建一个包含withCredentials的配置对象
      const requestOptions = {
        withCredentials: true, // 允许跨域请求携带 Cookie
      };

      // 创建一个<a>标签
      const link = document.createElement('a');
      link.href = 'http://localhost:9191/goods1/toList';
      link.target = '_blank'; // 在新选项卡中打开链接
      link.setAttribute('rel', 'noopener'); // 防止新标签页访问父窗口

      // 将withCredentials配置添加到data-request-options属性中
      link.setAttribute('data-request-options', JSON.stringify(requestOptions));

      // 将<a>标签附加到文档中
      document.body.appendChild(link);

      // 模拟点击<a>标签
      link.click();

      // 完成后移除<a>标签
      link.parentNode.removeChild(link);

    },

    logout() {
      localStorage.removeItem("user");
      this.$router.go(0);
      this.$message.success("退出成功");
    },
    getBackendUrl(page) {
      // 在这里拼接后端服务器的地址
      const backendBaseUrl = 'localhost:9191/';
      return backendBaseUrl + page;
    }
  },
};
</script>
<style>
a {
  text-decoration: none;
}
.navagation {
  width: 100%;
  height: 60px;
  line-height: 60px;
  background-color: white;
  overflow: hidden;
}
.avatar {
  width: 45px;
  border-radius: 5px;
  position: relative;
  top: 10px;
  right: 5px;
}
.menu-item {
  padding-left: 50px;
  padding-right: 50px;
}
</style>