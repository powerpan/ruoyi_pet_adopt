<template>
  <!-- 外层容器，设置页面布局和宽度 -->
  <div class="container profile-page">
    <!-- 使用 Element UI 的行布局，设置列间距为20 -->
    <el-row :gutter="20">
      <!-- 左侧个人信息区略加宽，与基本资料分界右移 -->
      <el-col :span="8" :xs="24">
        <!-- 卡片组件，包裹个人信息 -->
        <el-card class="box-card profile-side-card">
          <!-- 卡片头部区域 -->
          <div slot="header" class="clearfix card-header">
            <!-- 个人信息标题 -->
            <span>个人信息</span>
          </div>
          <div>
            <!-- 头像部分，居中显示 -->
            <div class="text-center">
              <!-- 头像组件 -->
              <userAvatar />
            </div>
            <!-- 个人详细信息列表，采用自定义样式 -->
            <ul class="list-group list-group-striped">
              <!-- 用户名称 -->
              <li class="list-group-item">
                <svg-icon icon-class="user" />用户名称
                <!-- 右侧显示用户名 -->
                <div class="pull-right">{{ user.userName }}</div>
              </li>
              <!-- 用户昵称 -->
              <li class="list-group-item">
                <svg-icon icon-class="user" />用户昵称
                <!-- 右侧显示昵称 -->
                <div class="pull-right">{{ user.nickName }}</div>
              </li>
              <!-- 手机号码 -->
              <li class="list-group-item">
                <svg-icon icon-class="phone" />手机号码
                <!-- 右侧显示手机号码 -->
                <div class="pull-right">{{ user.phonenumber }}</div>
              </li>
              <!-- 用户邮箱 -->
              <li class="list-group-item">
                <svg-icon icon-class="email" />用户邮箱
                <!-- 右侧显示邮箱 -->
                <div class="pull-right">{{ user.email }}</div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16" :xs="24">
        <!-- 卡片组件，包裹基本资料和密码修改 -->
        <el-card class="profile-main-card">
          <!-- 卡片头部区域 -->
          <div slot="header" class="clearfix card-header">
            <!-- 基本资料标题 -->
            <span>基本资料</span>
          </div>
          <!-- 选项卡组件，切换基本资料和修改密码两个Tab -->
          <el-tabs v-model="activeTab">
            <!-- 基本资料页签 -->
            <el-tab-pane label="基本资料" name="userinfo">
              <!-- 用户信息表单组件，传递user对象 -->
              <userInfo :user="user" />
            </el-tab-pane>
            <!-- 修改密码页签 -->
            <el-tab-pane label="修改密码" name="resetPwd">
              <!-- 修改密码组件 -->
              <resetPwd />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
// 导入用户头像组件
import userAvatar from "./userAvatar";
// 导入用户信息表单组件
import userInfo from "./userInfo";
// 导入重置密码组件
import resetPwd from "./resetPwd";
// 导入获取用户个人资料的API方法
import { getUserProfile } from "@/api/user";

// 导出该组件的配置对象
export default {
  // 组件名称
  name: "Profile",
  // 注册本组件内用到的子组件
  components: { userAvatar, userInfo, resetPwd },
  // 定义组件内部响应式数据
  data() {
    return {
      // 用户信息对象
      user: {},
      // 当前激活的tab页，默认显示userinfo
      activeTab: "userinfo"
    };
  },
  // 组件创建完成生命周期钩子
  created() {
    // 调用自定义方法获取用户信息
    this.getUser();
  },
  // 组件方法集合
  methods: {
    // 获取用户个人信息数据的方法
    getUser() {
      // 调用api获取用户资料
      getUserProfile().then(response => {
        // 设置用户信息
        this.user = response.data;
      });
    }
  }
};
</script>
<style lang="scss" scoped>
.container{
  width: 75%;
  margin: 40px auto 10px auto;
  height: calc(100vh - 200px);
}

.profile-page {
  padding: 8px 4px 18px;
  min-height: calc(100vh - 190px);

  .box-card,
  .profile-main-card {
    border: 1px solid rgba(0, 0, 0, 0.06);
    border-radius: 16px;
    box-shadow: 0 8px 26px rgba(15, 23, 42, 0.07);
    overflow: hidden;
    background: linear-gradient(180deg, #ffffff 0%, #fbfcff 100%);
  }

  .card-header {
    font-size: 17px;
    font-weight: 600;
    letter-spacing: 0.3px;
    color: #1f2d3d;
  }

  .list-group {
    margin: 0;
    padding: 0;
    border-radius: 12px;
    overflow: hidden;
    border: 1px solid #edf2f7;
    background: #fff;
  }

  .list-group-item {
    list-style: none;
    padding: 12px 16px;
    border-bottom: 1px solid #f2f4f7;
    font-size: 14px;
    color: #4a5568;
    line-height: 1.55;
  }

  .list-group-item:last-child {
    border-bottom: 0;
  }

  .pull-right {
    float: right;
    max-width: 58%;
    text-align: right;
    word-break: break-all;
    color: #111827;
    font-weight: 500;
    font-size: 14px;
  }

  .text-center {
    text-align: center;
    margin-bottom: 14px;
  }

  ::v-deep .el-card__header {
    background: linear-gradient(180deg, #fafcff 0%, #f4f8ff 100%);
    border-bottom: 1px solid #e9eef6;
    padding: 14px 18px;
  }

  ::v-deep .el-card__body {
    padding: 16px 18px;
  }

  ::v-deep .el-tabs__nav-wrap::after {
    height: 1px;
    background-color: #e9eef6;
  }

  ::v-deep .el-tabs__item {
    font-size: 14px;
    color: #5f6b7a;
  }

  ::v-deep .el-tabs__item.is-active {
    color: #2f75ff;
    font-weight: 600;
  }

  ::v-deep .el-tabs__active-bar {
    background-color: #2f75ff;
    height: 3px;
    border-radius: 3px;
  }
}

@media (max-width: 992px) {
  .container.profile-page {
    width: 92%;
    margin-top: 24px;
    height: auto;
    min-height: calc(100vh - 160px);
  }
}
</style>