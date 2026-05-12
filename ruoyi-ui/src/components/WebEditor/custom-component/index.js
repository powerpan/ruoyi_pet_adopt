import Vue from 'vue'
import CircleShape from "@/components/WebEditor/custom-component/CircleShape/Component";
import Picture from "@/components/WebEditor/custom-component/Picture/Component";
import VText from "@/components/WebEditor/custom-component/VText/Component";
import VButton from "@/components/WebEditor/custom-component/VButton/Component";
import Group from "@/components/WebEditor/custom-component/Group/Component";
import RectShape from "@/components/WebEditor/custom-component/RectShape/Component";
import LineShape from "@/components/WebEditor/custom-component/LineShape/Component";
import VTable from "@/components/WebEditor/custom-component/VTable/Component";
import VChart from "@/components/WebEditor/custom-component/VChart/Component";
import CircleShapeAttr from "@/components/WebEditor/custom-component/CircleShape/Attr.vue";
import PictureAttr from "@/components/WebEditor/custom-component/Picture/Attr.vue";
import VTextAttr from "@/components/WebEditor/custom-component/VText/Attr.vue";
import VButtonAttr from "@/components/WebEditor/custom-component/VButton/Attr.vue";
import GroupAttr from "@/components/WebEditor/custom-component/Group/Attr.vue";
import RectShapeAttr from "@/components/WebEditor/custom-component/RectShape/Attr.vue";
import LineShapeAttr from "@/components/WebEditor/custom-component/LineShape/Attr.vue";
import VTableAttr from "@/components/WebEditor/custom-component/VTable/Attr.vue";
import VChartAttr from "@/components/WebEditor/custom-component/VChart/Attr.vue";


const components = [
  {name: 'CircleShape', component: CircleShape, attr: CircleShapeAttr},
  {name: 'Picture', component: Picture, attr: PictureAttr},
  {name: 'VText', component: VText, attr: VTextAttr},
  {name: 'VButton', component: VButton, attr: VButtonAttr},
  {name: 'Group', component: Group, attr: GroupAttr},
  {name: 'RectShape', component: RectShape, attr: RectShapeAttr},
  {name: 'LineShape', component: LineShape, attr: LineShapeAttr},
  {name: 'VTable', component: VTable, attr: VTableAttr},
  {name: 'VChart', component: VChart, attr: VChartAttr},
]
components.forEach(item => {
  Vue.component(item.name, item.component)
  Vue.component(item.name + 'Attr', item.attr)
})
import SVGStar from "@/components/WebEditor/custom-component/svgs/SVGStar/Component.vue";
import SVGStarAttr from "@/components/WebEditor/custom-component/svgs/SVGStar/Attr.vue";
import SVGTriangle from "@/components/WebEditor/custom-component/svgs/SVGTriangle/Component.vue";
import SVGTriangleAttr from "@/components/WebEditor/custom-component/svgs/SVGTriangle/Attr.vue";
const svgs = [
  {name:'SVGStar',component: SVGStar, componentAttr: SVGStarAttr},
  {name: 'SVGTriangle',component: SVGTriangle, componentAttr: SVGTriangleAttr}
]

svgs.forEach(item => {
  Vue.component(item.name, item.component)
  Vue.component(item.name + 'Attr', item.componentAttr)
})
