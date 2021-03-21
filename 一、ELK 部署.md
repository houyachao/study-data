# 一、ELK 部署

## ① 大坑

 如果你的是云服务器（比如Ubtunu），需要在阿里云配置安全组，将你在外网访问的端口暴露出来。下面是链接，是针对你的实例服务器来添加的。

```java
https://ecs-cn-zhangjiakou.console.aliyun.com/?spm=5176.8351553.recommends.decs.67bc1991h4SIXK#/securityGroupDetail/region/cn-zhangjiakou/groupId/sg-8vbg5dlr6q1b8chodkrx/detail/intranetIngress
```

```java
curl http://localhost:9200/_analyze -X POST -H 'Content-Type:application/json' -d '{"text":"侯亚超测试分词效果"}'

```

