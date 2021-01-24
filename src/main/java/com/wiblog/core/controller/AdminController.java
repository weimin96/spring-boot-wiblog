package com.wiblog.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tencentcloudapi.monitor.v20180724.models.GetMonitorDataResponse;
import com.wiblog.core.aop.AuthorizeCheck;
import com.wiblog.core.common.RoleEnum;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Article;
import com.wiblog.core.entity.Comment;
import com.wiblog.core.entity.Ops;
import com.wiblog.core.entity.User;
import com.wiblog.core.service.IArticleService;
import com.wiblog.core.service.ICommentService;
import com.wiblog.core.service.IOpsService;
import com.wiblog.core.service.IUserService;
import com.wiblog.core.thirdparty.MonitorData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理界面控制层
 *
 * @author pwm
 * @date 2019/8/2
 */
@RestController
public class AdminController {

    private final MonitorData monitorData;

    private final IUserService userService;

    private final IArticleService articleService;

    private final ICommentService commentService;

    private final IOpsService opsService;

    public AdminController(MonitorData monitorData, IUserService userService, IArticleService articleService, ICommentService commentService, IOpsService opsService) {
        this.monitorData = monitorData;
        this.userService = userService;
        this.articleService = articleService;
        this.commentService = commentService;
        this.opsService = opsService;
    }

    /**
     * 获取监控信息
     */
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    @PostMapping("/getMonitorData")
    public ServerResponse<?> getMonitorData(String metric, Integer period, Date startTime, Date endTime) {
        GetMonitorDataResponse res = monitorData.getMonitorData(metric, period, startTime, endTime);
        if (res == null || res.getDataPoints() == null) {
            return ServerResponse.error("获取监控数据失败", 30001);
        }
        return ServerResponse.success(res);
    }

    /**
     * 获取网站统计数据
     */
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    @GetMapping("/getStaticData")
    public ServerResponse<?> getStaticData() {
        int userCount = userService.count(new QueryWrapper<User>().eq("state", "1"));
        int articleCount = articleService.count(new QueryWrapper<Article>().eq("state", "1"));
        int commentCount = commentService.count(new QueryWrapper<Comment>().eq("state", "1"));
        Map<String, Object> hitMap = articleService.getMap(new QueryWrapper<Article>().select("sum(hits) as sum").eq("state", "1"));
        double hitCount = 0.0;
        if (hitMap != null) {
            hitCount = Double.parseDouble(String.valueOf(hitMap.get("sum")));
        }
        Map<String, Object> result = new HashMap<>(5);
        result.put("userCount", userCount);
        result.put("articleCount", articleCount);
        result.put("commentCount", commentCount);
        result.put("hitCount", (int) hitCount);
        result.put("profitCount", 0);
        return ServerResponse.success(result);
    }

    /**
     * 获取管理员操作日志
     */
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    @GetMapping("/getOpsData")
    public ServerResponse<?> getOpsData() {
        LambdaQueryWrapper<Ops> queryWrapper = new QueryWrapper<Ops>().lambda();
        queryWrapper.orderByDesc(Ops::getCreateTime).last("limit 10");
        List<Ops> list = opsService.list(queryWrapper);
        return ServerResponse.success(list);
    }
}
