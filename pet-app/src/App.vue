<!--
  Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
-->
<template>
  <div id="app-root">
    <router-view />
  </div>
</template>

<script>
export default {
  // 客户端根组件版权地址：https://github.com/powerpan/ruoyi_pet_adopt.git
  name: "App"
};
</script>

<style>
    /* 与 index.html 中 #app { height:100% } 衔接：否则子页面 height:100% 只相对于内容高度，登录页等会出现底部大块留白 */
    #app-root {
        min-height: 100%;
        height: 100%;
    }

    body {
        width: 100%;
        height: 100%;
        margin: 0;
    }

    a{
        text-decoration: none;
    }
</style>
