package com.wiblog.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 *
 * @author pwm
 * @date 2019/12/17
 */
@Configuration
public class WebSocketConfig {

    /**
     * WebSocket扩展协议配置
     * 使用@ServerEndpoint创立websocket endpoint
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }


}
