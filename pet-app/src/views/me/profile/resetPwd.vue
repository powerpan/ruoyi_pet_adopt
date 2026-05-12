<template>
  <!-- el-form 表单，使用 ref 在方法中引用，model 绑定 user 对象，rules 绑定表单校验规则，设置标签宽度 -->
  <el-form ref="form" :model="user" :rules="rules" label-width="80px" class="profile-form">
    <!-- 旧密码输入项，label 显示“旧密码”，prop 用于校验规则 -->
    <el-form-item label="旧密码" prop="oldPassword">
      <!-- el-input 控件，v-model 绑定 user.oldPassword，设置为密码输入框，提供显示密码功能 -->
      <el-input v-model="user.oldPassword" placeholder="请输入旧密码" type="password" show-password/>
    </el-form-item>
    <!-- 新密码输入项，label 显示“新密码”，prop 用于校验规则 -->
    <el-form-item label="新密码" prop="newPassword">
      <!-- el-input 控件，v-model 绑定 user.newPassword，设置为密码输入框，提供显示密码功能 -->
      <el-input v-model="user.newPassword" placeholder="请输入新密码" type="password" show-password/>
    </el-form-item>
    <!-- 确认密码输入项，label 显示“确认密码”，prop 用于校验规则 -->
    <el-form-item label="确认密码" prop="confirmPassword">
      <!-- el-input 控件，v-model 绑定 user.confirmPassword，设置为密码输入框，提供显示密码功能 -->
      <el-input v-model="user.confirmPassword" placeholder="请确认新密码" type="password" show-password/>
    </el-form-item>
    <!-- 表单底部按钮操作项 -->
    <el-form-item class="form-actions">
      <!-- 保存按钮，点击时触发 submit 方法 -->
      <el-button type="primary" @click="submit">保存</el-button>
      <!-- 关闭按钮，点击时触发 close 方法 -->
      <el-button class="btn-secondary" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
// 引入 updateUserPwd 方法，用于调用修改密码的 API
import { updateUserPwd } from "@/api/user";

export default {
  data() {
    // 定义自定义校验方法：确认密码必须和新密码一致
    const equalToPassword = (rule, value, callback) => {
      // 判断新密码与确认密码是否一致，若不一致回调错误
      if (this.user.newPassword !== value) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        // 一致则通过校验
        callback();
      }
    };
    return {
      // user 对象用于绑定表单数据
      user: {
        oldPassword: undefined,      // 旧密码
        newPassword: undefined,      // 新密码
        confirmPassword: undefined   // 确认密码
      },
      // 表单校验规则
      rules: {
        // 旧密码校验：必填
        oldPassword: [
          { required: true, message: "旧密码不能为空", trigger: "blur" }
        ],
        // 新密码校验：必填，长度限制，不能包含非法字符
        newPassword: [
          { required: true, message: "新密码不能为空", trigger: "blur" },
          { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" },
          { pattern: /^[^<>"'|\\]+$/, message: "不能包含非法字符：< > \" ' \\\ |", trigger: "blur" }
        ],
        // 确认密码校验：必填，值需与新密码相同
        confirmPassword: [
          { required: true, message: "确认密码不能为空", trigger: "blur" },
          { required: true, validator: equalToPassword, trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    // 提交表单方法
    submit() {
      // 调用 form 表单的校验方法
      this.$refs["form"].validate(valid => {
        // 校验通过 valid 为 true
        if (valid) {
          // 调用修改密码接口，传递旧密码和新密码
          updateUserPwd(this.user.oldPassword, this.user.newPassword).then(response => {
            // 修改成功后弹出成功消息
            this.$modal.msgSuccess("修改成功");
          });
        }
      });
    },
    // 关闭按钮点击方法
    close() {
      // 关闭当前分页
      this.$tab.closePage();
    }
  }
};
</script>

<style lang="scss" scoped>
.profile-form {
  padding: 4px 2px;

  ::v-deep .el-form-item {
    margin-bottom: 16px;
  }

  ::v-deep .el-form-item__label {
    color: #3a4b63;
    font-weight: 500;
    padding-bottom: 6px;
  }

  ::v-deep .el-input__inner {
    border-radius: 10px;
    border: 1px solid #dbe3ef;
    background: #fbfdff;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;
  }

  ::v-deep .el-input__inner:focus {
    border-color: #2f75ff;
    box-shadow: 0 0 0 3px rgba(47, 117, 255, 0.12);
    background: #fff;
  }
}

.form-actions {
  margin-top: 8px;
  margin-bottom: 4px;
}

.form-actions .el-button {
  min-width: 96px;
  height: 38px;
  border-radius: 10px;
  font-weight: 500;
}

.form-actions .btn-secondary {
  margin-left: 10px;
  color: #3a4b63;
  background: #f2f6fc;
  border-color: #d8e1ed;
}

.form-actions .btn-secondary:hover {
  color: #2f75ff;
  border-color: #9fc0ff;
  background: #eef4ff;
}
</style>
