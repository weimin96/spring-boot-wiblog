package com.wiblog.core.weixin;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;


/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/7/26
 */
@Slf4j
@Component
@PropertySource(value = "classpath:/config/wiblog.properties", encoding = "utf-8")
public class WeixinUtil {

    @Value("${weixin-appID}")
    private String appId;

    @Value("${weixin-appsecret}")
    private String appSecret;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取accessToken
     * @return accessToken
     */
    public String getAccessToken(){
        String accessToken = (String) redisTemplate.opsForValue().get(WeixinConstant.ACCESS_TOKEN_KEY);
        if (StringUtils.isBlank(accessToken)){
            return setAccessToken();
        }
        return accessToken;
    }

    /**
     * 设置accessToken
     * @return accessToken
     */
    public String setAccessToken(){
        /*准备发送GET请求*/
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
        url = url.replace("APPID", appId).replace("APPSECRET", appSecret);
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        String accessToken = (String) JSONObject.parseObject(response).get("access_token");

        if (StringUtils.isBlank(accessToken)){
            log.error("设置AccessToken失败：appid：{},appSecret：{}",appId,appSecret);
            return null;
        }
        redisTemplate.opsForValue().set(WeixinConstant.ACCESS_TOKEN_KEY,accessToken,7200, TimeUnit.SECONDS);
        log.info("设置accessToken->{}",accessToken);
        return accessToken;
    }

    /**
     * 获取ticket
     * @return ticket
     */
    public String getTicket() {
        return (String) redisTemplate.opsForValue().get(WeixinConstant.JS_API_TICKET_KEY);
    }

    /**
     * 设置ticket
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
            redisTemplate.opsForValue().set(WeixinConstant.JS_API_TICKET_KEY,ticket,7200, TimeUnit.SECONDS);
            log.info("设置ticket成功：{}",ticket);
            return ticket;
        }
        log.error("设置ticket失败,errcode：{}",errcode);
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

    public static Map parseXml(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map map = new HashMap();

        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        /*
         * 读取request的body内容 此方法会导致流读取问题 Premature end of file. Nested exception:
         * Premature end of file String requestBody =
         * inputStream2String(inputStream); System.out.println(requestBody);
         */
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();

        // 遍历所有子节点
        for (Element e : elementList){
            map.put(e.getName(), e.getText());
        }

        // 释放资源
        inputStream.close();
        inputStream = null;

        return map;
    }
}
