
// 设置 rem 函数，用于根据当前窗口宽度动态设置根元素(html)的字体大小
export function setRem(dom) {
    // 设定基准屏幕宽度为1920px，按设计稿来适配（默认字体大小16px）
    // 即1920px = 120rem，每1rem = 16px
    const screenWidth = 1920

    // 计算缩放比例，1920 / 16 = 120。scale代表1rem等于多少px
    const scale = screenWidth / 16

    // 获取当前页面的宽度（即dom元素的clientWidth）
    const htmlWidth = dom.clientWidth

    // 获取html的DOM节点（根节点）
    const htmlDom = dom.getElementsByTagName('html')[0]

    // 根据当前页面宽度计算出对应的字体大小，并设置到html根元素上
    // htmlWidth / scale 使得页面宽度变化时，rem等比缩放
    htmlDom.style.fontSize = htmlWidth / scale + 'px'

    // 监听窗口resize事件，窗口大小发生变化时重新设置rem
    window.onresize = function() {
        setRem()
    }
}


