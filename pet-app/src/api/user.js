// 引入自定义的request模块，用于发送HTTP请求
import request from '@/utils/request'
// 引入字符串解析工具，可能用于处理空字符串
import { parseStrEmpty } from "@/utils/ruoyi";



// 查询指定用户详细信息
// @param {string|number} userId - 用户ID
// @returns {Promise} 请求结果Promise
export function getUser(userId) {
    return request({
        // 拼接用户ID进行查询，防止userId为null时报错，使用parseStrEmpty处理
        url: '/system/user/' + parseStrEmpty(userId),
        method: 'get'
    })
}

// 重置用户密码
// @param {string|number} userId - 用户ID
// @param {string} password - 新密码
// @returns {Promise} 请求结果Promise
export function resetUserPwd(userId, password) {
    // 构造发送给后端的数据对象，包含用户ID和新密码
    const data = {
        userId,
        password
    }
    return request({
        // 发送PUT请求到重置密码接口
        url: '/system/user/resetPwd',
        method: 'put',
        data: data
    })
}

// 修改用户状态（启用/停用等）
// @param {string|number} userId - 用户ID
// @param {string} status - 用户状态
// @returns {Promise} 请求结果Promise
export function changeUserStatus(userId, status) {
    // 构造数据对象，包含用户ID和新状态
    const data = {
        userId,
        status
    }
    return request({
        // PUT请求提交状态变更
        url: '/system/user/changeStatus',
        method: 'put',
        data: data
    })
}

// 查询当前登录用户的个人信息
// @returns {Promise} 请求结果Promise
export function getUserProfile() {
    return request({
        // 与 getInfo 同级根路径，避免 Nginx 对 /app、/system 改写导致 404
        url: '/getUserProfile',
        method: 'get'
    })
}

// 修改当前用户个人信息
// @param {object} data - 需要更新的个人信息
// @returns {Promise} 请求结果Promise
export function updateUserProfile(data) {
    return request({
        // PUT请求提交修改后的个人资料
        url: '/updateUserProfile',
        method: 'put',
        data: data
    })
}

// 修改当前用户密码（个人中心）
// @param {string} oldPassword - 旧密码
// @param {string} newPassword - 新密码
// @returns {Promise} 请求结果Promise
export function updateUserPwd(oldPassword, newPassword) {
    // 构造密码对象，包含旧密码和新密码
    const data = {
        oldPassword,
        newPassword
    }
    return request({
        // PUT请求到更新密码接口，参数通过params传递
        url: '/updateUserPwd',
        method: 'put',
        params: data
    })
}

// 上传用户头像
// @param {FormData} data - 包含头像文件的FormData对象
// @returns {Promise} 请求结果Promise
export function uploadAvatar(data) {
    return request({
        // POST请求上传头像，data中应包含头像文件
        url: '/uploadUserAvatar',
        method: 'post',
        data: data
    })
}
