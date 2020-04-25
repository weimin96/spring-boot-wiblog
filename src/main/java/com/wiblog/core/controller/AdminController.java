package com.wiblog.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tencentcloudapi.monitor.v20180724.models.GetMonitorDataResponse;
import com.wiblog.core.aop.AuthorizeCheck;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Article;
import com.wiblog.core.entity.Comment;
import com.wiblog.core.entity.User;
import com.wiblog.core.scheduled.RecordScheduled;
import com.wiblog.core.service.IArticleService;
import com.wiblog.core.service.ICommentService;
import com.wiblog.core.service.IOpsService;
import com.wiblog.core.service.IUserService;
import com.wiblog.core.thirdparty.MonitorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/8/2
 */
@RestController
public class AdminController {

    @Autowired
    private MonitorData monitorData;

    @Autowired
    private IUserService userService;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IOpsService opsService;

    @AuthorizeCheck(grade = "2")
    @PostMapping("/getMonitorData")
    public ServerResponse getMonitorData(String metric, Integer period, Date startTime, Date endTime) {
        GetMonitorDataResponse res = monitorData.getMonitorData(metric,period,startTime,endTime);
        if (res == null || res.getDataPoints()==null){
            return ServerResponse.error("获取监控数据失败",30001);
        }
        return ServerResponse.success(res);
    }

    @AuthorizeCheck(grade = "2")
    @GetMapping("/getStaticData")
    public ServerResponse getStaticData() {
        int userCount = userService.count(new QueryWrapper<User>().eq("state","1"));
        int articleCount = articleService.count(new QueryWrapper<Article>().eq("state","1"));
        int commentCount = commentService.count(new QueryWrapper<Comment>().eq("state","1"));
        Map<String,Object> result =  new HashMap<>(5);
        result.put("userCount",userCount);
        result.put("articleCount",articleCount);
        result.put("commentCount",commentCount);
        result.put("hitCount",0);
        result.put("profitCount",0);
        return ServerResponse.success(result);
    }

    /*@AuthorizeCheck(grade = "2")
    @GetMapping("/getOpsPageList")
    public ServerResponse getOpsPageList(@RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                         @RequestParam(value = "orderBy", defaultValue = "asc") String orderBy){


    }*/

    @Autowired
    private RecordScheduled recordScheduled;
    @GetMapping("/push")
    public String push() {
        recordScheduled.pushArticle();
        return "";
    }
}
