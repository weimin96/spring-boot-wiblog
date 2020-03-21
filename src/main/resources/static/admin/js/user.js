var vm;
var app = new Vue({
    el: "#app",
    data: {
        searchKey: "",
        tableData: [],
        pageNum: 1,
        pageSize: 10,
        orderBy: 'asc',
        total: 0,
        title: "",
        username: "",
        state: 1,
        // 所有文章标题
        titleArray:[],
        // 所有用户名
        nameArray:[],
        // 状态复选框
        stateList:["正常"],
        // 用户授权对话框
        roleDialogVisible: false,
        // 权限列表
        roleList: [],
        // 被授权用户id
        roleUid: '',
        // 被授权用户角色id
        userRoleId: ''
    },
    beforeCreate(){
       vm = this;
    },
    mounted() {
        this.initCommentList();
        this.init();
    },
    methods: {
        init: function () {
            $.get("/u/getAllUsername",function (res) {
                if (res.code === 10000) {
                    vm.nameArray = res.data;
                }
            });
        },
        //列表
        initCommentList:function(){
            $.post("/u/userManageListPage", {
                username:this.username,
                state: this.state,
                pageSize: this.pageSize,
                pageNum: this.pageNum,
                orderBy:this.orderBy}, function (res) {
                if (res.code === 10000) {
                    vm.tableData = res.data.records;
                    vm.total = res.data.total;
                }
            });
        },
        dateFormatter: function (row, column) {
            let date = new Date(row.createTime);
            let year = date.getFullYear();
            let month = change(date.getMonth());
            let day = change(date.getDate());
            var hour=change(date.getHours());
            var minute=change(date.getMinutes());

            function change(t) {
                if (t < 10) {
                    return "0" + t;
                } else {
                    return t;
                }
            }
            return year + "/" + month + "/" + day+" "+hour+":"+minute;
        },
        // 按用户名查找
        queryNameSearch: function(queryString, cb) {
            var nameArray = this.nameArray;
            var results = queryString ? nameArray.filter(this.createFilter(queryString)) : nameArray;
            // 调用 callback 返回建议列表的数据
            cb(results);
        },
        // 过滤器
        createFilter(queryString) {
            return (array) => {
                return (array.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
            };
        },
        // 选择用户名
        selectName(item) {
            this.pageNum = 1;
            this.initCommentList();
        },
        nameIconClick(ev){
            this.pageNum = 1;
            this.initCommentList();
        },
        // 查看用户
        handleView: function(index,row){
            var d = new Date(row.createTime);
            window.parent.location.href = "/user/"+row.uid*12345;
        },
        // 用户授权
        handleEdit: function(index,row){
            this.roleUid = row.uid;
            $.post("/role/getAllRole",function (res) {
                if (res.code === 10000){
                    vm.roleList = res.data;
                    vm.roleDialogVisible = true;
                }else{
                    vm.$message.error(res.msg);
                }
            });
            vm.userRoleId = '';
            $.post("/role/getUserRole",{uid:row.uid},function (res) {
                if (res.code === 10000){
                    if(res.data!==undefined){
                        vm.userRoleId = res.data.roleId;
                    }
                }
            });
        },
        // 确认授权
        roleDialogEnter: function(){
            $.post("/role/assignPermission",{uid:this.roleUid,roleId:this.userRoleId},function (res) {
                if (res.code === 10000){
                    vm.$message({message:"分配权限成功",type: 'success'});
                    vm.roleDialogVisible = false;
                }else {
                    vm.$message.error(res.msg);
                }
            });
        },
        // 用户注销
        handleDelete: function(index,row){
            $.post("/u/deleteUser",{id:row.uid},function (res) {
                if(res.code === 10000){
                    vm.$message({message:"注销用户成功",type: 'success'});
                    vm.initCommentList();
                    vm.init();
                }else{
                    vm.$message.error(res.msg);
                }
            })
        },
        handlePageNum: function (val) {
            this.pageNum=val;
            this.initCommentList();
        },
        // 时间排序
        sortChange: function (column) {
            if(column.order === "ascending"){
                this.orderBy = "asc";
            }else{
                this.orderBy = "desc";
            }
            this.initCommentList();
        }

    }
});