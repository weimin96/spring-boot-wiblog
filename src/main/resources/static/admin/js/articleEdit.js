'use strict';

var vm;
var app = new Vue({
    el: "#app",
    data: {
        // 分类列表
        categoryList: [],
        categoryIds: [-1],
        // selected: '',
        tagTemp: '',
        tagList: [],
        // 是否显示标签编辑框
        isShowTagInput: false,
        articleId: '',
        article: {
            title: '',
            content: '',
            categoryId: -1,
            category: '',
            articleSummary: '',
            tags: '',
            imgUrl: '',
            articleUrl:'',
            privately: false,
            reward: false,
            comment: true
        },
        editor: {},
        pushLoading: false
    },
    beforeCreate() {
        vm = this;
    },
    created() {
        this.getCategory();
    },
    mounted() {
        if (this.articleId === '') {
            this.initEditor();
        }
    },
    methods: {
        // 初始化编辑器
        initEditor: function (md) {
            this.editor = editormd("editor", {
                width: "100%",
                height: "420",
                syncScrolling: true,
                path: "../lib/editor.md/lib/",
                markdown: md,
                imageUpload: true,
                imageFormats: ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
                imageUploadURL: "/uploadImageForEditorMd",
                editorTheme: "neo",
                watch: false,
                toolbarIcons: function () {
                    return ["bold", "italic", "del", "h1", "h2", "h3", "h4", "h5", "h6", "quote", "hr", "list-ul", "list-ol", "|", "table", "code", "link", "image", "|", "undo", "redo", "search", "goto-line", "watch", "preview", "fullscreen"]
                },
            });
        },
        // 初始化数据
        initData: function (categoryData) {
            this.articleId = window.location.search.substr(4);
            var id = this.articleId;
            if (id === '') {
                return;
            }
            $.get("/post/get/" + id, function (res) {
                if (res.code === 10000) {
                    vm.article = res.data;
                    vm.tagList = vm.article.tags.slice().split(/[\n\s+,，]/g);
                    vm.categoryIds=[];
                    vm.setCategoryIds(categoryData,vm.article.categoryId);
                    vm.initEditor(vm.article.content);
                }
            });
        },
        // 获取分类列表
        getCategory: function () {
            $.get("/category/getCategory", function (data) {
                if (data.code === 10000) {
                    vm.categoryList = vm.setCategoryTree(data.data,0);
                    vm.initData(data.data);
                }
            });
        },
        // 构造分类级联列表
        setCategoryTree: function(data,pid){
            let tree = [];
            for (let i = 0; i < data.length; i++) {
                if(data[i].parentId === pid){
                    data[i].value=data[i].id;
                    data[i].label=data[i].name;
                    data[i].children=vm.setCategoryTree(data,data[i].id);
                    tree.push(data[i]);
                }
            }
            if (tree.length === 0){
                return null;
            }
            return tree;
        },
        // 获取已有的分类id列表
        setCategoryIds: function(data,id){
            for (let i = 0; i <data.length ; i++) {
                if(data[i].id === id){
                    if (data[i].parentId !== 0){
                        vm.setCategoryIds(data,data[i].parentId);
                        vm.categoryIds.push(id);
                        return;
                    }else{
                        vm.categoryIds.push(id);
                        return;
                    }
                }
            }
        },
        // 文章发表修改
        pushArticle: function () {
            this.pushLoading =true;
            this.article.content = this.editor.getMarkdown();
            this.article.articleSummary = this.article.content.substr(0, 200);
            // 发表新文章
            if (this.articleId === '' || this.articleId === null) {
                $.post('/post/push', {
                    title: this.article.title,
                    content: this.article.content,
                    tags: this.article.tags,
                    categoryId: this.article.categoryId,
                    imgUrl: this.article.imgUrl,
                    privately: this.article.privately,
                    reward: this.article.reward,
                    comment: this.article.comment,
                }, function (res) {
                    if (res.code === 10000) {
                        window.parent.location.href = window.location.protocol + "//" + window.location.host + res.data;
                    } else {
                        vm.pushLoading =false;
                        vm.$message.error(res.msg);
                    }
                })
            } else {
                $.post('/post/update', {
                    id: this.articleId,
                    title: this.article.title,
                    content: this.article.content,
                    tags: this.article.tags,
                    categoryId: this.article.categoryId,
                    imgUrl: this.article.imgUrl,
                    privately: this.article.privately,
                    reward: this.article.reward,
                    comment: this.article.comment,
                }, function (res) {
                    if (res.code === 10000) {
                        window.parent.location.href = window.location.protocol + "//" + window.location.host + vm.article.articleUrl;
                    } else {
                        vm.pushLoading =false;
                        vm.$message.error(res.msg);
                    }
                })
            }
        },
        // 点击显示标签编辑框
        showTagInput: function () {
            this.isShowTagInput = true;
            this.$nextTick(_ => {
                this.$refs.saveTagInput.$refs.input.focus();
            });
        },
        // 标签输入完成
        handleInputConfirm: function () {
            var tagTemp = this.tagTemp;
            if (tagTemp) {
                this.tagList.push(tagTemp);
                this.article.tags = this.tagList.toString();
            }
            this.isShowTagInput = false;
            this.tagTemp = '';
        },
        // 删除标签
        handleClose: function (tag) {
            this.tagList.splice(this.tagList.indexOf(tag), 1);
        },
        // 设置文章分类
        changeCategory: function (value) {
            this.article.categoryId=value[value.length-1]
        },
        // 上传图片成功
        uploadImgSuccess(res, file) {
            this.article.imgUrl = res.data;
        }
    }
});
