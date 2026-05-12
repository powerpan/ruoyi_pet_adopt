<template>
  <section class="menuList">
    <div v-for="item in menuList"   :key="item.path">
      <el-submenu :index="item.id" v-if="!!item.children&&item.children.length>0" popper-class="header-nav-popper" @click.native="handleClick(item.path)" >
        <template #title>
          <span>{{ item.name }}</span>
        </template>
        <MenuList :menuList="item.children" />
      </el-submenu>
      <el-menu-item v-else :index="item.id" @click.native="handleClick(item.path)">
        <template #title>
          <span>{{ item.name }}</span>
        </template>
      </el-menu-item>
    </div>
  </section>
</template>

<script>
/**
 * 递归生成菜单组件
 */
import MenuList from '@/components/layout/MenuList.vue'
export default {
  name: 'MenuList',
  props: ['menuList'],
  components: {
    MenuList,
  },
  methods: {
    handleClick(path) {
      if(path){
        this.$router.push({ path, query:{} }).catch(() => {})

      }
    }
  }
}
</script>

<style lang="scss" scoped>
.menuList {
  /* 该组件用于递归渲染，具体视觉交由 Header 的全局 popper 样式统一控制 */
}
</style>