<template>
  <div class="picture-root" style="overflow: hidden;">
    <!-- 无翻转时走 img：解码与绘制由浏览器负责，大图感知明显快于 canvas -->
    <img
      v-if="useImgFastPath"
      :key="resolvedUrl || 'empty'"
      :src="resolvedUrl || undefined"
      alt=""
      decoding="async"
      draggable="false"
      class="picture-root__img"
      :style="imgInlineStyle"
    >
    <canvas
      v-show="!useImgFastPath"
      ref="canvas"
      class="picture-root__canvas"
    />
  </div>
</template>

<script>
import OnEvent from '../common/OnEvent'
import { resolveCanvasAssetUrl } from '@/utils/canvasAssetUrl'

export default {
    extends: OnEvent,
    props: {
        propValue: {
            type: Object,
            required: true,
            default: () => {},
        },
        element: {
            type: Object,
            default: () => {},
        },
    },
    computed: {
        useImgFastPath() {
            const f = (this.propValue && this.propValue.flip) || {}
            return !f.horizontal && !f.vertical
        },
        resolvedUrl() {
            return resolveCanvasAssetUrl(this.propValue && this.propValue.url)
        },
        imgInlineStyle() {
            const { width, height } = this.element.style || {}
            return {
                width: (width != null ? width : 0) + 'px',
                height: (height != null ? height : 0) + 'px',
                display: 'block',
                objectFit: 'fill',
            }
        },
    },
    data() {
        return {
            width: 0,
            height: 0,
            img: null,
            canvas: null,
            ctx: null,
        }
    },
    watch: {
        useImgFastPath(val) {
            if (!val) {
                this.$nextTick(() => this.initCanvasAndDraw())
            }
        },
        'element.style.width': function () {
            if (!this.useImgFastPath) {
                this.drawImage()
            }
        },
        'element.style.height': function () {
            if (!this.useImgFastPath) {
                this.drawImage()
            }
        },
        'propValue.flip.vertical': function () {
            if (!this.useImgFastPath) {
                this.mirrorFlip()
            }
        },
        'propValue.flip.horizontal': function () {
            if (!this.useImgFastPath) {
                this.mirrorFlip()
            }
        },
        resolvedUrl() {
            if (!this.useImgFastPath) {
                this._resolvedPictureUrl = null
                this.$nextTick(() => this.drawImage())
            }
        },
    },
    mounted() {
        if (!this.useImgFastPath) {
            this.initCanvasAndDraw()
        }
    },
    methods: {
        initCanvasAndDraw() {
            this.canvas = this.$refs.canvas
            if (!this.canvas) {
                return
            }
            this.ctx = this.canvas.getContext('2d')
            this.drawImage()
        },

        drawImage() {
            if (this.useImgFastPath) {
                return
            }
            const { width, height } = this.element.style
            if (!this.canvas || !this.ctx) {
                return
            }
            if (this.canvas.width !== width) {
                this.canvas.width = width
            }
            if (this.canvas.height !== height) {
                this.canvas.height = height
            }
            const resolved = this.resolvedUrl
            if (!this.img) {
                this.img = document.createElement('img')
            }
            if (this._resolvedPictureUrl !== resolved) {
                this._resolvedPictureUrl = resolved
                this.img.onload = () => {
                    this.ctx.drawImage(this.img, 0, 0, width, height)
                    this.mirrorFlip()
                }
                this.img.src = resolved || ''
            } else {
                this.mirrorFlip()
            }
        },

        mirrorFlip() {
            if (this.useImgFastPath) {
                return
            }
            const { vertical, horizontal } = this.propValue.flip
            const { width, height } = this.element.style
            if (!this.img || !this.img.complete || !this.ctx) {
                return
            }
            const hvalue = horizontal ? -1 : 1
            const vValue = vertical ? -1 : 1

            this.ctx.clearRect(0, 0, width, height)
            this.ctx.translate(width / 2 - width * hvalue / 2, height / 2 - height * vValue / 2)
            this.ctx.scale(hvalue, vValue)
            this.ctx.drawImage(this.img, 0, 0, width, height)
            this.ctx.setTransform(1, 0, 0, 1, 0, 0)
        },
    },
}
</script>

<style scoped>
.picture-root__img {
  pointer-events: none;
  vertical-align: top;
}
.picture-root__canvas {
  display: block;
  pointer-events: none;
}
</style>
