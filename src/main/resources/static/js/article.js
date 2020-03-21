(function () {
    'use strict';

    $('body').scrollspy({target: '#navbar-example'});
    $('#navbar-card').affix({
        offset: {
            top: 160,
            bottom: ($('footer').outerHeight(true)) + 40
        }
    });


    let that;

    let app = new Vue({
        el: "#app",
        components: {
            popularArticle
        },
        data: {
            nologin: true,
            author: '',
            tagList: '',
            categoryList: [],
            article: {
                title: '',
                content: '',
                createTime: '',
                id: '',
                commentsCounts: 0,
                hits: 0,
                likes: 0,
                categoryId: 0,
                reward: false,
                comment: false
            },
            // 评论分页
            pageSize: 10,
            pageNum: 1,
            total: 0,
            //评论文章文本内容
            replyContent: '',
            // 评论用户文本内容
            replyUserContent: '',
            // 评论用户头像
            user_avatar: "/img/reply-avatar.svg",
            // 评论排序
            orderBy: 'asc',
            commentList: [],
            // 是否有评论
            isHasComment: false,
            // 更多评论弹出层
            moreCommentVisible: false,
            // 单条主评论索引
            showIndex: 0,
            commentItem: {},
            // 格式化评论时间
            now: "",
            commentTime: [60 * 60 * 3 * 1000, 60 * 60 * 2 * 1000, 60 * 60 * 1000, 60 * 30 * 1000, 60 * 10 * 1000, 60 * 5 * 1000],
            commentTimeStr: ["三小时前", "两小时前", "一小时前", "半小时前", "10分钟前", "刚刚"],
            showReplyIndex: -1,
            showReplyDialogIndex: -1,
            // 父评论id
            parentId: 0,
            // 导航
            navList: [],
            // 点赞
            isLike: "0"

        },
        beforeCreate: function () {
            that = this;
        },
        created() {
            this.initData();
        },
        mounted() {
            this.$bus.emit('popular-article-init');
        },
        methods: {
            // 生成目录
            getNavTree(content) {
                let tempArr = [];
                content.replace(/<h(2|3) id="(.*)?">(.*)?</g, function (match, m1, m2, m3) {
                    tempArr.push({
                        level: m1,
                        id: m2,
                        title: m3,
                        children: []
                    });
                });
                let navIndex = -1;
                tempArr.forEach(item => {
                    if (item.level === "2") {
                        that.navList.push(item);
                        navIndex++;
                    } else {
                        that.navList[navIndex].children.push(item)
                    }
                });
                return this.navList;
            },
            initData: function () {
                new Promise((resolve, reject) => {
                    $.get("/post/getArticle", {url: window.location.pathname}, function (res) {
                        if (res.code === 10000) {
                            that.article = res.data;
                            that.author = res.data.author;
                            that.tagList = res.data.tags.slice().split(/[\n\s+,，]/g);
                            let htmlContent = marked(that.article.content);
                            document.getElementById('content').innerHTML = htmlContent;
                            that.navList = that.getNavTree(htmlContent);
                            // 点赞
                            that.isLike = localStorage.getItem("article_" + that.article.id);
                            // 点击
                            that.hit();
                            resolve(that.article.comment);
                        }
                    });
                }).then(function (value) {
                    if (value) {
                        // 获取评论
                        that.getComment(that.orderBy);
                        that.getCategoryList();

                    }
                });

                // 评论框
                if (user !== null) {
                    this.nologin = false;
                    if (user.avatarImg !== "") {
                        this.user_avatar = user.avatarImg;
                    }
                }


            },
            getCategoryList() {
                $.get("/category/getCategory", function (data) {
                    if (data.code === 10000) {
                        that.getParentCategory(data.data, that.article.categoryId);
                    }
                });
            },
            getParentCategory(list, id) {
                if (id === 0) {
                    return;
                }
                $.each(list, function (index, item) {
                    if (item.id === id) {
                        that.getParentCategory(list, item.parentId);
                        that.categoryList.push(item);
                        return null;
                    }
                });
            },
            // 评论排序
            getComment: function (orderBy) {
                this.orderBy = orderBy;
                this.showReplyIndex = -1;
                $.post("/comment/commentListPage", {
                    pageSize: this.pageSize,
                    pageNum: this.pageNum,
                    articleId: that.article.id,
                    orderBy: this.orderBy
                }, function (res) {
                    if (res.code === 10000) {
                        that.commentList = res.data.data.records;
                        that.now = res.data.time;
                        that.total = res.data.data.total;
                        if (that.commentList.length > 0) {
                            that.isHasComment = true;
                            that.commentItem = that.commentList[that.showIndex];
                            // 评论成功滑到底部
                            if (that.moreCommentVisible) {
                                that.$nextTick(function () {
                                    var $dialog = document.getElementById("dialog");
                                    $dialog.scrollTop = $dialog.scrollHeight;
                                });
                            }
                        }
                    } else {
                        that.$message.error("获取评论失败");
                    }
                });
            },
            handlePageNum: function (val) {
                this.pageNum = val;
                this.getComment(this.orderBy);
            },
            // 回复评论
            replyUser: function (commentId) {
                if (this.replyUserContent.trim() === "") {
                    this.$message.error("请输入评论内容");
                    return;
                }
                $.post("/comment/reply", {
                        content: this.replyUserContent,
                        articleId: this.article.id,
                        parentId: this.parentId,
                        genId: commentId
                    },
                    function (res) {
                        if (res.code === 10000) {
                            that.$message({message: "评论成功", type: "success"});
                            that.replyUserContent = "";
                            that.initData();
                            that.showReplyIndex = -1;
                        } else {
                            that.$message.error(res.msg);
                        }
                    });
            },
            // 回复文章
            replyArticle: function () {
                if (this.replyContent.trim() === "") {
                    this.$message.error("请输入评论内容");
                    return;
                }
                $.post("/comment/reply", {content: this.replyContent, articleId: this.article.id},
                    function (res) {
                        if (res.code === 10000) {
                            that.$message({message: "评论成功", type: "success"});
                            that.replyContent = "";
                            that.initData();
                            that.showReplyIndex = -1;
                        } else {
                            that.$message.error(res.msg);
                        }
                    });
            },
            // 回复评论框显示
            showReplyBtn: function (index, parentId) {
                this.showReplyIndex = this.showReplyIndex === index ? -1 : index;
                // 楼中楼回复时设置父评论id
                this.parentId = parentId;
            },
            // 弹出层回复评论框显示
            showReplyDialogBtn: function (index, parentId) {
                this.showReplyDialogIndex = this.showReplyDialogIndex === index ? -1 : index;
                // 楼中楼回复时设置父评论id
                this.parentId = parentId;
            },
            // 更多评论弹出层
            showMoreComment: function (index) {
                this.moreCommentVisible = true;
                this.showIndex = index;
                this.commentItem = this.commentList[index];
            },
            login() {
                Cookies.set('back', window.location.href, {expires: 1, path: '/'});
                window.location.href = "/login";
            },
            like() {
                if (this.isLike === "1") {
                    this.$message.error("您的爱心太过泛滥啦...");
                    return;
                }
                $.post("/post/record/like", {articleId: this.article.id}, function (res) {
                    if (res.code === 10000) {
                        that.article.likes = res.data;
                        localStorage.setItem("article_" + that.article.id, "1");
                        that.$message({"message": "点赞成功", "type": "success"});
                    }
                });
            },
            hit(){
                $.post("/post/record/hit", {articleId: this.article.id}, function (res) {
                    if (res.code === 10000) {
                        that.article.hits = res.data;
                    }
                });
            }
        },
        filters: {
            // 用于评论的时间格式化
            commentDateFormat: function (d) {
                var date = new Date(d);
                var year = date.getFullYear();
                var month = change(date.getMonth());
                var day = change(date.getDate());
                var hour = change(date.getHours());
                var minute = change(date.getMinutes());

                function change(t) {
                    if (t < 10) {
                        return "0" + t;
                    } else {
                        return t;
                    }
                }

                var result = "";
                $.each(that.commentTime, function (index, item) {
                    if (that.now - item > date.getTime()) {
                        return null;
                    } else {
                        result = that.commentTimeStr[index];
                    }
                });
                // 返回数组内的时间文字
                if (result !== "") {
                    return result;
                }
                // 同一年不显示年份
                if (new Date(that.now).getFullYear() !== year) {
                    result += year + "."
                }
                return result + month + "." + day + " " + hour + ":" + minute;
            },
            articleDateFormat: function (d) {
                var date = new Date(d);
                var year = date.getFullYear();
                var month = change(date.getMonth() + 1);
                var day = change(date.getDate());

                function change(t) {
                    if (t < 10) {
                        return "0" + t;
                    } else {
                        return t;
                    }
                }

                return year + "-" + month + "-" + day;
            }
        }
    });


}());


scrolltotop.init();