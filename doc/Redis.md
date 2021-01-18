## redis安装（docker方式）
```
docker pull redis

# 下载对应版本的conf文件放到 /root

docker run -p 6379:6379 --name redis -v $PWD/redis/data:/data  -d redis redis-server --appendonly yes --requirepass "123456"
```