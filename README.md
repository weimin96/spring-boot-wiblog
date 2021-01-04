# 基于spring boot的博客网站

![maven](https://img.shields.io/maven-central/v/org.apache.maven/apache-maven)
![GitHub](https://img.shields.io/github/license/weimin96/spring-boot-wiblog)

线上地址：[https://www.wiblog.cn/](https://www.wiblog.cn/)

管理界面：

- 概览页

![image](https://wiblog-1251822424.cos.ap-guangzhou.myqcloud.com/20200321220710-1.png)

- 用户管理

![image](https://wiblog-1251822424.cos.ap-guangzhou.myqcloud.com/20200321220716-2.png)

- 文章列表

![image](https://wiblog-1251822424.cos.ap-guangzhou.myqcloud.com/20200321220719-3.png)

- 文章编辑

![image](https://wiblog-1251822424.cos.ap-guangzhou.myqcloud.com/20200321220723-4.png)

- 评论管理

![image](https://wiblog-1251822424.cos.ap-guangzhou.myqcloud.com/20200321220730-5.png)

- 分类管理

![image](https://wiblog-1251822424.cos.ap-guangzhou.myqcloud.com/20200321220734-6.png)

- 图片管理

![image](https://wiblog-1251822424.cos.ap-guangzhou.myqcloud.com/20200321220737-7.png)

- 日志管理

![image](https://wiblog-1251822424.cos.ap-guangzhou.myqcloud.com/20200321220742-8.png)

## 功能

![image](https://wiblog-1251822424.cos.ap-guangzhou.myqcloud.com/20200321225642-9.png)

## 技术栈

spring boot 、spring-mvc 、mybatis-plus 、mysql、redis 、elasticsearch、thymeleaf、vue、element-ui

## 环境搭建

1. 运行`myblog.sql`导入数据库（`\src\main\resources\myblog.sql`）
2. 配置`\src\main\resources\application-dev.properties`（配置服务器ip/数据库账号密码）
3. 配置`\src\main\resources\wiblog.properties`（图片存储/公众号/github第三方登录）
4. 打包运行（maven、java环境）
```
# 打包
mvn clean package
# 运行
java -jar target\core-0.0.1-SNAPSHOT.jar
```

主页：http://127.0.0.1:8080

后台管理地址 http://127.0.0.1:8080/admin

默认管理员账号为：admin / 123456

## License

Apache Dubbo software is licenced under the Apache License Version 2.0. See the [LICENSE](https://github.com/apache/dubbo/blob/master/LICENSE) file for details.
