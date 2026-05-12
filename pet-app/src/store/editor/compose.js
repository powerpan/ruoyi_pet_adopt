import store from '../index'
import generateID from '@/utils/web-editor/generateID'
import eventBus from '@/utils/web-editor/eventBus'
import decomposeComponent from '@/utils/web-editor/decomposeComponent'
import { $ } from '@/utils/web-editor/utils'
import { commonStyle, commonAttr } from '@/components/WebEditor/custom-component/component-list'
import { createGroupStyle } from '@/utils/web-editor/style'

export default {
    state: {
        areaData: { // 选中区域包含的组件以及区域位移信息
            style: {
                top: 0,
                left: 0,
                width: 0,
                height: 0,
            },
            components: [],
        },
        editor: null,
    },
    mutations: {
        getEditor(state) {
            state.editor = $('#editor')
        },

        setAreaData(state, data) {
            state.areaData = data
        },


        // 将已经放到 Group 组件数据删除，也就是在 componentData 中删除，因为它们已经从 componentData 挪到 Group 组件中了
        batchDeleteComponent({ componentData }, deleteData) {
            deleteData.forEach(component => {
                for (let i = 0, len = componentData.length; i < len; i++) {
                    if (component.id == componentData[i].id) {
                        componentData.splice(i, 1)
                        break
                    }
                }
            })
        },

        decompose({ curComponent, editor }) {
            const parentStyle = { ...curComponent.style }
            const components = curComponent.propValue
            const editorRect = editor.getBoundingClientRect()

            store.commit('deleteComponent')
            components.forEach(component => {
                decomposeComponent(component, editorRect, parentStyle)
                store.commit('addComponent', { component })
            })
        },
    },
}
