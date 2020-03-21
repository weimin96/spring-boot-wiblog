package com.wiblog.core.config;

import com.wiblog.core.interceptor.BaseInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 向mvc中添加自定义组件
 *
 * @author pwm
 * @date 2019/7/1
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private BaseInterceptor baseInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseInterceptor).excludePathPatterns(
                "/js/**","/css/**","/lib/**","/fonts/**","/img/**"
        );
    }
}
