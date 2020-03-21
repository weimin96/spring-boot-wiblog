
let vm;
let app = new Vue({
    el: "#app",
    data() {
        return{
            websocket:{}
        }
    },
    beforeCreate() {
        vm = this;
    },
    mounted() {
        this.createWebsocket();
    },
    methods: {
        createWebsocket(){
            let token = this.$cookies.get("uToken");
            this.websocket = new WebSocket('wss://www.wiblog.cn/websocket/log/'+token);
            // this.websocket = new WebSocket('wss://127.0.0.1/websocket/log/'+token);
            this.websocket.onopen = this.open;
            this.websocket.onmessage = this.onmessage;
        },
        // websocket连接
        open(event){
            console.log("socket连接成功");
        },
        onmessage(event){
            $("#log-container div").append(event.data);
        }
    }
});