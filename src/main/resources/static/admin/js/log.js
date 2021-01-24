let vm;
let app = new Vue({
    el: "#app",
    data() {
        return {
            websocket: {},
            logList: [],
            logDetail: "",
            fileVisible: false,
            pageNum: 0,
            path: "",
            total: 0,
            currentVisible: false
        }
    },
    beforeCreate() {
        vm = this;
    },
    mounted() {
        this.getLogList();
        //this.createWebsocket();
    },
    computed: {
        sortLog() {
            return this.sortByKey(this.logList,"name");
        }
    },
    methods: {
        getLogList() {

            $.get("/getLogList", function (res) {
                if (res.code === 10000) {

                    vm.logList = res.data;

                }
            })
        },
        pageUp() {
            if (this.pageNum === 0) {
                return;
            }
            this.pageNum--;
            $.get("/showLog", {path: this.path, pageNum: this.pageNum}, function (res) {
                if (res.code === 10000) {
                    vm.logDetail = res.data.list;
                }
            })
        },
        pageDown() {
            if (this.total === this.pageNum) {
                return;
            }
            this.pageNum++;
            $.get("/showLog", {path: this.path, pageNum: this.pageNum}, function (res) {
                if (res.code === 10000) {
                    vm.logDetail = res.data.list;
                }
            })
        },
        handleView(i, row) {
            this.path = row.path;
            $.get("/showLog", {path: this.path, pageNum: this.pageNum}, function (res) {
                if (res.code === 10000) {
                    vm.logDetail = res.data.list;
                    vm.total = res.data.total;
                    vm.fileVisible = true;
                }
            })
        },
        handleDel() {

        },
        currentData() {
            this.createWebsocket();
            this.currentVisible = true;
        },
        createWebsocket() {
            let token = this.$cookies.get("uToken");
            let host = window.location.host;
            // wss/ws
            let protocol;
            alert(window.location.protocol)
            if (window.location.protocol === "http:"){
                protocol = "ws"
            }else{
                protocol = "wss"
            }
            this.websocket = new WebSocket(protocol+'://'+host+'/websocket/log/' + token);
            this.websocket.onopen = this.open;
            this.websocket.onmessage = this.onmessage;
        },
        // websocket连接
        open(event) {
            console.log("socket连接成功");
        },
        onmessage(event) {
            $("#log-container").append(event.data);
        },
        closeCurrentData() {
            this.currentVisible = false;
            this.websocket.close();
        },
        //数组对象方法排序:
        sortByKey(array, key) {
            return array.sort(function (a, b) {
                var x = a[key];
                var y = b[key];
                return y.localeCompare(x)
            });
        }

    }
});