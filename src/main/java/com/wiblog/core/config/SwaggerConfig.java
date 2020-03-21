//package com.wiblog.core.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
///**
// * Swagger 配置类
// *
// * @author pwm
// * @date 2019/7/20
// */
//@EnableSwagger2
//@Configuration
//public class SwaggerConfig {
//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(new ApiInfoBuilder()
//                        .title("api文档")
//                        .description("接口说明")
//                        .version("1.0")
//                        .build())
//                .select()
//                // 扫描的包
//                .apis(RequestHandlerSelectors.basePackage("com.wiblog.core.controller"))
//                .paths(PathSelectors.any())
//                .build();
//    }
//}
