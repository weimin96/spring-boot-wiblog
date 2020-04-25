'use strict';

let vue = new Vue({
    el: "#index-header",
    data: {
        auth:"",
        category: [],
        focusSearchLabel: false,
        searchInput: "",
        isLogin: false,
        avatarImg: "",
        username: "",
        showUserMessage: false,
        showMessage: false,
        userUrl: "",
        messageCount: 0,
        typeList: [0, 0, 0, 0, 0],
    },
    mounted() {
        this.getCategory();
        if (user != null) {
            this.isLogin = true;
            this.avatarImg = user.avatarImg;
            this.username = user.username;
            this.userUrl = user.uid*12345;
            this.getMessage();
            // 管理权限
            this.getAuth();
        }
    },
    methods: {
        getAuth(){
            $.get("/role/getAdminUrl",function (res) {
                if (res.code === 10000){
                    vue.auth = res.data;
                }
            })
        },
        getMessage() {
            $.get("/getMessageCount", function (data) {
                if (data.code === 10000) {
                    $.each(data.data, function (index, item) {
                        vue.typeList[item.type] = item.count;
                        vue.messageCount += item.count;
                    });
                }
            });
        },
        getCategory: function () {
            $.get("/category/getCategory", function (data) {
                if (data.code === 10000) {
                    vue.category = vue.setCategoryTree(data.data, 0);
                    vue.$nextTick(function () {
                        $("#super-menu").superfish();
                    })
                }
            });
        },
        // 构造分类级联列表
        setCategoryTree: function (data, pid) {
            let tree = [];
            for (let i = 0; i < data.length; i++) {
                if (data[i].parentId === pid) {
                    data[i].value = data[i].id;
                    data[i].label = data[i].name;
                    data[i].children = this.setCategoryTree(data, data[i].id);
                    tree.push(data[i]);
                }
            }
            if (tree.length === 0) {
                return null;
            }
            return tree;
        },
        /*showSearchBtn(e){
            this.showSearch = true;
            this.$nextTick(function (e) {
                $("#searchInput").focus();
            });

        },*/
        focusSearch() {
            this.focusSearchLabel = true;
            $("#searchInput").focus();
        },
        blurSearch() {
            this.focusSearchLabel = false;
            this.searchInput = "";
        },
        // 搜索
        search(){
            if (this.searchInput.trim() === ""){
                return;
            }
            window.location.href=window.location.protocol+"//"+window.location.host+"/search?key="+this.searchInput;
        },
        // 管理后台
        gotoAdmin(){
            window.location.href = this.auth;
        },
        // 个人中心
        gotoUserCenter() {
            window.parent.location.href = "/user/" + this.userUrl;
        },
        logout() {
            $.get("/u/logout", function (res) {
                if (res.code === 10000) {
                    window.location.reload();
                }
            })
        },
        login() {
            Cookies.set('back', window.location.href, {expires: 1, path: '/'});
            window.location.href = "/login";
        }
    }
});

Vue.component('menu-tree', {
    props: ['value'],
    template: '<li v-if="value.children"><a href="javascript:;">{{value.name}}</a>' +
        '<ul><menu-tree v-for="(item,key) in value.children" :key="key" v-bind:value="item"></menu-tree></ul></li>' +
        '<li v-else><a class="menu-item" :href="\'/category/\'+value.url">{{value.name}}</a></li>'
});

// 获取url参数
function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] === variable){return decodeURI(pair[1]);}
    }
    return("");
}


