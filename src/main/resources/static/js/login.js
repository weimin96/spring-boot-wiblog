'use strict';

let app = new Vue({
    el: "#app",
    data: {
        account: '',
        password: '',
        msgError: '',
        code:"",
        qrcodeVisable:false
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
                password: md5(app.password)
            },function (res) {
                if (res.code === 10000){
                    let url = Cookies.get('back');
                    if(url !== undefined && !url.endsWith('register')){
                        location.href = url;
                    }else{
                        location.href = "/";
                    }

                }else{
                    app.msgError = res.msg;
                }
            })
        },
        githubLogin(){
            $.post('/u/getGithubUrl', {type: 'login'},function (res) {
                if (res.code === 10000){
                    window.location.href = res.data;
                }
            })
        },
        wechatCode(){
            console.log("验证码"+this.code);
            $.get("/wx/login",{code:this.code},function (res) {
                if(res.code===10000){
                    let url = Cookies.get('back');
                    if(url !== undefined){
                        location.href = url;
                    }else{
                        location.href = "/";
                    }
                }else{
                    app.$message.error(res.msg);
                }
            })
        }
    }
});
