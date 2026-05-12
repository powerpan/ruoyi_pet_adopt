
/**
 * 防抖函数：返回一个新的被包装的函数，能控制 func 在 wait 时间内只执行一次
 *
 * @param {Function} func 要执行的函数
 * @param {number} wait 延迟的毫秒数
 * @param {boolean} immediate 是否立即执行一次（在边界调用）
 * @return {*} 根据 func 返回的结果
 */
export function debounce(func, wait, immediate) {
  // 声明定时器(timeout)、参数(args)、上下文(context)、时间戳(timestamp)、返回结果(result)
  let timeout, args, context, timestamp, result

  // 延迟执行的内部函数（用于 setTimeout）
  const later = function() {
    // 计算自上次触发以来的时间间隔
    const last = +new Date() - timestamp

    // 如果时间间隔小于 wait 并且大于0，说明还未到期，需要在剩余时间后再执行 later
    if (last < wait && last > 0) {
      timeout = setTimeout(later, wait - last)
    } else {
      // 否则清除 timeout
      timeout = null
      // 如果 immediate 为 false（非立即执行模式），到达延迟时执行 func
      if (!immediate) {
        // 调用 func，并绑定正确的 this 和参数
        result = func.apply(context, args)
        // 如果没有新的 timeout 了，释放 context 和 args 以节省内存
        if (!timeout) context = args = null
      }
    }
  }

  // 返回一个新的函数，调用时触发防抖逻辑
  return function(...args) {
    // 保留调用时的 this 上下文和参数
    context = this
    // 记录调用时间
    timestamp = +new Date()
    // 立即执行的条件：immediate 为 true，并且没有激活的 timeout
    const callNow = immediate && !timeout
    // 如果没有激活的 timeout，启动一个新的 setTimeout
    if (!timeout) timeout = setTimeout(later, wait)
    // 如果满足条件立即执行 func
    if (callNow) {
      result = func.apply(context, args)
      // 及时清理 context 和 args，避免内存泄漏
      context = args = null
    }

    // 返回 func 的执行结果
    return result
  }
}
