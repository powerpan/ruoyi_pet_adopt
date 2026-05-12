<!--
  Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
-->
<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <img class="register-logo" src="../assets/img/logo.png" alt="logo" />
        <div class="register-title-wrap">
          <h1 class="register-title">宠物领养平台</h1>
          <p class="register-subtitle">创建您的账号</p>
        </div>
      </div>

      <div class="register-body">
        <p class="field-label">账号</p>
        <el-input
          v-model="username"
          class="register-input"
          placeholder="请输入账号（不少于 6 位）"
          clearable
        />
        <p class="field-label">密码</p>
        <el-input
          v-model="password"
          class="register-input"
          placeholder="请输入密码"
          show-password
        />
        <p class="field-label">确认密码</p>
        <el-input
          v-model="checkPassword"
          class="register-input"
          placeholder="请再次输入密码"
          show-password
        />

        <template v-if="captchaEnabled">
          <p class="field-label">验证码</p>
          <div class="register-captcha-row">
            <el-input
              v-model="code"
              class="register-input register-captcha-input"
              placeholder="请输入验证码"
              auto-complete="off"
              clearable
            >
              <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
            </el-input>
            <div class="register-captcha-img-wrap" @click="getCode">
              <img v-if="codeUrl" :src="codeUrl" class="register-captcha-img" alt="验证码" />
            </div>
          </div>
        </template>

        <div class="agree-row">
          <el-checkbox v-model="agree">
            我已阅读并同意「宠物领养平台」用户注册协议
          </el-checkbox>
        </div>

        <el-button
          type="primary"
          class="register-btn"
          :loading="submitting"
          @click="postLogin"
        >
          注 册
        </el-button>

        <div class="register-footer">
          <router-link class="register-link" to="/login">
            已有账号？去登录
            <i class="el-icon-right"></i>
          </router-link>
        </div>
      </div>
    </div>

    <div class="register-page-footer">
      <!-- 注册页底部版权信息指向项目仓库：https://github.com/powerpan/ruoyi_pet_adopt.git -->
      <AppLegalFooter theme="dark" compact />
    </div>
  </div>
</template>

<script>
import { register, getCodeImg } from "@/api/login";
import AppLegalFooter from "@/components/AppLegalFooter";

export default {
  name: "Register",
  components: { AppLegalFooter },
  data() {
    return {
      username: "",
      password: "",
      checkPassword: "",
      agree: false,
      submitting: false,
      /** 与登录页一致：后端 RegisterBody 需要 code + uuid，否则验证码校验会报「已失效」 */
      code: "",
      uuid: "",
      codeUrl: "",
      captchaEnabled: true,
    };
  },
  created() {
    this.getCode();
  },
  methods: {
    getCode() {
      getCodeImg().then((res) => {
        if (res.captchaEnabled === false) {
          this.captchaEnabled = false;
          this.codeUrl = "";
          this.uuid = "";
          return;
        }
        this.captchaEnabled = true;
        if (res.img) {
          this.codeUrl = "data:image/jpeg;base64," + res.img;
          this.uuid = res.uuid || "";
        } else {
          this.codeUrl = "";
          this.uuid = res.uuid || "";
          this.$message.warning("验证码图片未返回，请点击刷新");
        }
      }).catch((err) => {
        this.codeUrl = "";
        this.$message.error(
          (err && err.message) || "获取验证码失败，请检查网络或 Nginx /prod-api 反代后重试"
        );
      });
    },
    postLogin() {
      if (this.username.length < 6) {
        this.$message.warning("请输入不少于 6 位的用户名");
        return;
      }
      if (!this.agree) {
        this.$message.warning('请勾选「我已同意用户注册协议」');
        return;
      }
      if (this.password !== this.checkPassword) {
        this.$message.warning("两次输入的密码不一致");
        return;
      }
      if (this.captchaEnabled && !this.code) {
        this.$message.warning("请输入验证码");
        return;
      }
      const payload = {
        username: this.username,
        password: this.password,
        code: this.captchaEnabled ? this.code : undefined,
        uuid: this.captchaEnabled ? this.uuid : undefined,
      };
      this.submitting = true;
      localStorage.setItem("token", "");
      register(payload)
        .then((res) => {
          if (res && res.code === 200) {
            this.$message.success(res.msg || "注册成功，请登录");
            this.$router.push("/login");
          }
        })
        .catch(() => {
          this.getCode();
          this.code = "";
        })
        .finally(() => {
          this.submitting = false;
        });
    },
  },
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100%;
  padding: 48px 16px 72px;
  box-sizing: border-box;
  background-image:
    linear-gradient(135deg, rgba(23, 33, 31, .64), rgba(35, 130, 118, .28)),
    url("../assets/img/login-background.jpg");
  background-size: cover;
  background-position: center;
  position: relative;
}

.register-page::before {
  content: "";
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0);
  pointer-events: none;
}

.register-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 440px;
  padding: 36px 36px 32px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 20px 50px rgba(23, 33, 31, .18);
  border: 1px solid rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(12px);
}

.register-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 28px;
  padding-bottom: 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.register-logo {
  width: 54px;
  height: 54px;
  object-fit: contain;
  flex-shrink: 0;
}

.register-title-wrap {
  text-align: left;
}

.register-title {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  color: var(--pet-text);
  letter-spacing: 0;
}

.register-subtitle {
  margin: 8px 0 0;
  font-size: 14px;
  color: var(--pet-text-muted);
  letter-spacing: 0;
}

.register-body {
  letter-spacing: 0;
}

.field-label {
  margin: 0 0 8px;
  font-size: 13px;
  font-weight: 600;
  color: #44524e;
}

.field-label:not(:first-of-type) {
  margin-top: 18px;
}

.register-input {
  width: 100%;
}

.register-input ::v-deep .el-input__inner {
  height: 46px;
  line-height: 46px;
  border-radius: 8px;
}

.register-captcha-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: nowrap;
}

.register-captcha-input {
  flex: 1;
  min-width: 0;
}

.register-captcha-img-wrap {
  flex-shrink: 0;
  width: 112px;
  height: 46px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  background: #f5f7fa;
}

.register-captcha-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.input-icon {
  width: 18px;
  height: 18px;
  margin-left: 2px;
}

.agree-row {
  margin-top: 20px;
  padding-top: 4px;
}

.agree-row ::v-deep .el-checkbox {
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
  white-space: normal;
}

.agree-row ::v-deep .el-checkbox__label {
  line-height: 1.5;
}

.register-btn {
  width: 100%;
  margin-top: 24px;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0;
  border-radius: 8px;
  border: none;
  box-shadow: 0 10px 24px rgba(35, 130, 118, .22);
}

.register-footer {
  margin-top: 28px;
  text-align: center;
}

.register-link {
  font-size: 14px;
  font-weight: 500;
  color: var(--pet-primary);
  text-decoration: none;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.85;
  }

  .el-icon-right {
    margin-left: 4px;
    font-size: 13px;
  }
}

.register-page-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 0 18px 14px;
  text-align: center;
  color: rgba(255, 255, 255, 0.92);
  letter-spacing: 0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  z-index: 2;
}
</style>
