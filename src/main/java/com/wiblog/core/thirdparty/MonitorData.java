package com.wiblog.core.thirdparty;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.monitor.v20180724.MonitorClient;
import com.tencentcloudapi.monitor.v20180724.models.GetMonitorDataRequest;
import com.tencentcloudapi.monitor.v20180724.models.GetMonitorDataResponse;
import com.wiblog.core.utils.DateUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 腾讯云 云监控
 *
 * @author pwm
 * @date 2019/10/24
 */
@Component
@PropertySource(value = "classpath:/wiblog.properties", encoding = "utf-8")
public class MonitorData {

    @Value("${qcloud-secret-id}")
    private String secretId;

    @Value("${qcloud-secret-key}")
    private String secretKey;

    @Value("${cvm-region}")
    private String region;

    @Value("${cvm-instance-id}")
    private String instanceId;

    /**
     * CPU平均负载
     * 一段时间内正在使用和等待使用CPU的平均任务数
     * 单位 -
     * 时间粒度period： 10s、60s、300s
     */
    private static final String CPU_LOAD_AVG = "CPULoadAvg";

    /**
     * 内存使用量
     * 用户实际使用的内存量，不包括缓冲区与系统缓存占用的内存
     * 单位 MB
     */
    private static final String MEM_USED = "MemUsed";

    /**
     * 内存利用率
     * 单位 %
     */
    private static final String MEM_USAGE = "MemUsage";

    /**
     * 外网出带宽
     * 单位 Mbps
     */
    private static final String WAN_OUT = "WanOuttraffic";

    /**
     * 外网入带宽
     * 单位 Mbps
     */
    private static final String WAN_IN = "WanIntraffic";

    /**
     * 获取监控数据
     * @param metric 类型
     * @param period 时间粒度
     * @param startTime startTime
     * @param endTime endTime
     * @return GetMonitorDataResponse
     */
    public GetMonitorDataResponse getMonitorData(String metric, Integer period, Date startTime, Date endTime) {
        String start = DateUtil.formatDate(startTime);
        String end = DateUtil.formatDate(endTime);
        try {
            Credential cred = new Credential(secretId, secretKey);

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("monitor.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            MonitorClient client = new MonitorClient(cred, region, clientProfile);
            String params = "{\"Namespace\":\"QCE/CVM\"," +
                    "\"MetricName\":\"" + metric+"\"," +
                    "\"Period\":" + period+"," +
                    "\"StartTime\":\""+start+"\"," +
                    "\"EndTime\":\""+end+"\"," +
                    "\"Instances\":[{\"Dimensions\":[{\"Name\":\"InstanceId\",\"Value\":\""+instanceId+"\"}]}]}";
            GetMonitorDataRequest req = GetMonitorDataRequest.fromJsonString(params, GetMonitorDataRequest.class);

            return client.GetMonitorData(req);
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        return null;
    }
}
