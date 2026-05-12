// 编辑器自定义事件
const events = {
    /**
     * 官网为 Vue Router hash 模式时，配置里若填 /service?id=1 会请求整站路径导致 Apache 404，此处补成 /#/service?id=1
     */
    redirect(url) {
        if (!url) return
        const s = String(url).trim()
        if (/^https?:\/\//i.test(s)) {
            window.location.href = s
            return
        }
        if (s.indexOf('#') >= 0) {
            window.location.href = s.startsWith('#')
                ? window.location.origin + '/' + s
                : (s.startsWith('/') ? window.location.origin + s : s)
            return
        }
        const path = s.startsWith('/') ? s : '/' + s
        window.location.href = window.location.origin + '/#' + path.slice(1)
    },

    alert(msg) {
        if (msg) {
            // eslint-disable-next-line no-alert
            alert(msg)
        }
    },
}

const mixins = {
    methods: events,
}

const eventList = [
    {
        key: 'redirect',
        label: '跳转事件',
        event: events.redirect,
        param: '',
    },
    {
        key: 'alert',
        label: 'alert 事件',
        event: events.alert,
        param: '',
    },
]

export {
    mixins,
    events,
    eventList,
}