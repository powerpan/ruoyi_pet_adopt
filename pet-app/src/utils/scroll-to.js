// 二次缓动动画函数（可实现平滑滚动效果）
// t: 当前时间（以 ms 递增）
// b: 初始值（起点）
// c: 变化量（终点-起点）
// d: 持续时间（ms）
Math.easeInOutQuad = function(t, b, c, d) {
  // 将时间正规化为动画进度的 0-2 区间
  t /= d / 2
  if (t < 1) {
    // 前半段加速阶段
    return c / 2 * t * t + b
  }
  // t 进入后半段，递减使进入减速阶段
  t--
  // 后半段减速阶段
  return -c / 2 * (t * (t - 2) - 1) + b
}

// 兼容性 requestAnimationFrame 封装，动画每帧调用刷新函数
// 如浏览器不支持，则使用定时器模拟 60 FPS
var requestAnimFrame = (function() {
  return window.requestAnimationFrame // 标准 API
    || window.webkitRequestAnimationFrame // Webkit 前缀
    || window.mozRequestAnimationFrame // Firefox 前缀
    // 模拟 requestAnimationFrame，每帧约 16.7ms（1000/60）
    || function(callback) { window.setTimeout(callback, 1000 / 60) }
})()

/**
 * 移动所有可能的滚动元素，兼容不同浏览器内核
 * @param {number} amount 要滚动的垂直距离位置
 */
function move(amount) {
  // 设置 <html> 元素的滚动位置（标准浏览器）
  document.documentElement.scrollTop = amount
  // 设置 <body> 父节点的滚动位置（部分浏览器或怪异模式）
  document.body.parentNode.scrollTop = amount
  // 设置 <body> 的滚动位置（部分老浏览器）
  document.body.scrollTop = amount
}

// 获取当前页面垂直滚动距离（三者取其一）
function position() {
  return document.documentElement.scrollTop // 优先 html
    || document.body.parentNode.scrollTop // 其次 body 的父节点
    || document.body.scrollTop // 最后 body
}

/**
 * 平滑滚动到指定垂直位置
 * @param {number} to 目标位置
 * @param {number} duration 动画时长（ms）
 * @param {Function} callback 动画结束回调
 */
export function scrollTo(to, duration, callback) {
  // 起始滚动距离
  const start = position()
  // 距离变化量
  const change = to - start
  // 每次递进的时间（ms）
  const increment = 20
  // 当前已消耗时间
  let currentTime = 0
  // 若未传递 duration，则默认 500ms
  duration = (typeof (duration) === 'undefined') ? 500 : duration

  // 执行动画滚动的主函数
  var animateScroll = function() {
    // 时间进度往前推进 increment 毫秒
    currentTime += increment
    // 根据当前时间计算出应该滚动到的值
    var val = Math.easeInOutQuad(currentTime, start, change, duration)
    // 实际设置页面滚动
    move(val)
    // 如果尚未到动画时长，则继续下一帧
    if (currentTime < duration) {
      requestAnimFrame(animateScroll)
    } else {
      // 动画结束后执行回调（如果有）
      if (callback && typeof (callback) === 'function') {
        callback()
      }
    }
  }
  // 启动动画
  animateScroll()
}
