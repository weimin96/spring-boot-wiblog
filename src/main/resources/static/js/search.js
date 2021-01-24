let vm;
let app = new Vue({
    el: "#app",
    data: {
        contentList:[],
        category:{},
        searchVal:""
    },
    beforeCreate() {
        vm = this;
    },
    mounted() {
        this.getData();
    },
    methods: {
        getSearchData(){
            let key = getQueryVariable("key");
            this.searchVal = key;
            $.get("/post/searchArticle",{keyword:key},function (res) {
                if (res.code===10000){
                    vm.contentList = res.data;
                }
            })
        },
        getData: function () {
            $.get("/category/getCategory", function (data) {
                if (data.code === 10000) {
                    $.each(data.data,(index,item) => {
                        vm.category[item.id]=item.name;
                    });
                    vm.getSearchData()
                }
            });
        },
        // 搜索
        search(){
            if (this.searchVal.trim() === ""){
                return;
            }
            window.location.href="/search?key="+this.searchVal;
        },
    },filters: {
        articleDateFormat: function (d) {
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

            return year+"年"+month + "月" + day+"日";
        }
    }
});