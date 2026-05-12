// 引入axios库，用于发送HTTP请求
import axios from 'axios'
// 从element-ui中引入Notification、MessageBox、Message、Loading用于用户界面消息提示
import { Notification, MessageBox, Message, Loading } from 'element-ui'
// 引入Vuex中的store对象，用于状态管理
import store from '@/store'
// 引入获取token的函数，用于身份认证
import { getToken } from '@/utils/auth'
// 引入自定义错误码处理
import errorCode from '@/utils/errorCode'
// 引入工具函数，包括参数转换和blob校验
import { tansParams, blobValidate } from "@/utils/ruoyi";
// 引入自定义缓存插件
import cache from '@/plugins/cache'
// 引入file-saver库中的saveAs方法，用于文件下载
import { saveAs } from 'file-saver'

// 定义一个变量用于保存下载过程中loading实例
let downloadLoadingInstance;
// 是否显示重新登录的标志对象，初始状态为不显示
export let isRelogin = { show: false };

// 设置axios的默认请求头，指定内容类型为JSON格式并设置字符编码
axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建一个axios实例，便于自定义配置和统一管理请求
const service = axios.create({
  // 设置基础请求url，所有接口请求的公共前缀，取自环境变量
  baseURL: process.env.VUE_APP_BASE_API,
  // 普通接口超时（秒）；登录/鉴权等见 api/login.js 单独加长，避免普通用户权限较多时误报「系统接口请求超时」
  timeout: 30000
})

// request拦截器，拦截每一个HTTP请求发送前的配置
service.interceptors.request.use(config => {
  // 判断请求头中是否设置 isToken=false，用于是否需要携带token，默认为true（需要携带）
  const isToken = (config.headers || {}).isToken === false
  // 判断请求头中是否设置 repeatSubmit=false，用于是否需要防止重复提交，默认为true（防止）
  const isRepeatSubmit = (config.headers || {}).repeatSubmit === false

  // 如果本地存在token且需要携带token，则在请求头加上Authorization字段
  if (getToken() && !isToken) {
    config.headers['Authorization'] = 'Bearer ' + getToken() // 将自定义token加到Authorization头部
  }

  // 对GET请求，将params参数拼接到url上（以字符串的方式），并清空原params
  if (config.method === 'get' && config.params) {
    // 使用工具函数 tansParams 将params对象转为 url 查询字符串
    let url = config.url + '?' + tansParams(config.params);
    // 移除最后一个多余的&或?等
    url = url.slice(0, -1);
    config.params = {};
    config.url = url; // 更新url
  }

  // 对于POST或PUT请求，需要进行防重复提交处理
  if (!isRepeatSubmit && (config.method === 'post' || config.method === 'put')) {
    // 构建本次请求的对象信息（url, data, 当前时间戳）
    const requestObj = {
      url: config.url,
      data: typeof config.data === 'object' ? JSON.stringify(config.data) : config.data,
      time: new Date().getTime()
    }
    // 计算请求数据对象的大小（以key数量衡量），用于判断是否超过阈值
    const requestSize = Object.keys(JSON.stringify(requestObj)).length; // 请求数据大小
    const limitSize = 5 * 1024 * 1024; // 最大存储限制5M

    // 如果请求数据超过阈值，不进行防重复校验，并警告
    if (requestSize >= limitSize) {
      console.warn(`[${config.url}]: ` + '请求数据大小超出允许的5M限制，无法进行防重复提交验证。')
      return config;
    }

    // 从sessionStorage获取上一次请求的对象信息
    const sessionObj = cache.session.getJSON('sessionObj')
    // 如果没有缓存对象，则将本次请求对象缓存
    if (sessionObj === undefined || sessionObj === null || sessionObj === '') {
      cache.session.setJSON('sessionObj', requestObj)
    } else {
      // 取出缓存中的url、data和请求时间
      const s_url = sessionObj.url;                  // 缓存中的请求地址
      const s_data = sessionObj.data;                // 缓存中的请求数据
      const s_time = sessionObj.time;                // 缓存中的请求时间
      const interval = 1000;                         // 防重复提交的间隔时间（单位ms），1秒内重复则阻止

      // 判断：本次请求和缓存请求完全一致，且间隔时间小于1秒，则视为重复提交
      if (s_data === requestObj.data && requestObj.time - s_time < interval && s_url === requestObj.url) {
        const message = '数据正在处理，请勿重复提交';
        console.warn(`[${s_url}]: ` + message) // 提示警告信息
        return Promise.reject(new Error(message)) // 拒绝本次请求
      } else {
        // 如果不是重复提交，则更新缓存中的请求对象为当前对象
        cache.session.setJSON('sessionObj', requestObj)
      }
    }
  }
  // 返回处理后的config对象
  return config
}, error => {
    // 请求前拦截发生异常时的处理
    console.log(error)
    Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(
  res => {
    // 统一为数字，避免后端/序列化把 code 变成字符串 "200" 时误判为失败
    const raw = res.data && res.data.code
    const code =
      raw === undefined || raw === null || raw === ''
        ? 200
        : Number(raw)
    const numCode = Number.isFinite(code) ? code : 200
    // 根据状态码获取对应的错误提示信息，如果没有则尝试读取后端返回的msg字段或用默认错误提示
    const msg = errorCode[numCode] || res.data.msg || errorCode['default']
    const noErrorMessage = !!(res.config && res.config.noErrorMessage)
    // 如果请求类型为二进制（如文件下载），则直接返回数据
    if (res.request.responseType === 'blob' || res.request.responseType === 'arraybuffer') {
      return res.data
    }
    // 处理未授权（401）情况，提示重新登录，这部分默认注释说明可自行开启
    if (numCode === 401) {
      const authMsg = res.data.msg || '登录状态已过期，请重新登录'
      if (!noErrorMessage) {
        Message({ message: authMsg, type: 'error' })
      }
      return Promise.reject(new Error(authMsg))
    }
    // 处理500错误，弹出错误提示，并拒绝该请求
    else if (numCode === 500) {
      if (!noErrorMessage) {
        Message({ message: msg, type: 'error' })
      }
      return Promise.reject(new Error(msg))
    }
    // 处理自定义业务警告（如601），弹出警告提示，并拒绝
    else if (numCode === 601) {
      if (!noErrorMessage) {
        Message({ message: msg, type: 'warning' })
      }
      return Promise.reject(new Error(msg))
    }
    // 处理除200、500、601以外的其它状态码，进行全局错误通知
    else if (numCode !== 200) {
      if (!noErrorMessage) {
        Notification.error({ title: msg })
      }
      return Promise.reject(new Error(msg))
    }
    // 当状态码为200时认为请求正常，直接返回数据
    else {
      return res.data
    }
  },
  error => {
    // 捕获响应出错时的异常信息
    console.log('err' + error)
    const noErrorMessage = !!(error && error.config && error.config.noErrorMessage)
    // 解构出错误对象中的message字段
    let { message } = error;
    // 对网络错误做友好中文提示
    if (message == "Network Error") {
      message = "后端接口连接异常";
    }
    // 对超时错误做中文提示
    else if (message.includes("timeout")) {
      message = "系统接口请求超时";
    }
    // 对状态码错误做中文提示，截取状态码后三位显示
    else if (message.includes("Request failed with status code")) {
      const status = error.response && error.response.status
      const path = error.response && error.response.data && error.response.data.path
      if (status === 401) {
        message = "登录状态已过期，请重新登录";
      } else if (status === 404) {
        message = path && path.indexOf('/dev-api/') === 0
          ? "接口不存在或本地代理未正确转发"
          : "接口不存在，请检查请求地址";
      } else {
        message = "系统接口" + message.substr(message.length - 3) + "异常";
      }
    }
    // 弹出错误提示框，显示5秒钟
    if (!noErrorMessage) {
      Message({ message: message, type: 'error', duration: 5 * 1000 })
    }
    // 返回一个被拒绝的Promise，便于后续catch拦截异常处理
    return Promise.reject(error)
  }
)

// 通用下载方法
export function download(url, params, filename, config, method = "post") {
  // 显示加载动画，提示正在下载数据
  downloadLoadingInstance = Loading.service({
    text: "正在下载数据，请稍候",
    spinner: "el-icon-loading",
    background: "rgba(0, 0, 0, 0.7)",
  });

  // 判断请求方法为POST
  if (method == 'post') {
    // 发送POST请求进行文件下载
    return service.post(
      url, // 下载url
      params, // 请求参数
      {
        // 对请求参数进行自定义转换
        transformRequest: [(params) => { return tansParams(params); }],
        // 设定请求头为表单方式
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        // 指定响应类型为blob（二进制流）
        responseType: 'blob',
        // 合并自定义配置
        ...config
      }
    ).then(async (data) => {
      // 校验响应是否为Blob类型（即文件流）
      const isBlob = blobValidate(data);
      if (isBlob) {
        // 创建Blob对象用于保存文件
        const blob = new Blob([data]);
        // 使用saveAs方法保存文件，文件名为filename
        saveAs(blob, filename);
      } else {
        // 如果不是有效的Blob，则将内容作为文本解析
        const resText = await data.text();
        // 尝试从文本解析为JSON对象
        const rspObj = JSON.parse(resText);
        // 获取后端返回的错误消息
        const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default'];
        // 弹出错误消息提示框
        Message.error(errMsg);
      }
      // 关闭加载动画
      downloadLoadingInstance.close();
    }).catch((r) => {
      // 捕获异常并打印到控制台
      console.error(r);
      // 弹出通用下载错误提示
      Message.error('下载文件出现错误，请联系管理员！');
      // 关闭加载动画
      downloadLoadingInstance.close();
    });
  } else {
    // 如果请求方法不是POST，采用GET请求方式
    return service.get(
      url, // 下载url
      params, // 请求参数
      {
        // 对请求参数进行自定义转换
        transformRequest: [(params) => { return tansParams(params); }],
        // 设定请求头为表单方式
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        // 指定响应类型为blob（二进制流）
        responseType: 'blob',
        // 合并自定义配置
        ...config
      }
    ).then(async (data) => {
      // 校验响应是否为Blob类型（即文件流）
      const isBlob = blobValidate(data);
      if (isBlob) {
        // 创建Blob对象用于保存文件
        const blob = new Blob([data]);
        // 使用saveAs方法保存文件，文件名为filename
        saveAs(blob, filename);
      } else {
        // 如果不是有效的Blob，则将内容作为文本解析
        const resText = await data.text();
        // 尝试从文本解析为JSON对象
        const rspObj = JSON.parse(resText);
        // 获取后端返回的错误消息
        const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default'];
        // 弹出错误消息提示框
        Message.error(errMsg);
      }
      // 关闭加载动画
      downloadLoadingInstance.close();
    }).catch((r) => {
      // 捕获异常并打印到控制台
      console.error(r);
      // 弹出通用下载错误提示
      Message.error('下载文件出现错误，请联系管理员！');
      // 关闭加载动画
      downloadLoadingInstance.close();
    });
  }
}

// 导出默认的service实例，供其他模块调用HTTP服务
export default service
