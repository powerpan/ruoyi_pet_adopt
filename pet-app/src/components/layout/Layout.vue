<template>
  <el-container class="pet-shell">
    <div class="layout-header">
      <Header/>
    </div>
    <el-main class="pet-main">
      <router-view/>
    </el-main>
    <el-footer class="pet-footer-wrap">
      <Footer/>
    </el-footer>
  </el-container>
</template>

<script>
import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";

export default {
  name: "Layout",
  components: {Footer, Header},
}
</script>

<style scoped>
.pet-main,
.pet-footer-wrap {
  padding: 0;
}
</style>
