$(function () {
    let vm;
    let app = new Vue({
        el: "#app",
        data: {
            // 统计信息
            staticData:{
                articleCount:0,
                commentCount: 0,
                hitCount: 0,
                userCount: 0,
                profitCount: 0,
            },
            // 结束日期
            endTime: new Date(),
            // 开始日期
            startTime: new Date(),
            // 系统信息图表
            sysChart: {},
            sysData: [],
            period: 60*60,
            type: 3,
            // 访问信息图表
            hitChart:{},

            boxHeight:0,
            windowResize:0,
            timer:null,
            opsList:[]
        },
        beforeCreate() {
            vm = this;
        },
        mounted() {
            this.getStaticData();
            this.initSysChart();
            this.chooseType(this.type);
            this.initOpsList();
            this.windowResize = this.boxHeight = this.$refs.scrollList.offsetHeight
            this.boxHeight = this.$refs.scrollList.offsetHeight
            // window.addEventListener('resize', this.windowResize)
            this.getList();
        },
        methods: {
            // 获取统计信息
            getStaticData(){
              $.get("/getStaticData",function (res) {
                  if (res.code === 10000){
                      vm.staticData = res.data;
                  }
              })
            },
            // 获取系统信息
            getMonitorData() {
                new Promise((resolve, reject) => {
                    $.post("/getMonitorData", {
                        metric: "CPULoadAvg",
                        period: vm.period,
                        startTime: vm.startTime,
                        endTime: vm.endTime
                    }, function (res) {
                        if (res.code === 10000) {
                            let values = res.data.dataPoints[0].values;
                            resolve(values);
                        }
                    });
                }).then(function (value) {
                    $.post("/getMonitorData", {
                        metric: "MemUsed",
                        period: vm.period,
                        startTime: vm.startTime,
                        endTime: vm.endTime
                    }, function (res) {
                        if (res.code === 10000) {
                            let timestamps = res.data.dataPoints[0].timestamps;
                            let values = res.data.dataPoints[0].values;
                            vm.sysChart.setOption({
                                xAxis: {
                                    data: timestamps
                                },
                                series: [{
                                    data: value
                                }, {
                                    data: values
                                }]
                            })
                        }
                    });
                });

            },
            // 初始化系统信息图表
            initSysChart() {
                this.sysChart = echarts.init(document.getElementById('sysChart'));
                // 指定图表的配置项和数据
                let option = {
                    legend: {
                        data: ['CPU平均负载', '内存使用量'],
                        x: 'left',
                        padding: 20,
                    },
                    grid: {
                        top: 80
                    },
                    tooltip: {
                        trigger: 'axis',
                        backgroundColor: '#fff',
                        borderWidth: 1,
                        borderColor: '#909399',
                        textStyle: {
                            color: '#333',
                            fontWeight: 600,
                            fontSize: 12
                        },
                        formatter: function (params) {
                            var date = new Date(Number(params[0].name) * 1000);
                            var month = vm.formatNum((date.getMonth() + 1));
                            var day = vm.formatNum(date.getDate());
                            var hour = vm.formatNum(date.getHours());
                            var min = vm.formatNum(date.getMinutes());
                            return date.getFullYear() + "-" + month + "-" + day + " " +
                                hour + ":" + min + '<br />CPU平均负载： ' + params[0].value + '<br />内存使用量：' + params[1].value;
                        },
                    },
                    xAxis: {
                        data: [],
                        axisLine: {onZero: false},
                        interval: 4,
                        axisLabel: {
                            margin: 15,
                            formatter: function (value, index) {
                                var date = new Date(Number(value) * 1000);
                                var month = vm.formatNum((date.getMonth() + 1));
                                var day = vm.formatNum(date.getDate());
                                var hour = vm.formatNum(date.getHours());
                                var min = vm.formatNum(date.getMinutes());
                                if (vm.type === 1){
                                    return date.getHours()+"h";
                                }else if (vm.type === 2){
                                    return month + "/" + day+" "+hour+":"+min;
                                }else{
                                    return month + "/" + day;
                                }
                            },
                            interval: function (index, value) {
                                let date = new Date(Number(value) * 1000);
                                if (vm.type === 1){
                                    return date.getMinutes() === 0 && date.getHours() %2 ===0;
                                }else if (vm.type === 2){
                                    return date.getHours() % 12 === 0;
                                }else if (vm.type === 3) {
                                    return date.getHours() === 0;
                                }
                                return date.getDate() % 2 === 0;
                            }
                        },
                        axisTick: {
                            interval: function (index, value) {
                                let date = new Date(Number(value) * 1000);
                                if (vm.type === 1){
                                    return date.getMinutes() === 0 && date.getHours() %2 ===1;
                                } else if (vm.type === 2){
                                    return date.getHours() === 6 || date.getHours() === 18;
                                }else if (vm.type === 3){
                                    return date.getHours() === 12;
                                }
                                return  date.getDate() % 2 === 1;
                            }
                        }
                    },
                    yAxis: [
                        {
                            name: 'CPU平均负载',
                            type: 'value'
                        },
                        {
                            name: '内存使用量(MB)',
                            max: 1000,
                            type: 'value',
                        }
                    ],
                    series: [{
                        name: 'CPU平均负载',
                        type: 'line',
                        data: [],
                        itemStyle: {
                            color: '#3a8ee6',
                            // borderWidth:3,
                            // borderColor:'#4a9f09'
                        },
                        smooth: true
                    }, {
                        name: '内存使用量',
                        type: 'line',
                        yAxisIndex: 1,
                        data: [],
                        itemStyle: {
                            color: '#4a9f09',
                            // borderWidth:3,
                            // borderColor:'#3a8ee6'
                        },
                        smooth: true
                    }]
                };
                this.sysChart.setOption(option);
            },
            // 按钮 今天 近三天 近七天 近三十天
            chooseType(type){
                if(type === 1){
                    this.period = 300;
                    this.startTime.setFullYear(this.endTime.getFullYear());
                    this.startTime.setMonth(this.endTime.getMonth());
                    this.startTime.setDate(this.endTime.getDate());
                    this.startTime.setHours(0);
                    this.startTime.setMinutes(0);
                    this.startTime.setSeconds(0);
                }else if (type === 2){
                    this.period = 60*60;
                    this.startTime = new Date(this.endTime.getTime()-3*24*3600*1000);
                }else if (type === 3){
                    this.period = 60*60;
                    this.startTime = new Date(this.endTime.getTime()-7*24*3600*1000);
                }else{
                    this.period = 60*60*24;
                    this.startTime = new Date(this.endTime.getTime()-30*24*3600*1000);
                }
                this.type = type;
                // this.initSysChart();
                this.getMonitorData();
            },
            formatNum(num){
                if (num <10){
                    return "0"+num;
                }
                return num;
            },
            // 初始化访问信息图表
            initHitChart(){
                this.hitChart = echarts.init(document.getElementById('hitChart'));
            },
            // 自动滚动动画效果
            scrollFn() {
                this.timer = setInterval(() => {
                    if (this.$refs.scrollList.scrollTop + this.boxHeight === this.$refs.scrollList.scrollHeight) {
                        this.$refs.scrollList.scrollTop = 0
                    } else {
                        this.$refs.scrollList.scrollTop += 1
                    }
                }, 50)
            },
            getList(){
                clearInterval(this.timer)
                this.$nextTick(() => {
                    this.scrollFn()
                })
            },
            // 清除滚动效果定时器
            clearTimer() {
                clearInterval(this.timer)
            },
            initOpsList(){
                $.get("/getOpsData",function (res) {
                    if (res.code === 10000){
                        vm.opsList = res.data;
                    }
                })
            }
        },
        beforeDestroy() {
            if (this.timer) {
                this.clearTimer()
            }
        },
        filters: {
            dateFormat: function (d) {
                var date = new Date(d);
                var year = date.getFullYear();
                var month = change(date.getMonth()+1);
                var day = change(date.getDate());

                function change(t) {
                    if (t < 10) {
                        return "0" + t;
                    } else {
                        return t;
                    }
                }

                return year+"-"+month + "-" + day;
            }
        }
    });
});