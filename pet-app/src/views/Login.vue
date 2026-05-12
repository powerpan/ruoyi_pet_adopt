<!--
  Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
-->
<template>
  <!-- 登录页面主容器 -->
  <div class="login">
    <!-- 登录表单，绑定表单数据和校验规则 -->
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <!-- 系统标题 -->
      <h3 class="title iconfont">宠物领养平台</h3>

      <!-- 用户名表单项 -->
      <el-form-item prop="username">
        <!-- 输入用户名，使用v-model双向绑定，禁止自动填充，提示为“账号” -->
        <el-input
            v-model="loginForm.username"
            type="text"
            auto-complete="off"
            placeholder="账号"
        >
          <!-- 用户名输入框前置图标 -->
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon"/>
        </el-input>
      </el-form-item>

      <!-- 密码表单项 -->
      <el-form-item prop="password">
        <!-- 输入密码，v-model绑定，禁止自动填充，按回车触发登录方法，提示为“密码” -->
        <el-input
            v-model="loginForm.password"
            type="password"
            auto-complete="off"
            placeholder="密码"
            @keyup.enter.native="handleLogin"
        >
          <!-- 密码输入框前置图标 -->
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon"/>
        </el-input>
      </el-form-item>

      <!-- 验证码表单项，根据captchaEnabled决定是否显示 -->
      <el-form-item prop="code" v-if="captchaEnabled">
        <!-- 输入验证码，v-model绑定，回车登录，宽度设置为63%，提示为“验证码” -->
        <el-input
            v-model="loginForm.code"
            auto-complete="off"
            placeholder="验证码"
            style="width: 63%"
            @keyup.enter.native="handleLogin"
        >
          <!-- 验证码输入框前置图标 -->
          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon"/>
        </el-input>
        <!-- 验证码图片，点击刷新验证码 -->
        <div class="login-code">
          <img v-if="codeUrl" :src="codeUrl" @click="getCode" class="login-code-img" alt="验证码"/>
        </div>
      </el-form-item>

      <!-- 记住密码复选框 -->
      <el-checkbox v-model="loginForm.rememberMe" class="remember-me">记住密码</el-checkbox>

      <!-- 登录按钮和注册链接 -->
      <el-form-item style="width:100%;">
        <!-- 登录按钮，点击登录方法，loading状态禁用 -->
        <el-button
            :loading="loading"
            size="medium"
            type="primary"
            style="width:100%;"
            @click.native.prevent="handleLogin"
        >
          <!-- 按钮文本，未加载显示“登录”，加载中显示“登录中...” -->
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
        <!-- 注册链接，根据register开关显示 -->
        <div style="float: right;" v-if="register">
          <router-link class="link-type" :to="'/register'">立即注册</router-link>
        </div>
      </el-form-item>

    </el-form>
    <!-- 底部版权信息 -->
    <div class="el-login-footer">
      <!-- 登录页版权入口保持和主站页脚一致：https://github.com/powerpan/ruoyi_pet_adopt.git -->
      <AppLegalFooter theme="dark" compact />
    </div>
  </div>
</template>

<script>
import {getCodeImg} from "@/api/login";
import Cookies from "js-cookie";
import {encrypt, decrypt} from '@/utils/jsencrypt'
import AppLegalFooter from '@/components/AppLegalFooter'

export default {
  // 组件名称，方便调试和识别
  name: "Login",
  components: { AppLegalFooter },
  // 定义组件的数据源
  data() {
    return {
      // 存储验证码图片的 URL
      codeUrl: "",
      // 登录表单的数据对象
      loginForm: {
        // 用户名，默认不预填演示账号
        username: "",
        // 密码，默认不预填演示密码
        password: "",
        // 记住密码的复选框初始值，默认不记住
        rememberMe: false,
        // 验证码输入值
        code: "",
        // 验证码的唯一标识，用于服务端校验
        uuid: ""
      },
      // 登录表单的校验规则
      loginRules: {
        // 用户名校验规则：必填，失去焦点时触发
        username: [
          {required: true, trigger: "blur", message: "请输入您的账号"}
        ],
        // 密码校验规则：必填，失去焦点时触发
        password: [
          {required: true, trigger: "blur", message: "请输入您的密码"}
        ],
        // 验证码校验规则：必填，内容变更时触发
        code: [{required: true, trigger: "change", message: "请输入验证码"}]
      },
      // 登录按钮 loading 状态标记，防止重复提交
      loading: false,
      // 验证码功能开关（true：开启，false：关闭）
      captchaEnabled: true,
      // 注册功能开关（true：显示注册链接，false：不显示）
      register: true,
      // 路由重定向参数，登录成功后跳转目标
      redirect: undefined
    };
  },
  // 监听器，监控 $route 路由变化
  watch: {
    $route: {
      // 当路由发生变化时执行 handler 方法
      handler: function (route) {
        // 读取路由中的 redirect 参数，用于登录后重定向
        this.redirect = route.query && route.query.redirect;
      },
      // 立即执行，初始化时触发一次
      immediate: true
    }
  },
  // 组件生命周期钩子：created，在实例创建后自动调用
  created() {
    // 加载验证码图片
    this.getCode();
    // 从 Cookie 中读取已保存的用户名和密码
    this.getCookie();
  },
  methods: {
    // 获取验证码图片的方法
    getCode() {
      getCodeImg().then(res => {
        if (res.captchaEnabled === false) {
          this.captchaEnabled = false;
          this.codeUrl = "";
          this.loginForm.uuid = "";
          return;
        }
        this.captchaEnabled = true;
        if (res.img) {
          this.codeUrl = "data:image/jpeg;base64," + res.img;
          this.loginForm.uuid = res.uuid || "";
        } else {
          this.codeUrl = "";
          this.loginForm.uuid = res.uuid || "";
          this.$message.warning("验证码图片未返回，请点击图片区域刷新");
        }
      }).catch((err) => {
        this.codeUrl = "";
        this.$message.error(
          (err && err.message) || "获取验证码失败，请检查网络或 Nginx /prod-api 反代后重试"
        );
      });
    },
    // 从 Cookie 中读取已保存的账号信息，初始化表单
    getCookie() {
      // 从 Cookie 中获取用户名
      const username = Cookies.get("username");
      // 从 Cookie 中获取加密后的密码
      const password = Cookies.get("password");
      // 从 Cookie 中获取记住密码勾选状态
      const rememberMe = Cookies.get('rememberMe')
      // 设置登录表单的数据
      this.loginForm = {
        // 若 Cookie 未保存用户名，则使用当前表单中的用户名
        username: username === undefined ? this.loginForm.username : username,
        // 若 Cookie 未保存密码，则使用当前表单中的密码，否则需要解密
        password: password === undefined ? this.loginForm.password : decrypt(password),
        // 若 Cookie 未保存记住密码状态，默认不记住，否则将其转为布尔值
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
        code: this.loginForm.code || "",
        uuid: this.loginForm.uuid || ""
      };
    },
    // 登录按钮点击事件处理方法
    handleLogin() {
      // 调用 loginForm 表单的校验方法
      this.$refs.loginForm.validate(valid => {
        // 如果表单验证通过
        if (valid) {
          // 显示加载中状态，防止重复提交
          this.loading = true;
          // 如果选择了记住密码
          if (this.loginForm.rememberMe) {
            // 将用户名、加密后的密码和记住密码状态存入 Cookie，保存 30 天
            Cookies.set("username", this.loginForm.username, {expires: 30});
            Cookies.set("password", encrypt(this.loginForm.password), {expires: 30});
            Cookies.set('rememberMe', this.loginForm.rememberMe, {expires: 30});
          } else {
            // 如果未选择记住密码，则移除相关的 Cookie 信息
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          // 发起登录请求（调用 Vuex 的 Login action）
          this.$store.dispatch("Login", this.loginForm).then(() => {
            // 先拉取用户信息再进首页，避免 Header 仅有 token、头像/昵称为空
            return this.$store.dispatch("GetInfo");
          }).then(() => {
            this.$router.push(this.redirect || "/").catch(() => {});
          }).catch(() => {
            // 登录失败，关闭加载中状态
            this.loading = false;
            // 如果已开启验证码，则需要刷新验证码图片
            if (this.captchaEnabled) {
              this.getCode();
            }
          });
        }
      });
    }
  }
};
</script>


<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;
  /* 父链正常时用 100%；H5/WebView 下父高度偶发未撑满时用视口兜底 */
  min-height: 100%;
  min-height: 100vh;
  height: 100%;
  padding: 32px 16px;
  background-image:
    linear-gradient(135deg, rgba(23, 33, 31, .64), rgba(35, 130, 118, .28)),
    url("../assets/img/login-background.jpg");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.title {
  font-size: 24px;
  margin: 0 auto 34px;
  text-align: center;
  color: var(--pet-text, #17211f);
  font-weight: 800;
}

.login-form {
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.92);
  width: 440px;
  padding: 34px 34px 18px;
  border: 1px solid rgba(255, 255, 255, .72);
  box-shadow: 0 20px 50px rgba(23, 33, 31, .18);
  backdrop-filter: blur(12px);

  .el-input {
    height: 48px;

    input {
      height: 48px;
    }
  }
  .remember-me {
    font-size: 14px;
    margin: 0 0 28px;
    color: var(--pet-text-muted, #63716d);
  }
  .input-icon {
    height: 49px;
    width: 18px;
    margin-left: 4px;
  }

  .el-button {
    height: 48px;
    font-size: 16px;
    letter-spacing: 0;
  }
}

.login-tip {
  font-size: 14px;
  text-align: center;
  color: #bfbfbf;
}

.login-code {
  width: 33%;
  height: 48px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-login-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  padding: 0 18px 14px;
  text-align: center;
  color: #fff;
  letter-spacing: 0;
  box-sizing: border-box;
}

.login-code-img {
  height: 48px;
}
</style>
