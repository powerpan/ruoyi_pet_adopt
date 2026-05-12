<template>
  <div>
    <!-- 头像显示区。点击触发头像编辑功能 -->
    <div class="user-info-head" @click="editCropper()">
      <!-- 展示当前头像，若无则用默认头像 -->
      <img 
        v-bind:src="options.img" 
        title="点击上传头像" 
        class="img-circle img-lg" 
      />
    </div>
    <!-- 弹窗对话框，包含裁剪、预览等功能 -->
    <el-dialog 
      :title="title" 
      :visible.sync="open" 
      width="800px" 
      append-to-body 
      @opened="modalOpened"  
      @close="closeDialog"
    >
      <!-- 第一个分区，左右布局：裁剪操作区和头像预览区 -->
      <el-row>
        <!-- 左侧：裁剪器区域 -->
        <el-col :xs="24" :md="12" :style="{height: '350px'}">
          <!-- vue-cropper 组件，负责图片裁剪操作 -->
          <!-- 当前裁剪图片数据 -->
          <!-- 显示裁剪区域尺寸和位置 -->
          <!-- 是否自动启用裁剪框 -->
          <!-- 裁剪框宽度 -->
          <!-- 裁剪框高度 -->
          <!-- 是否固定裁剪框尺寸 -->
          <!-- 裁剪后输出图片类型 -->
          <!-- 裁剪框变化时触发实时预览 -->
          <!-- 弹窗完全打开后才渲染，解决布局偏移 -->
          <vue-cropper
            ref="cropper"
            :img="options.img"                  
            :info="true"                        
            :autoCrop="options.autoCrop"        
            :autoCropWidth="options.autoCropWidth"      
            :autoCropHeight="options.autoCropHeight"    
            :fixedBox="options.fixedBox"        
            :outputType="options.outputType"    
            @realTime="realTime"                
            v-if="visible"                      
          />
        </el-col>
        <!-- 右侧：图片预览区 -->
        <el-col :xs="24" :md="12" :style="{height: '350px'}">
          <div class="avatar-upload-preview">
            <!-- 实时显示裁剪后的头像预览（包含缩放、移动等效果） -->
            <img :src="previews.url" :style="previews.img" />
          </div>
        </el-col>
      </el-row>
      <!-- 间隔 -->
      <br />
      <!-- 操作按钮区，分布多个控件 -->
      <el-row>
        <!-- 选择图片上传按钮 -->
        <!-- 实际不上传，走自定义上传流程 -->
        <!-- 覆盖默认上传请求行为 -->
        <!-- 不显示默认文件列表 -->
        <!-- 上传前拦截，校验文件类型/大小等 -->
        <el-col :lg="2" :sm="3" :xs="3">
          <el-upload 
            action="#"                           
            :http-request="requestUpload"        
            :show-file-list="false"              
            :before-upload="beforeUpload"        
          >
            <el-button size="small">
              选择
              <i class="el-icon-upload el-icon--right"></i>
            </el-button>
          </el-upload>
        </el-col>
        <!-- 放大图片操作按钮 -->
        <el-col :lg="{span: 1, offset: 2}" :sm="2" :xs="2">
          <el-button icon="el-icon-plus" size="small" @click="changeScale(1)"></el-button>
        </el-col>
        <!-- 缩小图片操作按钮 -->
        <el-col :lg="{span: 1, offset: 1}" :sm="2" :xs="2">
          <el-button icon="el-icon-minus" size="small" @click="changeScale(-1)"></el-button>
        </el-col>
        <!-- 向左旋转图片按钮 -->
        <el-col :lg="{span: 1, offset: 1}" :sm="2" :xs="2">
          <el-button icon="el-icon-refresh-left" size="small" @click="rotateLeft()"></el-button>
        </el-col>
        <!-- 向右旋转图片按钮 -->
        <el-col :lg="{span: 1, offset: 1}" :sm="2" :xs="2">
          <el-button icon="el-icon-refresh-right" size="small" @click="rotateRight()"></el-button>
        </el-col>
        <!-- 提交上传按钮，右侧对齐 -->
        <el-col :lg="{span: 2, offset: 6}" :sm="2" :xs="2">
          <el-button type="primary" size="small" @click="uploadImg()">提 交</el-button>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script>
// 引入Vuex store对象，用于获取和设置用户头像等全局数据
import store from "@/store";
// 引入 vue-cropper 组件，用于图片裁剪
import { VueCropper } from "vue-cropper";
// 引入上传头像API，用于提交裁剪后的头像数据到后端
import { uploadAvatar } from "@/api/user";
// 引入防抖函数，优化事件绑定
import { debounce } from '@/utils/index'
// 引入默认头像图片，当用户未设置头像时显示
import person from "@/assets/img/me/person2.png";

export default {
  // 注册本地组件
  components: { VueCropper },
  data() {
    return {
      // 控制弹出层的显示与隐藏
      open: false,
      // 控制裁剪组件的显示与隐藏
      visible: false,
      // 弹窗标题，默认为“修改头像”
      title: "修改头像",
      // 裁剪组件相关配置
      options: {
        // 裁剪图片的地址，优先取store中的用户头像，没有则用默认头像
        img: !!store.getters.avatar ? store.getters.avatar : person,
        // 是否自动生成裁剪方框
        autoCrop: true,
        // 默认裁剪框的宽度
        autoCropWidth: 200,
        // 默认裁剪框的高度
        autoCropHeight: 200,
        // 是否固定裁剪框大小（不允许用户拉伸/缩放方框）
        fixedBox: true,
        // 裁剪后图片的格式，默认为png
        outputType: "png",
        // 上传到后端时的文件名（初始为avatar，选择新图片后会自动替换为图片文件名）
        filename: 'avatar'
      },
      // 用于实时预览裁剪效果的对象
      previews: {},
      // 保存窗口resize事件处理函数的引用，便于解绑事件
      resizeHandler: null
    };
  },
  methods: {
    // 点击“编辑头像”按钮时触发，显示弹出裁剪层
    editCropper() {
      this.open = true;
    },
    // 弹窗打开动画结束后的回调钩子，显示裁剪组件并监听resize事件
    modalOpened() {
      this.visible = true;
      // 只绑定一次防抖后的resize事件
      if (!this.resizeHandler) {
        this.resizeHandler = debounce(() => {
          this.refresh();
        }, 100);
      }
      window.addEventListener("resize", this.resizeHandler);
    },
    // 刷新裁剪组件，当窗口大小变化时调用
    refresh() {
      this.$refs.cropper.refresh();
    },
    // 覆盖上传组件的原生上传请求，不做任何操作（仅做自定义上传流程，必留空函数）
    requestUpload() {
      // 空函数，必留，否则会导致el-upload组件报错
    },
    // 左旋转图片事件
    rotateLeft() {
      this.$refs.cropper.rotateLeft();
    },
    // 右旋转图片事件
    rotateRight() {
      this.$refs.cropper.rotateRight();
    },
    // 缩放图片，参数num为放大或缩小倍数
    changeScale(num) {
      num = num || 1;
      this.$refs.cropper.changeScale(num);
    },
    // 上传前的预处理，主要做文件类型校验&读入base64进行裁剪展示
    beforeUpload(file) {
      // 校验文件类型，必须是图片，否则弹窗报错
      if (file.type.indexOf("image/") == -1) {
        this.$modal.msgError("文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。");
      } else {
        // 使用FileReader读取本地文件，再转为base64给图片裁剪组件展示
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
          // 设置裁剪组件的数据源为新图片
          this.options.img = reader.result;
          // 保存当前文件名，用于上传用
          this.options.filename = file.name;
        };
      }
    },
    // 提交裁剪后的图片数据到接口进行上传
    uploadImg() {
      // 获取裁剪后的图片二进制数据（Blob对象）
      this.$refs.cropper.getCropBlob(data => {
        // 上传时需构建FormData对象
        let formData = new FormData();
        // 将裁剪后的图片数据append到formData，字段名后端需和接口一致
        formData.append("avatarfile", data, this.options.filename);
        // 调用API上传头像
        uploadAvatar(formData).then(response => {
          // 上传成功后关闭弹窗
          this.open = false;
          // 更新本地头像路径（拼接API前缀与后端返回的路径）
          this.options.img = process.env.VUE_APP_BASE_API + response.imgUrl;
          // 同步保存到全局store，便于全局头像及时显示
          store.commit('SET_AVATAR', this.options.img);
          // 弹窗提示用户“修改成功”
          this.$modal.msgSuccess("修改成功");
          // 隐藏裁剪组件
          this.visible = false;
        });
      });
    },
    // 裁剪框内容变化时触发，实时预览区域会响应更新（外部直接赋值）
    realTime(data) {
      this.previews = data;
    },
    // 关闭弹窗和裁剪组件时触发，恢复为全局头像，解绑resize事件
    closeDialog() {
      // 关闭时恢复全局store中的头像
      this.options.img = store.getters.avatar;
      // 隐藏裁剪组件
      this.visible = false;
      // 解绑resize事件
      window.removeEventListener("resize", this.resizeHandler);
    }
  }
};
</script>
<style   lang="scss" scoped>
.user-info-head {
  position: relative;
  display: inline-block;
  height: 120px;
}

.user-info-head:hover:after {
  content: '+';
  position: absolute;
  left: 50%;
  right: 0;
  top: 0;
  bottom: 0;
  color: #eee;
  background: rgba(0, 0, 0, 0.5);
  font-size: 24px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  cursor: pointer;
  line-height: 110px;
  border-radius: 50%;
}
</style>
