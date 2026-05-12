import SockJS from 'sockjs-client'; // 引入 SockJS 用于创建 WebSocket 连接
import Stomp from 'stompjs'; // 引入 Stomp 协议库
import {getToken} from '@/utils/auth' // 引入获取鉴权 Token 的方法
import store from '@/store' // 引入 Vuex store，用于获取用户信息

const socket = () => {
  // 获取后端服务的基础地址，根据开发环境变量动态切换
  let baseUrl = process.env.VUE_APP_BASE_API;
  // 创建 Stomp 客户端，底层使用 SockJS 建立连接，地址为 baseUrl+'/chat-websocket'
  let stompClient = Stomp.over(new SockJS(baseUrl+'/chat-websocket'));
  // 用 SockJS 取代原生 WebSocket 以支持更多的传输方式与兼容性

  return {
    // 简单封装的日志输出方法
    log(v){
      console.log(v)
    },
    // 公开 stompClient 实例，便于外部直接访问
    stompClient: stompClient,
    // 连接方法，接收回调函数参数
    connect(callback) {
      let that=this; // 保存当前 this 指针以供回调内访问
      this.stompClient.connect({
        "Authorization": 'Bearer ' + getToken(), // 通过 Header 传递 JWT Token 实现鉴权
        reconnectDelay: 10000, // stomp 重连时间间隔为 10 秒
        heartbeatIncoming: 4000, // 心跳检测：服务器端发送到客户端的心跳周期（4 秒）
        heartbeatOutgoing: 4000, // 心跳检测：客户端发送到服务器端的心跳周期（4 秒）
      }, function (f) { // 连接成功的回调
        that.log('Info: STOMP connection opened：'+f); // 输出连接成功日志
        callback(); // 执行外部传入的回调函数
      },function () { // 连接断开的回调处理
        //断开处理
        that.log('Info: STOMP connection closed.'); // 输出断开日志
      });
      //启动
    },
    // 主动关闭连接
    close() {
      if (this.stompClient !== null) { // 如果连接对象不为空
        this.stompClient.deactivate() // 调用 deactivate 方法断开连接
      }
    },
    // 发送消息接口；参数为目标地址 addr、接收方 to 以及消息内容 msg
    send(addr,to, msg) {
      console.log(addr,to,msg) // 打印目标地址、接收人、消息内容方便调试
      this.stompClient.send(
        "/webSocket"+addr, // 拼接实际通道地址，服务端根据该地址分发消息
        {}, // headers，可传递附加信息，这里暂时为空
        JSON.stringify({
          datetime:"2019-09-25", // 消息时间（写死，实际用时建议改为动态生成）
          from:store.getters.id, // 发送者用户id，通过 Vuex 获取
          to:to, // 接收者用户id
          ...msg // 扩展输入的消息对象，实现灵活扩展
        })
      );
    },
    // 订阅消息接口；参数为订阅的地址和回调函数
    subscribe(addr, callback) {
      this.stompClient.subscribe(addr, (res) => { // 订阅指定主题或队列地址
        var result = JSON.parse(res.body); // 解析后端返回的数据
        callback(result); // 执行回调，并将解析出的消息体作为参数传递
      });
    }
  }
}
export default socket // 默认导出 socket 工厂函数
