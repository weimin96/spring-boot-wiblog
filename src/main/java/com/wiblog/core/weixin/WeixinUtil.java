package com.wiblog.core.weixin;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiblog.core.common.Constant;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.User;
import com.wiblog.core.entity.UserAuth;
import com.wiblog.core.mapper.UserAuthMapper;
import com.wiblog.core.mapper.UserMapper;
import com.wiblog.core.utils.IPUtil;
import com.wiblog.core.utils.MessageUtil;
import com.wiblog.core.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/7/26
 */
@Slf4j
@Component
@PropertySource(value = "classpath:/wiblog.properties", encoding = "utf-8")
public class WeixinUtil {

    @Value("${weixin-appID}")
    private String appId;

    @Value("${weixin-appsecret}")
    private String appSecret;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAuthMapper userAuthMapper;

    /**
     * 获取accessToken
     *
     * @return accessToken
     */
    public String getAccessToken() {
        String accessToken = (String) redisTemplate.opsForValue().get(WeixinConstant.ACCESS_TOKEN_KEY);
        if (StringUtils.isBlank(accessToken)) {
            return setAccessToken();
        }
        return accessToken;
    }

    /**
     * 设置accessToken
     *
     * @return accessToken
     */
    public String setAccessToken() {
        /*准备发送GET请求*/
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
        url = url.replace("APPID", appId).replace("APPSECRET", appSecret);
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        String accessToken = (String) JSONObject.parseObject(response).get("access_token");

        if (StringUtils.isBlank(accessToken)) {
            log.error("设置AccessToken失败：appid：{},appSecret：{}", appId, appSecret);
            return null;
        }
        redisTemplate.opsForValue().set(WeixinConstant.ACCESS_TOKEN_KEY, accessToken, 7200, TimeUnit.SECONDS);
        log.info("设置accessToken->{}", accessToken);
        return accessToken;
    }

    /**
     * 获取ticket
     *
     * @return ticket
     */
    public String getTicket() {
        return (String) redisTemplate.opsForValue().get(WeixinConstant.JS_API_TICKET_KEY);
    }

    /**
     * 设置ticket
     *
     * @param accessToken accessToken
     * @return ticket
     */
    public String setTicket(String accessToken) {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
        url = url.replace("ACCESS_TOKEN", accessToken);
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSONObject.parseObject(response);
        String errcode = jsonObject.getString("errcode");
        if ("0".equals(errcode)) {
            String ticket = jsonObject.getString("ticket");
            redisTemplate.opsForValue().set(WeixinConstant.JS_API_TICKET_KEY, ticket, 7200, TimeUnit.SECONDS);
            log.info("设置ticket成功：{}", ticket);
            return ticket;
        }
        log.error("设置ticket失败,errcode：{}", errcode);
        return null;
    }

    public Map<String, String> sign(String url) {
        String ticket = getTicket();
        Map<String, String> ret = new HashMap<>(5);
        String nonceStr = createNonceStr();
        String timestamp = createTimestamp();
        String string1;
        String signature = "";
        // 注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + ticket +
                "&noncestr=" + nonceStr +
                "&timestamp=" + timestamp +
                "&url=" + url;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes(StandardCharsets.UTF_8));
            signature = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        ret.put("url", url);
        //注意这里 要加上自己的appId
        ret.put("appId", appId);
        ret.put("jsapi_ticket", ticket);
        ret.put("nonceStr", nonceStr);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);
        return ret;
    }

    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private String createNonceStr() {
        return UUID.randomUUID().toString();
    }

    private String createTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    public String messageHandler(HttpServletRequest request) {
        String respMessage = null;
        try {
            // xml请求解析
            Map<String, String> requestMap = MessageUtil.xmlToMap(request);
            if (requestMap == null) {
                return null;
            }
            // 发送方帐号（openid）
            String fromUserName = requestMap.get("FromUserName");
            // 公众帐号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");
            // 事件类型
            String eventType = requestMap.get("Event");
            // 消息内容
            String content = requestMap.get("Content");
            log.info("接收微信消息{}", requestMap);

            MsgEnum msgEnum = MsgEnum.fromString(msgType);
            TextMessage text = new TextMessage();
            // 事件
            if (msgEnum == MsgEnum.EVENT) {
                //扫码关注公众号
                //{CreateTime=1588561340, EventKey=, Event=subscribe,
                // ToUserName=gh_27c767c19e20, FromUserName=oCtGTwMjE3zmLU6uIuJIvnv6-UKs, MsgType=event}
                EventEnum eventEnum = EventEnum.fromString(eventType);
                // 订阅
                if (eventEnum == EventEnum.SUBSCRIBE) {

                    String sb = "Hi，欢迎关注歪博~" + "\n\n" +
                            "点击获取网站登录校验码 " +
                            "<a href=\"weixin://bizmsgmenu?msgmenucontent=登录&msgmenuid=1\">登录</a>";
                    text.setContent(sb);
                }
            } else if (msgEnum == MsgEnum.TEXT) {
                //接收回复
                //{Content=登录, CreateTime=1588565004, ToUserName=gh_27c767c19e20,
                // FromUserName=oCtGTwMjE3zmLU6uIuJIvnv6-UKs, MsgType=text, MsgId=22742783737763533}
                String code = VerifyCodeUtils.getRandomCode();
                redisTemplate.opsForValue().set(Constant.RedisKey.WECHAT_LOGIN_CODE_ + code.toLowerCase(), fromUserName, 5, TimeUnit.MINUTES);
                text.setContent("验证码：" + code + "\n\n本验证码5分钟内有效，如超时请重新发送“登录”二字获取");
            }

            text.setToUserName(fromUserName);
            text.setFromUserName(toUserName);
            text.setCreateTime(String.valueOf(System.currentTimeMillis()));
            text.setMsgType("text");
            respMessage = MessageUtil.textMessageToXml(text);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return respMessage;
    }

    public ServerResponse login(String code,HttpServletRequest request) {
        if (StringUtils.isBlank(code)) {
            return ServerResponse.error("参数错误", 30001);
        }
        String openid = (String) redisTemplate.opsForValue().get(Constant.RedisKey.WECHAT_LOGIN_CODE_ + code.toLowerCase());
        if (StringUtils.isBlank(openid)) {
            return ServerResponse.error("验证码过期", 30002);
        }
        String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
        url = url.replace("ACCESS_TOKEN", getAccessToken()).replace("OPENID", openid);
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject == null){
            return ServerResponse.error("服务器异常", 30003);
        }
        User user = registerWechat(jsonObject,request);
        redisTemplate.delete(Constant.RedisKey.WECHAT_LOGIN_CODE_ + code);
        return ServerResponse.success(user);
    }

    /**
     * 用户注册 存入数据库
     *
     * @param wechatUser user
     */
    @Transactional(rollbackFor = Exception.class)
    public User registerWechat(JSONObject wechatUser,HttpServletRequest request) {
        String openid = (String) wechatUser.get("openid");
        UserAuth userAuth = userAuthMapper.selectOne(new QueryWrapper<UserAuth>()
                .eq("identity_type", "wechat")
                .eq("identifier", openid)
                .eq("state", 1));
        User user = User.of();
        // 未注册 直接插入数据
        if (userAuth == null) {
            String ip = IPUtil.getIpAddr(request);
            String[] address = IPUtil.getIpInfo(ip);
            log.info("用户地址{}-{}",address[0],address[1]);

            user.setAvatarImg((String) wechatUser.get("headimgurl"));
            user.setUsername((String) wechatUser.get("nickname"));
            user.setState(true).setRegion(address[0]).setCity(address[1]);
            Integer sex = (Integer) wechatUser.get("sex");
            user.setSex(1 == sex ? "male" : "female");
            user.setCreateTime(new Date());
            userMapper.insertReturnId(user);

            userAuth = new UserAuth();
            userAuth.setIdentityType("wechat");
            userAuth.setIdentifier(openid);
            userAuth.setPassword(openid);
            userAuth.setUid(user.getUid());
            userAuth.setCreateTime(new Date());
            userAuthMapper.insert(userAuth);
        } else { // 已经注册 使用原来帐号
            user = userMapper.selectOne(new QueryWrapper<User>().eq("uid", userAuth.getUid()));
        }
        return user;
    }

}
