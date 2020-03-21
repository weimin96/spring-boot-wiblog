package com.wiblog.core.utils;

import com.alibaba.fastjson.JSONObject;
import com.wiblog.core.common.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * IP工具类
 *
 * @author pwm
 * @date 2019/7/3
 */
@Slf4j
public class IPUtil {

    private static final String UNKNOW = "unknown";

    /**
     * 获取ip地址
     *
     * @param request http request
     * @return ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOW.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOW.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOW.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                //根据网卡取本机配置的IP
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                if (inetAddress != null) {
                    ipAddress = inetAddress.getHostAddress();
                }
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        log.info("ip地址{}",ipAddress);
        return ipAddress;
    }

    /**
     * @return 本机IPSocketException
     * @throws SocketException
     */
    public static String getRealIp() throws SocketException {
        // 本地IP，如果没有配置外网IP则返回它
        String localIp = null;
        // 外网IP
        String netIp = null;

        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        // 是否找到外网IP
        boolean isFound = false;
        while (netInterfaces.hasMoreElements() && !isFound) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                ip = address.nextElement();
                // 外网IP
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                    netIp = ip.getHostAddress();
                    isFound = true;
                    break;
                    // 内网IP
                } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                    localIp = ip.getHostAddress();
                }
            }
        }

        if (netIp != null && !"".equals(netIp)) {
            return netIp;
        } else {
            return localIp;
        }
    }

    /**
     * 根据ip获取地理位置
     *
     * @param ip ip
     * @return String[]
     */
    public static String[] getIpInfo(String ip) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = Constant.IP_BAIDU_URL + ip;
            log.info("url:{}",url);
            String response = restTemplate.getForObject(url, String.class);
            Map responseMap = JSONObject.parseObject(response);
            Map data = ((List<Map>)responseMap.get("data")).get(0);
            String location = ((String) data.get("location")).split(" ")[0];

            return new String[]{"", location};
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new String[]{"", ""};
        }
    }
}
