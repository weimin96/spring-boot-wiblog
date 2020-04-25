package com.wiblog.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiblog.core.entity.Article;
import com.wiblog.core.entity.Category;
import com.wiblog.core.entity.User;
import com.wiblog.core.service.IArticleService;
import com.wiblog.core.service.ICategoryService;
import com.wiblog.core.service.IUserRoleService;
import com.wiblog.core.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

/**
 * @author pwm
 * @date 2019/4/12
 */
@Controller
@Slf4j
public class WebController extends BaseController {

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserRoleService userRoleService;

    @Autowired
    private ICategoryService categoryService;

    /**
     * 跳转首页
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 跳转首页
     */
    @GetMapping("/index")
    public String index2() {
        return "index";
    }


    /**
     * 跳转登录页
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 跳转文章页
     */
    @GetMapping("/post/{url}")
    public String article(HttpServletRequest request, @PathVariable String url) {
        Article article = articleService.getOne(new QueryWrapper<Article>().select("privately")
                .eq("article_url", "/post/" + url)
                .eq("state",1));
        if (article == null) {
            return "/error/404";
        }
        User user = getLoginUser(request);
        if (article.getPrivately() && !userRoleService.checkAuthorize(user, 2).isSuccess()) {
            return "/error/404";
        }
        return "article";
    }

    /**
     * 跳转用户中心
     */
    @GetMapping("/user/{url}")
    public String userCenter(HttpServletRequest request, @PathVariable String url) {
        long uid = Long.parseLong(url) / 12345;
        User user = userService.getById(uid);
        if (user == null) {
            return "/error/404";
        }
        log.info("用户信息{}",user);
        return "user";
    }

    /**
     * 跳转分类页
     */
    @GetMapping("/category/{url}")
    public String category(HttpServletRequest request, @PathVariable String url) {
        Category category = categoryService.getOne(new QueryWrapper<Category>().eq("url", url));
        if (category == null) {
            return "/error/404";
        }
        return "category";
    }

    /**
     * 跳转注册页
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * 跳转搜索页
     */
    @GetMapping("/search")
    public String search() {
        return "search";
    }

    /**
     * 后台主页框架
     */
    @GetMapping("/admin")
    public String adminIndex() {
        return "admin/index";
    }

    /**
     * 后台主页内容
     */
    @GetMapping("/admin/home")
    public String adminIndexHome() {
        return "admin/home";
    }

    /**
     * 后台文章中心
     */
    @GetMapping("/admin/articleList")
    public String articleList() {
        return "admin/articleList";
    }

    /**
     * 后台文章编辑
     */
    @GetMapping("/admin/articleEdit")
    public String articleEdit() {
        return "admin/articleEdit";
    }

    /**
     * 后台文章评论管理
     */
    @GetMapping("/admin/comment")
    public String comment() {
        return "admin/comment";
    }

    /**
     * 后台用户管理
     */
    @GetMapping("/admin/user")
    public String user() {
        return "admin/user";
    }

    /**
     * 后台图片管理
     */
    @GetMapping("/admin/pic")
    public String pic() {
        return "admin/picture";
    }

    /**
     * 后台分类管理
     */
    @GetMapping("/admin/category")
    public String category() {
        return "admin/category";
    }

    /**
     * 后台日志管理
     */
    @GetMapping("/admin/log")
    public String log() {
        return "admin/log";
    }
}
