
## elasticsearch 安装（docker方式）

```
docker pull elasticsearch:7.9.1

docker network create esnet

docker run --name es -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e "ES_JAVA_OPTS=-Xms516m -Xmx516m" -d elasticsearch:7.9.1

```

配置跨域

```
docker exec -it es /bin/bash

vi ./config/elasticsearch.yml 
```
添加

```
http.cors.enabled: true
http.cors.allow-origin: "*"
```
保存退出重启

```
docker restart es
```