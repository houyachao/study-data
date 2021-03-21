# 一、SpringCloud

## 1.面试题

**①什么是微服务？**

```
	微服务强调的是服务的大小，它关注的是某一个点，是具体解决某一个问题/提供落地对应服务的一个服务应用,
狭意的看,可以看作Eclipse里面的一个个微服务工程/或者Module。
	微服务化的核心就是将传统的一站式应用，根据业务拆分成一个一个的服务，彻底地去耦合,每一个微服务提供单个业务功能的服务，一个服务做一件事，从技术角度看就是一种小而独立的处理过程，类似进程概念，能够自行单独启动或销毁，拥有自己独立的数据库。
```

**②微服务之间是如何独立通讯的？**

```
1.         同步：RPC，REST等

2.         异步：消息队列。要考虑消息可靠传输、高性能，以及编程模型的变化等。
```

**③SpringCloud和Dubbo有哪些区别？**

|        |     Dubbo     | Spring Cloud                 |
| :----: | :-----------: | :--------------------------- |
| 服务注册中心 |   Zookeeper   | Spring Cloud Netflix Eureka  |
| 服务调用方式 |      RPC      | REST API                     |
|  服务监控  | Dubbo-monitor | Spring Boot Admin            |
|  断路器   |      不完善      | Spring Cloud Netflix Hystrix |
|  服务网关  |       无       | Spring Cloud Netflix Zuul    |
| 分布式配置  |       无       | Spring Cloud Config          |
|  服务跟踪  |       无       | Spring Cloud Sleuth          |
|  消息总线  |       无       | Spring Cloud Bus             |
|  数据流   |       无       | Spring Cloud Stream          |
|  批量任务  |       无       | Spring Cloud Task            |

```
最大区别：SpringCloud抛弃了Dubbo的RPC通信，采用的是基于HTTP的REST方式。
严格来说，这两种方式各有优劣。虽然从一定程度上来说，后者牺牲了服务调用的性能，但也避免了上面提到的原生RPC带来的问题。而且REST相比RPC更为灵活，服务提供方和调用方的依赖只依靠一纸契约，不存在代码级别的强依赖，这在强调快速演化的微服务环境下，显得更加合适。
 
品牌机与组装机的区别
很明显，Spring Cloud的功能比DUBBO更加强大，涵盖面更广，而且作为Spring的拳头项目，它也能够与Spring Framework、Spring Boot、Spring Data、Spring Batch等其他Spring项目完美融合，这些对于微服务而言是至关重要的。使用Dubbo构建的微服务架构就像组装电脑，各环节我们的选择自由度很高，但是最终结果很有可能因为一条内存质量不行就点不亮了，总是让人不怎么放心，但是如果你是一名高手，那这些都不是问题；而Spring Cloud就像品牌机，在Spring Source的整合下，做了大量的兼容性测试，保证了机器拥有更高的稳定性，但是如果要在使用非原装组件外的东西，就需要对其基础有足够的了解。
 
社区支持与更新力度
最为重要的是，DUBBO停止了5年左右的更新，虽然2017.7重启了。对于技术发展的新需求，需要由开发者自行拓展升级（比如当当网弄出了DubboX），这对于很多想要采用微服务架构的中小软件组织，显然是不太合适的，中小公司没有这么强大的技术能力去修改Dubbo源码+周边的一整套解决方案，并不是每一个公司都有阿里的大牛+真实的线上生产环境测试过。


总结：
	两则所解决的问题域并不一样：Dubbo的定位始终是一款RPC框架，而SpringCloud的目标是为服务架构下的一站式解决方案。如果非要比较的话，我觉得Dubbo可以类比到Netflix OSS技术栈，而Spring Cloud集成了Netflix OSS作为分布式服务治理解决方案，但除此之外SpringCloud还提供了包括config，stream，security，sleuth等等分布式问题解决方案。
	当前由于RPC协议、注册中心元数据不匹配等问题，在面临微服务基础架构选型时Dubbo与SpringCloud是只能二选一。
```



**④SpringBoot和SpringCloud，请你谈谈对他们的理解？**

```
1)、SpringBoot专注于快速方便的开发单个个体微服务。
2)、SpringCloud是关注全局的微服务协调、整理、治理的框架,它将SpringBoot开发的单体整合并管理起来。
3)、SpringBoot可以离开SpringCloud独立使用开发项目,但是SpringCloud离不开SpringBoot,属于依赖关系。
```

**⑤什么是服务熔断？什么是服务降级？**

```
	熔断机制是应对雪崩效应的一种微服务链路保护机制。当扇出链路的某个微服务不可用或者响应时间太长时,会进行服务降级,进而熔断该节点微服务的调用,快速返回“错误”的响应信息。当检测到该节点微服务调用响应正常后恢复调用链路。在SpringCloud框架里熔断机制通过Hystrix实现,Hystrix会监控微服务间调用的状况,当失败的调用到一定阈值,缺省是5秒内调用20次,如果失败,就会启动熔断机制。熔断机制的注解是@HystrixCommand
     服务降级,一般是从整体负荷考虑。就是当某个服务熔断之后,服务器将不再被调用,此时客户端可以自己准备一个本地的fallback回调,返回一个缺省值。这样做,虽然水平下降,但好歹可用,比直接挂掉强。
```

**⑥微服务的优缺点分别是什么？说下你在项目开发中碰到的坑？**

```
优点:1)、每个服务足够内聚,足够小,代码容易理解这样能聚焦一个指定的业务功能或业务需求。
          2)、开发简单,开发效率提高,一个服务可能就是专一的只干一件事。
          3)、微服务能够被小团队开发,这个团队可以是2到5个开发人员组成。
          4)、微服务是松耦合的,是有功能意义的服务,无论是在开发阶段或部署阶段都是独立的。
          5)、微服务能使用不同的语言开发。
          6)、易于第三方集成,微服务允许容易且灵活的方式集成自动部署,通过持续集成集成工具,如Jenkins、Hudson等。
          7)、微服务易于被一个开发人员理解,修改和维护,这样小团队能够更关注自己的工作成果。无需通过合作体现价值。
          8)、微服务允许你融合最新技术。
          9)、微服务支持业务逻辑代码,不会和HTML和CSS其他界面组件混合。
        10)、每个微服务都有自己的存储能力,可以有自己的数据库,也可以由统一的数据库。
        
缺点:1)、开发人员要处理分布式系统的复杂性。
          2)、多服务运维难度,随着服务的增加,运维的压力也在增加。
          3)、系统部署依赖。
          4)、服务间通讯成本。
          5)、数据一致性。
          6)、系统集成测试。
          7)、性能监控.....
```

**⑦你所知道的微服务技术栈有哪些？请举例。**

| 微服务条目                | 落地技术                                     | 备注   |
| -------------------- | ---------------------------------------- | ---- |
| 服务开发                 | Springboot、Spring、SpringMVC              |      |
| 服务配置与管理              | Netflix公司的Archaius、阿里的Diamond等           |      |
| 服务注册与发现              | Eureka、Consul、Zookeeper等                 |      |
| 服务调用                 | Rest、RPC、gRPC                            |      |
| 服务熔断器                | Hystrix、Envoy等                           |      |
| 负载均衡                 | Ribbon、Nginx等                            |      |
| 服务接口调用(客户端调用服务的简化工具) | Feign等                                   |      |
| 消息队列                 | Kafka、RabbitMQ、ActiveMQ等                 |      |
| 服务配置中心管理             | SpringCloudConfig、Chef等                  |      |
| 服务路由（API网关）          | Zuul等                                    |      |
| 服务监控                 | Zabbix、Nagios、Metrics、Spectator等         |      |
| 全链路追踪                | Zipkin，Brave、Dapper等                     |      |
| 服务部署                 | Docker、OpenStack、Kubernetes等             |      |
| 数据流操作开发包             | SpringCloud Stream封装与（Redis,Rabbit、Kafka等发送接收消息） |      |
| 事件消息总线               | Spring Cloud Bus                         |      |
| 。。。                  | 。。。                                      |      |

**⑧作为服务注册中心，Eureka比zookeeper好在哪里，请说说两个的区别？**

```
1)、Zookeeper保证了CP(C:一致性,P:分区容错性),Eureka保证了AP(A:高可用) 
        (1)、当向注册中心查询服务列表时,我们可以容忍注册中心返回的是几分钟以前的信息,但不能容忍直接down掉不可用。也就是说,服务注册功能对高可用性要求比较高,但zk会出现这样一种情况,当master节点因为网络故障与其他节点失去联系时,剩余节点会重新选leader。问题在于,选取leader时间过长,30 ~ 120s,且选取期间zk集群都不可用,这样就会导致选取期间注册服务瘫痪。在云部署的环境下,因网络问题使得zk集群失去master节点是较大概率会发生的事,虽然服务能够恢复,但是漫长的选取时间导致的注册长期不可用是不能容忍的。 
          (2)、Eureka保证了可用性,Eureka各个节点是平等的,几个节点挂掉不会影响正常节点的工作,剩余的节点仍然可以提供注册和查询服务。而Eureka的客户端向某个Eureka注册或发现是发生连接失败,则会自动切换到其他节点,只要有一台Eureka还在,就能保证注册服务可用,只是查到的信息可能不是最新的。除此之外,Eureka还有自我保护机制,如果在15分钟内超过85%的节点没有正常的心跳,那么Eureka就认为客户端与注册中心发生了网络故障,此时会出现以下几种情况: 
           ①、Eureka不在从注册列表中移除因为长时间没有收到心跳而应该过期的服务。 
           ②、Eureka仍然能够接受新服务的注册和查询请求,但是不会被同步到其他节点上(即保证当前节点仍然可用) 
           ③、当网络稳定时,当前实例新的注册信息会被同步到其他节点。

因此,Eureka可以很好的应对因网络故障导致部分节点失去联系的情况,而不会像Zookeeper那样使整个微服务瘫痪。
```



## 2.微服务概述

### ①微服务和微服务架构？

业界大牛马丁.福勒（Martin Fowler） 这样描述微服务：
论文网址：            https://martinfowler.com/articles/microservices.html

微服务
强调的是服务的大小，它关注的是某一个点，是具体解决某一个问题/提供落地对应服务的一个服务应用,
狭意的看,可以看作Eclipse里面的一个个微服务工程/或者Module。

微服务架构

微服务架构是⼀种架构模式，它提倡将单⼀应⽤程序划分成⼀组⼩的服务，服务之间互相协调、互相配合，为⽤户提供最终价值。每个服务运⾏在其独⽴的进程中，服务与服务间采⽤轻量级的通信机制互相协作（通常是基于HTTP协议的RESTful API）。每个服务都围绕着具体业务进⾏构建，并且能够被独⽴的部署到⽣产环境、类⽣产环境等。另外，应当尽量避免统⼀的、集中式的服务管理机制，对具体的⼀个服务⽽⾔，应根据业务上下⽂，选择合适的语⾔、⼯具对其进⾏构建。

技术维度理解：

		微服务化的核心就是将传统的一站式应用，根据业务拆分成一个一个的服务，彻底地去耦合,每一个微服务提供单个业务功能的服务，一个服务做一件事，从技术角度看就是一种小而独立的处理过程，类似进程概念，能够自行单独启动或销毁，拥有自己独立的数据库。

### ②微服务的优缺点：

优点：
​	每个服务足够内聚，足够小，代码容易理解这样聚焦一个指定的业务功能或业务需求。
​	开发简单，开发效率提高，一个服务可能就是专一的只干一件事。
​	微服务能够被小团队单独开发。
​	微服务是松耦合的，是有功能意义的服务，无论是在开发阶段或部署阶段都是独立的。
​	微服务能使用不同的语言开发。
​	易于和第三方集成，微服务允许容易且灵活的方式集成自动部署，通过持续集成工具，如Jenkins，	     Hudson，bamboo。
​	微服务易于被一个开发人员理解，修改和维护，这样小团队能够更关注自己的工作成果，无需通过合作才能体现价值。
​	微服务允许你利用融合最新技术。
​	**微服务只是业务逻辑代码，不会和HTML，css，或其他界面组件混合。**
​	**每个微服务都有自己的存储能力，可以有自己的数据库，也可以有统一数据库。**

缺点：
​	开发人员要处理分布式系统的复杂性
​	多服务运维难度，随着服务的增加，运维的压力也在增大
​	系统部署依赖
​	服务间通信成本
​	数据一致性
​	系统集成测试
​	性能监控

### ③微服务技术栈有哪些？

|        微服务条目         |                   落地技术                   |  备注  |
| :------------------: | :--------------------------------------: | :--: |
|         服务开发         |       Springboot、Spring、SpringMVC        |      |
|       服务配置与管理        |      Netflix公司的Archaius、阿里的Diamond等      |      |
|       服务注册与发现        |         Eureka、Consul、Zookeeper等         |      |
|         服务调用         |              Rest、RPC、gRPC               |      |
|        服务熔断器         |              Hystrix、Envoy等              |      |
|         负载均衡         |              Ribbon、Nginx等               |      |
| 服务接口调用(客户端调用服务的简化工具) |                  Feign等                  |      |
|         消息队列         |         Kafka、RabbitMQ、ActiveMQ等         |      |
|       服务配置中心管理       |         SpringCloudConfig、Chef等          |      |
|     服务路由（API网关）      |                  Zuul等                   |      |
|         服务监控         |     Zabbix、Nagios、Metrics、Spectator等     |      |
|        全链路追踪         |           Zipkin，Brave、Dapper等           |      |
|         服务部署         |       Docker、OpenStack、Kubernetes等       |      |
|       数据流操作开发包       | SpringCloud Stream封装与（Redis,Rabbit、Kafka等发送接收消息） |      |
|        事件消息总线        |             Spring Cloud Bus             |      |
|         。。。          |                   。。。                    |      |

### ④为什么要选用SpringCloud作为微服务架构？

**选型依据：**

​	整体解决方案和框架成熟度。

​	社区热度。

​	可维护性。

​	学习曲线。

各微服务框架对比：

从功能定位来看：SpringCloud/Netflix 具有完整的微服务框架。

### ⑤SpringCloud是什么？

**5.1 官网说明：**

```
	SpringCloud，基于SpringBoot提供了一套微服务解决方案，包括服务注册与发现，配置中心，全链路监控，服务网关，负载均衡，熔断器等组件，除了基于NetFlix的开源组件做高度抽象封装之外，还有一些选型中立的开源组件。
 
SpringCloud利用SpringBoot的开发便利性巧妙地简化了分布式系统基础设施的开发，SpringCloud为开发人员提供了快速构建分布式系统的一些工具，包括配置管理、服务发现、断路器、路由、微代理、事件总线、全局锁、决策竞选、分布式会话等等,它们都可以用SpringBoot的开发风格做到一键启动和部署。
 
	SpringBoot并没有重复制造轮子，它只是将目前各家公司开发的比较成熟、经得起实际考验的服务框架组合起来，通过SpringBoot风格进行再封装屏蔽掉了复杂的配置和实现原理，最终给开发者留出了一套简单易懂、易部署和易维护的分布式系统开发工具包。
```

​	SpringCloud=分布式微服务架构下的一站式解决方案，是各个微服务架构落地技术的集合体，俗称微服务全家桶。

**5.2 SpringCloud和SpringBoot是什么关系？**

```
SpringBoot专注于快速方便的开发单个个体微服务。
 
SpringCloud是关注全局的微服务协调整理治理框架，它将SpringBoot开发的一个个单体微服务整合并管理起来，
为各个微服务之间提供，配置管理、服务发现、断路器、路由、微代理、事件总线、全局锁、决策竞选、分布式会话等等集成服务
 
SpringBoot可以离开SpringCloud独立使用开发项目，但是SpringCloud离不开SpringBoot，属于依赖的关系.
 
SpringBoot专注于快速、方便的开发单个微服务个体，SpringCloud关注全局的服务治理框架。
```

5.3 去哪下？

**官网：** http://projects.spring.io/spring-cloud/

**参考书：**

	https://springcloud.cc/spring-cloud-netflix.html
	本次开发API说明
		http://cloud.spring.io/spring-cloud-static/Dalston.SR1/   英文版
		https://springcloud.cc/spring-cloud-dalston.html		中文版
	springcloud中国社区
		http://springcloud.cn/							技术大牛
	springcloud中文网
		https://springcloud.cc/							技术大牛
###  ⑥RestTemplate

```
RestTemplate提供了多种便捷访问远程Http服务的方法， 
是一种简单便捷的访问restful服务模板类，是Spring提供的用于访问Rest服务的客户端模板工具集。
 
官网地址
https://docs.spring.io/spring-framework/docs/4.3.7.RELEASE/javadoc-api/org/springframework/web/client/RestTemplate.html
 
使用
使用restTemplate访问restful接口非常的简单粗暴无脑。
(url, requestMap, ResponseBean.class)这三个参数分别代表 
REST请求地址、请求参数、HTTP响应转换被转换成的对象类型。
 
 
 
 
```

## 3.Eureka服务组成与发现

### ①Eureka是什么？

**Netflix在设计Eureka时遵守的就是AP原则。**

```
	Eureka是Netflix的一个子模块，也是核心模块之一。Eureka是一个基于REST的服务，用于定位服务，以实现云端中间层服务发现和故障转移。
	服务注册与发现对于微服务架构来说是非常重要的，有了服务发现与注册，只需要使用服务的标识符，就可以访问到服务，而不需要修改服务调用的配置文件了。功能类似于dubbo的注册中心，比如Zookeeper。
```

### ②原理讲解

​	**1. Eureka 的基本架构**

```
	Spring Cloud 封装了 Netflix 公司开发的 Eureka 模块来实现服务注册和发现(请对比Zookeeper)。 
Eureka 采用了 C-S 的设计架构。Eureka Server 作为服务注册功能的服务器，它是服务注册中心。
	而系统中的其他微服务，使用 Eureka 的客户端连接到 Eureka Server并维持心跳连接。这样系统的维护人员就可以通过 Eureka Server 来监控系统中各个微服务是否正常运行。SpringCloud 的一些其他模块（比如Zuul）就可以通过 Eureka Server 来发现系统中的其他微服务，并执行相关的逻辑。
```

​							请注意和Dubbo的架构对比：

![Eureka](C:\Users\Administrator\Desktop\笔记\SpringCloud\Eureka.png)

![Dubbo-zookeep](C:\Users\Administrator\Desktop\笔记\SpringCloud\Dubbo-zookeep.png)

```
Eureka包含两个组件：Eureka Server和Eureka Client
Eureka Server提供服务注册服务
各个节点启动后，会在EurekaServer中进行注册，这样EurekaServer中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观的看到
 
EurekaClient是一个Java客户端，用于简化Eureka Server的交互，客户端同时也具备一个内置的、使用轮询(round-robin)负载算法的负载均衡器。在应用启动后，将会向Eureka Server发送心跳(默认周期为30秒)。如果Eureka Server在多个心跳周期内没有接收到某个节点的心跳，EurekaServer将会从服务注册表中把这个服务节点移除（默认90秒）
```

例如：

​	Eureka Server 提供服务注册和发现。

​	Service Provider服务提供方将自身服务注册到Eureka，从而使服务消费方能够找到。

​	Service Consumer服务消费方从Eureka获取注册服务列表，从而能够消费服务。

### ③构建步骤：

​	完整的provider

1. ```
   创建microservicecloud-provider-dept-8001工程
   ```

2. appliction.yml

   ```application.yml
   server:
     port: 8001

   mybatis:
     config-location: classpath:mybatis/mybatis.cfg.xml
     type-aliases-package: com.atguigu.springcloud.entities
     mapper-locations:
       - classpath:mybatis/mapper/**/*.xml

   spring:
     application:
       name: microservicecloud-dept #这个名字是注册到Eureka的名字，也是暴露给服务的端口的名字
     datasource:
       type: com.alibaba.druid.pool.DruidDataSource
       driver-class-name: org.gjt.mm.mysql.Driver
       url: jdbc:mysql://localhost:3306/clouddb01?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=utf8          
       username: root
       password: 456654
       dbcp2:
         min-idle: 5
         initial-size: 5
         max-total: 5
         max-wait-millis: 200
   eureka:
     client: #客户端注册进Eureka服务列表内
       service-url:
         defaultZone: http://localhost:7001/eureka
     instance:
       instance-id: mincroservicecloud-dept8001 #修改注册到Eureka链接名,自定义服务名称信息
       prefer-ip-address: true #访问路径可以显示IP地址

   info:
     app.name: houyachao
     company.name: www.houyachao.com
     build.artifactId: $project.artifactId$
     build.version: $project.version$

   ```

3. 在启动类上添加注解

   ```
   @EnableEurekaClientn

   ```
   ​

   **构建Eureka**

   microservicecloud-eureka-7001
   eureka服务注册中心Module

   ```
   1.新建microservicecloud-eureka-7001
   2.POM
   	<dependencies>
      <!--eureka-server服务端 -->
      <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-eureka-server</artifactId>
      </dependency>
      <!-- 修改后立即生效，热部署 -->
      <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>springloaded</artifactId>
      </dependency>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
      </dependency>
     </dependencies>

   3.YML
   	server: 
     port: 7001
    
   eureka:
     instance:
       hostname: localhost #eureka服务端的实例名称
     client:
       register-with-eureka: false #false表示不向注册中心注册自己。
       fetch-registry: false #false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服																					务
       service-url:
         defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/        #设置与Eureka Server交互的地址查询服务和注册服务都需要依赖这个地址。
    
   4.EurekaServer7001_App主启动类
   	@EnableEurekaServer
   5.测试
   	http://localhost:7001/
   	结果页面
   		No application available 没有服务被发现 O(∩_∩)O
   		因为没有注册服务进来当然不可能有服务被发现
   ```
   microservicecloud-provider-dept-8001
   将已有的部门微服务注册进eureka服务中心

   	1.修改microservicecloud-provider-dept-8001
   	2.POM
   		修改部分
   		<!-- 将微服务provider侧注册进eureka -->
   	   <dependency>
   	     <groupId>org.springframework.cloud</groupId>
   	     <artifactId>spring-cloud-starter-eureka</artifactId>
   	   </dependency>
   	   <dependency>
   	     <groupId>org.springframework.cloud</groupId>
   	     <artifactId>spring-cloud-starter-config</artifactId>
   	   </dependency>
   		完整内容
   	3.YML
   		修改部分
   		eureka:
   	  client: #客户端注册进eureka服务列表内
   	    service-url: 
   	      defaultZone: http://localhost:7001/eureka
   		完整内容
   	4.DeptProvider8001_App主启动类
   		@EnableEurekaClient
   	5.测试
   		先要启动EurekaServer
   		http://localhost:7001/
   		微服务注册名配置说明

### ④actuator与注册微服务信息完善

1. 主机名称:服务名称修改

    修改microservicecloud-provider-dept-8001
    		YML
    			修改部分
    			eureka:
    	  			client: #客户端注册进eureka服务列表内
    	   			  service-url: 
    	      		     defaultZone: http://localhost:7001/eureka
    	 			 instance:
    	    			 instance-id: microservicecloud-dept8001

2. 访问信息有IP信息提示

    当前问题
    		没有IP提示
    	修改microservicecloud-provider-dept-8001
    		YML
    			修改部分
    			eureka:
    	  			client: #客户端注册进eureka服务列表内
    	    			service-url: 
    	      				defaultZone: http://localhost:7001/eureka
    	  			instance:
    	    		instance-id: microservicecloud-dept8001   #自定义服务名称信息
    	    		prefer-ip-address: true     #访问路径可以显示IP地址	

③微服务info内容详细信息

	当前问题
		超链接点击服务报告ErrorPage
	修改microservicecloud-provider-dept-8001
		POM
			修改部分
			<dependency>
	       		<groupId>org.springframework.boot</groupId>
	       		<artifactId>spring-boot-starter-actuator</artifactId>
	  	 	</dependency>
	  	 	
	总的父工程microservicecloud修改pom.xml添加构建build信息
		POM
			修改部分
	            <build>
	           <finalName>microservicecloud</finalName>
	           <resources>
	             <resource>
	               <directory>src/main/resources</directory>
	               <filtering>true</filtering>
	             </resource>
	           </resources>
	           <plugins>
	             <plugin>
	               <groupId>org.apache.maven.plugins</groupId>
	               <artifactId>maven-resources-plugin</artifactId>
	               <configuration>
	                 <delimiters>
	                  <delimit>$</delimit>
	                 </delimiters>
	               </configuration>
	             </plugin>
	           </plugins>
	          </build>


	修改microservicecloud-provider-dept-8001
		YML
			修改部分
			info:
	          app.name: atguigu-microservicecloud
	          company.name: www.atguigu.com
	          build.artifactId: $project.artifactId$
	          build.version: $project.version$
### ⑤Eureka自我保护

​	一句话：某时刻某一个微服务不可用了，eureka不会立刻清理，依旧会对该微服务的信息进行保存。

```
什么是自我保护模式？
  
	默认情况下，如果EurekaServer在一定时间内没有接收到某个微服务实例的心跳，EurekaServer将会注销该实例（默认90秒）。但是当网络分区故障发生时，微服务与EurekaServer之间无法正常通信，以上行为可能变得非常危险了——因为微服务本身其实是健康的，此时本不应该注销这个微服务。Eureka通过“自我保护模式”来解决这个问题——当EurekaServer节点在短时间内丢失过多客户端时（可能发生了网络分区故障），那么这个节点就会进入自我保护模式。一旦进入该模式，EurekaServer就会保护服务注册表中的信息，不再删除服务注册表中的数据（也就是不会注销任何微服务）。当网络故障恢复后，该Eureka Server节点会自动退出自我保护模式。
 
	在自我保护模式中，Eureka Server会保护服务注册表中的信息，不再注销任何服务实例。当它收到的心跳数重新恢复到阈值以上时，该Eureka Server节点就会自动退出自我保护模式。它的设计哲学就是宁可保留错误的服务注册信息，也不盲目注销任何可能健康的服务实例。一句话讲解：好死不如赖活着
 
	综上，自我保护模式是一种应对网络异常的安全保护措施。它的架构哲学是宁可同时保留所有微服务（健康的微服务和不健康的微服务都会保留），也不盲目注销任何健康的微服务。使用自我保护模式，可以让Eureka集群更加的健壮、稳定。
 
	在Spring Cloud中，可以使用eureka.server.enable-self-preservation = false 禁用自我保护模式。

```

### ⑥集群搭建

**可以参考博客**：https://blog.csdn.net/j080624/article/details/81112809

1. 新建 microservicecloud-eureka-7002/microservicecloud-eureka-7003工程

2. 按照7001位模板粘贴POM

3. 修改7002和7003启动类

4. 修改映射配置

   4.1 找到 C:\Windows\System32\drivers\etc路径下的hosts文件

   4.2 修改映射配置添加进hosts文件

   ​	127.0.0.1    eureka7001.com

   ​	127.0.0.1    eureka7002.com

   ​	127.0.0.1    eureka7003.com

5.   三台eureka服务器的yml 配置

   ```
   server:
     port: 7001
   ```


   eureka:
     instance:
       #hostname: localhost #eureka服务器端的实例名称
       hostname: eureka7001.com
     client:
       register-with-eureka: false #false 表示不向注册中心注册自己
       fetch-registry: false #false 表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
       service-url:
         # defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/ #设置与Eureka 									Server交互的地址查询服务和注册服务都需要依赖这个歌地址
         defauleZone: http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/


   ```



6. mincroservicecloud-provider-dept-8001微服务发布到上面3台eureka集群配置中

   ```
   defaultZone: http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/,http://eureka7001.com:7001/eureka/
   ```



### **小坑**:  

如果defaultZone: 写错，这会报一下错误

   ```
报Cannot execute request on any known server 这个错，总的来说就是连接Eureka服务端地址不对。

因为配置eureka.client.serviceUrl.defaultZone错了，
```



### ⑦作为服务注册中心，Eureka比Zookeeper好在哪里？

```
	著名的CAP理论指出，一个分布式系统不可能同时满足C（一致性）、A（可用性）、P（分区容错性）。由于分区容错性P在是分布式系统中必须要保证的，因此我们只能在A和C之间进行权衡。
因此：
	Zookeeper保证的是CP。
	Eureka则是AP。

1. Zookeeper保证CP
  当向注册中心查询服务列表时，我们可以容错注册中心返回的是几分钟前的注册信息，但不能接受服务直接Down掉不可以用。也就是说，服务注册功能对可用性的要求高于一致性。但是zk会出现这样一种情况，当master 节点因为网络故障与其它节点失去联系时，剩余节点会重新leader选举。问题在于，选举leader的时间太长，30-120s。且选举期间整个zk集群都是不可用的，这就导致在选举期间注册服务瘫痪。在云部署的环境下，因网络问题使得zk集群失去master节点是较大概率会发生的事，虽然服务能够最终恢复，但是漫长的选举时间导致的注册长期不可用是不能容忍的。

2.Eureka保证AP
	Eureka看明白了这一点，因此在设计时就优先保证可用性。Eureka各个节点都是平等的，几个节点挂掉不会影响正常节点的工作，剩余的节点依然可以提供注册和查询服务，而Eureka的客户端在向某个Eureka注册或死如果发现链接失败，则会自动切换至其他节点，只有有一台Eureka还在，就能保证注册服务可以用（保证可用性），只不过查到的信息可能不是最新的（不保证强一致性）。处此之外，Eureka还有一种自我保护机制，如果在15分钟内超过85%的节点没有正常的心跳，那么Eureka就认为客户端与注册中心出现了网络故障，此时会出现以下几种情况：
	①Eureka不再从注册列表中移除因为长时间没收到心跳而应该过期的服务。
	②Eureka仍然能够接受新服务的注册和查询请求，但是不会被同步到其他节点上（即保证当前节点依然可用）。
	③当网络稳定时，当前实例新的注册信息会被同步到其他节点中。
```

**因此，Eureka可以很好的应对因网络故障导致部分节点失去联系的情况，而不会像Zookeeper那样使整个注册服务瘫痪。**





```
## 4.Ribbon负载均衡

### ①概述：

​	Spring Cloud Ribbon是基于Netflix Ribbon实现的一套**客户端       负载均衡**的工具。

​	简单的说，Ribbon是Netflix发布的开源项目，主要功能是提供客户端的软件负载均衡算法，将Netflix的中间层服务连接在一起。Ribbon客户端组件提供一系列完善的配置项如连接超时，重试等。简单的说，就是在配置文件中列出Load Balancer（简称LB）后面所有的机器，Ribbon会自动的帮助你基于某种规则（如简单轮询，随机连接等）去连接这些机器。我们也很容易使用Ribbon实现自定义的负载均衡算法。

**能干嘛？**

LB（负载均衡）：
LB，即负载均衡(Load Balance)，在微服务或分布式集群中经常用的一种应用。
负载均衡简单的说就是将用户的请求平摊的分配到多个服务上，从而达到系统的HA。
常见的负载均衡有软件Nginx，LVS，硬件 F5等。
相应的在中间件，例如：dubbo和SpringCloud中均给我们提供了负载均衡，SpringCloud的负载均衡算法可以自定义。 

集中式LB：
	即在服务的消费方和提供方之间使用独立的LB设施(可以是硬件，如F5, 也可以是软件，如nginx), 由该设施负责把访问请求通过某种策略转发至服务的提供方；

进程内LB：
	将LB逻辑集成到消费方，消费方从服务注册中心获知有哪些地址可用，然后自己再从这些地址中选择出一个合适的服务器。

Ribbon就属于进程内LB，它只是一个类库，集成于消费方进程，消费方通过它来获取到服务提供方的地址。
```java

官方资料：https://github.com/Netflix/ribbon/wiki/Getting-Started

②Ribbon配置初步

**具体可以看另一个文档，尚硅谷自带的。**

	1.修改microservicecloud-consumer-dept-80工程
	2.修改pom.xml文件
		内容
		<!-- Ribbon相关 -->
   <dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-eureka</artifactId>
   </dependency>
   <dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-ribbon</artifactId>
   </dependency>
   <dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-config</artifactId>
   </dependency>
  </dependencies>
	
	3.修改application.yml   追加eureka的服务注册地址
		内容
		server:
 			 port: 80

		eureka:
  			client:
   			 register-with-eureka: false
   			 service-url: 
     defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
     
	4.对ConfigBean进行新注解@LoadBalanced    获得Rest时加入Ribbon的配置
	@Configuration
	public class ConfigBean
	{	
 	 @Bean
 	 @LoadBalanced
 	 public RestTemplate getRestTemplate()
	  {
  	 return new RestTemplate();
  		}
	}
	5.主启动类DeptConsumer80_App添加@EnableEurekaClient
	6.修改DeptController_Consumer客户端访问类
//private static final String REST_URL_PREFIX = "http://localhost:8001";
  private static final String REST_URL_PREFIX = "http://MICROSERVICECLOUD-DEPT";
	7.先启动3个eureka集群后，再启动microservicecloud-provider-dept-8001并注册进eureka
	8.启动microservicecloud-consumer-dept-80
	9.测试
		http://localhost/consumer/dept/get/1
		http://localhost/consumer/dept/list
		http://localhost/consumer/dept/add?dname=大数据部
	10.小总结
		Ribbon和Eureka整合后Consumer可以直接调用服务而不用再关心地址和端口号
 	在使用springcloud ribbon客户端负载均衡的时候，可以给RestTemplate bean 加一个**@LoadBalanced注解**，就能让这个RestTemplate在请求时拥有客户端负载均衡的能力。

打开@LoadBalanced的注解源码，唯一不同的地方就是多了一个@Qulifier注解.

搜索@LoadBalanced注解的使用地方，发现只有一处使用了,在LoadBalancerAutoConfiguration这个自动装配类中：这段自动装配的代码的含义不难理解，就是利用了RestTemplate的拦截器，使用RestTemplateCustomizer对所有标注了@LoadBalanced的RestTemplate Bean添加了一个LoadBalancerInterceptor拦截器，而这个拦截器的作用就是对请求的URI进行转换获取到具体应该请求哪个服务实例ServiceInstance。



总结：Ribbon其实就是一个软负载均衡的客户端组件，它可以和其他所需请求的客户端结合使用，和Eureka结合知识其中的一个实例。

```



### ②Ribbon集群

**Ribbon在工作时分成两步**
第一步先选择 EurekaServer ,它优先选择在同一个区域内负载较少的server.
第二步再根据用户指定的策略，在从server取到的服务注册列表中选择一个地址。
其中Ribbon提供了多种策略：比如轮询、随机和根据响应时间加权。

	请看尚硅谷的课件。

### ③Ribbon的核心组件 IRule

**IRule：根据特定算法中从服务列表中选取一个要访问的服务：**

**如下：**

![Ribbon算法](C:\Users\Administrator\Desktop\笔记\SpringCloud\Ribbon算法.png)

默认的是轮询算法。

实现代码：

```java
在客户端的配置类里面添加：

@Bean
public IRule myRule(){
	//return new RoundRobinRule();  //默认的轮询
	//return new RandomRule();  //随机算法
	return new RetryRule();
}
```

### ④自定义Ribbon的负载均衡策略

源码：https://github.com/Netflix/ribbon/tree/master/ribbon-loadbalancer/src/main/java/com/netflix/loadbalancer

1. 在主启动类添加@RibbonClient

   ```
   在启动该微服务的时候就能去加载我们的自定义Ribbon配置类，从而使配置生效，形如：
   @RibbonClient(name="MICROSERVICECLOUD-DEPT",configuration=mySelfRule.class)

   	name: 是对哪个微服务进行制定。
   	configuration: 进行制定的配置类。
   ```

2. 注意配置细节

   ```
   官方文档明确给出了警告：
   	这个自定义配置类不能放在@ComponentScan所扫描的当前包下以及子包下，否则我们自定义的这个配置类就会被所有的Ribbon客户端锁共享，也就是说我们达不到特殊化定制的目的了。
   ```

3. 步骤

   ```
   1. 新建package 包，存放我们自己定义的负载均衡算法。

   ```

4.  问题依旧轮询策略，但是加上新需求，每个服务器要求被调用5次，也即以前是每台机器一次，现在是每台机器5次。

   ```java
   public class RandomRule_HYC extends AbstractLoadBalancerRule {

     
     //根据需求，我们要求每个服务被调用5次，并且使用轮询
     //  total = 0 ，从0开始，每个微服务被调用5次
     //  index = 0  ，也从0开始，指定是哪个微服务被调用。。微服务一共有3 个
     //  所以当index >3 时，则让index = 0.
     
     private int total=0;
     private int index=0;
     
       /**
        * Randomly choose from all living servers
        */
       @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE")
       public Server choose(ILoadBalancer lb, Object key) {
           if (lb == null) {
               return null;
           }
           Server server = null;

           while (server == null) {
               if (Thread.interrupted()) {
                   return null;
               }
               List<Server> upList = lb.getReachableServers();  //可用的服务列表
               List<Server> allList = lb.getAllServers();		//总共有多少个微服务

               int serverCount = allList.size();
               if (serverCount == 0) {
                   /*
                    * No servers. End regardless of pass, because subsequent passes
                    * only get more restrictive.
                    */
                   return null;
               }

               //int index = chooseRandomInt(serverCount);
              // server = upList.get(index);
             
             if(total < 5){
               total++;
               server=upList.get(index);
             }else{
               total=0;
               index ++ ;
               if(index >= upList.size()){
                 index =0;
               }
             }
             
            
               if (server == null) {
                   /*
                    * The only time this should happen is if the server list were
                    * somehow trimmed. This is a transient condition. Retry after
                    * yielding.
                    */
                   Thread.yield();
                   continue;
               }

               if (server.isAlive()) {
                   return (server);
               }

               // Shouldn't actually happen.. but must be transient or a bug.
               server = null;
               Thread.yield();
           }

           return server;

       }

       protected int chooseRandomInt(int serverCount) {
           return ThreadLocalRandom.current().nextInt(serverCount);
       }

   	@Override
   	public Server choose(Object key) {
   		return choose(getLoadBalancer(), key);
   	}

   	@Override
   	public void initWithNiwsConfig(IClientConfig clientConfig) {
   		// TODO Auto-generated method stub
   		
   	}
   }
   ```


   ```

5.  修改MySelRule配置类

   ```java
   在客户端的配置类里面添加：

   @Bean
   public IRule myRule(){
   	//return new RoundRobinRule();  //默认的轮询
   	//return new RandomRule();  //随机算法
   	//return new RetryRule(); 
   	reutrn new RundomRule_HYC();  //自定义为每天微服务执行5次
   }
   ```

   ## 4.Feign负载均衡 服务调用

###    ①概述：

```
官网解释：
http://projects.spring.io/spring-cloud/spring-cloud.html#spring-cloud-feign
 
 	Feign是一个声明式WebService客户端。使用Feign能让编写Web Service客户端更加简单, 它的使用方法是定义一个接口，然后在上面添加注解，同时也支持JAX-RS标准的注解。Feign也支持可拔插式的编码器和解码器。Spring Cloud对Feign进行了封装，使其支持了Spring MVC标准注解和HttpMessageConverters。Feign可以与Eureka和Ribbon组合使用以支持负载均衡。

 	Feign是一个声明式的Web服务客户端，使得编写Web服务客户端变得非常容易，
只需要创建一个接口，然后在上面添加注解即可。
参考官网：https://github.com/OpenFeign/feign 
 
 Feign能干什么
	Feign旨在使编写Java Http客户端变得更容易。
	前面在使用Ribbon+RestTemplate时，利用RestTemplate对http请求的封装处理，形成了一套模版化的调用方法。但是在实际开发中，由于对服务依赖的调用可能不止一处，往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一些客户端类来包装这些依赖服务的调用。所以，Feign在此基础上做了进一步封装，由他来帮助我们定义和实现依赖服务接口的定义。在Feign的实现下，我们只需创建一个接口并使用注解的方式来配置它(以前是Dao接口上面标注Mapper注解,现在是一个微服务接口上面标注一个Feign注解即可)，即可完成对服务提供方的接口绑定，简化了使用Spring cloud Ribbon时，自动封装服务调用客户端的开发量。


Feign集成了Ribbon
	利用Ribbon维护了MicroServiceCloud-Dept的服务列表信息，并且通过轮询实现了客户端的负载均衡。而与Ribbon不同的是，通过feign只需要定义服务绑定接口且以声明式的方法，优雅而简单的实现了服务调用。
 
```

### ②Feign使用步骤

请看尚硅谷给的课件。

​	

总结：  

​	 Feign通过接口的方法调用Rest服务（之前是Ribbon+RestTemplate），
该请求发送给Eureka服务器（http://MICROSERVICECLOUD-DEPT/dept/list）,
通过Feign直接找到服务接口，由于在进行服务调用的时候融合了Ribbon技术，所以也支持负载均衡作用。

## 5.Hystrix断路器

### ①概述

```
分布式系统面临的问题
复杂分布式体系结构中的应用程序有数十个依赖关系，每个依赖关系在某些时候将不可避免地失败。

服务雪崩
多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其它的微服务，这就是所谓的“扇出”。如果扇出的链路上某个微服务的调用响应时间过长或者不可用，对微服务A的调用就会占用越来越多的系统资源，进而引起系统崩溃，所谓的“雪崩效应”.
 
对于高流量的应用来说，单一的后端依赖可能会导致所有服务器上的所有资源都在几秒钟内饱和。比失败更糟糕的是，这些应用程序还可能导致服务之间的延迟增加，备份队列，线程和其他系统资源紧张，导致整个系统发生更多的级联故障。这些都表示需要对故障和延迟进行隔离和管理，以便单个依赖关系的失败，不能取消整个应用程序或系统。
```

​	Hystrix是一个用于处理分布式系统的延迟和容错的开源库，在分布式系统里，许多依赖不可避免的会调用失败，比如超时、异常等**，Hystrix能够保证在一个依赖出问题的情况下，不会导致整体服务失败，避免级联故障，以提高分布式系统的弹性**。

​	“断路器”本身是一种开关装置，**当某个服务单元发生故障之后，通过断路器的故障监控（类似熔断保险丝），向调用方返回一个符合预期的、可处理的备选响应（FallBack），而不是长时间的等待或者抛出调用方无法处理的异常**，这样就保证了服务调用方的线程不会被长时间、不必要地占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。

能干嘛：

​	服务降级。

​	服务熔断。

​	服务限流。

​	接近实时的监控。

​	。。。。

**官网资料：**

	https://github.com/Netflix/Hystrix/wiki/How-To-Use

### ②服务熔断

服务熔断
​	熔断机制是应对雪崩效应的一种微服务链路保护机制。
​	当扇出链路的某个微服务不可用或者响应时间太长时，会进行服务的降级，进而熔断该节点微服务的调用，快速返回"错误"的响应信息。当检测到该节点微服务调用响应正常后恢复调用链路。在SpringCloud框架里熔断机制通过Hystrix实现。Hystrix会监控微服务间调用的状况，当失败的调用到一定阈值，缺省是5秒内20次调用失败就会启动熔断机制。熔断机制的注解是@HystrixCommand。



服务熔断：一般是某个服务故障或者异常引起，类似现实世界中的 “保险丝”，当某个异常条件被触发，熔断整个服务，而不是一直等到此服务超时。

过程可以参考尚硅谷课件。

### ③服务降级

1. 是什么？

   **整体资源快不够了，忍痛将某些服务先关掉，待渡过难关，再开启回来。**

   **服务降级处理是在客户端实现完成的，与服务端没有关系。**

   所谓降级，一般是从整体负荷考虑。就是当某个服务熔断之后，服务器不再被调用，此时客户端可以自己准备一个本地的fallback回调，返回一个缺省值。这样做，虽然服务水平下降，但好歹可用，比直接挂掉要强。

2. 步骤请看尚硅谷课件。



### ④服务监控hystrixDashboard

1. 概述

   ​	除了隔离依赖服务的调用以外，Hystrix还提供了准实时的调用监控（Hystrix Dashboard），Hystrix会持续地记录所有通过Hystrix发起的请求的执行信息，并以统计报表和图形的形式展示给用户，包括每秒执行多少请求多少成功，多少失败等。Netflix通过hystrix-metrics-event-stream项目实现了对以上指标的监控。Spring Cloud也提供了Hystrix Dashboard的整合，对监控内容转化成可视化界面。

2. 操作步骤

   请看尚硅谷课件。

## 6.Zuul路由网关

### **1.概述**

Zuul包含了对请求的**路由和过滤**两个最主要的功能：
​	其中路由功能负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础而过滤器功能则负责对请求的处理过程进行干预，是实现请求校验、服务聚合等功能的基础.


​	Zuul和Eureka进行整合，将Zuul自身注册为Eureka服务治理下的应用，同时从Eureka中获得其他微服务的消息，也即以后的访问微服务都是通过Zuul跳转后获得。

    注意：Zuul服务最终还是会注册进Eureka

**提供=代理+路由+过滤三大功能**

官网资料：

	https://github.com/Netflix/zuul/wiki/Getting-Started

2. ### 路由基本配置

   请看尚硅课件。

3. ### 路由访问映射规则

   请看尚硅谷课件。

## 7.SpringCloud-Config分布式配置中心

### ①概述

1. **是什么：**

![SpringCloud-config](C:\Users\Administrator\Desktop\笔记\SpringCloud\SpringCloud-config.png)

```
上图：   
	我们可以将外部配置通过git 上传到GitHub仓库中，然后ConfigServer会通过检查仓库中的变化，自动的下载下来，并发布给客户端。
	就好比，Configserver相当于班长，后面的相当于班主任，班主任那里有什么情况变化，班长会得知到，通知给办理的同学。

是什么：
	SpringCloud Config 为微服务架构中的微服务提供集中化的外部配置支持，配置服务器为各个不同微服务应用的所有环境提供了一个中心化的外部配置。
怎么用：
	SpringCloud Config分为服务端和客户端两部分。
	服务端也称为分布式配置中心，它是一个独立的微服务应用，用来连接配置服务器并未客户端提供获取配置信息，加密/解密信息等访问接口。
	客户端则是通过指定的配置中心来管理应用资源，以及与业务相关的配置内容，并在启动的时候从配置中心获取和加载配置信息、配置服务器默认采用git来存储配置信息。这样就有助于对环境配置进行版本管理，并且可以通过git客户端工具来方便的管理和访问配置内容。

```

2. **能干嘛：**

```
1.集中管理配置文件。
2.不同环境不同配置，动态化的配置更新，分环境部署比如，dev/test/prod/beta/release。
3.运行期间动态调整配置，不再需要在每个服务部署的机器上编写配置文件，服务会向配置中心统一拉去配置自己的信	息	。
4.当配置发生变动时，服务不再需要重启即可感知到配置的变化并应用新的配置。
5.将配置信息以REST接口的形式暴露。


与GitHub整合配置。
```



3. ### SpringCloud Config服务端配置

   ```
   1.用自己的GitHub账号在GitHub上新建一个名为microservicecloud-config的新Repository
   2.由上一步获得SSh协议的git地址   git@github.com:zzyybs/microservicecloud-config.git
   3.本地硬盘目录上新建一个git仓库并clone下来。
   	本地地址：D:\44\MySpringCloud
   	git命令：git clone git@github.com:zzyybs/microservicecloud-config.git
   4.在本地D:\44\MySpringCloud\microservicecloud-config 里面新建一个application.yml
   	内容：
   		spring:	
   		  profiles: 
   		    active:	
   		    - dev
   		 -----
   		 spring:	
   		   profiles: dev #开发环境
   		   application:
   		   	 name: microservicecloud-config-atguigu-dev
   		   -----
   		 spring:	
   		   profiles: test #开发环境
   		   application:
   		   	 name: microservicecloud-config-atguigu-test
   		 #一定要将上述保存为UTF-8格式
   5.将上一步的YML文件推送到GitHub上
   	git add .
   	git commit -m "wo ti jiao le"
   	git push origin master
   6.新建Module模块microservicecloud-config-3344 它即为Cloud的配置中心模块
   7.POM
   	<!-- springCloud Config -->
   	<dependency>
   		<groupId>org.springframework.cloud</groupId>
   		<artifactId>spring-cloud-config-server</artifactId>
   	</dependency>
   	<!--热部署 -->
   	<dependency>
   		<groupId>org.springframework</groupId>
   		<artifactId>springloaded</artifactId>
   	</dependency>
   	<dependency>
   		<groupId>org.springframework.boot</groupId>
   		<artifactId>spring-boot-devtools</artifactId>
   	</dependency>
   	...
   	<!--避免Config的Git插件报错，org/eclipse/jgit/api/TransportConfigCallback -->
   	<dependency>
   		<groupId>org.eclipse.jgit</groupId>
   		<artifactId>org.eclipse.jgit</artifactId>
   		<version>4.10.0.201712302008-r</version>
   	</dependency>
   8.修改YML
   	server:
   	  port: 3344
   	  
   	 spring:
   	   application:
   	     name: microserviceclod-config
   	   cloud:
   	     config:
   	       server:
   	         git:
   	           uri: #GitHub上面git仓库名字
   	        
   9.启动主启动类,并加  @EnableConfigServer 注解
   10.Windows下修改hosts文件，增加映射 
   		127.0.0.1 config-3344.com
   11.测试通过Config微服务是否可以从GitHub上获取配置内容
   	启动微服务3344
   	http://config-3344.com:3344/application-dev.yml
   	http://config-3344.com:3344/application-test.yml
   	http://config-3344.com:3344/application-xxx.yml   (不存在的配置)
   	
   11.成功实现了用SpringCloud Config通过GItHub获取配置信息。
   	
   ```

   ​

4. ### 客户端配置与测试

   ```
   1. 在本地D:\44\mySpringCloud\microservicecloud-config 路径下新建文件microservicecloud-config-client.yml
   2. microservicecloud-config-client.yml 内容
   	spring:
   		profiles:
   			active:
   			- dex
   	-------
   	server:
   		port:8201
   	spring:
   		profiles: dev
   		application:
   			name: microservicecloud-config-client
   	eureka:
   		client:
   			service-url:
   				defaultZone: http://eureka-dev.com:7001/eureka/
   	-------
   	server:
   		port:8202
   	spring:
   		profiles: test
   		application:
   			name: microservicecloud-config-client
   	eureka:
   		client:
   			service-url:
   				defaultZone: http://eureka-test.com:7001/eureka/

   3. 将上一步提交到GitHub上
   4. 新建microservicecloud-config-client-3355
   5.POM 可以参考上面的
   	<!-- SpringCloud Config客户端 -->
   	<dependency>
   		<groupId>org.springframework.cloud</groupId>
   		<artifactId>spring-cloud-starter-config</artifactId>
   	</dependency>
   6. 新建bootstrap.yml
   	spring:
   		cloud:
   			config:
   				name: microservicecloud-client #需要从GitHub上读取的资源的名称，注意没有yml后																				缀名
   				profile: dev  #本次访问的配置项
   				label: master
   				uri: http://config-3344.com:3344 #本微服务启动先去找3344号服务，通过SpringCloudConfig获取GitHub的服务地址

   7.application.yml
   	spring:
   		application:
   			name: microservicecloud-config-client
   			
   8.windows下修改hosts文件，增加映射 
   	127.0.0.1 client-config.com
   9.新建rest类，验证是否能从GitHub上读取配置信息
   @RestController
   public class ConfigClientRest{
     @Value("${spring.application.name}")
     private String applicationName;
     @Value("${eureka.client.service-url.defaultZone}")
     private String eurekaServers;
     @Value("${server.port}")
     private String port;
     @RequestMapping("/config")
     public String getConfig(){
       String str = "applicationName:"+applicationName+"\t eurekaServers:"+eurekaServers+"\t port: "+port;
       System.out.println(str);
       return str;
     }
   }

   12. 主启动类
   ```

   application.yml 是用户级的资源配置项

   bootstrap.yml 是系统级的，优先级更加高

   SpringCloud会创建一个Bootstrap Context ，作为Spring应用Application Context的父上下文。初始化的时候，Bootstrap Context 负责从外部源加载配置属性并解析配置，这两个上下文共享一个从外部获取的Environment。

   Bootstrap属性有高优先级，默认情况下，他们不会被本地配置覆盖，Bootstrap context 和Application Context有着不同的约定，所有新增了一个Bootstrap.ml文件，保证Bootstrap Context 和Application Context配置的分离。



## 8. 架构：

![Snipaste_2018-12-20_14-18-36](C:\Users\Administrator\Desktop\笔记\SpringCloud\Snipaste_2018-12-20_14-18-36.png)

![Snipaste_2018-12-20_14-20-41](C:\Users\Administrator\Desktop\笔记\SpringCloud\Snipaste_2018-12-20_14-20-41.png)



























































































































