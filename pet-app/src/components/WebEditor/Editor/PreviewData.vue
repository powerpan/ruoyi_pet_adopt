<template>
  <div ref="container" class="bg">
    <div class="canvas-container">
      <div
        class="canvas"
        :style="{
                    ...getCanvasStyle(canvasStyleData),
                    width: changeStyleWithScale(canvasStyleData.width) + 'px',
                    height: changeStyleWithScale(canvasStyleData.height) + 'px',
                }"
      >
        <ComponentWrapper
          v-for="(item, index) in copyData"
          :key="index"
          :config="item"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { getStyle, getCanvasStyle } from '@/utils/web-editor/style'
import { mapState } from 'vuex'
import ComponentWrapper from './ComponentWrapper'
import { changeStyleWithScale } from '@/utils/web-editor/translate'
import { toPng } from 'html-to-image'
import { deepCopy } from '@/utils/web-editor/utils'


export default {
  components: { ComponentWrapper },
  props: {
    componentData: {
      type: Array,
      default: () => ([]),
    },
    canvasStyleData: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      copyData: [],
    }
  },
  created() {
    this.$set(this, 'copyData', deepCopy(this.componentData))
  },
  methods: {
    getStyle,
    getCanvasStyle,
    changeStyleWithScale,

  },
}
</script>

<style lang="scss" scoped>
.bg {
  width: 100%;
  height: 100%;
  align-items: center;
  justify-content: center;
  overflow: auto;
  padding: 20px;

  .canvas-container {
    width: calc(100% - 40px);
    height: calc(100% - 120px);
    overflow: auto;

    .canvas {
      background: #fff;
      position: relative;
      margin: auto;
    }
  }
}
</style>
