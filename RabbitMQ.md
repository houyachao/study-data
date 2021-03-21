# 一、RabbitMQ

## 1、RabbitMQ 介绍

RabbitMQ 是一个开源的消息代理和队列服务器，用来通过普通协议在完全不同的应用之间共享数据，是使用Erlang 语言开发的开源消息队列系统，基于AMQP协议来实现。AMQP的主要特点是面向消息、队列、路由（包括点对点和发布\订阅）、可靠性、安全。AMQP协议更多用在企业系统内，对数据一致性、稳定性和可靠性要求很高的场景，对性能和吞吐量的要求还在其次。

## 2、AMQP定义 

是具有现代特征的二进制协议。是一个提供统一消息服务的应用层标准高级消息队列协议，是应用层协议的一个开放标准，为面向消息的中间件设计。

## 3、AMQP核心概念

①Server：又称Broker，接收客户端的连接，实现AMQP实体服务。虚拟主机

②Connection：连接，应用程序与Broker的网络连接。

③Channel：网络信道，几乎所有的操作都在Channel中进行，Chanel 是进行消息读写的通道。客户端可建立多个Chanel，每个Channel代表一个会话任务。

④Message：消息，服务器和应用程序之间传送的数据，由Properties和Body组成。Properties可以对消息进行修饰，比如消息的优先级，延迟等高级特性；Body则就是消息体内容。
⑤Virtual host：虚拟地址，用于进行逻辑隔离，最上层的消息路由。一个Virtual Host 里面可以有若干个Exchange和Queue，同一个Virtual Host 里面不能有相同名称的Exchange 或Queue。

⑥Exchange：交换机，接收消息，根据路由键转发消息到绑定的队列。

⑦Binding：Exchange和Queue之间的虚拟链接，binding中可以包含routing key。

⑧Routing key： 一个路由规则，虚拟机可用它确定如何路由一个特定消息。

⑨Queue：也称Message Queue，消息队列，保存消息并将他们转发给消费者。

![AMQP协议模型](C:\Users\Administrator\Desktop\笔记\RabbitMQ\AMQP协议模型.png)

## 3 RabbitMQ安装与使用

### 3.1 准备

```java
yum install
build-essential openssl openssl-devel unixODBC unixODBC-devel
make gcc gcc-c++ kernel-devel m4 ncurses-devel tk tc xz
```

### 3.2 下载

```java
wget www.rabbitmq.com/releases/erlang/erlang-18.3-1.e17.centos.x86_64.rpm
wget http://repo.iotti.biz/CentOS/7/x87_64/socat-1.7.3.2-5.e17.lux.x86_64.rpm
wget www.rabbitmq.com/releases/rabbitmq-server/v3.6.5/rabbitmq-server-3.6.5-1.noarch.rpm
```

### 3.3 配置 vim /etc/hosts  以及 /etc/hostname

### 3.4 配置文件

```java
vim /usr/lib/rabbitmq/lib/rabbitmq_server-3.6.5/ebin/rabbit.app
比如修改密码，配置等等，例如：loopback_users 中的<<"guest">>，只保留guest
服务启动和停止：
启动  rabbitmq-server start & 
停止  rabbitmqctl  app_stop
```

### 3.5 管理插件

```java
rabbitmq-plugins enable rabbitmq_management
```

### 3.6 访问地址

```java
http://服务器地址:15672/
```

![RabbitMQ消息是如何流转的](C:\Users\Administrator\Desktop\笔记\RabbitMQ\RabbitMQ消息是如何流转的.png)

## 4、命令行操作

①rabbitmqctl   stop_app： 关闭应用

②rabbitmqctl   start_app： 启动应用

③rabbitmqctl   status：   节点状态

④rabbitmqctl    add_user   username  password： 添加用户

⑤rabbitmqctl     list_users：   列出所有用户

⑥rabbitmqctl 	delete_user  username：  删除用户

⑦rabbitmqctl    	clear_permission -p vhostpath username: 	清除用户权限

⑧rabbitmqctl	list_user_permissions username： 列出用户权限

⑨rabbitmqctl  	change_password  username  newpassword： 修改密码

⑩rabbitmqctl	set_perissions  -p  vhostpath  username  ".*"    ".*"   ".*" :   设置用户权限

​    rabbitmqctl 	add_vhost  vhostpath:  创建虚拟主机

​    rabbitmqctl   	list_vhosts:   列出所有虚拟主机

​    rabbitmqctl	list_permissions  -p   vhostpath:    列出虚拟主机和上所有权限

​    rabbitmqctl	delete_vhost  vhostpath:	删除虚拟主机

​    rabbitmqctl 	list_queues:   查看所有队列信息

​    rabbitmqctl	-p  vhostpath  purge_queue  blue:  清除队列里的消息

​    rabbitmqctl	reset： 移除所有数据，要在rabbitmqctl  stop_app之后使用

​    rabbitmqctl	join_cluster  <clusternode>   [--ram]:   组成集群命令

   rabbitmqctl	cluster_status:  查看集群状态

​    rabbitmqctl  change_cluster_node_type  disc | ram   修改集群节点的存储形式

​    rabbitmqctl  forget_cluster_node   [--offline]  忘记节点（摘除节点）

## 5、Java代码

具体可以看代码：

```java
发送消息：
channel.basicPublish("",queueName,null,msg)
	第一个参数：exchange
	第二个参数：routingKey

创建一个队列：
channel.queueDeclare(queueName,durable,exclusive,autoDelete,arguments);
	第一个参数：key  可以是个字符串。
	第二个参数：是否持久化，true 持久化，false 不持久化， 服务器重启，队列也不会消失
	第三个参数：独占，只有我这一个channel去监听，其他的不能监听。true，false。  保证顺序消费。
	第四个参数：如果脱离了绑定关系，会自动删除，  true，false
	第五个参数：扩展参数。

创建消费者
QueueingConsumer q = new QueueingConsumer(chnnel);
设置channel  消费者队列：
channel.basicConsume(queueName,autoAck,callback);
	第一个参数： 是你消费的队列是哪一个
	第二个参数： 是否自动签收   true，false
	第三个参数： 消费者对象

获取消息
Delivery delivery = q.nextDelivery();
```

![exchange交换机](C:\Users\Administrator\Desktop\笔记\RabbitMQ\exchange交换机.png)



## 6、Exchange 交换机

### ①交换机属性

​	Name：  交换机名称

​	Type：	交换机类型  direct、topic、fanout、headers

​	Durability： 是否需要持久化，true为持久化

​	Auto DELETE：  当最后一个绑定到Exchange 上的队列删除后，自动删除该Exchange

​	Internal：  当前Exchange 是否用于RabbitMQ 内部使用，默认为False

​	Arguments:	扩展参数，用于扩展AMQP协议自定化使用

### ②Direct Exchange

​	所有发送到Direct Exchange 的消息被转发到RouteKEY 中指定的Queue

​	注意：Direct 模式可以使用RabbitMQ 自带的Exchange：default Exchange，所以不需要将Exchange 进行任何绑定（binding）操作，消息传递时，RouteKey 必须完全匹配才会被队列接收，否则该消息会被抛弃。

```java
生成者：
//1 创建ConnectionFactory
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		//2 创建Connection
		Connection connection = connectionFactory.newConnection();
		//3 创建Channel
		Channel channel = connection.createChannel();  
		//4 声明
		String exchangeName = "test_direct_exchange";
		String routingKey = "test.direct";
		//5 发送
		
		String msg = "Hello World RabbitMQ 4  Direct Exchange Message 111 ... ";
		channel.basicPublish(exchangeName, routingKey , null , msg.getBytes()); 

消费者：
 ConnectionFactory connectionFactory = new ConnectionFactory() ;  
        
        connectionFactory.setHost("192.168.11.76");
        connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(3000);
        Connection connection = connectionFactory.newConnection();
        
        Channel channel = connection.createChannel();  
		//4 声明
		String exchangeName = "test_direct_exchange";
		String exchangeType = "direct";
		String queueName = "test_direct_queue";
		String routingKey = "test.direct";
		
		//表示声明了一个交换机
		channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
		//表示声明了一个队列
		channel.queueDeclare(queueName, false, false, false, null);
		//建立一个绑定关系:
		channel.queueBind(queueName, exchangeName, routingKey);
		
        //durable 是否持久化消息
        QueueingConsumer consumer = new QueueingConsumer(channel);
        //参数：队列名称、是否自动ACK、Consumer
        channel.basicConsume(queueName, true, consumer);  
        //循环获取消息  
        while(true){  
            //获取消息，如果没有消息，这一步将会一直阻塞  
            Delivery delivery = consumer.nextDelivery();  
            String msg = new String(delivery.getBody());    
            System.out.println("收到消息：" + msg);  
        } 
```

### ③Topic Exchange

所有发送到Topic Exchange 的消息被转发到所有关心RouteKEY 中指定Topic 的Queue上

Exchange 将RouteKEY 和某Topic 进行模糊匹配，此时队列需要绑定一个Topic

注意：  可以使用通配符进行模糊匹配

​	"#" 匹配一个或多个词

​	“*”  匹配不多不少一个词

![topic exchange](C:\Users\Administrator\Desktop\笔记\RabbitMQ\topic exchange.png)

```java
生成者：
//1 创建ConnectionFactory
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		//2 创建Connection
		Connection connection = connectionFactory.newConnection();
		//3 创建Channel
		Channel channel = connection.createChannel();  
		//4 声明
		String exchangeName = "test_topic_exchange";
		String routingKey1 = "user.save";
		String routingKey2 = "user.update";
		String routingKey3 = "user.delete.abc";
		//5 发送
		
		String msg = "Hello World RabbitMQ 4 Topic Exchange Message ...";
		channel.basicPublish(exchangeName, routingKey1 , null , msg.getBytes()); 
		channel.basicPublish(exchangeName, routingKey2 , null , msg.getBytes()); 	
		channel.basicPublish(exchangeName, routingKey3 , null , msg.getBytes()); 
		channel.close();  
        connection.close();

消费者：
 ConnectionFactory connectionFactory = new ConnectionFactory() ;  
        
        connectionFactory.setHost("192.168.11.76");
        connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(3000);
        Connection connection = connectionFactory.newConnection();
        
        Channel channel = connection.createChannel();  
		//4 声明
		String exchangeName = "test_topic_exchange";
		String exchangeType = "topic";
		String queueName = "test_topic_queue";
		//String routingKey = "user.*";
		String routingKey = "user.*";
		// 1 声明交换机 
		channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
		// 2 声明队列
		channel.queueDeclare(queueName, false, false, false, null);
		// 3 建立交换机和队列的绑定关系:
		channel.queueBind(queueName, exchangeName, routingKey);
		
        //durable 是否持久化消息
        QueueingConsumer consumer = new QueueingConsumer(channel);
        //参数：队列名称、是否自动ACK、Consumer
        channel.basicConsume(queueName, true, consumer);  
        //循环获取消息  
        while(true){  
            //获取消息，如果没有消息，这一步将会一直阻塞  
            Delivery delivery = consumer.nextDelivery();  
            String msg = new String(delivery.getBody());    
            System.out.println("收到消息：" + msg);  
        } 
```

### ④Fanout Exchange

​	不处理路由键，只需要简单的将队列绑定到交换机上。

​	发送到交换机的消息都会被转发到与交换机绑定的所有队列上。

​	Fanout 交换机转发消息是最快的。

```java
生成者：
//1 创建ConnectionFactory
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		//2 创建Connection
		Connection connection = connectionFactory.newConnection();
		//3 创建Channel
		Channel channel = connection.createChannel();  
		//4 声明
		String exchangeName = "test_fanout_exchange";
		//5 发送
		for(int i = 0; i < 10; i ++) {
			String msg = "Hello World RabbitMQ 4 FANOUT Exchange Message ...";
			channel.basicPublish(exchangeName, "", null , msg.getBytes()); 			
		}
		channel.close();  
        connection.close();  

消费者：
 ConnectionFactory connectionFactory = new ConnectionFactory() ;  
        
        connectionFactory.setHost("192.168.11.76");
        connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(3000);
        Connection connection = connectionFactory.newConnection();
        
        Channel channel = connection.createChannel();  
		//4 声明
		String exchangeName = "test_fanout_exchange";
		String exchangeType = "fanout";
		String queueName = "test_fanout_queue";
		String routingKey = "";	//不设置路由键
		channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
		channel.queueDeclare(queueName, false, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);
		
        //durable 是否持久化消息
        QueueingConsumer consumer = new QueueingConsumer(channel);
        //参数：队列名称、是否自动ACK、Consumer
        channel.basicConsume(queueName, true, consumer); 
        //循环获取消息  
        while(true){  
            //获取消息，如果没有消息，这一步将会一直阻塞  
            Delivery delivery = consumer.nextDelivery();  
            String msg = new String(delivery.getBody());    
            System.out.println("收到消息：" + msg);  
        } 
```



## 7、消息如何保障100%的投递成功？

①什么是生产端的可靠性投递？

​	保障消息的成功发出。

​	保障MQ节点的成功接收。

​	发送端收到MQ节点（Broker）确认应答。

​	完善的消息进行补偿机制。

解决方案：

### 	1、消息落库，对消息状态进行打标。  

​	你在发送消息的时候，要将你的消息持久化到你的数据库中，消息设置一个状态，比如我刚发送一个消息，状态为发送中。当发送到服务端，服务端给我一个应答，然后将消息做一个变更。  对于没有响应的状态，我们做一个轮询操作，轮询没有一直处于不OK的状态，然后我们做一个重新发送，做一些最大努力尝试的次数，直到消息状态变为发送成功。

![可靠性投递解决方案一](C:\Users\Administrator\Desktop\笔记\RabbitMQ\可靠性投递解决方案一.png)

​	 1、发送消息之前，先将业务数据（比如，订单）先保存到BIZ DB 数据库表中，然后将消息和订单表进行关联 保存到MSG DB 表中，先对‘业务数据进行落库，然后再发送消息给MQ Broker。

​	2、发送一个confirm 消息，如果MQ Broker 收到这个消息，会给我们返回 一个确认。

​	3、生产端需要写一个监听，监听MQBroker 返回的结果，再去操作一下 我们的日志记录表，将该分订单的状态做一个更新。

​	4、如果confirm Listener 一直没有收到响应回答，则我们需要写一个定时任务（去做一个补偿），去查询日子记录表长时间没有得到响应结果的数据，将消息进行重发。直到confirm listener 收到该消息的响应为止。

​	5、如果重投的次数超过三次，则将该消息记录设为 投递失败状态。 可以人为去解决。

发送消息先到数据库，分为两个库，一个库保存消息状态，一个库保存发送业务比如订单。如果第一步（step1）没有出错，保存成功，则会进行第二步（Step2）发送消息到MQ Broker端，收到消息会给一个确认Step3，第三步会监听消息确认成功没。如果确认成功，则直接执行第四步（Step4）将消息状态数据库更改状态，如果第三部Step3出错，MSG消息状态一直不会改变，导致消息永远不会发送成功。可以给MSG设置一个最大容忍超时时间，如果超过最大容忍时间，分布式定时任务会去轮询查找这个状态，回去执行第六步Step6，去重新发送消息，如果重新发送短信次数超过预定的次数执行第七步Step7，不能让他一直发送。  最后可以人工做一个交易补偿去处理这个消息。

### 2、消息的延迟投递，做二次确认，回调检查。

![可靠性投递解决方案二](C:\Users\Administrator\Desktop\笔记\RabbitMQ\可靠性投递解决方案二.png)

流程说明：

Upstream Service:上游服务

DownStream Service:下游服务

Callback service:回调服务

执行流程：

Step1：

​    业务信息入库(BIZ DB)之后，向MQ发送一个消息：first Send。这个消息时通知下游服务进行下游业务处理的；

Setp2:

​    First Send消息发送后，同时发送一个Second Send Delay Check消息。这个消息是用来延时check验证的。如延迟5分钟(具体延迟多少时间，根据实际业务)；

Setp3:

​    下游服务有一个用于接收上有服务发送消息的消费者:Listener Consume。当消费者接收到生产者发送的消息后进行业务处理。处理完成后，执行第四步操作；

Setp4:

​    当消费者消费后，自己业务处理完成之后，会发送一个确认的消息(Send Confirm)给队列。

Step5:

​    下游服务生产一个确认消息后，在回调服务(Callback Serivce)中会有一个消费者(Listener Confirm)消费这个确认消息后，将消息信息入库或更新消息状态。

五分钟后，延迟校验消息开始生效，进行校验，进行第六步

Setp6:

​    当延迟校验在Callback Service服务中进行查询消息库，发现没有或者是状态没有进行预期的更新后，延迟校验会会通过主键或者其他唯一标识通过RPC方式发起一个RPC ReSend Command请求到上游服务，询问此条消息下游未正常处理。请求上游服务再次发送消息操作。

此种操作相对于第一种操作的优点：减少了一次同步操作数据库的操作。这样在高并发的情况下就能提高效率。。。

先首次发送一次消息到上游服务端（UpStream），上游服务端对业务落库，然后落库成功后，执行第一步Step1 到MQ，而上游服务端（UpStream）有个延迟发送消息的任务，假如是每隔5分钟发送一次消息到MQ，这样做是为了第四步Step4出错和第一步消息没有发出去做的。而DownStream会监听MQ，消费消息后，会执行第四步Step4 发送一个确认消息的命令，而Callbake服务端会监听MQ消息确认Confirm，如果监听到了，就会将消息落库。。 如果第二步Step2发送的第二次消息到MQ ，则Callback会去检查你发送的第二次消息，然后根据你消息去数据库查看，如果确实没有就会异步发送RPC 到你的UpStream服务端，然后依次执行。

​	方案二是企业最常用的，因为在高并发情况下，保证了性能，减少了对数据库的IO操作。

## 8、在海量订单产生的业务高峰期，如何避免消息的重复消费？

①消费端实现幂等性，就意味着，我们的消息永远不会消费多次，即使我们收到了多条一样的消息。

​	主流幂等性操作：

1、唯一ID+ 指纹码  机制，利用数据库主键去重。

2、利用Redis的原子性去实现。

## 9、Confirm 确认消息

### ①Confirm 消息确认机制

​	消息的确认，是指生产者投递消息后，如果Broker收到消息，则会给我们生产者一个应答。

​	生产者进行接收应答，用来确定这条消息是否正常的发送到Broker，这种方式也是消息的可靠性投递的核心保障。

![确认机制流程图](C:\Users\Administrator\Desktop\笔记\RabbitMQ\确认机制流程图.png)

### ②如何实现Confirm确认消息？

第一步： 在channel 上开启确认模式： channel.confirmSelect()

第二步： 在channel 上添加监听：  addConfirmListener，监听成功和失败的返回结果，根据具体的结果对消息进行重新发送，或记录日志等后续处理！

### ③代码实现

```java
生成端：
//1 创建ConnectionFactory
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		//2 获取C	onnection
		Connection connection = connectionFactory.newConnection();
		
		//3 通过Connection创建一个新的Channel
		Channel channel = connection.createChannel();
		
		
		//4 指定我们的消息投递模式: 消息的确认模式 
		channel.confirmSelect();
		
		String exchangeName = "test_confirm_exchange";
		String routingKey = "confirm.save";
		
		//5 发送一条消息
		String msg = "Hello RabbitMQ Send confirm message!";
		channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
		
		//6 添加一个确认监听
		channel.addConfirmListener(new ConfirmListener() {
			@Override
			public void handleNack(long deliveryTag, boolean multiple) throws IOException {
				System.err.println("-------no ack!   失败-----------");
			}
			
			@Override
			public void handleAck(long deliveryTag, boolean multiple) throws IOException {
				System.err.println("-------ack!   成功-----------");
			}
		});


消费端：
//1 创建ConnectionFactory
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		//2 获取C	onnection
		Connection connection = connectionFactory.newConnection();
		
		//3 通过Connection创建一个新的Channel
		Channel channel = connection.createChannel();
		
		String exchangeName = "test_confirm_exchange";
		String routingKey = "confirm.#";
		String queueName = "test_confirm_queue";
		
		//4 声明交换机和队列 然后进行绑定设置, 最后制定路由Key
		channel.exchangeDeclare(exchangeName, "topic", true);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);
		
		//5 创建消费者 
		QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
		channel.basicConsume(queueName, true, queueingConsumer);
		
		while(true){
			Delivery delivery = queueingConsumer.nextDelivery();
			String msg = new String(delivery.getBody());
			
			System.err.println("消费端: " + msg);
		}
```

## 10、Return 消息机制

Return Listener 用于处理一些不可路由的消息！

我们的消息生产者，通过指定一个Exchange 和 RoutingKEY，把消息送达到某一个队列中去，然后我们的消费者监听队列，进行消费处理操作！

但是在某些情况下，如果我们在发送消息的时候，当前的exchange 不存在或者指定的路由key 路由不到，这个时候如果我们需要监听这种不可达的消息，就要使用return Listener！

在基础API 中有一个关键的配置项：

Mandatory： 如果为true，则监听器会接收到路由不可达的消息，然后进行后续处理，如果为false，那么broker端自动删除该消息！

![return 消息机制流程](C:\Users\Administrator\Desktop\笔记\RabbitMQ\return 消息机制流程.png)

```java
生成端：
ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		String exchange = "test_return_exchange";
		String routingKey = "return.save";
		String routingKeyError = "abc.save";
		
		String msg = "Hello RabbitMQ Return Message";
		
		
		channel.addReturnListener(new ReturnListener() {
			@Override
			public void handleReturn(int replyCode, String replyText, String exchange,
					String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
				
				System.err.println("---------handle  return----------");
				System.err.println("replyCode: " + replyCode);
				System.err.println("replyText: " + replyText);
				System.err.println("exchange: " + exchange);
				System.err.println("routingKey: " + routingKey);
				System.err.println("properties: " + properties);
				System.err.println("body: " + new String(body));
			}
		});
		
		
		channel.basicPublish(exchange, routingKeyError, true, null, msg.getBytes());
		
		//channel.basicPublish(exchange, routingKeyError, true, null, msg.getBytes());

消费端：
ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		String exchangeName = "test_return_exchange";
		String routingKey = "return.#";
		String queueName = "test_return_queue";
		
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);
		
		QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
		
		channel.basicConsume(queueName, true, queueingConsumer);
		
		while(true){
			
			Delivery delivery = queueingConsumer.nextDelivery();
			String msg = new String(delivery.getBody());
			System.err.println("消费者: " + msg);
		}
```

## 11、消费端自定义监听

我们一般就是在代码中编写while 循环，进行consumer.nextDelivery 方法进行获取下一条消息，然后进行消费处理！但是我们使用自定义的COnsumer更加的方便，解耦性更加的强，也是在实际工作中最常用的使用方式。

可以实现Consumer接口，，也可以继承 DefaultConsumer类。

```java
生成端：
ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		String exchange = "test_consumer_exchange";
		String routingKey = "consumer.save";
		
		String msg = "Hello RabbitMQ Consumer Message";
		
		for(int i =0; i<5; i ++){
			channel.basicPublish(exchange, routingKey, true, null, msg.getBytes());
		}

消费端：
ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		
		String exchangeName = "test_consumer_exchange";
		String routingKey = "consumer.#";
		String queueName = "test_consumer_queue";
		
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);
		
		channel.basicConsume(queueName, true, new MyConsumer(channel));
		
自定义接收：
public class MyConsumer extends DefaultConsumer {

	public MyConsumer(Channel channel) {
		super(channel);
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
		System.err.println("-----------consume message----------");
		System.err.println("consumerTag: " + consumerTag);
		System.err.println("envelope: " + envelope);
		System.err.println("properties: " + properties);
		System.err.println("body: " + new String(body));
	}


}
```

## 12、消费端限流

### ①什么是消费端的限流

假设一个场景，首先，我们RabbitMQ 服务器有成千上万条未处理的消息，我们随便打开一个消费者客户端，会出现下面情况：

巨量的消息瞬间全部推送过来，但是我们单个客户端无法同时处理这么多数据！

RabbitMQ 提供了一种qos（服务质量保证）功能，即在非自动确认消息的前提下，如果一定数目的消息（通过基于consumer 或者 channel 设置的QoS 的值）未被确认钱，不进行消费新的消息。

```java
void BasicQos(uint prefetchSize, ushort prefetchCOunt, bool global);

prefetchSize: 0

prefetchCount: 会告诉RabbitMQ 不用同时给一个消费者推送多余N 个消息，即一旦有N个消息还没有ack，则该consumer 将block掉，直到有消息ack。

global：true\false 是否将上面设置应用于channel。简单点说，就是上面限制是channel 级别的还是consumer级别。

prefetchSize 和 global 这两项，rabbitmq 没有实现，暂且不研究prefetch_count 在no_ask = false 的情况下生效，即在自动应答的情况下这两个值是不生效的。

```

```java
生成端：
ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		String exchange = "test_qos_exchange";
		String routingKey = "qos.save";
		
		String msg = "Hello RabbitMQ QOS Message";
		
		for(int i =0; i<5; i ++){
			channel.basicPublish(exchange, routingKey, true, null, msg.getBytes());
		}

消费端：
ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		
		String exchangeName = "test_qos_exchange";
		String queueName = "test_qos_queue";
		String routingKey = "qos.#";
		
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);
		
		//1 限流方式  第一件事就是 autoAck设置为 false
		
		channel.basicQos(0, 1, false);
		
		channel.basicConsume(queueName, false, new MyConsumer(channel));
		
自定义消费端监听：
public class MyConsumer extends DefaultConsumer {

	private Channel channel ;
	
	public MyConsumer(Channel channel) {
		super(channel);
		this.channel = channel;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
		System.err.println("-----------consume message----------");
		System.err.println("consumerTag: " + consumerTag);
		System.err.println("envelope: " + envelope);
		System.err.println("properties: " + properties);
		System.err.println("body: " + new String(body));
		//确认ack，没有它后面的消息不会被接收
		channel.basicAck(envelope.getDeliveryTag(), false);
	}
}
```

## 13、消费端ACK 与重回队列

### ①消费端的手工ACK 和 NACK

消费端进行消费的时候，如果由于业务异常我们可以进行日志的记录，然后进行补偿。

如果由于服务器宕机等严重问题，那我们就需要手工进行ACK保障消费端消费成功。

### ②消费端的重回队列

消费端重回队列是为了对没有处理成功的消息，把消息重新会递给Broker！

一般我们在实际应用中，都会关闭重回队列，也就是设置为False。

```java
生产端：
ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		String exchange = "test_ack_exchange";
		String routingKey = "ack.save";
		
		
		
		for(int i =0; i<5; i ++){
			
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put("num", i);
			
			AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
					.deliveryMode(2)
					.contentEncoding("UTF-8")
					.headers(headers)
					.build();
			String msg = "Hello RabbitMQ ACK Message " + i;
			channel.basicPublish(exchange, routingKey, true, properties, msg.getBytes());
		}

消费端：
ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		
		String exchangeName = "test_ack_exchange";
		String queueName = "test_ack_queue";
		String routingKey = "ack.#";
		
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);
		
		// 手工签收 必须要关闭 autoAck = false
		channel.basicConsume(queueName, false, new MyConsumer(channel));
		
消费端自定义监听：
public class MyConsumer extends DefaultConsumer {

	private Channel channel ;
	
	public MyConsumer(Channel channel) {
		super(channel);
		this.channel = channel;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, 		 AMQP.BasicProperties properties, byte[] body) throws IOException {
		System.err.println("-----------consume message----------");
		System.err.println("body: " + new String(body));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if((Integer)properties.getHeaders().get("num") == 0) {
			//如果num==0，就不确认，让这条消息回到队列的末尾，因为第三个参数设置为true
			channel.basicNack(envelope.getDeliveryTag(), false, true);
		} else {
			//确认消息
			channel.basicAck(envelope.getDeliveryTag(), false);
		}	
	}
}
```

## 14、TTL队列\消息

TTL 是 Time To Live 的缩写，也就是生存时间。

RabbitMQ 支持消息的过期时间，在消息发送时可以进行指定。

RabbitMQ 支持队列的过期时间，从消息入队列开始计算，只要超过了队列的超时时间配置，那么消息会自动的清除。

## 15、死信队列

### ①死信队列：DLX，Dead-Letter-Exchange

利用DLX，当消息在一个队列中变成死信（dead message）之后，它能被重新publish 到另一个Exchange，这个Exchange就是DLX。

DLX也是一个正常的Exchange，和一般的Exchange 没有区别，它能在任何的队列上被指定，实际上就是设置某个队列的属性。

当这个队列中有死信时，RabbitMQ 就会自动的将这个消息重新发布到设置的Exchange 上去，进而被路由到另一个队列。

可以监听这个队列中消息做相应的处理，这个特性可以弥补RabbitMQ3.0以前支持的immediate参数的功能。

### ②消息变成死信有一下几种情况

  消息被拒绝（basic.reject/ basic.nack）并且requeue = false

消息TTL 过期

队列达到最大长度	

### ③死信队列设置

首先需要设置死信队列的exchange 和queue，然后进行绑定：

​	exchange： dlx.exchange

​	Queue: dlx.queue

​	RoutingKey: #

然后我们进行正常声明交换机，队列，绑定，只不过我们需要在队列加上一个参数即可：arguments.put("x-dead-letter-exchange","dlx.exchange");

这样消息在过期，requeue，队列在达到最大长度时，消息就可以直接路由到死信队列！

```java
生成端：
ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		String exchange = "test_dlx_exchange";
		String routingKey = "dlx.save";
		
		String msg = "Hello RabbitMQ DLX Message";
		
		for(int i =0; i<1; i ++){
			
			AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
					.deliveryMode(2)
					.contentEncoding("UTF-8")
					.expiration("10000")
					.build();
			channel.basicPublish(exchange, routingKey, true, properties, msg.getBytes());
		}

消费端：
ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		// 这就是一个普通的交换机 和 队列 以及路由
		String exchangeName = "test_dlx_exchange";
		String routingKey = "dlx.#";
		String queueName = "test_dlx_queue";
		
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		
		Map<String, Object> agruments = new HashMap<String, Object>();
		agruments.put("x-dead-letter-exchange", "dlx.exchange");
		//这个agruments属性，要设置到声明队列上
		channel.queueDeclare(queueName, true, false, false, agruments);
		channel.queueBind(queueName, exchangeName, routingKey);
		
		//要进行死信队列的声明:
		channel.exchangeDeclare("dlx.exchange", "topic", true, false, null);
		channel.queueDeclare("dlx.queue", true, false, false, null);
		channel.queueBind("dlx.queue", "dlx.exchange", "#");
		
		channel.basicConsume(queueName, true, new MyConsumer(channel));
		
自定义消费规则：
public class MyConsumer extends DefaultConsumer {


	public MyConsumer(Channel channel) {
		super(channel);
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
		System.err.println("-----------consume message----------");
		System.err.println("consumerTag: " + consumerTag);
		System.err.println("envelope: " + envelope);
		System.err.println("properties: " + properties);
		System.err.println("body: " + new String(body));
	}


}

```



# 二、RabbitMQ整合

## 1、RabbitMQ 整合Spring AMQP

### ①RabbitAdmin 类

可以很好的操作RabbitMQ，在Spring中直接进行注入即可。

```java
@Bean
	public ConnectionFactory connectionFactory(){
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses("192.168.11.76:5672");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		connectionFactory.setVirtualHost("/");
		return connectionFactory;
	}
	
	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
		rabbitAdmin.setAutoStartup(true);
		return rabbitAdmin;
	}
```

注意：autoStartup 必须要设置为true，否则Spring 容器不会加载RabbitAdmin类。

RabbitAdmin底层实现就是从Spring容器中获取Exchange，Bingding，RoutingKey以及Queue的@Bean声明。

然后使用RabbitTemplate 的execute 方法执行对应的声明，修改，删除等一系列RabbitMQ基础功能操作。

```java
1.先定义一个配置类
@Configuration
@ComponentScan({"com.bfxy.spring.*"})
public class RabbitMQConfig {
    @Bean
	public ConnectionFactory connectionFactory(){
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses("192.168.11.76:5672");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		connectionFactory.setVirtualHost("/");
		return connectionFactory;
	}
	
	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
		rabbitAdmin.setAutoStartup(true);
		return rabbitAdmin;
	}
}

2. 测试
@Autowired
	private RabbitAdmin rabbitAdmin;
	
	@Test
	public void testAdmin() throws Exception {
		rabbitAdmin.declareExchange(new DirectExchange("test.direct", false, false));
		
		rabbitAdmin.declareExchange(new TopicExchange("test.topic", false, false));
		
		rabbitAdmin.declareExchange(new FanoutExchange("test.fanout", false, false));
		
		rabbitAdmin.declareQueue(new Queue("test.direct.queue", false));
		
		rabbitAdmin.declareQueue(new Queue("test.topic.queue", false));
		
		rabbitAdmin.declareQueue(new Queue("test.fanout.queue", false));
		
		rabbitAdmin.declareBinding(new Binding("test.direct.queue",
				Binding.DestinationType.QUEUE,
				"test.direct", "direct", new HashMap<>()));
		
		rabbitAdmin.declareBinding(
				BindingBuilder
				.bind(new Queue("test.topic.queue", false))		//直接创建队列
				.to(new TopicExchange("test.topic", false, false))	//直接创建交换机 建立关联关系
				.with("user.#"));	//指定路由Key
		
		
		rabbitAdmin.declareBinding(
				BindingBuilder
				.bind(new Queue("test.fanout.queue", false))		
				.to(new FanoutExchange("test.fanout", false, false)));
		
		//清空队列数据
		rabbitAdmin.purgeQueue("test.topic.queue", false);
	}

3.
    <dependency>
			<groupId>com.rabbitmq</groupId>
			<artifactId>amqp-client</artifactId>
			<version>3.6.5</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-amqp</artifactId>
		</dependency>		
```

### ②RabbitTemplate 消息模板

我们在与SpringAMQP 整合的时候进行发送消息的关键类，该类提供了丰富的发送消息方法，包括可靠性投递消息方法，回调监听消息接口 ConfirmCallback，返回值确认接口ReturnCallback 等等。 同样我们需要进行注入到Spring容器中，然后直接使用。

在与Spring 整合时需要实例化，但是在与SpringBoot 整合时，在配置文件里添加配置即可。

```java
@Bean
	public ConnectionFactory connectionFactory(){
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses("192.168.11.76:5672");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		connectionFactory.setVirtualHost("/");
		return connectionFactory;
	}
@Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    	RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    	return rabbitTemplate;
    }


	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	public void testSendMessage2() throws Exception {
		//1 创建消息
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentType("text/plain");
		Message message = new Message("mq 消息1234".getBytes(), messageProperties);
		
		rabbitTemplate.send("topic001", "spring.abc", message);
		
		rabbitTemplate.convertAndSend("topic001", "spring.amqp", "hello object message send!");
		rabbitTemplate.convertAndSend("topic002", "rabbit.abc", "hello object message send!");
	}
```

### ③SimpleMessageListenerContainer 消息监听容器

简单消息监听容器，这个类非常的强大，我们可以对他进行很多设置，对于消费者的配置项，这个类都可以满足。

监听队列（多个队列）、自动启动，自动声明功能。

设置事务特性，事务管理器，事务属性，事务容量，是否开启事务，回滚事务等。

设置消息确认和自动确认模式，是否重回队列，异常捕获handler函数。

设置消费者标签生成策略，是否独占模式，消费者属性等。

设置具体的监听器，消息转换器等。

注意：SimpleMessageListenerContainer可以进行动态设置，比如在运行中的应用可以动态的修改其消费者数量的大小，接收消息的模式等。

很多基于RabbitMQ 的自制定化后端管控台在进行动态设置的时候，也是根据这一特性去实现的，所以可以看出SpringAMQP非常的强大。

```java
@Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {
    	
    	SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
    	container.setQueues(queue001(), queue002(), queue003(), queue_image(), queue_pdf());
    	container.setConcurrentConsumers(1);
    	container.setMaxConcurrentConsumers(5);
    	container.setDefaultRequeueRejected(false);
    	container.setAcknowledgeMode(AcknowledgeMode.AUTO);
    	container.setExposeListenerChannel(true);
    	container.setConsumerTagStrategy(new ConsumerTagStrategy() {
			@Override
			public String createConsumerTag(String queue) {
				return queue + "_" + UUID.randomUUID().toString();
			}
		});
        container.setMessageListener(new ChannelAwareMessageListener() {
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				String msg = new String(message.getBody());
				System.err.println("----------消费者: " + msg);
			}
		});
    }
```

### ④MessageListenerAdapter 即消息监听适配器

```java
自定义类：
public class TextMessageConverter implements MessageConverter {

	@Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		return new Message(object.toString().getBytes(), messageProperties);
	}

	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		String contentType = message.getMessageProperties().getContentType();
		if(null != contentType && contentType.contains("text")) {
			return new String(message.getBody());
		}
		return message.getBody();
	}

}
public class MessageDelegate {

	public void handleMessage(byte[] messageBody) {
		System.err.println("默认方法, 消息内容:" + new String(messageBody));
	}
	
	public void consumeMessage(byte[] messageBody) {
		System.err.println("字节数组方法, 消息内容:" + new String(messageBody));
	}
	
	public void consumeMessage(String messageBody) {
		System.err.println("字符串方法, 消息内容:" + messageBody);
	}
	
	public void method1(String messageBody) {
		System.err.println("method1 收到消息内容:" + new String(messageBody));
	}
	
	public void method2(String messageBody) {
		System.err.println("method2 收到消息内容:" + new String(messageBody));
	}
	
	
	public void consumeMessage(Map messageBody) {
		System.err.println("map方法, 消息内容:" + messageBody);
	}
	
	
	public void consumeMessage(Order order) {
		System.err.println("order对象, 消息内容, id: " + order.getId() + 
				", name: " + order.getName() + 
				", content: "+ order.getContent());
	}
	
	public void consumeMessage(Packaged pack) {
		System.err.println("package对象, 消息内容, id: " + pack.getId() + 
				", name: " + pack.getName() + 
				", content: "+ pack.getDescription());
	}
	
	public void consumeMessage(File file) {
		System.err.println("文件对象 方法, 消息内容:" + file.getName());
	}
}

		/**
    	 * 1 适配器方式. 默认是有自己的方法名字的：handleMessage
    		// 可以自己指定一个方法的名字: consumeMessage
    		// 也可以添加一个转换器: 从字节数组转换为String
    		*/
    	MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
    	adapter.setDefaultListenerMethod("consumeMessage");
    	adapter.setMessageConverter(new TextMessageConverter());
    	container.setMessageListener(adapter);

		/**
    	 * 2 适配器方式: 我们的队列名称 和 方法名称 也可以进行一一的匹配
    	 * */
    	MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
    	adapter.setMessageConverter(new TextMessageConverter());
    	Map<String, String> queueOrTagToMethodName = new HashMap<>();
    	queueOrTagToMethodName.put("queue001", "method1");
    	queueOrTagToMethodName.put("queue002", "method2");
    	adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
    	container.setMessageListener(adapter);    	
    	
    	
```

MessageListenerAdapter 即 消息监听适配器

通过messageListenerAdapter 的代码我们可以看出如下核心属性：

defaultListenerMethod 默认监听方法名称：用于设置监听方法名称。

queueOrTagToMethodName 队列标识与方法名称组成的集合。

可以--进行队列与方法名称的匹配。

队列和方法名称绑定，即指定队列里的消息会被绑定的方法所接受处理。

### ⑤MessageConverter 消息转换器

我们在进行发送消息的时候，正常情况下消息体为二进制的数据方式进行传输，如果希望内部帮我们进行转换，或者指定自定义的转换器，就需要用到MessageConverter

自定义常用转换器： MessageConvert，一般来讲都需要实现这个接口。

重写下面两个方法：

   toMessage：java对象转换为Message。

​	fromMessage：Message对象转换为java对象。

JSON转换器：Jackson2JsonMessageConvert： 可以进行java对象的转换功能。

DefaultJackson2JavaTypeMapper映射器：可以进行java对象的映射关系。

自定义二进制转换器：比如图片类型、PDF、PPT、流媒体。

```java
自定义类：
public class ImageMessageConverter implements MessageConverter {

	@Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		throw new MessageConversionException(" convert error ! ");
	}

	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		System.err.println("-----------Image MessageConverter----------");
		
		Object _extName = message.getMessageProperties().getHeaders().get("extName");
		String extName = _extName == null ? "png" : _extName.toString();
		
		byte[] body = message.getBody();
		String fileName = UUID.randomUUID().toString();
		String path = "d:/010_test/" + fileName + "." + extName;
		File f = new File(path);
		try {
			Files.copy(new ByteArrayInputStream(body), f.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

}
public class PDFMessageConverter implements MessageConverter {

	@Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		throw new MessageConversionException(" convert error ! ");
	}

	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		System.err.println("-----------PDF MessageConverter----------");
		
		byte[] body = message.getBody();
		String fileName = UUID.randomUUID().toString();
		String path = "d:/010_test/" + fileName + ".pdf";
		File f = new File(path);
		try {
			Files.copy(new ByteArrayInputStream(body), f.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

}

public class MessageDelegate {

	public void handleMessage(byte[] messageBody) {
		System.err.println("默认方法, 消息内容:" + new String(messageBody));
	}
	
	public void consumeMessage(byte[] messageBody) {
		System.err.println("字节数组方法, 消息内容:" + new String(messageBody));
	}
	
	public void consumeMessage(String messageBody) {
		System.err.println("字符串方法, 消息内容:" + messageBody);
	}
	
	public void method1(String messageBody) {
		System.err.println("method1 收到消息内容:" + new String(messageBody));
	}
	
	public void method2(String messageBody) {
		System.err.println("method2 收到消息内容:" + new String(messageBody));
	}
	
	
	public void consumeMessage(Map messageBody) {
		System.err.println("map方法, 消息内容:" + messageBody);
	}
	
	
	public void consumeMessage(Order order) {
		System.err.println("order对象, 消息内容, id: " + order.getId() + 
				", name: " + order.getName() + 
				", content: "+ order.getContent());
	}
	
	public void consumeMessage(Packaged pack) {
		System.err.println("package对象, 消息内容, id: " + pack.getId() + 
				", name: " + pack.getName() + 
				", content: "+ pack.getDescription());
	}
	
	public void consumeMessage(File file) {
		System.err.println("文件对象 方法, 消息内容:" + file.getName());
	}
}

public class TextMessageConverter implements MessageConverter {

	@Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		return new Message(object.toString().getBytes(), messageProperties);
	}

	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		String contentType = message.getMessageProperties().getContentType();
		if(null != contentType && contentType.contains("text")) {
			return new String(message.getBody());
		}
		return message.getBody();
	}

}

 // 1.1 支持json格式的转换器
     
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");
        
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        adapter.setMessageConverter(jackson2JsonMessageConverter);
        
        container.setMessageListener(adapter);
        
// 1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象转换

        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");
        
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        
        adapter.setMessageConverter(jackson2JsonMessageConverter);
        container.setMessageListener(adapter);
        
        
        
        //1.3 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象多映射转换
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        
        Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();
		idClassMapping.put("order", com.bfxy.spring.entity.Order.class);
		idClassMapping.put("packaged", com.bfxy.spring.entity.Packaged.class);
		
		javaTypeMapper.setIdClassMapping(idClassMapping);
		
		jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        adapter.setMessageConverter(jackson2JsonMessageConverter);
        container.setMessageListener(adapter);
        @Test
	public void testSendJavaMessage() throws Exception {
		
		Order order = new Order();
		order.setId("001");
		order.setName("订单消息");
		order.setContent("订单描述信息");
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(order);
		System.err.println("order 4 json: " + json);
		
		MessageProperties messageProperties = new MessageProperties();
		//这里注意一定要修改contentType为 application/json
		messageProperties.setContentType("application/json");
		messageProperties.getHeaders().put("__TypeId__", "com.bfxy.spring.entity.Order");
		Message message = new Message(json.getBytes(), messageProperties);
		
		rabbitTemplate.send("topic001", "spring.order", message);
	}
	
	@Test
	public void testSendMappingMessage() throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		
		Order order = new Order();
		order.setId("001");
		order.setName("订单消息");
		order.setContent("订单描述信息");
		
		String json1 = mapper.writeValueAsString(order);
		System.err.println("order 4 json: " + json1);
		
		MessageProperties messageProperties1 = new MessageProperties();
		//这里注意一定要修改contentType为 application/json
		messageProperties1.setContentType("application/json");
		messageProperties1.getHeaders().put("__TypeId__", "order");
		Message message1 = new Message(json1.getBytes(), messageProperties1);
		rabbitTemplate.send("topic001", "spring.order", message1);
		
		Packaged pack = new Packaged();
		pack.setId("002");
		pack.setName("包裹消息");
		pack.setDescription("包裹描述信息");
		
		String json2 = mapper.writeValueAsString(pack);
		System.err.println("pack 4 json: " + json2);

		MessageProperties messageProperties2 = new MessageProperties();
		//这里注意一定要修改contentType为 application/json
		messageProperties2.setContentType("application/json");
		messageProperties2.getHeaders().put("__TypeId__", "packaged");
		Message message2 = new Message(json2.getBytes(), messageProperties2);
		rabbitTemplate.send("topic001", "spring.pack", message2);
	}

@Test
	public void testSendExtConverterMessage() throws Exception {
//			byte[] body = Files.readAllBytes(Paths.get("d:/002_books", "picture.png"));
//			MessageProperties messageProperties = new MessageProperties();
//			messageProperties.setContentType("image/png");
//			messageProperties.getHeaders().put("extName", "png");
//			Message message = new Message(body, messageProperties);
//			rabbitTemplate.send("", "image_queue", message);
		
			byte[] body = Files.readAllBytes(Paths.get("d:/002_books", "mysql.pdf"));
			MessageProperties messageProperties = new MessageProperties();
			messageProperties.setContentType("application/pdf");
			Message message = new Message(body, messageProperties);
			rabbitTemplate.send("", "pdf_queue", message);
	}
```

# 三、SpringBoot 整合配置详情

生产端：

①publisher-confirms，实现一个监听器用于监听Broker端给我们返回的确认请求：RabbitTemplate.ConfirmCallback

②publisher-returns，保证消息对Broker端是可达的，如果出现路由键不可达的情况，则使用监听器对不可达的消息进行后续的处理，保证消息的路由成功：RabbitTemplate.ReturnCallback.

注意一点，在发送消息的时候对template 进行配置mandatory = true，保证监听有效。

生产端还可以配置其他属性，比如发送重试，超时时间，次数，间隔等。



消费端：

```java
spring.rabbitmq.listener.simple.acknowledge-mode = MANUAL
spring.rabbitmq.listener.simple.concurrency = 1
spring.rabbitmq.listener.simple.max-concurrency = 5
```

首先配置手工确认模式，用于ACK的手工处理，这样我们可以保证消息的可靠性送达，或者再消费端消费失败的时候可以做到重回队列，根据业务记录日志等处理。

可以设置消费端的监听个数和最大个数，用于控制消费端的并发情况。

①RabbitListener 注解使用

消费端监听 @RabbitMQListener 注解，这个对于在实际工作中非常的好用。

@RabbitListener 是一个组合注解，里面可以注解配置。

@QueueBinding 、 @Queue、@Exchange 直接通过这个组合注解一次性搞定消费端交换机、队列、绑定、路由、并且配置监听功能等。

```java
生产者

@Component
public class RabbitSender {

	//自动注入RabbitTemplate模板类
	@Autowired
	private RabbitTemplate rabbitTemplate;  
	
	//回调函数: confirm确认
	final ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
		@Override
		public void confirm(CorrelationData correlationData, boolean ack, String cause) {
			System.err.println("correlationData: " + correlationData);
			System.err.println("ack: " + ack);
			if(!ack){
				System.err.println("异常处理....");
			}
		}
	};
	
	//回调函数: return返回
	final ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
		@Override
		public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText,
				String exchange, String routingKey) {
			System.err.println("return exchange: " + exchange + ", routingKey: " 
				+ routingKey + ", replyCode: " + replyCode + ", replyText: " + replyText);
		}
	};
	
	//发送消息方法调用: 构建Message消息
	public void send(Object message, Map<String, Object> properties) throws Exception {
		MessageHeaders mhs = new MessageHeaders(properties);
		Message msg = MessageBuilder.createMessage(message, mhs);
		rabbitTemplate.setConfirmCallback(confirmCallback);
		rabbitTemplate.setReturnCallback(returnCallback);
		//id + 时间戳 全局唯一 
		CorrelationData correlationData = new CorrelationData("1234567890");
		rabbitTemplate.convertAndSend("exchange-1", "springboot.abc", msg, correlationData);
	}
	
	//发送消息方法调用: 构建自定义对象消息
	public void sendOrder(Order order) throws Exception {
		rabbitTemplate.setConfirmCallback(confirmCallback);
		rabbitTemplate.setReturnCallback(returnCallback);
		//id + 时间戳 全局唯一 
		CorrelationData correlationData = new CorrelationData("0987654321");
		rabbitTemplate.convertAndSend("exchange-2", "springboot.def", order, correlationData);
	}	
}

application.properties
spring.rabbitmq.addresses=192.168.11.76:5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.virtual-host=/
spring.rabbitmq.connection-timeout=15000

spring.rabbitmq.publisher-confirms=true
spring.rabbitmq.publisher-returns=true
spring.rabbitmq.template.mandatory=true



消费者：

@Component
public class RabbitReceiver {

	
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = "queue-1", 
			durable="true"),
			exchange = @Exchange(value = "exchange-1", 
			durable="true", 
			type= "topic", 
			ignoreDeclarationExceptions = "true"),
			key = "springboot.*"
			)
	)
	@RabbitHandler
	public void onMessage(Message message, Channel channel) throws Exception {
		System.err.println("--------------------------------------");
		System.err.println("消费端Payload: " + message.getPayload());
		Long deliveryTag = (Long)message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
		//手工ACK
		channel.basicAck(deliveryTag, false);
	}
	/**
	 * 
	 * 	spring.rabbitmq.listener.order.queue.name=queue-2
		spring.rabbitmq.listener.order.queue.durable=true
		spring.rabbitmq.listener.order.exchange.name=exchange-1
		spring.rabbitmq.listener.order.exchange.durable=true
		spring.rabbitmq.listener.order.exchange.type=topic
		spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions=true
		spring.rabbitmq.listener.order.key=springboot.*
	 * @param order
	 * @param channel
	 * @param headers
	 * @throws Exception
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}", 
			durable="${spring.rabbitmq.listener.order.queue.durable}"),
			exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}", 
			durable="${spring.rabbitmq.listener.order.exchange.durable}", 
			type= "${spring.rabbitmq.listener.order.exchange.type}", 
			ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"),
			key = "${spring.rabbitmq.listener.order.key}"
			)
	)
	@RabbitHandler
	public void onOrderMessage(@Payload com.bfxy.springboot.entity.Order order, 
			Channel channel, 
			@Headers Map<String, Object> headers) throws Exception {
		System.err.println("--------------------------------------");
		System.err.println("消费端order: " + order.getId());
		Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
		//手工ACK
		channel.basicAck(deliveryTag, false);
	}
}

application.properties
spring.rabbitmq.addresses=192.168.11.76:5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.virtual-host=/
spring.rabbitmq.connection-timeout=15000

spring.rabbitmq.listener.simple.acknowledge-mode=manual
spring.rabbitmq.listener.simple.concurrency=5
spring.rabbitmq.listener.simple.max-concurrency=10

spring.rabbitmq.listener.order.queue.name=queue-2
spring.rabbitmq.listener.order.queue.durable=true
spring.rabbitmq.listener.order.exchange.name=exchange-2
spring.rabbitmq.listener.order.exchange.durable=true
spring.rabbitmq.listener.order.exchange.type=topic
spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions=true
spring.rabbitmq.listener.order.key=springboot.*
```



# 四、SpringCloud Stream 整合

①Barista 接口： Barista 接口式定义来作为后面类的参数，这一接口定义来通道类型和通道名称，通道名称是作为配置用，通道类型则决定了app 会使用这一通道进行发送消息还是从中接收消息。

@Output：输出注解，用于定义发送消息接口。

@Input：输入注解，用于定义消息的消费者接口。

@StreamListener：用于定义监听方法的主键。

使用SpringCloud Stream 非常简单，只需要使用好这3个注解即可，在实现高性能消息的生产和消费的场景非常适合，但是使用SpringCloudStream框架有一个非常大的问题就是不能实现可靠性的投递，也就是没法保证消息的100%可靠性，会存在少量消息丢失的问题。

这个原因是因为SpringCloudStream框架为了和Kafka兼顾所以在实际工作中使用它的目的就是针对高性能的消息通信的！这点就是在当前版本SpringCloudStream的定位。

```java
pom.xml
		<dependency>
		    <groupId>org.springframework.cloud</groupId>
		    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
		    <version>1.3.4.RELEASE</version>
		</dependency>
		
application.properties
server.port=8001
server.servlet.context-path=/producer

spring.application.name=producer
spring.cloud.stream.bindings.output_channel.destination=exchange-3
spring.cloud.stream.bindings.output_channel.group=queue-3
spring.cloud.stream.bindings.output_channel.binder=rabbit_cluster

spring.cloud.stream.binders.rabbit_cluster.type=rabbit
spring.cloud.stream.binders.rabbit_cluster.environment.spring.rabbitmq.addresses=192.168.11.76:5672
spring.cloud.stream.binders.rabbit_cluster.environment.spring.rabbitmq.username=guest
spring.cloud.stream.binders.rabbit_cluster.environment.spring.rabbitmq.password=guest
spring.cloud.stream.binders.rabbit_cluster.environment.spring.rabbitmq.virtual-host=/
    
/**
 * <B>中文类名：</B><BR>
 * <B>概要说明：</B><BR>
 * 这里的Barista接口是定义来作为后面类的参数，这一接口定义来通道类型和通道名称。
 * 通道名称是作为配置用，通道类型则决定了app会使用这一通道进行发送消息还是从中接收消息。
 */
public interface Barista {
	  
    //String INPUT_CHANNEL = "input_channel";  
    String OUTPUT_CHANNEL = "output_channel";  

    //注解@Input声明了它是一个输入类型的通道，名字是Barista.INPUT_CHANNEL，也就是position3的input_channel。这一名字与上述配置app2的配置文件中position1应该一致，表明注入了一个名字叫做input_channel的通道，它的类型是input，订阅的主题是position2处声明的mydest这个主题  
//    @Input(Barista.INPUT_CHANNEL)  
//    SubscribableChannel loginput();  
    //注解@Output声明了它是一个输出类型的通道，名字是output_channel。这一名字与app1中通道名一致，表明注入了一个名字为output_channel的通道，类型是output，发布的主题名为mydest。  
    @Output(Barista.OUTPUT_CHANNEL)
    MessageChannel logoutput();  

//	String INPUT_BASE = "queue-1";  
//	String OUTPUT_BASE = "queue-1";  
//	@Input(Barista.INPUT_BASE)  
//	SubscribableChannel input1();  
//	MessageChannel output1();  
      
}  
@EnableBinding(Barista.class)
@Service  
public class RabbitmqSender {  
  
    @Autowired  
    private Barista barista;  
    
    // 发送消息
    public String sendMessage(Object message, Map<String, Object> properties) throws Exception {  
        try{
        	MessageHeaders mhs = new MessageHeaders(properties);
        	Message msg = MessageBuilder.createMessage(message, mhs);
            boolean sendStatus = barista.logoutput().send(msg);
            System.err.println("--------------sending -------------------");
            System.out.println("发送数据：" + message + ",sendStatus: " + sendStatus);
        }catch (Exception e){  
        	System.err.println("-------------error-------------");
        	e.printStackTrace();
            throw new RuntimeException(e.getMessage());
           
        }  
        return null;
    }  
    
}  


消费端：
application.properties

server.port=8002
server.context-path=/consumer
spring.application.name=consumer
spring.cloud.stream.bindings.input_channel.destination=exchange-3
spring.cloud.stream.bindings.input_channel.group=queue-3
spring.cloud.stream.bindings.input_channel.binder=rabbit_cluster
spring.cloud.stream.bindings.input_channel.consumer.concurrency=1
spring.cloud.stream.rabbit.bindings.input_channel.consumer.requeue-rejected=false
spring.cloud.stream.rabbit.bindings.input_channel.consumer.acknowledge-mode=MANUAL
spring.cloud.stream.rabbit.bindings.input_channel.consumer.recovery-interval=3000
spring.cloud.stream.rabbit.bindings.input_channel.consumer.durable-subscription=true
spring.cloud.stream.rabbit.bindings.input_channel.consumer.max-concurrency=5

spring.cloud.stream.binders.rabbit_cluster.type=rabbit
spring.cloud.stream.binders.rabbit_cluster.environment.spring.rabbitmq.addresses=192.168.11.76:5672
spring.cloud.stream.binders.rabbit_cluster.environment.spring.rabbitmq.username=guest
spring.cloud.stream.binders.rabbit_cluster.environment.spring.rabbitmq.password=guest
spring.cloud.stream.binders.rabbit_cluster.environment.spring.rabbitmq.virtual-host=/
    

/**
 * <B>中文类名：</B><BR>
 * <B>概要说明：</B><BR>
 * 这里的Barista接口是定义来作为后面类的参数，这一接口定义来通道类型和通道名称。
 * 通道名称是作为配置用，通道类型则决定了app会使用这一通道进行发送消息还是从中接收消息。
 * @author ashen（Alienware）
 * @since 2016年7月22日
 */

public interface Barista {
	  
    String INPUT_CHANNEL = "input_channel";  

    //注解@Input声明了它是一个输入类型的通道，名字是Barista.INPUT_CHANNEL，也就是position3的input_channel。这一名字与上述配置app2的配置文件中position1应该一致，表明注入了一个名字叫做input_channel的通道，它的类型是input，订阅的主题是position2处声明的mydest这个主题  
    @Input(Barista.INPUT_CHANNEL)  
    SubscribableChannel loginput();        
}  

@EnableBinding(Barista.class)
@Service
public class RabbitmqReceiver {  

    @StreamListener(Barista.INPUT_CHANNEL)  
    public void receiver(Message message) throws Exception {  
		Channel channel = (com.rabbitmq.client.Channel) message.getHeaders().get(AmqpHeaders.CHANNEL);
		Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
    	System.out.println("Input Stream 1 接受数据：" + message);
    	System.out.println("消费完毕------------");
    	channel.basicAck(deliveryTag, false);
    }  
}  
```



# 五、RabbitMQ 集群架构模式

①镜像模式： 集群模式非常经典的就是Mirror 镜像模式，保证100% 数据不丢失，在实际工作中也是用的最多的。 并且实现集群非常的简单，一般互联网大厂都会构建这种镜像集群模式。

​	Mirror 镜像队列，目的是为了保证RabbitMQ 数据的高可靠性解决方案，主要就是实现数据的同步，一般来讲是2-3个节点实现数据同步（对于100% 数据可靠性解决方案一般是3个节点）。

![镜像模式](C:\Users\Administrator\Desktop\笔记\RabbitMQ\镜像模式.png)

②多活模式： 这种模式也是实现异地数据复制的主流模式，因为Shovel 模式配置比较复制，所以一般来说实现异地集群都是使用这种双活 或者多活模型来取实现的。 这种模型需要依赖 RabbitMQ 的federation 插件，可以实现持续的可靠的AMQP 数据通信，多活模式在实际配置与应用非常的简单。

​	RabbitMQ 部署架构采用双中心（多中心），那么在两套（或多套）数据中心中各部署一套RabbitMQ 集群，各中心的RabbitMQ服务除了需要为业务提供正常的消息服务外，中心之间还需要实现部分队列消息共享。

![多活模式架构图](C:\Users\Administrator\Desktop\笔记\RabbitMQ\多活模式架构图.png)

Federation 插件是一个不需要构建Cluster，而在Brokers 之间传输消息的高性能插件，Federation 插件可以在Brokers 或者Cluster 之间传输消息，连接的双方可以使用不同的 users 和 virtual hosts，双方也可以使用版本不同的RabbitMQ 和Erlang。 Federation 插件使用AMQP 协议通讯，可以接受不连续的传输。

Federation Exchanges，可以看成 Downstream 从 Upstream 主动拉取消息，但并不是拉取所有消息，必须是在Downstream 上已经明确定义Bindings 关系的 Exchange，也就是有实际的物理 QUeue  来接收消息，才会从 Upstream 拉取消息到Downstream。  使用AMQP 协议实施代理间通讯，Downstream 会将绑定关系组合在一起，绑定\解除绑定命令将发送到 Upstream 交换机。因此，Federation Exchange 只接收具体订阅的消息，

![多活模式2](C:\Users\Administrator\Desktop\笔记\RabbitMQ\多活模式2.png)































































