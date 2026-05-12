<template>
  <el-form ref="form" :model="form" :rules="rules" label-width="96px" class="profile-form">
    <el-form-item label="用户昵称" prop="nickName">
      <el-input v-model="form.nickName" maxlength="30" class="profile-input" />
    </el-form-item>
    <el-form-item label="手机号码" prop="phonenumber">
      <el-input v-model="form.phonenumber" maxlength="11" class="profile-input" />
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="form.email" maxlength="50" class="profile-input" />
    </el-form-item>
    <el-form-item label="性别">
      <el-radio-group v-model="form.sex">
        <el-radio label="0">男</el-radio>
        <el-radio label="1">女</el-radio>
      </el-radio-group>
    </el-form-item>
    <!-- 操作按钮项，包括“保存”和“关闭”按钮，分别绑定submit和close方法 -->
    <el-form-item class="form-actions">
      <el-button type="primary" @click="submit">保存</el-button>
      <el-button class="btn-secondary" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
// 引入用户接口的更新方法
import { updateUserProfile } from "@/api/user";

// 导出 Vue 组件对象
export default {
  // 定义传入的props属性
  props: {
    user: {
      // user属性类型为Object
      type: Object
    }
  },
  // 组件内部数据定义
  data() {
    return {
      // 用于绑定表单数据的对象
      form: {},
      // 表单校验规则
      rules: {
        // 用户昵称校验规则
        nickName: [
          { required: true, message: "用户昵称不能为空", trigger: "blur" }
        ],
        // 邮箱校验规则
        email: [
          { required: true, message: "邮箱地址不能为空", trigger: "blur" },
          {
            type: "email",
            message: "请输入正确的邮箱地址",
            trigger: ["blur", "change"]
          }
        ],
        // 手机号校验规则
        phonenumber: [
          { required: true, message: "手机号码不能为空", trigger: "blur" },
          {
            // 判断手机号格式的正则表达式
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: "请输入正确的手机号码",
            trigger: "blur"
          }
        ]
      }
    };
  },
  // 监听user属性变化，自动更新表单数据
  watch: {
    user: {
      // handler函数，user发生变化时触发
      handler(user) {
        if (user) {
          // 将user中的相关属性同步到form表单
          this.form = { 
            nickName: user.nickName, 
            phonenumber: user.phonenumber, 
            email: user.email, 
            sex: user.sex 
          };
        }
      },
      // 组件初始时立即执行handler
      immediate: true
    }
  },
  // 定义组件方法
  methods: {
    // 提交表单方法
    submit() {
      // 触发表单校验
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 校验通过后，提交更新用户信息的api请求
          updateUserProfile(this.form).then(response => {
            // 操作成功弹窗提示
            this.$modal.msgSuccess("修改成功");
            // 同步更新user对象中的手机号和邮箱
            this.user.phonenumber = this.form.phonenumber;
            this.user.email = this.form.email;
          });
        }
      });
    },
    // 关闭表单/页面方法
    close() {
      // 调用$tab.closePage关闭当前tab页面
      this.$tab.closePage();
    }
  }
};
</script>

<style lang="scss" scoped>
.profile-form {
  padding: 8px 4px 4px;
  max-width: 520px;

  ::v-deep .el-form-item {
    margin-bottom: 20px;
  }

  ::v-deep .el-form-item__label {
    color: #3a4b63;
    font-weight: 500;
    font-size: 14px;
    line-height: 40px;
    padding-bottom: 0;
  }

  ::v-deep .profile-input .el-input__inner {
    height: 40px;
    line-height: 40px;
    font-size: 14px;
  }

  ::v-deep .el-input__inner,
  ::v-deep .el-textarea__inner {
    border-radius: 10px;
    border: 1px solid #dbe3ef;
    background: #fbfdff;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;
  }

  ::v-deep .el-input__inner:focus,
  ::v-deep .el-textarea__inner:focus {
    border-color: #2f75ff;
    box-shadow: 0 0 0 3px rgba(47, 117, 255, 0.12);
    background: #fff;
  }

  ::v-deep .el-radio__label {
    color: #4a5568;
    font-size: 14px;
  }

  ::v-deep .el-radio__input.is-checked .el-radio__inner {
    border-color: #2f75ff;
    background: #2f75ff;
  }

  ::v-deep .el-radio__input.is-checked + .el-radio__label {
    color: #2f75ff;
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
