

# 1.Springboot整合

## 1.1.缓存注解

| Cache          | 缓存接口，定义缓存操作。实现由RedisCache，EhCacheCache，ConcurrentMapCache等 |
| -------------- | ---------------------------------------- |
| CacheManager   | 缓存管理器，管理各种缓存（Cache）组件                    |
| @Cacheable     | 主要针对方法配置，能够根据方法的请求参数对其结果进行缓存。            |
| @CacheEvict    | 清空缓存                                     |
| @EnableCaching | 开启基于注解的缓存                                |
| keyGenerator   | 缓存数据时key生成策略                             |
| serialize      | 缓存数据时value序列化策略                          |
| @CachePut      | 保证方法被调用，又希望结果被缓存                         |

```application.properties
//开启驼峰命名
mybaties.configuration.map-underscore-to-camel-case=true
//将日志信息打印
logging.level.打印的包路径=debug
//开启自动配置报告
debug=true
//整合Redis 
spring.redis.host=主机地址
```

## 1.2.体验缓存

步骤：

1.开启基于注解的缓存@EnableCaching，在主程序上标注

2.标注缓存注解即可

​	@Cacheable

​	@CacheEvict

```java
@CacheConfig(cacheNames="emp",cacheManager="employeeCacheManager")  //抽取缓存的公共配置   这里配置好以后，下面都不要再配置了。     启用配置好的缓存管理，也可以在每一个方法上启用。
@Service
public class EmployeeService{
  
  @Autowired
  EmployeeMapper employeeMapper;
  
  /**
  	将方法的运行结果进行缓存，以后再要相同的数据。直接从缓存中获取，不用调用方法；
  	CacheManager管理多个Cache组件的，对缓存的真正CRUD操作在Cache组件中，每一个缓存组件有自己唯一一个名字；
  	原理：
  		1.缓存自动配置类：CacheAutoConfiguration
  		2.缓存的配置类
  		org.springframework.boot.autoconfigure.cache.GenericCacheConfiguration
        org.springframework.boot.autoconfigure.cache.JCacheCacheConfiguration
        org.springframework.boot.autoconfigure.cache.EhCacheCacheConfiguration
        org.springframework.boot.autoconfigure.cache.HazelcastCacheConfiguration
        org.springframework.boot.autoconfigure.cache.InfinispanCacheConfiguration
        org.springframework.boot.autoconfigure.cache.CouchbaseCacheConfiguration
        org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration
        org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration
        org.springframework.boot.autoconfigure.cache.GuavaCacheConfiguration
        org.springframework.boot.autoconfigure.cache.SimpleCacheConfiguration【默认】
        org.springframework.boot.autoconfigure.cache.NoOpCacheConfiguration
        3.哪个配置类默认生效：SimpleCacheConfiguration；
        4.给容器中注册了一个CacheManager：ConcurrentMapCacheManager
        5.可以获取和创建ConcurrentMapCache类型的缓存组件；他的作用将数据保存在ConcurrentMap中；
        
        运行流程：
        @Cacheable：
        1.方法运行之前，先去查询Cache（缓存组件），按照CacheNames指定的名字获取；
        	（CacheManager先获取相应的缓存），第一次获取缓存如果没有Cache组件会自动创建。
        2.去Cache中查找缓存的内容，使用一个key, 默认就是方法的参数；
        	key是按照某种策略生成的；默认是使用keyGenerator生成的，默认使用SimpleKeyGenerator生成key
        		SimpleKeyGenerator生成key的默认策略；
        			如果没有参数：key=new SimpleKey();
        			如果有一个参数：key=参数的值；
        			如果有多个参数：key=new SimpleKey(params);
        3.没有查到缓存就调用目标方法；
        4.将目标方法返回的结果，放入缓存中。

		核心：	
			1).使用CacheManager【ConcurrentMapCacheManager】按照名字得到Cache【ConcurrentMapCache】组件
			2).key使用keyGenerator生成的，默认是SimpleKeyGenerator。

  	
  	几个属性：
  		cacheNames/values:指定缓存组件的名字，将方法的返回结果放在哪个缓存中，是数组的方式，可以指定多						个缓存；；
  		key:缓存数据使用的key;可以用它来指定。默认是使用方法参数的值  1-方法的返回值
  			编写SpEl；#id；参数id的值  #a0  #p0 #root.args[0]
  		keyGenerator: key的生成器；可以自己指定key的生成器的组件id
  			key/keyGenerator: 二选一使用。
  		cacheManager:指定缓存管理器；或者cacheResolver指定获取解析器
  		condition: 指定符合条件的情况下才缓存；
  			condition = "#id>0"。
  			condition = "#a0>1": 第一个参数的值 > 1 的时候才进行缓存。
  		unless: 否定缓存；当unless指定的条件为true，方法的返回值就不会被缓存；可以获取到结果进行判断				unless = "#result == null";
  			unless = "#a0==2": 如果第一个参数的值是2，结果不缓存。
  		sync: 是否使用异步模式
  */
  @Cacheable(cacheNames="{emp}")
  public Employee getEmp(Integer id){
    System.out.println("查询"+id+"号员工");
    Employee emp=employeeMapper.getEmpById(id);
    return emp;
  }
  
  /**
  	@CachePut: 即调用方法，又更新缓存数据；同步更新缓存
  	修改了数据库的某个数据，同时更新缓存；
  	运行时机：
  		1.先调用目标方法
  		2.将目标方法的结果缓存起来
  		
  	测试步骤：
  		1.查询1号员工；查到的结果会放在缓存中；
  			key: 1 value: lastName: 张三
  		2.以后查询还是之前的结果
  		3.更新1号员工；【lastName:zhangsan; gender:0】
  			将方法的返回值也放进缓存了；
  			默认情况下; key: 传入的employee对象 值：返回的employee对象。   方法的参数
  		4.查询1号员工？
  			应该是更新后的员工；
  				key="#employee.id": 使用传入的参数的员工id；
  				key="#result.id"：使用返回后的id；
  					@Cahceable的key是不能用#result，因为@Cacheable是先执行缓存，然后才执行目标方					 法，所有没办法将缓存结果的属性作为key。
  */
  @CachePut(value="emp",key="#result.id")
  public Employee updateEmp(Employee employee){
    System.out.println("updateEmp: "+employee);
    employeeMapper.updateEmp(employee);
    return employee;
  } 
  
  /**
  	@CacheEvict: 缓存清除
  	key: 指定要清除的数据
  	allEntries = true： 指定清除这个缓存中所有的数据。
  	beforeInvocation = false：缓存的清除是否在方法之前执行
  		默认代表缓存清除操作是在方法执行之后执行；如果出现异常缓存就不会清除。
  	
  	beforeInvocation = true：
  		代表清除缓存操作是在方法运行之前执行，无论方法是否出现异常，缓存都清除
  */
  @CacheEvict(value="emp",beforeInvocation=true)
  public void deleteEmp(Integer id){
    System.out.println("deleteEmp: "+ id);
  }
  
  //Caching 定义复杂的缓存
  @Caching{
    cacheable={
      @Cacheable(value="emp",key="#lastName")
    },
    put={
      @CachePut=(value="emp",key="#result.id"),
      @cachePut=(value="emp",key="#result.email")
    }
  }
  public Employee getEmpByLastName(String lastName){
    System.out.println("根据名字查询员工信息： "+ lastName);
  }
  
  @Qualifier("deptCacheManager")  //明确指定deptCacheManager 缓存管理器
  @Autowried
  RedisCacheManager deptCacheManager;
  
  //使用缓存管理器得到缓存，进行api调用
  public Department getDeptById(Integer id){
    System.out.println("查询部门："+id);
    Department department = departmentMapper.getDeptById(id);
    //获取某个缓存
    Cahce dept = deptCacheManager.getCache("dept");       //使用编码的方式使用缓存
    dept.put("dept:1",department);
    return department;
  }
}
```

```java
区别：
	@Cacheable: 是先执行缓存，去缓存中查询数据，如果缓存中没有数据，则去执行目标方法，然后将目标方法结					果的返回值放入缓存。
	@CachePut： 是先执行目标方法，然后将目标方法的返回值存入缓存
```

### 1.2.1Springboot整合缓存

调用缓存在service 层调用，看上面的代码：

```application.properties
//整合Redis 
spring.redis.host=主机地址
```

```java
//测试redis缓存
public class TestRedis{
  
  @Autowired
  StringRedisTemplate stringRedisTemplate;  //操作k-v都是字符串的数据
  @Autowired
  RedisTemplate redisTemplate;  //操作k-v都是对象的数据
  @Autowired
  RedisTemplate<Object,Employee> empRedisTemplate;   //自定义序列化规则
  
  /**
  	Redis常见的五大数据类型
  	String(字符串)，List（列表），Set（集合），Hash（散列），ZSet（有序集合）
  	stringRedisTemplate.opsForValue() [String (字符串)]
  	stringRedisTemplate.opsForList() [List(列表)]
  	stringRedisTemplate.opsForSet() [Set(集合)]
  	stringRedisTemplate.opsForHash() [Hash(散列)]
  	stringRedisTemplate.opsForZSet() [ZSet(有序集合)]
    */
  //测试保存对象
  @Test
  public void test01(){
    Employee empById = employeeMapper.getEmpById(1);
    //默认如果保存对象，使用JDK序列化机制，序列化后的数据保存到redis中
    //redisTemplate.opsForValue().set("emp-01",empById);
    //1.将数据以json的方式保存
    //2.redisTemplate 默认的序列化规则；   改变默认的序列化规则；empRedisTemplate
    empRedisTemplate.opsForValue().set("emp-01", empById);
  }
}
```

```java

@Configuratrion
public class MyRedisConfig{
  //自定义序列化，将对象转变为json字符串存入redis 缓存中
  @Bean
  public RedisTemplate<Object,Employee> empRedisTemplate(RedisConnectionFactory 					redisConnectionFactory) throws UnknownHostException{
    RedisTemplate<Object, Employee> template = new RedisTemplate<Object, Employee>();
    template.setConnectionFactory(redisConnectionFactory);
    Jackson2JsonRedisSerializer<Employee> ser = new Jackson2JsonRedisSerializer<Employee>		(Employee.class);
    template.setDefaultSerializer(ser);
    return template;
  }
  //自定义缓存管理器。 CacheManagerCustomizers可以来定制缓存的一些规则
  @Primary   //将某个缓存管理器作为默认的，   当有多个缓存管理器的时候
  @Bean
  public RedisCacheManager employeeCacheManager(RedisTemplate<Object,Employee> empRedisTemplate){
    RedisCacheManager cahceManager = new RedisCacheManager(empRedisTemplate);
    //key 多了一个前缀
    //使用前缀，默认会将CacheName作为key 的前缀
    cacheManager.setUsePrefix(true);
    return cacheManager;
  }
  
}
```

## 1.3.SpringBoot与消息

### 一、 简述：

1.大多应用中，可通过消息服务中间件来提升系统异步通信、扩展解耦能力

2.消息服务中两个重要概念：

​     	 **消息代理（message broker）和目的地（destination）**

​	当消息发送者发送消息以后，将由消息代理接管，消息代理保证消息传递到指定目的地。

#### **3.消息队列主要有两种形式的目的地**

​       1.队列（queue）：点对点消息通信（point-to-point）

​       2.主题（topic）：发布（publish）/订阅（subscribe）消息通信

#### 4.点对点式：

​	–消息发送者发送消息，消息代理将其放入一个队列中，消息接收者从队列中获取消息内容，消息读取后被移出队列

​	–消息只有唯一的发送者和接受者，但并不是说只能有一个接收者

#### 5.发布订阅式：

​	–发送者（发布者）发送消息到主题，多个接收者（订阅者）监听（订阅）这个主题，那么就会在消息到达时同时收到消息

#### 6.JMS（Java Message Service）JAVA消息服务：

​	–基于JVM消息代理的规范。ActiveMQ、HornetMQ是JMS实现

#### 7.AMQP（Advanced Message Queuing Protocol）

​	–高级消息队列协议，也是一个消息代理的规范，兼容JMS

​	–RabbitMQ是AMQP的实现

------



|        | JMS                                      | AMQP                                     |
| ------ | ---------------------------------------- | ---------------------------------------- |
| 定义     | Java  api                                | 网络线级协议                                   |
| 跨语言    | 否                                        | 是                                        |
| 跨平台    | 否                                        | 是                                        |
| Model  | 提供两种消息模型：  （1）、Peer-2-Peer  （2）、Pub/sub  | 提供了五种消息模型：  （1）、direct  exchange  （2）、fanout  exchange  （3）、topic  change  （4）、headers  exchange  （5）、system  exchange  本质来讲，后四种和JMS的pub/sub模型没有太大差别，仅是在路由机制上做了更详细的划分； |
| 支持消息类型 | 多种消息类型：  TextMessage  MapMessage  BytesMessage  StreamMessage  ObjectMessage  Message  （只有消息头和属性） | byte[]  当实际应用时，有复杂的消息，可以将消息序列化后发送。       |
| 综合评价   | JMS  定义了JAVA  API层面的标准；在java体系中，多个client均可以通过JMS进行交互，不需要应用修改代码，但是其对跨平台的支持较差； | AMQP定义了wire-level层的协议标准；天然具有跨平台、跨语言特性。   |

8.Spring支持

​	–spring-jms提供了对JMS的支持

​	–spring-rabbit提供了对AMQP的支持

​	–需要ConnectionFactory的实现来连接消息代理

​	–提供JmsTemplate、RabbitTemplate来发送消息

​	–@JmsListener（JMS）、@RabbitListener（AMQP）注解在方法上监听消息代理发布的消息

​	–@EnableJms、@EnableRabbit开启支持

9.SpringBoot自动配置

​	–JmsAutoConfiguration

​	–RabbitAutoConfiguration

### 二、 RabbitMQ简介

RabbitMQ简介：

RabbitMQ是一个由erlang开发的AMQP(Advanved Message Queue Protocol)的开源实现。

核心概念：

#### **Message**：

消息，消息是不具名的，它由消息头和消息体组成。消息体是不透明的，而消息头则由一系列的可选属性组成，这些属性包括routing-key（路由键）、priority（相对于其他消息的优先权）、delivery-mode（指出该消息可能需要持久性存储）等。

#### **Publisher：**

消息的生产者，也是一个向交换器发布消息的客户端应用程序。

#### **Exchange**：

交换器，用来接收生产者发送的消息并将这些消息路由给服务器中的队列。

Exchange有4种类型：direct(默认)，fanout, topic, 和headers，不同类型的Exchange转发消息的策略有所区别。

#### **Queue：**

消息队列，用来保存消息直到发送给消费者。它是消息的容器，也是消息的终点。一个消息可投入一个或多个队列。消息一直在队列里面，等待消费者连接到这个队列将其取走。

#### **Binding：**

绑定，用于消息队列和交换器之间的关联。一个绑定就是基于路由键将交换器和消息队列连接起来的路由规则，所以可以将交换器理解成一个由绑定构成的路由表。

Exchange 和Queue的绑定可以是多对多的关系。

#### **Connection：**

网络连接，比如一个TCP连接。

#### **Channel：**

信道，多路复用连接中的一条独立的双向数据流通道。信道是建立在真实的TCP连接内的虚拟连接，AMQP 命令都是通过信道发出去的，不管是发布消息、订阅队列还是接收消息，这些动作都是通过信道完成。因为对于操作系统来说建立和销毁TCP 都是非常昂贵的开销，所以引入了信道的概念，以复用一条 TCP 连接。

#### **Consumer：**

消息的消费者，表示一个从消息队列中取得消息的客户端应用程序。

#### **Virtual Host：**

虚拟主机，表示一批交换器、消息队列和相关对象。虚拟主机是共享相同的身份认证和加密环境的独立服务器域。每个vhost 本质上就是一个 mini 版的 RabbitMQ 服务器，拥有自己的队列、交换器、绑定和权限机制。vhost 是 AMQP 概念的基础，必须在连接时指定，RabbitMQ 默认的 vhost 是 / 。

#### **Broker：**

表示消息队列服务器实体。

![消息图片](C:\Users\Administrator\Desktop\笔记\SpringBoot\消息图片.png)

### 三、RabbitMQ运行机制

AMQP 中的消息路由

​	•AMQP 中消息的路由过程和 Java 开发者熟悉的 JMS 存在一些差别，AMQP 中增加了 Exchange 和 Binding 的角色。生产者把消息发布到 Exchange 上，消息最终到达队列并被消费者接收，而 Binding 决定交换器的消息应该发送到那个队列。

![图片1](C:\Users\Administrator\Desktop\笔记\SpringBoot\图片1.png)

Exchange类型

•Exchange分发消息时根据类型的不同分发策略有区别，目前共四种类型：direct、fanout、topic、headers 。headers 匹配 AMQP 消息的 header 而不是路由键， headers 交换器和 direct 交换器完全一致，但性能差很多，目前几乎用不到了，所以直接看另外三种类型：

①消息中的路由键（routing key）如果和 Binding 中的 binding key 一致，
交换器就将消息发到对应的队列中。路由键与队列名完全匹配，如果一个队列绑定到交换机要求路由键为“dog”，则只转发 routing key 标记为“dog”的消息，不会转发“dog.puppy”，也不会转发“dog.guard”等等。它是完全匹配、单播的模式。

![图片3](C:\Users\Administrator\Desktop\笔记\SpringBoot\图片3.png)

②每个发到 fanout 类型交换器的消息都会分到所有绑定的队列上去。fanout
交换器不处理路由键，只是简单的将队列绑定到交换器上，每个发送到交换器的消息都会被转发到与该交换器绑定的所有队列上。很像子网广播，每台子网内的主机都获得了一份复制的消息。fanout
类型转发消息是最快的。

![图片4](C:\Users\Administrator\Desktop\笔记\SpringBoot\图片4.png)

③topic 交换器通过模式匹配分配消息的路由键属性，将路由键和某个模式进行匹配，此时队列需要绑定到一个模式上。它将路由键和绑定键的字符串切分成单词，这些单词之间用点隔开。它同样也会识别两个通配符：符号“#”和*符号“*”。#匹配0个或多个单词，*匹配一个单词。

![图片5](C:\Users\Administrator\Desktop\笔记\SpringBoot\图片5.png)



### 消息-RabbitTemplate发送接受消息&序列化机制

192.168.47.130:15692

自动配置：

1.RabbitAutoConfiguration

2.有自动配置了链接工程ConnectionFactory

3.RabbitProperties 封装了RabbitMQ的配置

4.RabbitTemplate： 给RabbitMQ发送和接受消息；

5.AmqpAdmin : RabbitMQ 系统管理功能组件

​	AmqpAdmin: 创建和删除 Queue，Exchange，Binding

6.@EnableRabbit + @RabbitListener 监听消息队列的内容



```application.properties

spring.rabbitmq.host=192.168.47.130
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

```java
public class Springboot02AmqpApplicationTests{
  
  @Autowired
  RabbitTemplate rabbitTemplate;
  @Autowired
  AmqpAdmin amqpAdmin;
  
  @Test
  public void createExchange(){
    //创建交换器
    amqpAdmin.declareExchange(new DirectExchange("amqpadmin.exchange"));
    //创建消息队列
    amqpAdmin.declareQueue(new Queue("amqpadmin.queue",true));
    //创建绑定规则
    amqpAdmin.declareBinding(new Binding("amqpadmin.queue",Binding.DestinationType.QUEUE,"amqpadmin.exchange","atguigu.haha",null));
    
  }
  
  //1.点播（点对点）
  @Test
  public void contextLoads(){
    //Message 需要自己构造一个，定义消息体内容和消息头
    //rabbitTemplate.send(exchage,routeKey,message);
    
    //object 默认当成消息体，只需要传入要发送的对象，自动序列化发送给rabbitmq
    //rabbitTemplate.convertAndSend(exchage,routeKey,object);
    Map<String,Object> map = new HashMap<>();
    map.put("msg","侯亚超");
    map.put("data",Arrays.asList("hello",123,true));
    //对象被默认序列化发送出去
    rabbitTemplate.convertAndSend("exchange.direct","atguigu.news",map);
  }
  @Test
  public void receive(){
    //接受数据
    Object o = rabbitTemplate.receiveAndConvert("atguigu.news");
    System.out.println(o);
  }
  
  //2.广播
  @Test
  public void sendMsg(){
    rabbitTemplate.convertAndSend("exchange.fanout","",new Book("西游记","吴承恩"));
  }
}
```

```java
//自定义json序列化机制，，，默认使用java序列化
@Configuration
public class MyAMQPConfig{
  
  //底层直接封装好了，先判断是否有自己的序列化机制，如果有，就用自己的。可看 RabbitAutoConfiguration
  @Bean
  public MessageConverter messageConverter(){
    return new Jackson2JsonMessageConverter();
  }
}
```

```java
@EnableRabbit //开启基于注解的Rabbit模式
@SpringBootApplication
public class Springboot02AmqpApplication{
  
  public static void main(String[] args){
    SpringApplication.run(Springboot02AmqpApplication.class,args);
  }
}
```

```java
@Service
public calss BookService {
  
  //指定要监听的消息队列
  @RabbitListener(queues = "atguigu.news")
  public void receive(Book book){       //这里监听的是跟 Book有关的消息，通过反序列化转化为Book对象
    System.out.println("收到消息："+ book);
  }
  
  //监听消息头和消息体
  @RabbitListener(queues = "atguigu")
  public void receive02(Message message){
    System.out.println(message.getBody());
    System.out.println(message.getMessageProperties());
  }
}
  
```



## 1.4.SpringBoot-与检索-Elasticsearch

#### 	一、检索简介：

​	我们的应用经常需要添加检索功能，开源的 [ElasticSearch](https://www.elastic.co/) 是目前全文搜索引擎的首选。他可以快速的存储、搜索和分析海量数据。Spring Boot通过整合Spring Data ElasticSearch为我们提供了非常便捷的检索功能支持；

​	Elasticsearch是一个分布式搜索服务，提供Restful API，底层基于Lucene，采用多shard（分片）的方式保证数据安全，并且提供自动resharding的功能，github等大型的站点也是采用了ElasticSearch作为其搜索服务，

安装：

​	查看镜像：docker search elasticsearch

​	通过加速下载：docker pull registry.docker-cn.com/library/elasticsearch

​	查看下载：docker images

​	启动： docker run -e ES_JAVA_OPTS="-Xms256m" -d -p 9200:9200 -p 9300:9300 --name 自己起个名字  ID

​	检查： docker ps

测试：在浏览器输入 192.168.47.130:9200



参考文档：https://www.elastic.co/guide/cn/elasticsearch/guide/current/index.html



SpringBoot默认支持两种技术和ES交互；

1. Jest（默认不生效）  ：需要导入jest 的工具包（io.searchbox.client.JestClient）

2. SpringData ElasticSearch

   1). Client 节点信息clusterNodes ；clusterName

   2). ElasticsearchTemlpate 操作es

   3). 编写一个ElasticsearchRepository的子接口来操作ES

   ```java
   //整合Jest     更多应用参考文档 https://github.com/searchbox-io/Jest/tree/master/jest
   public class SpringBoot03{
     @Autowired
     JestClient jestClient;
     
     @Test
     public void contextLoads(){
       //1.给ES中索引（保存）一个文档
       Article article = new Article();  //创建一个对象
       article.setId(1);
       article.setTitle("好消息");
       article.setAuthor("侯亚超");
       article.setContent("Hello World");
       
       //构建一个索引功能
       Index index = new Index.Builder(article).index("atguigu").type("news").build();
       
       //执行
       try{
         jestCline.execute(index);
       }catch (IOException e){
         e.printStackTrace();
       }
     }
     
     //测试索引
     @Test
     public void search(){
       //查询表达式
       String json = "{\n" +
               "    \"id\": \"myTemplateId\"," +
               "    \"params\": {\n" +
               "        \"query_string\" : \"search for this\"" +
               "    }\n" +
               "}";
       //构建搜索功能
      Search search = new Search.Builder(json).addIndex("atguigu").addType("news").build();
      
       //执行
       try{
         SearchResult result=jestCline.execute(search);
         System.out.println(result);
       }catch (IOException e){
         e.printStackTrace();
       }
     }
   }
   ```

   ​

整合SpringDataElasticsearch：检索

```application.properties
spring.data.elasticsearch.cluster-name=elasticsearch
spring.data.elasticsearch.cluster-nodes=192.168.47.130:9301
```

SpringData ElasticSearch【ES版本有可能不适合】

版本适配说明：https://github.com/spring-projects/spring-data-elasticsearch

如果版本不适配：

​	安装对应版本的ES

两种用法：https://github.com/spring-projects/spring-data-elaticsearch

1.编写一个ElasticsearchRepository

```java
@Document(indexName="atguigu",type="book")    //指定索引的名字和类型
public class Book{
  private Integer 
```

```java
public interface BookRepository extends ElasticsearchRepository<Book,Integer>{
  //自定义一个方法，不用实现，就可以调用使用
  public List<Book> findByBookNameLike(String bookName);  
}
```

```java
public class Springboot03{
  @Autowired
  BookRepository bookRepository;
  @Test
  public void test{
    //Book book = new Book();
    //book.setId(1);
    //book.setBookName("西游记");
    //bookRepository.index(book);
    for(Book book : bookRepository.findByBookNameLike("游")){
      System.out.println(book);
    }
  }
}
```



## 1.5 Springboot 与 异步任务 整合：

1. 先在主程序中开启异步注解功能

   @EnableAsync

   2. 在service层方法上添加@Async

      ```java
      @Service
      public class AsyncServic{
        //如果不添加该注解，则程序会先睡眠3秒钟才运行，添加了该注解，则不会睡眠直接执行。
        @Async
        public void hello(){
          Thread.sleep(3000);
          System.out.println("处理数据中。。。。");
        }
      }
      ```

      ​

## 1.6Springboot 与 定时任务 整合：

​	项目开发中经常需要执行一些定时任务，比如需要在每天凌晨时候，分析一次前一天的日志信息。Spring为我们提供了异步执行任务调度的方式，提供TaskExecutor 、TaskScheduler 接口。

| 字段   | 允许值                  | 允许的特殊字符          |
| ---- | -------------------- | ---------------- |
| 秒    | 0-59                 | , -  * /         |
| 分    | 0-59                 | , -  * /         |
| 小时   | 0-23                 | , -  * /         |
| 日期   | 1-31                 | , -  * ? / L W C |
| 月份   | 1-12                 | , -  * /         |
| 星期   | 0-7或SUN-SAT  0,7是SUN | , -  * ? / L C # |

| 特殊字符 | 代表含义              |
| ---- | ----------------- |
| ,    | 枚举                |
| -    | 区间                |
| *    | 任意                |
| /    | 步长                |
| ?    | 日/星期冲突匹配          |
| L    | 最后                |
| W    | 工作日               |
| C    | 和calendar联系后计算过的值 |
| #    | 星期，4#2，第2个星期四     |

1. 先在主程序中开启异步注解功能

   **@EableScheduling**

2. 在service层方法上添加  **@Scheduled**

   ​

```java
@Service
public class ScheduledService {
  /**
  	second(秒)，minute(分)，hour(时)，day of month (日)，month(月)，day of week（周几）。
  	0 * * * * MON-FRI
  	【0 0/5 14,18 * * ?】 每天14点整，和18点整，每隔5分钟执行一次
  	【0 15 10 ? * 1-6】每个月的周一至周六10:15分执行一次
  	【0 0 2 ？ * 6L】每个月的最后一个工作日凌晨2点执行一次
  	【0 0 2-4 ？ * 1#1】每个月的第一个周一凌晨2点到4点期间，每个整点都执行一次
  */
  @Scheduled(cron="0/4 * * * * MON-SAT")  //每4秒执行一次
  public void hello(){
    System.out.println("hello.......");
  }
}
```



## 1.7.SpringBoot与邮件任务

•邮件发送需要引入spring-boot-starter-mail

•SpringBoot 自动配置MailSenderAutoConfiguration

```application.properties
spring.mail.username=814428354@qq.com
spring.mail.password=在qq邮箱里面设置给出的密码
spring.mail.host=smtp.qq.com
spring.mail.properties.mail.smtp.ssl.enable=true
```

```java
public class Springboot04{
  @Autowired
  JavaMailSenderImpl mailSender;
  
  @Test
  public void cotextLods(){
    //1.创建一个复杂的消息邮件
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
    
    //设置邮箱
    helper.setSubject("通知-今晚开会");
    helper.setText("<b style='color:red'>今天7点开会</b>",true);
    helper.setTo("814428354@163.com");
    helper.setFrom("12234232@qq.com");
    
    //上传文件
    helper.addAttachment("1.jpg",new File("路径"));
  }
}
```





## 1.8.SpringBoot与安全-Spring Security

Spring Security是针对Spring项目的安全框架，也是Spring Boot底层安全模块默认的技术选型。他可以实现强大的web安全控制。对于安全控制，我们仅需引入spring-boot-starter-security模块，进行少量的配置，即可实现强大的安全管理。
几个类：

WebSecurityConfigurerAdapter：自定义Security策略

AuthenticationManagerBuilder：自定义认证策略

@EnableWebSecurity：开启WebSecurity模式

•应用程序的两个主要区域是“认证”和“授权”（或者访问控制）。这两个主要区域是Spring Security 的两个目标。

•“认证”（Authentication），是建立一个他声明的主体的过程（一个“主体”一般是指用户，设备或一些可以在你的应用程序中执行动作的其他系统）。

•“授权”（Authorization）指确定一个主体是否允许在你的应用程序执行一个动作的过程。为了抵达需要授权的店，主体的身份已经有认证过程建立。



```java
/**
	1.引入SpringSecurity;
	2.编写SpringSecurity的配置类；
	 @EnableWebSecurity      extends WebSecurityConfigurerAdapter
	3.控制请求的访问权限：
*/
@SpringBootApplication
主配置类
```

```java
@EnableWebSecurity
public class MySecurityConfig extends WebSecurityConfugureAdapter{
  
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    //定制请求的授权规则
    http.authorizeRequests().antMathchers("/").permitAll()
      			.antMatchers("/level1/**").hasRole("VIP1")
      			.antMatchers("/level2/**").hasRole("VIP2")
      			.antMatchers("/level3/**").hasRole("VIP3");
    //开启自动配置的登录功能，效果，如果没有登录，没有权限就会来到登录页面
    http.formLogin().usernameParameter("user").passwordParameter("pwd").logonPage("/userlogin");
    //1. /Login来到登录页      都封装好了
    //2. 重定向到/Login?error表示登录失败
    //3. 更多详细规定
    //4.默认POST形式的 /login代表处理登录
    //5. 一旦定制loginPage: 那么loginPage的POST请求就是登录
    
    //开启配置的注销功能
    http.logout().logoutSuccessUrl("/");  //注销成功来到首页
    //1.访问/logout 表示用户注销，清空session
    //2.注销成功会返回 /login?logout 页面
    
    
    //开启配置的记住我功能
    http.rememberMe().rememberMeParamter("remember");   //定制记住我
    //登录成功以后，将cookie发给浏览器保存，以后访问页面带上这个cookie，只要通过检查就可以免登录
    //点击注销会删除cookie
  }
  
  //定义认证规则
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
      			.withUser("houyachao").password("123456").roles("VIP1","VIP2")
      .and
      .withUser("lisi").password("123456").roles("VIP2","VIP3")
      .and
      .withUser("wangwu").password("123456").roles("VIP1","VIP3");
  }
}
```



## 1.9WEB&安全

1.登陆/注销

​	–HttpSecurity配置登陆、注销功能

2.Thymeleaf提供的SpringSecurity标签支持

​	–需要引入thymeleaf-extras-springsecurity4

​	–sec:authentication=“name”获得当前用户的用户名

​	–sec:authorize=“hasRole(‘ADMIN’)”当前用户必须拥有ADMIN权限时才会显示标签内容

3.rememberme

​	–表单添加remember-me的checkbox

​	–配置启用remember-me功能

4.CSRF（Cross-site request forgery）跨站请求伪造

HttpSecurity启用csrf功能，会为表单添加csrf的值，提交携带来预防CSRF



参考文档：https://docs.spring.io/spring-security/site/docs/current/guides/html5/helloworld-boot.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
	  xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity5">  <!--导入空间 -->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
</head>
<body>
<h1 align="center">欢迎光临武林秘籍管理系统</h1>
<div sec:authorize="!isAuthenticated()">    <!--如果用户没有登录-->
	<h2 align="center">游客您好，如果想查看武林秘籍 <a th:href="@{/login}">请登录</a></h2>
</div>
<div sec:authorize="isAuthenticate()">    <!-- 如果用户登录了，获取用户的名字和角色有哪些 -->
	<h2><spen sec:authentication="name"></spen>您好，您的角色有：
		<spen sec:authentication="principal.authorities"></spen></h2>
	<form th:action="@{/logout}" method="post">
		<input type="submit" value="注销" />
	</form>
</div>
<hr />

<div sec:authorize="hasRole('VIP1')">   <!-- 如果具有VIP1 角色，则显示 -->
	<h3>普通武功秘籍</h3>
	<ul>
		<li><a th:href="@{/level1/1}">罗汉拳</a></li>
		<li><a th:href="@{/level1/2}">武当长拳</a></li>
		<li><a th:href="@{/level1/3}">全真剑法</a></li>
	</ul>
</div>
```



记住我功能的实现请看上面代码。

## 2.0SpringBoot与Dubbo, Zookeeper整合

首先创建两个工程，来模拟：

provider-ticker: 提供者

consumer-user：消费者

1. 将服务提供者注册到注册中心

   1.引入dubbo和zkclient 相关依赖

   2.配置dubbo的扫描和注册中心地址

   3。使用@Service发布服务：  这里使用@Service 是aliyun提供的。、、

   ​

```application.properties
dubbo.application.name=起个名字
dubbo.registry.address=zookeeper://192.168.47.130:2181
dubbo.scan.base-packages=com.atguigu.ticket.service
```

```java
//provider-ticker: 提供者
@Component
@Service //将服务发布出去
public class TicketServiceImpl implements TiketService{
  @Override
  public String getTiclet(){return "<<厉害了，我的国>>";}
}
```

consumer-user：消费者

将服务提供者注册到注册中心

1.引入dubbo和zkclient 相关依赖

2.配置dubbo的扫描和注册中心地址

```application.properties
dubbo.application.name=起个名字
dubbo.registry.address=zookeeper://192.168.47.130:2181
```

```java
//1.需要将提供者发布的服务service层目录赋值到该工程中
//2.然后消费者在service 实现层直接调用抽象方法并且需要加@Reference注解就可以了。它会根据调用的方法去zoopeeper中去查找服务
@Service
public class UserService{
  @Reference   
  TicketService ticketService;
  
  public void hello(){
    String ticket=ticketService.getTicket();
    System.out.println("买到票了："+ticket); 
  }
}
```



## 2.1.SpringBoot-分布式-SpringCloud整合

Spring Cloud

Spring Cloud是一个分布式的整体解决方案。Spring Cloud 为开发者提供了在分布式系统（配置管理，服务发现，熔断，路由，微代理，控制总线，一次性token，全局琐，leader选举，分布式session，集群状态）中快速构建的工具，使用Spring Cloud的开发者可以快速的启动服务或构建应用、同时能够快速和云平台资源进行对接。

•SpringCloud分布式开发五大常用组件

•服务发现——Netflix Eureka

•客服端负载均衡——Netflix Ribbon

•断路器——Netflix Hystrix

•服务网关——Netflix Zuul

•分布式配置——Spring Cloud Config



### 2.2.分布式-SpringColud-Eureka注册中心

eureka-server:     是一个注册中心   。。。   创建工程的时候加入Eureka Server.

```application.yml
server: 
	port: 8761
ereka:
	instance:
		hostname: eureka-serve     // eureka实例的主机名
	client:
		register-with-eureka: false  //不把自己注册到eureka 上
		fetch-registry: false       //不从eureka 上来获取服务的注册信息
		service-url:
			defaultZone: http://localhost:8761/eureka/
```

```java
/**
	注册中心
	1、配置Eureka信息
	2.@EnableEurekaServer
*/
```

 provider-ticket ：服务提供者     创建工程的时候添加Eureka Discovery

```java
@Service
public class ProviderService{
  
  public String getTicket(){
    System.out.println("0000");
    return "<<Provider  厉害了！>>"
  }
}

@Controller
public class ProviderController{
  @Autowired
  private ProviderService providerService;
  
  @RequestMapping("/")
  @RespontBody
  public String getTick(){
    String string = providerService.getgetTicket();
    System.out.println(string);
  }
}
```

```application.yml
server: 
	port: 8001
spring: 
	application:
		name: provider-ticket
		
eureka:
	instance:
		prefer-ip-address: true   //注册服务的时候使用服务的ip地址
	client:
		service-url:
			defaultZone: http//localhost:8761/eureka/
	
```



 consumer-user ：服务消费者     创建工程的时候添加Eureka Discovery

```application.yml
spring:
	application:
		name: consumer-user
server:
	port: 8200
	
	
eureka:
	instance:
		prefer-ip-address: true   //注册服务的时候使用服务的ip地址
	client:
		service-url:
			defaultZone: http//localhost:8761/eureka/
```

```java
@EnableDiscoveryClient    //开启发现服务功能
@SpringBootApplication
public class ConsumerUserApplication{
  
  public static void main(String[] args){
    SpringApplication.run(ConsumerUserApplication.class,args);
  }
  
  @LoadBalanced //使用负载均衡机制
  @Bean     //远程调用
  public RestTemplate restTemplate(){
    return new RestTemplate();
  }
}

@RestController
public class UserController{
  @Autowired
  RestTemplate restTemplate;
  @GetMapping("/buy")
  public String buyTicket(String name){       //这里的地址是注册管理里面的名字
    String s=restTemplate.getForObject("http://PROVIDER-TICKET/ticket",String.class);
    return  name+"购买了"+s;
  }
}
```



## 2.3.Springboot-热部署

Spring Boot Devtools（推荐）           

–引入依赖

```java
<dependency>  
       <groupId>org.springframework.boot</groupId>  
       <artifactId>spring-boot-devtools</artifactId>   
</dependency> 

```

–IDEA使用ctrl+F9

–或做一些小调整

 	 Intellij IEDA和Eclipse不同，Eclipse设置了自动编译之后，修改类它会自动编译，而IDEA在非RUN或DEBUG情况下才会自动编译（前提是你已经设置了Auto-Compile）。

​	•设置自动编译（settings-compiler-make projectautomatically）

​	•ctrl+shift+alt+/（maintenance）

​	•勾选compiler.automake.allow.when.app.running

只要导入了依赖热部署就配置好了。使用ctrl+F9 ，等于又编译了一次。

## 2.4.Springboot与监控管理

​	通过引入spring-boot-starter-actuator，可以使用Spring Boot为我们提供的准生产环境下的应用监控和管理功能。我们可以通过HTTP，JMX，SSH协议来进行操作，自动得到审计、健康及指标信息等

•步骤：

​	–引入spring-boot-starter-actuator

​	–通过http方式访问监控端点

​	–可进行shutdown（POST 提交，此端点默认关闭）

•监控和管理端点 

| 端点名         | 描述                    |
| ----------- | --------------------- |
| autoconfig  | 所有自动配置信息              |
| auditevents | 审计事件                  |
| beans       | 所有Bean的信息             |
| configprops | 所有配置属性                |
| dump        | 线程状态信息                |
| env         | 当前环境信息                |
| health      | 应用健康状况                |
| info        | 当前应用信息                |
| metrics     | 应用的各项指标               |
| mappings    | 应用@RequestMapping映射路径 |
| shutdown    | 关闭当前应用（默认关闭）          |
| trace       | 追踪信息（最新的http请求）       |

### 定制端点信息：

–定制端点一般通过endpoints+端点名+属性名来设置。

–修改端点id（endpoints.beans.id=mybeans）

–开启远程应用关闭功能（endpoints.shutdown.enabled=true）

–关闭端点（endpoints.beans.enabled=false）

–开启所需端点

​	•endpoints.enabled=false

​	•endpoints.beans.enabled=true

–定制端点访问根路径

​	•management.context-path=/manage

–关闭http端点

​	•management.port=-1

























































