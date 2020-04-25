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

                    vm.logList = [{name: "log.2020-04-15.0.log", path: "/home/pwm/log/log.2020-04-15.0.log"},
                    {name: "log.2020-04-05.0.log", path: "/home/pwm/log/log.2020-04-05.0.log"},
                    {name: "log.2020-03-31.0.log", path: "/home/pwm/log/log.2020-03-31.0.log"},
                    {name: "log.log", path: "/home/pwm/log/log.log"},
                    {name: "log.2020-04-06.0.log", path: "/home/pwm/log/log.2020-04-06.0.log"},
                    {name: "log.2020-03-21.0.log", path: "/home/pwm/log/log.2020-03-21.0.log"},
                    {name: "log.2020-04-13.0.log", path: "/home/pwm/log/log.2020-04-13.0.log"},
                    {name: "log.2020-04-16.0.log", path: "/home/pwm/log/log.2020-04-16.0.log"},
                    ]//res.data;

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
            this.websocket = new WebSocket('wss://www.wiblog.cn/websocket/log/' + token);
            // this.websocket = new WebSocket('wss://127.0.0.1/websocket/log/'+token);
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