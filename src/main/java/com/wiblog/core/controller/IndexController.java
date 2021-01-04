package com.wiblog.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiblog.core.common.BaseController;
import com.wiblog.core.common.Constant;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.User;
import com.wiblog.core.service.IMessageService;
import com.wiblog.core.service.IUserService;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页
 *
 * @author pwm
 * @date 2019/11/6
 */
@RestController
public class IndexController extends BaseController {

    private final IMessageService messageService;

    private final IUserService userService;

    private final RedisTemplate<String, Object> redisTemplate;

    public IndexController(IMessageService messageService, IUserService userService, RedisTemplate<String, Object> redisTemplate) {
        this.messageService = messageService;
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/getMessageCount")
    public ServerResponse<?> getMessageCount(HttpServletRequest request) {
        User user = getLoginUser(request);
        if (user != null) {
            return messageService.getMessageCount(user.getUid());
        }
        return ServerResponse.error("用户未登录", 30001);
    }


    /**
     * 发现附近的人
     *
     * @param request request
     * @param lat     lat
     * @param lng     lng
     * @return ServerResponse
     */
    @GetMapping("/getNearUser")
    public ServerResponse<?> getNearUser(HttpServletRequest request, Double lat, Double lng) {
        if (lat == null || lng == null) {
            return ServerResponse.error("参数错误", 30001);
        }
        User user = getLoginUser(request);
        if (user == null) {
            return ServerResponse.error("用户未登录", 30001);
        }
        Point point = new Point(lng, lat);

        Circle circle = new Circle(point, Metrics.KILOMETERS.getMultiplier());
        //设置geo查询参数
        GeoRadiusCommandArgs geoRadiusArgs = GeoRadiusCommandArgs.newGeoRadiusArgs();
        //查询返回结果包括距离
        geoRadiusArgs = geoRadiusArgs.includeDistance();
        //按查询出的坐标距离中心坐标的距离进行排序
        geoRadiusArgs.sortAscending();
        //限制查询数量
        geoRadiusArgs.limit(10);

        GeoResults<GeoLocation<Object>> geoResults = redisTemplate.opsForGeo().radius(Constant.RedisKey.NEAR_USER_KEY, circle, geoRadiusArgs);
        redisTemplate.opsForGeo().add(Constant.RedisKey.NEAR_USER_KEY, point, String.valueOf(user.getUid()));
        List<Map<String, Object>> result = new ArrayList<>(10);
        if (geoResults != null) {
            List<GeoResult<GeoLocation<Object>>> content = geoResults.getContent();
            for (GeoResult<GeoLocation<Object>> item : content) {
                Long uid = Long.valueOf((String) item.getContent().getName());
                // 排除自己
                if (uid.equals(user.getUid())) {
                    continue;
                }
                Map<String, Object> map = new HashMap<>(5);
                double distance = item.getDistance().getValue();
                int dis;
                if (distance <= 100) {
                    dis = 100;
                } else {
                    String num = String.valueOf(distance).split("\\.")[0];
                    dis = (Integer.parseInt(num.substring(0, 1)) * (int) Math.pow(10, num.length() - 1));
                }
                // 单位
                String unit;
                if ("m".equals(item.getDistance().getMetric().getAbbreviation())) {
                    unit = "米";
                } else {
                    unit = "公里";
                }
                map.put("distance", dis + unit + "以内");

                User nearUser = userService.getOne(new QueryWrapper<User>().eq("uid", uid).eq("state", 1));
                map.put("uid", nearUser.getUid());
                map.put("name", nearUser.getUsername());
                map.put("avatar", nearUser.getAvatarImg());
                map.put("sex", nearUser.getSex());
                result.add(map);
            }
        }


        return ServerResponse.success(result, "获取附近的人成功");
    }
}
