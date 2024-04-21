
<template>
    <div id="bk" class="wrapper">
        <div class="login-box">
            <div class="title">
                <img
                    src="@/resource/03.png"
                    style="
                        width: 30px;
                        height: 30px;
                        margin: 0 5px -5px 0;
                        -webkit-user-drag: none;
                        -khtml-user-drag: none;
                        -moz-user-drag: none;
                        user-drag: none;
                    "
                />
                <b style="font-size: 28px"> 登录在线商城 </b>
            </div>
            <div style="margin-top: 30px">
                <el-form label-width="70px">
                    <el-form-item label="用户名">
                        <el-input
                            v-model.trim="user.username"
                            aria-required="true"
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="密码" style="margin-top: 25px">
                        <el-input
                            v-model.trim="user.password"
                            show-password
                            aria-required="true"
                        ></el-input>
                    </el-form-item>
                    <el-form-item style="text-align: center;">
                        <el-button
                            type="success"
                            @click="onSubmit"
                            style="font-size: 22px"
                        >
                            <i
                                class="iconfont icon-r-yes"
                                style="font-size: 22px"
                            ></i
                            >登录</el-button
                        >
                        <el-button
                            @click="$router.push('/register')"
                            style="font-size: 22px"
                            >
                            <i
                                class="iconfont icon-r-add"
                                style="font-size: 22px"
                            ></i
                            >注册</el-button
                        >
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script>
import md5 from "js-md5";

export default {
    name: "Login",
    data() {
        return {
            to: "/", //登陆成功跳转的页面
            user: {},
        };
    },
    created() {
        this.to = this.$route.query.to ? this.$route.query.to : "/";
    },
    methods: {
      onSubmit() {
        if (this.user.username === "" || this.user.password === "") {
          this.$message.error("账号或密码不能为空");
          return false;
        }
        let form = {};
        Object.assign(form, this.user);
        form.password = md5(this.user.password);

        // 创建一个包含withCredentials的配置对象
        const requestOptions = {
          withCredentials: true, // 允许跨域请求携带 Cookie
        };

        this.request.post("/login", form, requestOptions).then((res) => {
          if (res.code === "200") {
            this.$message.success({
              message: "登陆成功",
              showClose: true,
            });
            this.$router.push(this.to);
            localStorage.setItem("user", JSON.stringify(res.data));
          } else {
            this.$message.error(res.msg);
          }
        });
      }

      // onSubmit() {
        //     if (this.user.username === "" || this.user.password === "") {
        //         this.$message.error("账号或密码不能为空");
        //         return false;
        //     }
        //     let form = {};
        //     Object.assign(form, this.user);
        //     form.password = md5(this.user.password);
        //     this.request.post("/login", form).then((res) => {
        //         if (res.code === "200") {
        //             this.$message.success({
        //                 message: "登陆成功",
        //                 showClose: true,
        //             });
        //             this.$router.push(this.to);
        //             localStorage.setItem("user", JSON.stringify(res.data));
        //         } else {
        //             this.$message.error(res.msg);
        //         }
        //     });
        // },
    },
};
</script>

<style scoped>
.wrapper {
    height: 100vh;
    background-color: rgb(161, 202, 220);
    overflow: auto;
}
.login-box {
    margin: 220px auto;
    padding: 40px;
    width: 450px;
    height: 280px;
    background-color: #ffffff;
    border-radius: 10px;
}
.title {
    text-align: center;
    margin: 30px auto;
    font-size: 25px;
}

#bk {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    overflow-y: auto;
    height: 100%;
    background: url("../resource/01.jpg") center top / cover no-repeat;
}
</style>