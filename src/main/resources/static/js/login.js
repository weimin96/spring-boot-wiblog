'use strict';

let app = new Vue({
    el: "#app",
    data: {
        account: '',
        password: '',
        msgError: ''
    },
    created(){
        this.init();
    },
    methods: {
        init: function () {
            if (user !== null){
                window.location.href = '/';
            }
            this.account=getQueryVariable("name");
        },
        login: function (event) {
            if (this.password === "" || this.account === ""){
                this.msgError = "用户名和密码不能为空";
                return;
            }
            if(this.password.length<6){
                this.msgError="密码长度必须大于6位";
                return;
            }
            $.post('/u/login', {
                account: app.account,
                password: app.password
            },function (res) {
                if (res.code === 10000){
                    location.href = Cookies.get('back');
                }else{
                    app.msgError = res.msg;
                }
            })
        },
        githubLogin(){
            window.location.href = "https://github.com/login/oauth/authorize?client_id=9d543dc4501558c6759f&redirect_uri=https://127.0.0.1/u/github/callback&response_type=code&state=login";
        }
    }
});
