var vm;
var app = new Vue({
    el: "#app",
    data: {
        categoryList: [],
        // 插入/修改分类
        type: "",
        id: "",
        title: "",
        name: "",
        url: "",
        rank: "",
        parentId: 0,
        addCategoryVisible: false
    },
    beforeCreate() {
        vm = this;
    },
    mounted() {
        this.init();
    },
    methods: {
        init: function () {
            this.getCategory();
        },
        // 获取分类列表
        getCategory: function () {
            $.get("/category/getCategory", {type:"database"},function (data) {
                if (data.code === 10000) {
                    vm.categoryList = vm.setCategoryTree(data.data, 0, 1);
                }
            });
        },
        // 构造分类级联列表
        setCategoryTree: function (data, pid, level) {
            let tree = [];
            for (let i = 0; i < data.length; i++) {
                if(data[i].id === -1){
                    continue;
                }
                if (data[i].parentId === pid) {
                    data[i].children = vm.setCategoryTree(data, data[i].id, level + 1);
                    data[i].fullUrl = "/category/"+data[i].url;
                    data[i].level = level;
                    tree.push(data[i]);
                }
            }
            if (tree.length === 0) {
                return null;
            }
            return tree;
        },
        // 修改/插入分类弹窗
        addCategoryDialog: function (row,type) {
            this.type = type;
            if(type === "add"){
                this.title = "插入分类";
                this.parentId = row.id;
                // this.id = row.id;
            }else{
                this.title = "修改分类";
                this.name = row.name;
                this.url = row.url;
                this.rank = row.rank;
                this.id = row.id;
            }
            this.addCategoryVisible = true;
        },
        // 插入分类确认
        addCategory: function () {
            if(this.type === 'add'){
                $.post("/category/addCategory", {
                    name: this.name,
                    url: this.url,
                    parentId: this.parentId
                }, function (res) {
                    if (res.code === 10000) {
                        vm.$message({message: res.msg, type: "success"});
                        vm.addCategoryVisible = false;
                        vm.name = "";
                        vm.url = "";
                        vm.init();
                    }
                });
            }else{
                $.post("/category/updateCategory", {
                    id: this.id,
                    name: this.name,
                    url: this.url,
                    rank: this.rank
                }, function (res) {
                    if (res.code === 10000) {
                        vm.$message({message: res.msg, type: "success"});
                        vm.addCategoryVisible = false;
                        vm.name = "";
                        vm.url = "";
                        vm.init();
                    }
                });
            }

        },
        // 删除分类
        delCategory: function (row) {
            this.$confirm('是否删除该分类标签?', '警告', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                $.post("/category/delCategory", {id: row.id}, function (res) {
                    if (res.code === 10000) {
                        vm.$message({message: res.msg, type: "success"});
                        vm.init();
                    }
                });
            });
        },
        // 更新缓存
        updateCache(){
            $.get("/category/updateCache", function (res) {
                if (res.code === 10000) {
                    vm.$message({message: res.msg, type: "success"});
                }
            })
        }
    }
});