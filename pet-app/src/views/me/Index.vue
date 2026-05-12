<!--<template>-->
<!--  <div class="container">-->
<!--    <div class="container-box">-->


<!--      <div class="title-box"><span>基本设置</span></div>-->
<!--      <el-form label-position="top" ref="form" :model="user" label-width="80px">-->
<!--        <div class="content">-->
<!--          <div class="left">-->
<!--            <el-form-item style="padding: 0" label="昵称">-->
<!--              <el-input v-model="user.nickname"></el-input>-->
<!--            </el-form-item>-->
<!--            <el-form-item style="padding: 0" label="密码">-->
<!--              <el-input type="password" v-model="user.password"></el-input>-->
<!--            </el-form-item>-->
<!--            <el-form-item label="性别">-->
<!--              <el-radio-group v-model="user.gender">-->
<!--                <el-radio label="男生"></el-radio>-->
<!--                <el-radio label="女生"></el-radio>-->
<!--              </el-radio-group>-->
<!--            </el-form-item>-->
<!--            <el-form-item label="出生年月">-->
<!--              <el-col>-->
<!--                <el-date-picker-->
<!--                    type="date" placeholder="选择日期"-->
<!--                    v-model="user.birthday"-->
<!--                    value-format="yyyy-MM-dd"-->
<!--                    style="width: 100%;"/>-->
<!--              </el-col>-->
<!--            </el-form-item>-->
<!--            <el-form-item label="个人简介">-->
<!--              <el-input :rows="5" type="textarea" v-model="user.info"></el-input>-->
<!--            </el-form-item>-->
<!--          </div>-->
<!--          <div class="right">-->
<!--            <img style="padding-bottom: 10px;padding-left: 10px;width: 150px; height: 150px;" alt=""-->
<!--                 :src="user.avatar">-->
<!--            <el-upload-->
<!--                style="padding-left: 10px;letter-spacing: 1px"-->
<!--                class="upload-demo"-->
<!--                accept=".png,.jpg"-->
<!--                :headers="header"-->
<!--                :action="uploadAction"-->
<!--                :on-success="handleUploadSuccess"-->
<!--                multiple-->
<!--                :limit="1">-->
<!--              <el-button style="width: 150px" size="small" type="primary">-->
<!--                <i class="el-icon-upload2"></i> 点击上传-->
<!--              </el-button>-->
<!--              <div slot="tip" class="el-upload__tip">只能上传jpg/png文件</div>-->
<!--            </el-upload>-->
<!--            <el-form-item label="昵称">-->
<!--              <el-input v-model="user.nickname"></el-input>-->
<!--            </el-form-item>-->
<!--            <el-form-item style="padding: 0" label="邮箱">-->
<!--              <el-input type="email" v-model="user.email"></el-input>-->
<!--            </el-form-item>-->
<!--          </div>-->

<!--        </div>-->
<!--        <div class="btn">-->
<!--          <el-form-item style="padding-top: 20px">-->
<!--            <el-button type="primary" @click="onSubmit">更新基本信息</el-button>-->
<!--          </el-form-item>-->
<!--        </div>-->

<!--      </el-form>-->
<!--    </div>-->
<!--  </div>-->

<!--</template>-->


<!--<script>-->
<!--import {findById, updateUser} from "@/api/user";-->
<!--import config from "@/config";-->
<!--import  person from "@/assets/img/me/person2.png";-->

<!--export default {-->

<!--  data() {-->
<!--    return {-->
<!--      header: {-->
<!--        "Authorization": localStorage.getItem("token")-->
<!--      },-->
<!--      uploadAction: config.API_URL + '/upload',-->
<!--      user: {-->
<!--        nickname: '',-->
<!--        password: '',-->
<!--        email: '',-->
<!--        birthday: '',-->
<!--        gender: '',-->
<!--        info: '',-->
<!--        avatar: person-->
<!--      }-->
<!--    }-->
<!--  },-->

<!--  mounted() {-->
<!--    if (localStorage.getItem("uid") !== null) {-->
<!--      findById(localStorage.getItem("uid")).then(res => {-->
<!--        this.user = res.data;-->
<!--      })-->
<!--    }-->
<!--  },-->

<!--  methods: {-->

<!--    onSubmit() {-->
<!--      updateUser(this.user).then(res => {-->
<!--        if (res.success) {-->
<!--          this.user = res.data;-->
<!--          this.$message({-->
<!--            type: 'success',-->
<!--            message: '用户基本信息更新成功!'-->
<!--          });-->
<!--        }-->
<!--      })-->
<!--    },-->
<!--    handleUploadSuccess(res) {-->
<!--      this.user.avatar = res;-->
<!--      updateUser(this.user).then(res => {-->
<!--        if (res.success) {-->
<!--          this.user = res.data;-->
<!--          this.$message({-->
<!--            type: 'success',-->
<!--            message: '头像上传成功!'-->
<!--          });-->
<!--        }-->
<!--      })-->
<!--    }-->

<!--  },-->

<!--}-->

<!--</script>-->


<!--<style lang="scss" scoped>-->
<!--&gt; > > .el-form&#45;&#45;label-top .el-form-item__label {-->
<!--  padding: 0;-->
<!--}-->

<!--.el-form-item {-->

<!--  margin-bottom: 5px;-->
<!--}-->
<!--.container-box {-->
<!--  width: 75%;-->
<!--  margin: 0 auto;-->
<!--}-->
<!--.container {-->
<!--  width: 100%;-->
<!--  padding: 20px;-->
<!--  background-color: #efefef;-->

<!--  .title-box {-->
<!--    background-color: #fff;-->
<!--    border-top: 5px solid #efefef;-->
<!--    border-bottom: 5px solid #efefef;-->
<!--    height: 60px;-->
<!--    border-radius: 5px;-->
<!--    padding-left: 50px;-->
<!--    span {-->
<!--      font-size: 20px;-->
<!--      line-height: 60px;-->
<!--      letter-spacing: 1px;-->
<!--      font-weight: 400;-->
<!--    }-->
<!--  }-->

<!--  .content {-->
<!--    border-radius: 5px;-->
<!--    justify-content: space-between;-->
<!--    padding: 10px 40px;-->
<!--    height: calc(   100vh - 300px);-->
<!--    background-color: #fff;-->

<!--    .left {-->
<!--      width: 50%;-->
<!--      float: left;-->
<!--      box-sizing: border-box;-->
<!--      padding: 20px 30px 0 30px;-->
<!--      .el-form-item {-->
<!--        margin-bottom: 10px;-->
<!--      }-->
<!--    }-->

<!--    .right {-->
<!--      box-sizing: border-box;-->
<!--      width: 50%;-->
<!--      padding: 20px 30px 0 30px;-->
<!--      float: left;-->
<!--    }-->
<!--  }-->

<!--  .btn {-->
<!--    width: 100%;-->
<!--    box-sizing: border-box;-->
<!--    height:80px;-->
<!--    background-color: #fff;-->
<!--    border-radius: 5px;-->
<!--    padding: 0 40px;-->
<!--  }-->
<!--}-->

<!--</style>-->
