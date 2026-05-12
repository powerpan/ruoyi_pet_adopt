<template>
  <div class="canvas-container">
    <div ref="container" id="canvasBox"
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
</template>

<script>
import {getStyle, getCanvasStyle} from '@/utils/web-editor/style'
import ComponentWrapper from './ComponentWrapper'
import {changeStyleWithScale} from '@/utils/web-editor/translate'
import {deepCopy} from '@/utils/web-editor/utils'
import {setRem} from "@/utils/rem";

export default {
  components: {ComponentWrapper},
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
  watch: {
    'componentData': {
      handler(newVal) {
        this.$set(this, 'copyData', deepCopy(newVal))
      },
      immediate: true
    }
  },
  data() {
    return {
      copyData: [],
    }
  },
  created: function () {
    this.$set(this, 'copyData', deepCopy(this.componentData))
  },
  mounted() {

  },
  methods: {
    getStyle,
    getCanvasStyle,
    changeStyleWithScale,

  },
}
</script>

<style lang="scss" scoped>

.canvas-container {
  width: 100%;
  height: 100%;
  align-items: center;
  justify-content: center;
  overflow: auto;

  .canvas {
    background: #fff;
    position: relative;
    margin: auto;
  }
}

</style>
