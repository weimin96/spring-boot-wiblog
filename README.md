# 基于spring boot的博客网站

![maven](https://img.shields.io/maven-central/v/org.apache.maven/apache-maven)
![license](https://img.shields.io/github/license/weimin96/spring-boot-wiblog)
![Spring%20Boot](https://img.shields.io/badge/Spring%20Boot-2.4.0-green)
![Author](https://img.shields.io/badge/Author-@weimin96-yellowgreen)

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

## 开发环境

### 软件工具

- JDK: 1.8
- Maven: 3.3+
- MySql: 5.6+ [安装教程](./doc/mysql.md)
- Redis: 4.0+ [安装教程](./doc/Redis.md)
- elasticSearch 7.x [安装教程](./doc/elasticSearch.md)

### IDE插件

- Lombok Plugin

## 工程导入

### 使用git导入工程
```
git clone https://github.com/weimin96/spring-boot-wiblog.git
```
### 创建数据库

打开Navicat（此处可以选择其他的客户端）-> 连接数据库 ->  运行sql文件 -> 选择 `\src\main\resources\myblog.sql`

### 修改配置文件

配置`\src\main\resources\application-dev.properties`（配置服务器ip/数据库账号密码/elasticsearch ip地址）

配置`\src\main\resources\wiblog.properties`（腾讯云密钥图片存储/公众号/github第三方登录）

### 打包运行
```
# 打包
mvn clean package
# 运行
java -jar target\core-0.0.1-SNAPSHOT.jar
```

主页：http://127.0.0.1:8080

后台管理地址 http://127.0.0.1:8080/admin

默认管理员账号为：admin / 123456
