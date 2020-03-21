$(function(){
    //菜单点击
    $(".iframe_item").on('click',function(){
        var url = $(this).attr('data-url');
        $("#iframe").attr('src',url);
        return false;
    });

    $(".aside-select-li").on("click",function(){
        if($(this).hasClass("aside-select")){
            $(this).removeClass("aside-select");
        }else{
            $(this).addClass("aside-select");
        }
    });

    let $container = $("#container");
    $(".menu-fold").on("click",function () {
        if($container.hasClass("aside-hover")) {
            $container.removeClass("aside-hidden aside-hover");
        }else{
            $container.addClass("aside-hidden aside-hover");
        }
    });

    $(".aside-list").hover(function(){
        if($container.hasClass("aside-hover")){
            $container.removeClass("aside-hidden");
        }
    },function () {
        if($container.hasClass("aside-hover")) {
            $container.addClass("aside-hidden");
        }
    })
});

