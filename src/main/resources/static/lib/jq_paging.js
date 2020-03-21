(function ($, window, document, undefined) {
    //定义分页类
    function Paging(element, options) {
        this.element = element;
        //传入形参
        this.options = {
            pageNum: options.pageNum || 1,
            pages: options.pages,
            callback: options.callback
        };
        //根据形参初始化分页html和css代码
        this.init();
    }

    //对Paging的实例对象添加公共的属性和方法
    Paging.prototype = {
        constructor: Paging,
        init: function () {
            this.creatHtml();
            this.bindEvent();
        },
        creatHtml: function () {
            var me = this;
            var content = "";
            var pageNum = me.options.pageNum;
            var pages = me.options.pages;
            var isLastPage = false;
            var hasNextPage = false;
            if (pageNum === pages){
                isLastPage = true;
            }else {
                hasNextPage = true;
            }
            content += '<ul class="pagination">';

            // 如果不是第一页 显示首页和上一页
            if (pageNum !== "1") {
                content += '<li><a href="#" id="startPage" class="extend" title="首页">«</a></li>';
                content += '<li><a href="#" id="prePage" class="prev" title="上一页">&lt;</a></li>';
            }

            //显示页码
            // 第一页显示在最左边
            if (pageNum === 1) {
                for (let i = 1; i < 4 && i <= pages; i++) {
                    if (1 === i) {
                        content += '<li class="active"><a href="#">' + 1 + '</a></li>';
                    } else {
                        content += '<li><a href="#">' + i + '</a></li>';
                    }
                }
            } else {

                // 如果有下一页显示在中间
                if (hasNextPage) {
                    content += '<li><a href="#">' + (pageNum - 1) + '</a></li>';
                    content += '<li class="active"><a href="#">' + pageNum + '</a></li>';
                    content += '<li><a href="#">' + (pageNum + 1) + '</a></li>';
                } else if (pageNum > 2) {
                    content += '<li><a href="#">' + (pageNum - 2) + '</a></li>';
                    content += '<li><a href="#">' + (pageNum - 1) + '</a></li>';
                    content += '<li class="active"><a href="#">' + pageNum + '</a></li>';
                } else {
                    content += '<li><a href="#">' + (pageNum - 1) + '</a></li>';
                    content += '<li class="active"><a href="#">' + pageNum + '</a></li>';
                }
            }

            // 如果是最后一页 不显示末页和下一页
            if (!isLastPage) {
                content += '<li><a href="#" id="nextPage" class="next" title="下一页">&gt;</a></li>';
                content += '<li><a href="#" id="endPage" class="extend" title="尾页">»</a></li>';
            }

            me.element.html(content);
        },
        //添加页面操作事件
        bindEvent: function () {
            let me = this;
            me.element.off('click', 'a');
            me.element.on('click', 'a', function () {
                let currentPage = $(this).html();
                let id = $(this).attr("id");
                if (id === "prePage") {
                    if (me.options.pageNum === 1) {
                        me.options.pageNum = 1;
                    } else {
                        me.options.pageNum = +me.options.pageNum - 1;
                    }
                } else if (id === "nextPage") {
                    if (me.options.pageNum === me.options.pages) {
                        me.options.pageNum = me.options.pages
                    } else {
                        me.options.pageNum = +me.options.pageNum + 1;
                    }
                } else if (id === "startPage") {
                    me.options.pageNum = 1;
                }else if (id === "endPage") {
                    me.options.pageNum = me.options.pages;
                }else {
                    me.options.pageNum = +currentPage;
                }
                me.creatHtml();
                if (me.options.callback) {
                    me.options.callback(me.options.pageNum);
                }
            });
        }
    };
    //通过jQuery对象初始化分页对象
    $.fn.paging = function (options) {
        return new Paging($(this), options);
    }
})(jQuery, window, document);