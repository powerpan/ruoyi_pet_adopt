// 查询用户详细（接口相关功能注释文件）
// 导入自定义的request工具，用于进行ajax请求
import request from "@/utils/request";

// 获取指定会话的聊天记录
// 参数：conversationId - 需要获取聊天记录的会话ID
// 返回值：Promise对象，包含接口返回的聊天记录数据
export function getChatRecord(conversationId) {
    return request({
        // 请求的url，带有会话ID的查询参数
        url: '/chat/getChatRecord?conversationId=' + conversationId,
        // 请求方法为GET
        method: 'get'
    })
}

// 获取客服信息
// 无参数
// 返回值：Promise对象，包含客服详细数据
export function getCustomerService() {
    return request({
        // 请求的url：获取客服信息
        url: '/chat/getCustomerService',
        // 请求方法为GET
        method: 'get'
    })
}

// 获取临时用户信息
// 无参数
// 返回值：Promise对象，包含生成的临时用户数据
export function getTemporaryUser(){
    return request({
        // 请求的url：获取临时用户
        url: '/chat/getTemporaryUser',
        // 请求方法为GET
        method: 'get'
    })

}

// 基础智能客服：仅公司基础信息问答
export function askBasicCompany(data) {
    return request({
        url: '/app/chat/basic',
        method: 'post',
        data,
        // 聊天问答可能会涉及模型/检索，单独加长超时
        timeout: 45000
    })
}

// AI 智能客服：优先使用 DeepSeek
export function askCompanyAi(data) {
    return request({
        url: '/app/chat/ask',
        method: 'post',
        data,
        // 聊天问答可能会涉及模型/检索，单独加长超时
        timeout: 45000
    })
}