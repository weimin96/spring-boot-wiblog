
### 安装
下载安装
```
<!--下载并安装MySQL官方的 Yum Repository-->
wget -i -c http://dev.mysql.com/get/mysql57-community-release-el7-10.noarch.rpm

yum -y install mysql57-community-release-el7-10.noarch.rpm

<!--开始安装-->
sudo yum install mysql-community-server

<!--启动mysql-->
sudo systemctl start mysqld.service
```

重新设置登录密码
```
<!--查看临时密码-->
grep 'password' /var/log/mysqld.log |head -n 1

<!--修改密码-->
ALTER USER 'root'@'localhost' IDENTIFIED BY '123456';

<!--刷新数据-->
flush privileges;
```
配置数据库的远程连接
```
CREATE USER 'root'@'%' IDENTIFIED BY '123456';

GRANT ALL PRIVILEGES ON * . * TO 'root'@'%' IDENTIFIED BY 'k93j)4jPH' WITH GRANT OPTION MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0 ;
```
设置时区
```
show variables like '%time_zone%';
set global time_zone='+8:00';
```

### 启用bin_log

```
vi /etc/my.cnf
```

[mysqld]下加入
```
server-id=1
log-bin=mysqlbin-log
```
重启服务
```
service mysqld restart
```
登录mysql查看状态
```
# 查看bin-log日志是否开启
show variables like '%log_bin%';
```
