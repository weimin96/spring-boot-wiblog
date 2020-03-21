'use strict';
let vm;
let app = new Vue({
    el: "#post-list",
    components: {
        popularArticle
    },
    data: {
        articleList: [],
        nearUserList: [],
    },
    beforeCreate: function () {
        vm = this;
    },
    mounted() {
        this.initData(1);
        this.$bus.emit('popular-article-init');
    },
    methods: {
        initData(currentPage) {
            $.ajax({
                type: 'POST',
                url: '/post/articles',
                dataType: 'json',
                data: {
                    pageNum: currentPage,
                    pageSize: "5"
                },
                success: function (data) {
                    vm.articleList = data.data.records;
                    $("#pagination").paging({
                        pageNum: data.data['current'],//当前所在页码
                        pages: data.data['pages'],//总页数
                        callback: function (currentPage) {
                            vm.initData(currentPage);
                        }
                    });
                }
            });
        },

        getGeoLocation() {
            if (user === null) {
                vm.$message.error("请登录后再次尝试");
                return;
            }
            let option = {
                enableHighAccuracy: true,
                maximumAge: 0,//禁用缓存
                timeout: 30000
            };
            navigator.geolocation.getCurrentPosition(position => {
                $.get("/getNearUser", {lat: position.coords.latitude, lng: position.coords.longitude}, res => {
                    if (res.code === 10000) {
                        vm.nearUserList = res.data;
                    }
                });
            }, e => {
                switch (e.code) {
                    case e.PERMISSION_DENIED:
                        vm.$message.error("请通过定位授权");
                        break;
                    case e.POSITION_UNAVAILABLE:
                        vm.$message.error("当前浏览器无法获取定位，请更换浏览器再次尝试");
                        break;
                    case e.TIMEOUT:
                        vm.$message.error("当前浏览器无法获取定位，请更换浏览器再次尝试");
                        break;
                }
            }, option);
        },
        gotoUser(uid){
            window.parent.location.href = "/user/" + uid*12345;
        }
    },
    filters: {
        dateFormat: function (d) {
            var date = new Date(d);
            var year = date.getFullYear();
            var month = switchNum(date.getMonth());
            var day = change(date.getDate());

            function change(t) {
                if (t < 10) {
                    return "0" + t;
                } else {
                    return t;
                }
            }

            function switchNum(month) {
                var arry = ["一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"];
                return arry[Number(month)];
            }

            return month + "月" + day + ", " + year;
        }
    }
});







