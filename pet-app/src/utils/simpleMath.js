/** 标量乘除，替代 mathjs 以减小打包体积 */
export function multiply(a, b) {
  return Number(a) * Number(b)
}

export function divide(a, b) {
  const d = Number(b)
  if (d === 0) return NaN
  return Number(a) / d
}
