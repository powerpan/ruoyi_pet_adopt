<template>
  <div>
    <!-- 标题：基本设置，调整字体间距、粗细和底部内边距 -->
    <h3 style="letter-spacing: 1px;font-weight: 400;padding-bottom: 20px">基本设置</h3>

    <div>
      <!-- 用户基本信息编辑表单，宽350px，顶部标签，左右布局 -->
      <el-form style="width: 350px;float: left" label-position="top" ref="form" :model="user" label-width="80px">
        <!-- 昵称输入项 -->
        <el-form-item style="padding: 0" label="昵称">
          <!-- 绑定用户昵称字段 -->
          <el-input v-model="user.nickname"></el-input>
        </el-form-item>
        <!-- 密码输入项 -->
        <el-form-item style="padding: 0" label="密码">
          <!-- 密码类型的输入框，绑定用户密码字段 -->
          <el-input type="password" v-model="user.password"></el-input>
        </el-form-item>
        <!-- 性别选择项 -->
        <el-form-item label="性别">
          <!-- 单选框组，绑定用户性别 -->
          <el-radio-group v-model="user.gender">
            <!-- 选项：男生 -->
            <el-radio label="男生"></el-radio>
            <!-- 选项：女生 -->
            <el-radio label="女生"></el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 出生年月选择项 -->
        <el-form-item label="出生年月">
          <el-col>
            <!-- 日期选择器，选择用户出生日期，格式为yyyy-MM-dd -->
            <el-date-picker
                type="date" placeholder="选择日期"
                v-model="user.birthday"
                value-format="yyyy-MM-dd"
                style="width: 100%;"/>
          </el-col>
        </el-form-item>
        <!-- 邮箱输入项 -->
        <el-form-item style="padding: 0" label="邮箱">
          <!-- 邮箱类型输入框，绑定用户邮箱 -->
          <el-input type="email" v-model="user.email"></el-input>
        </el-form-item>
        <!-- 个人简介输入项 -->
        <el-form-item label="个人简介">
          <!-- 多行文本输入框，行数为5，绑定用户简介 -->
          <el-input :rows="5" type="textarea" v-model="user.info"></el-input>
        </el-form-item>
        <!-- 提交按钮，顶部填充20px -->
        <el-form-item style="padding-top: 20px">
          <!-- 主按钮，点击触发onSubmit方法，提交基本信息 -->
          <el-button type="primary" @click="onSubmit">更新基本信息</el-button>
        </el-form-item>
      </el-form>
      <div>
        <!-- 显示用户头像，固定宽高150px，左侧内边距150px，底部内边距10px -->
        <img style="padding-bottom: 10px;padding-left: 150px;width: 150px; height: 150px;" alt=""
             :src="user.avatar">
        <!-- 头像上传组件，支持jpg/png，仅允许上传一个文件，附带认证header和上传API地址 -->
        <el-upload
            style="padding-left: 500px;letter-spacing: 1px"
            class="upload-demo"
            accept=".png,.jpg"
            :headers="header"
            :action="uploadAction"
            :on-success="handleUploadSuccess"
            multiple
            :limit="1">
          <!-- 上传按钮，宽150px，小号主按钮，内含上传icon和文字 -->
          <el-button style="width: 150px" size="small" type="primary">
            <i class="el-icon-upload2"></i> 点击上传
          </el-button>
          <!-- 上传文件类型提示，仅限于jpg/png格式 -->
          <div slot="tip" class="el-upload__tip">只能上传jpg/png文件</div>
        </el-upload>
      </div>
    </div>
  </div>
</template>

<script>
import {findById, updateUser} from "@/api/user";
import config from "@/config";

export default {

  // data 选项用于定义组件的响应式数据
  data() {
    return {
      // header对象用于存放请求头，主要是用于上传组件的身份验证，这里从本地存储获取token
      header: {
        "Authorization": localStorage.getItem("token")
      },
      // uploadAction存储头像上传接口的完整URL，从配置文件中读取
      uploadAction: config.API_URL + '/upload',
      // user对象包含用户的所有信息字段，初始值均为空
      user: {
        nickname: '', // 用户昵称
        password: '', // 用户密码
        email: '',    // 用户邮箱
        birthday: '', // 用户生日
        gender: '',   // 用户性别
        info: '',     // 用户个人简介
        avatar: ''    // 用户头像地址
      }
    }
  },

  // 生命周期钩子，当组件挂载后自动调用
  mounted() {
    // 检查本地存储是否存在uid，即用户已登录
    if (localStorage.getItem("uid") !== null) {
      // 通过API调用findById方法，获取当前用户的信息
      findById(localStorage.getItem("uid")).then(res => {
        // 将请求返回的数据赋值给user对象，实现数据的回显
        this.user = res.data;
      })
    }
  },

  // methods对象定义了该组件的所有方法
  methods: {

    // 用户点击“更新基本信息”按钮时调用
    onSubmit() {
      // 调用updateUser接口更新用户信息，参数为当前user对象
      updateUser(this.user).then(res => {
        // 若返回结果success为true
        if (res.success) {
          // 更新本地user对象为接口返回的最新数据
          this.user = res.data;
          // 通过ElementUI的消息组件弹出成功提示
          this.$message({
            type: 'success',
            message: '用户基本信息更新成功!'
          });
        }
      })
    },

    // 头像上传成功后触发的回调
    handleUploadSuccess(res) {
      // 将返回的头像地址赋值给user.avatar，实现头像的实时更新
      this.user.avatar = res;
      // 立刻调用updateUser接口，将新的头像地址同步到后端
      updateUser(this.user).then(res => {
        // 如果后端返回succcess为true
        if (res.success) {
          // 用后端返回的新用户数据覆盖本地数据
          this.user = res.data;
          // 弹窗提示头像上传成功
          this.$message({
            type: 'success',
            message: '头像上传成功!'
          });
        }
      })
    }

  },

}
</script>

<style scoped>
.el-form--label-top .el-form-item__label {
  padding: 0;
}

.el-form-item {
  margin-bottom: 5px;
}
</style>