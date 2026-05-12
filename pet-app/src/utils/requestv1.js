// 导入 axios 请求库
import request from "axios"
// 导入自定义配置
import config from "@/config"
// 导入 element-ui 的通知组件
import {Notification} from 'element-ui'

// 从本地存储获取用户 uid
const uid = localStorage.getItem('uid');
// 从本地存储获取用户 token（用于身份验证）
const token = localStorage.getItem('token');

// 创建 axios 实例，配置基础 URL 和请求头（包含 token）
const service = request.create({
    baseURL: config.API_URL, // 设置基础请求地址
    headers: {
        "Authorization": token // 设置请求头的 Authorization 字段
    }
});

// 添加响应拦截器
service.interceptors.response.use(
    // 请求成功时的回调函数
    response => {
        // 获取响应数据部分
        const res = response.data;
        // 如果接口约定返回 success === true，则请求成功
        if (res.success === true) {
            // 如果消息不为空，弹出成功通知
            if (res.msg !== null) {
                Notification.success({
                    title: 'Success ', // 通知标题
                    message: res.msg,  // 通知内容
                    type: 'success'    // 通知类型
                });
            }
        } else {
            // 请求未成功，弹出错误通知
            Notification.error({
                title: '错误提示: ' + res.code, // 错误标题：包含错误码
                message: res.msg               // 错误提示消息
            });
        }
        // 返回响应体数据部分，传递给下一个 then
        return res
    },

    // 请求失败时的回调函数
    error => {
        // 控制台输出出错信息
        console.log('err' + error);
        // 返回一个被拒绝的 Promise 对象，供调用方处理错误
        return Promise.reject(error)
    }
);

// 导出 axios 实例供其他模块使用
export default service
