'use strict';

var patternUserName = /^(?!\d+$|.*?[`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·！￥…（）—《》？：“”【】、；‘’，。]+).+$/;
var patternEmail = /\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/;
let app = new Vue({
        el: "#app",
        data: {
            username: "",
            phone: "",
            phoneCode:"",
            imgCode:"",
            email: "",
            emailCode:"",
            password: "",
            confirmPassword: "",
            msgName: "",
            msgPhone: "",
            msgImgCode:"",
            msgPhoneCode:"",
            msgEmail: "",
            msgEmailCode: "",
            msgConfirmPassword: "",
            msgPassword: "",
            nameTag: "0",
            phoneTag: "0",
            phoneCheck:"0",
            imgTag:"0",
            emailTag: "0",
            emailCheck: "0",
            passwordTag: "0",
            confirmPasswordTag: "0",
            showEmailCode:false,
            showPhoneCode:false,
            emailCodeBtnMsg:"获取验证码",
            phoneCodeBtnMsg:"获取验证码",
            emailCodeButtonDisabled:false,
            phoneCodeButtonDisabled:false
        },
        methods: {
            checkName: function () {
                if(this.username === ""){
                    this.msgName = "用户名不能为空";
                    this.nameTag = '2';
                    return false;
                }
                if (this.username.length<4 || this.username.length>32){
                    this.msgName = "用户名长度必须大于4个字符且小于32字符";
                    this.nameTag = '2';
                    return false;
                }
                if(!patternUserName.test(this.username)){
                    this.msgName = "用户名不能为纯数字或带有特殊字符";
                    this.nameTag = '2';
                    return false;
                }

                $.post('/u/checkUsername', {
                    value: app.username
                }, function (res) {
                    if (res.code === 10000) {
                        app.msgName = '';
                        app.nameTag = '1';
                        return true;
                    } else {
                        app.msgName = res.msg;
                        app.nameTag = '2';
                        return false;
                    }
                });
            },
            checkPhone: function () {
                if (this.phone.length === 0) {
                    this.msgPhone = '';
                    this.phoneTag = '0';
                    this.phoneCheck = '0';
                    this.showPhoneCode = false;
                    return true;
                }
                $.post('/u/checkPhone', {value: app.phone}, function (res) {
                    if (res.code === 10000) {
                        app.msgPhone = '';
                        app.phoneTag = '3';
                        return true;
                    } else {
                        app.msgPhone = res.msg;
                        app.phoneTag = '2';
                        app.emailCheck = '2';
                        return false;
                    }
                });
            },
            checkImgCode(){
                if (this.phone.length !== 0 && this.imgCode === ""){
                    this.msgImgCode = "请输入验证码";
                    this.imgTag = "0";
                    this.phoneCheck = '2';
                }
                $.post("/checkVerify",{code:this.imgCode},function (res) {
                    if (res){
                        app.msgImgCode = "";
                        app.imgTag = "1";
                    } else{
                        app.msgImgCode = "验证码错误";
                        app.phoneCheck = '2';
                        app.imgTag = "2";
                    }
                })
            },
            checkPhoneCode(){
                if (this.phoneCode.length===0){
                    this.msgPhoneCode="请输入验证码";
                    app.phoneCheck = '2';
                    return;
                }
                if (this.phoneCode === "1111"){
                    app.msgPhoneCode="";
                    app.phoneCheck = '1';
                } else{
                    app.msgPhoneCode="验证码错误";
                    app.phoneCheck = '2';
                }
            },
            checkEmail: function () {
                if (this.email.length === 0) {
                    this.msgEmail = '';
                    this.emailTag = '0';
                    this.showEmailCode = false;
                    this.msgEmailCode="";
                    this.emailCheck = "0";
                    return true;
                }
                if (!patternEmail.test(this.email)) {
                    this.msgEmail = "邮箱格式不正确";
                    this.emailTag = '2';
                    return false;
                }
                $.post('/u/checkEmail', {
                    value: this.email
                }, function (res) {
                    if (res.code === 10000) {
                        app.msgEmail = '';
                        app.emailTag = '3';
                        return true;
                    } else {
                        app.msgEmail = res.msg;
                        app.emailTag = '2';
                        app.emailCheck = "2";
                        return false;
                    }
                });
            },
            checkEmailCode(){
                if (this.emailCode.length===0){
                    this.msgEmailCode="请输入验证码";
                    app.emailCheck = '2';
                    return;
                }
                $.post("/checkEmailCode",{email:this.email,code:this.emailCode},function (res) {
                    if (res.code === 10000){
                        app.msgEmailCode="";
                        app.emailCheck = '1';
                    } else {
                        app.msgEmailCode="验证码错误";
                        app.emailCheck = '2';
                    }
                })
            },
            checkPassword: function () {
                if (this.password.length < 6) {
                    this.msgPassword = '密码长度必须大于6位';
                    this.passwordTag = '2';
                    return false;
                } else {
                    this.msgPassword = '';
                    this.passwordTag = '1';
                    return true;
                }
            },
            checkConfirmPassword: function () {
                if (this.password !== this.confirmPassword) {
                    this.msgConfirmPassword = '两次密码不一致';
                    this.confirmPasswordTag = '2';
                    return false;
                } else {
                    this.msgConfirmPassword = '';
                    this.confirmPasswordTag = '1';
                    return true;
                }
            },
            phoneButtonCheck(){
               if (this.phoneTag !== '3' || this.imgTag !=="1" ){
                   return true;
               } else{
                   return this.phoneCodeButtonDisabled;
               }
            },
            emailButtonCheck(){
                if (this.emailTag !== '3'){
                    return true;
                }else{
                    return this.emailCodeButtonDisabled;
                }
            },
            getVerify(){
                $("#imgCode").attr("src","/getVerify?"+Math.random());
            },
            getPhoneCode(){
                let time = 60;
                this.phoneCodeButtonDisabled=true;
                app.$message({message: "发送短信验证码成功", type: "success"});
                /*$.get("/getPhoneCheckCode", {phone: this.phone}, function (res) {
                    if (res.code === 10000) {
                        app.$message({message: "发送短信验证码成功", type: "success"});
                    }else{
                        app.$message.error(res.msg);
                    }
                });*/
                setInterval(function () {
                    if (time === 0) {
                        app.phoneCodeButtonDisabled = false;
                        app.phoneCodeBtnMsg = "获取验证码";
                        return;
                        //clearInterval();
                    }
                    app.phoneCodeBtnMsg = time + "s";
                    time--;
                }, 1000)
            },
            getEmailCode(){
                let time = 60;
                this.emailCodeButtonDisabled=true;
                $.get("/getEmailCheckCode", {email: this.email}, function (res) {
                    if (res.code === 10000) {
                        app.$message({message: "发送验证码成功，请登录邮箱查看", type: "success"});
                    }else{
                        app.$message.error(res.msg);
                    }
                });
                setInterval(function () {
                    if (time === 0) {
                        app.emailCodeButtonDisabled = false;
                        app.emailCodeBtnMsg = "获取验证码";
                        return;
                        //clearInterval();
                    }
                    app.emailCodeBtnMsg = time + "s";
                    time--;
                }, 1000)
            },
            register: function (event) {
                if (this.nameTag !== "1") {
                    this.checkName();
                    return;
                }

                if (this.phoneTag !== "2" && this.emailTag !== "2" && this.checkPassword()
                    && this.checkConfirmPassword()) {
                    let data = {
                        username: app.username,
                        password: app.password
                    };
                    if (app.phone !== '') {
                        data.phone = app.phone;
                    }
                    if (app.email !== '') {
                        data.email = app.email;
                        data.emailCode=app.emailCode;
                    }
                    $.post('/u/register', data, function (res) {
                        if (res.code === 10000) {
                            app.$message({"message":"注册成功","type":"success"});

                            setTimeout(function () {
                                window.location.href = '/login?name='+app.username;
                            },1000);

                        } else {
                            app.$message.error(res.msg);
                        }
                    })
                } else {
                    event.preventDefault();
                    app.$message.error("注册失败");
                    return false;
                }
            }

        }
});