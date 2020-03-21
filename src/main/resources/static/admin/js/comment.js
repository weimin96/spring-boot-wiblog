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
        stateList:["正常"]
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
            $.post("/post/allArticles",function (res) {
                if (res.code === 10000) {
                    vm.titleArray = res.data;
                }
            });
            $.get("/u/getAllUsername",function (res) {
                if (res.code === 10000) {
                    vm.nameArray = res.data;
                }
            });
        },
        //评论列表
        initCommentList:function(){
            $.post("/comment/commentManageListPage", {title:this.title,
                username:this.username,
                state: this.state,
                pageSize: this.pageSize,
                pageNum: this.pageNum,
                orderBy:this.orderBy}, function (res) {
                if (res.code === 10000) {
                    vm.tableData = res.data.records;
                    vm.total = res.data.total;
                    /*$.each(vm.tableData,function (index,item) {
                        item.state = vm.opsFormatter(item);
                    });*/
                }
            });
        },
        stateFormatter: function(row,column){
            var state = row.state;
            return state === 1?"正常":"删除";
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
        // 按文章标题查找
        queryTitleSearch: function(queryString, cb) {
            var titleArray = this.titleArray;
            var results = queryString ? titleArray.filter(this.createFilter(queryString)) : titleArray;
            // 调用 callback 返回建议列表的数据
            cb(results);
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
        // 选择文章标题
        selectTitle(item) {
            this.pageNum = 1;
            this.initCommentList();
        },
        titleIconClick(ev){
            this.pageNum = 1;
            this.initCommentList();
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
        // 更改查找状态
        changeState: function(value){
            this.pageNum = 1;
            if(this.stateList.length === 2){
                this.state = null;
            }else if (this.stateList[0] === "正常"){
                this.state = 1;
            }else{
                this.state = 0;
            }
            this.initCommentList();
        },
        // 修改评论状态
        handleUpdateState: function (row) {
            if(row.state === 1){
                $.post("/comment/deleteComment",{id:row.id},function (res) {
                    if(res.code === 10000){
                        vm.$message({message:res.msg,type: 'success'});
                        vm.initCommentList();
                    }
                });
            }else{
                $.post("/comment/restoreComment",{id:row.id},function (res) {
                    if(res.code === 10000){
                        vm.$message({message:"恢复成功",type: 'success'});
                        vm.initCommentList();
                    }
                });
            }
        },
        handlePageNum: function (val) {
            this.pageNum=val;
            this.initCommentList();
        },
        // 排序
        sortChange: function (column) {
            if(column.order === "ascending"){
                this.orderBy = "asc";
            }else{
                this.orderBy = "desc";
            }
            this.initCommentList();
        }

    },
    filters:{
        opsFormatter: function (state) {
            return state === 1 ? "删除":"恢复";
        }
    }
});