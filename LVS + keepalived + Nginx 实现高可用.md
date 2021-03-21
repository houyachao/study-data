## 一、LVS + Keepalived + Nginx 实现高可用

### ① LVS 简介

​	 **LVS（Linux Virtual Server）即Linux虚拟服务器，是由章文嵩博士主导的开源负载均衡项目，目前LVS已经被集成到Linux内核模块中。该项目在Linux内核中实现了基于IP的数据请求负载均衡调度方案，其体系结构如图1所示，终端互联网用户从外部访问公司的外部负载均衡服务器，终端用户的Web请求会发送给LVS调度器，调度器根据自己预设的算法决定将该请求发送给后端的某台Web服务器，比如，轮询算法可以将外部的请求平均分发给后端的所有服务器，终端用户访问LVS调度器虽然会被转发到后端真实的服务器，但如果真实服务器连接的是相同的存储，提供的服务也是相同的服务，最终用户不管是访问哪台真实服务器，得到的服务内容都是一样的，整个集群对用户而言都是透明的。最后根据LVS工作模式的不同，真实服务器会选择不同的方式将用户需要的数据发送到终端用户，LVS工作模式分为NAT模式、TUN模式、以及DR模式。**

![lvs原理图](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\lvs原理图.jpg)

### ② LVS 的宗旨

1. **使用集群技术和Linux操作系统实现一个高性能、高可用的服务器.**
2. **很好的可伸缩性（Scalability）**
3. **很好的可靠性（Reliability）**
4. **很好的可管理性（Manageability）**

### ③LVS 三种工作模式的解析

#### 1、基于NAT 的LVS 模式负载均衡

​	NAT（Network Address Translation）即网络地址转换，其作用是通过数据报头的修改，使得位于企业内部的私有IP地址可以访问外网，以及外部用用户可以访问位于公司内部的私有IP主机。VS/NAT工作模式拓扑结构如图2所示，LVS负载调度器可以使用两块网卡配置不同的IP地址，eth0设置为私钥IP与内部网络通过交换设备相互连接，eth1设备为外网IP与外部网络联通。

​       第一步，用户通过互联网DNS服务器解析到公司负载均衡设备上面的外网地址，相对于真实服务器而言，LVS外网IP又称VIP（Virtual IP Address），用户通过访问VIP，即可连接后端的真实服务器（Real Server），而这一切对用户而言都是透明的，用户以为自己访问的就是真实服务器，但他并不知道自己访问的VIP仅仅是一个调度器，也不清楚后端的真实服务器到底在哪里、有多少真实服务器。

   第二步，用户将请求发送至124.126.147.168，此时LVS将根据预设的算法选择后端的一台真实服务器（192.168.0.1~192.168.0.3），将数据请求包转发给真实服务器，并且在转发之前LVS会修改数据包中的目标地址以及目标端口，目标地址与目标端口将被修改为选出的真实服务器IP地址以及相应的端口。

​    第三步，真实的服务器将响应数据包返回给LVS调度器，调度器在得到响应的数据包后会将源地址和源端口修改为VIP及调度器相应的端口，修改完成后，由调度器将响应数据包发送回终端用户，另外，由于LVS调度器有一个连接Hash表，该表中会记录连接请求及转发信息，当同一个连接的下一个数据包发送给调度器时，从该Hash表中可以直接找到之前的连接记录，并根据记录信息选出相同的真实服务器及端口信息。

![6.四层负载均衡包通信](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\6.四层负载均衡包通信.png)

#### 2、基于TUN 的LVS 负载均衡

 	在LVS（NAT）模式的集群环境中，由于所有的数据请求及响应的数据包都需要经过LVS调度器转发，如果后端服务器的数量大于10台，则调度器就会成为整个集群环境的瓶颈。我们知道，数据请求包往往远小于响应数据包的大小。因为响应数据包中包含有客户需要的具体数据，所以LVS（TUN）的思路就是将请求与响应数据分离，让调度器仅处理数据请求，而让真实服务器响应数据包直接返回给客户端。VS/TUN工作模式拓扑结构如图3所示。其中，**IP隧道（IP tunning）是一种数据包封装技术，它可以将原始数据包封装并添加新的包头（内容包括新的源地址及端口、目标地址及端口），从而实现将一个目标为调度器的VIP地址的数据包封装，通过隧道转发给后端的真实服务器（Real Server），通过将客户端发往调度器的原始数据包封装，并在其基础上添加新的数据包头（修改目标地址为调度器选择出来的真实服务器的IP地址及对应端口**），LVS（TUN）模式要求真实服务器可以直接与外部网络连接，真实服务器在收到请求数据包后直接给客户端主机响应数据。

![LVS 模式下的ip隧道](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\LVS 模式下的ip隧道.jpg)

#### 3、基于DR 的LVS 负载均衡

​	在LVS（TUN）模式下，由于需要在LVS调度器与真实服务器之间创建隧道连接，这同样会增加服务器的负担。与LVS（TUN）类似，DR模式也叫直接路由模式，其体系结构如图4所示，该模式中LVS依然仅承担数据的入站请求以及根据算法选出合理的真实服务器，最终由后端真实服务器负责将响应数据包发送返回给客户端。与隧道模式不同的是，直接路由模式（DR模式）要求调度器与后端服务器必须在同一个局域网内，VIP地址需要在调度器与后端所有的服务器间共享，因为最终的真实服务器给客户端回应数据包时需要设置源IP为VIP地址，目标IP为客户端IP，这样客户端访问的是调度器的VIP地址，回应的源地址也依然是该VIP地址（真实服务器上的VIP），客户端是感觉不到后端服务器存在的。由于多台计算机都设置了同样一个VIP地址，所以在直接路由模式中要求调度器的VIP地址是对外可见的，客户端需要将请求数据包发送到调度器主机，而所有的真实服务器的VIP地址必须配置在Non-ARP的网络设备上，也就是该网络设备并不会向外广播自己的MAC及对应的IP地址，真实服务器的VIP对外界是不可见的，但真实服务器却可以接受目标地址VIP的网络请求，并在回应数据包时将源地址设置为该VIP地址。调度器根据算法在选出真实服务器后，在不修改数据报文的情况下，将数据帧的MAC地址修改为选出的真实服务器的MAC地址，通过交换机将该数据帧发给真实服务器。整个过程中，真实服务器的VIP不需要对外界可见。

![7.2层负载均衡通信模型](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\7.2层负载均衡通信模型.png)

### ③ LVS负载均衡调度算法

​	根据前面的介绍，我们了解了LVS的三种工作模式，但不管实际环境中采用的是哪种模式，调度算法进行调度的策略与算法都是LVS的核心技术，LVS在内核中主要实现了一下十种调度算法。

#### 1.轮询调度

​	轮询调度（Round Robin 简称'RR'）算法就是按依次循环的方式将请求调度到不同的服务器上，该算法最大的特点就是实现简单。轮询算法假设所有的服务器处理请求的能力都一样的，调度器会将所有的请求平均分配给每个真实服务器。

#### 2.加权轮询调度

​	加权轮询（Weight Round Robin 简称'WRR'）算法主要是对轮询算法的一种优化与补充，LVS会考虑每台服务器的性能，并给每台服务器添加一个权值，如果服务器A的权值为1，服务器B的权值为2，则调度器调度到服务器B的请求会是服务器A的两倍。权值越高的服务器，处理的请求越多。

#### 3.最小连接调度

​	最小连接调度（Least Connections 简称'LC'）算法是把新的连接请求分配到当前连接数最小的服务器。最小连接调度是一种动态的调度算法，它通过服务器当前活跃的连接数来估计服务器的情况。调度器需要记录各个服务器已建立连接的数目，当一个请求被调度到某台服务器，其连接数加1；当连接中断或者超时，其连接数减1。

（集群系统的真实服务器具有相近的系统性能，采用最小连接调度算法可以比较好地均衡负载。)

#### 4.加权最小连接调度

​	加权最少连接（Weight Least Connections 简称'WLC'）算法是最小连接调度的超集，各个服务器相应的权值表示其处理性能。服务器的缺省权值为1，系统管理员可以动态地设置服务器的权值。加权最小连接调度在调度新连接时尽可能使服务器的已建立连接数和其权值成比例。调度器可以自动问询真实服务器的负载情况，并动态地调整其权值。

#### 5.基于局部的最少连接

​	基于局部的最少连接调度（Locality-Based Least Connections 简称'LBLC'）算法是针对请求报文的目标IP地址的 负载均衡调度，目前主要用于Cache集群系统，因为在Cache集群客户请求报文的目标IP地址是变化的。这里假设任何后端服务器都可以处理任一请求，算法的设计目标是在服务器的负载基本平衡情况下，将相同目标IP地址的请求调度到同一台服务器，来提高各台服务器的访问局部性和Cache命中率，从而提升整个集群系统的处理能力。LBLC调度算法先根据请求的目标IP地址找出该目标IP地址最近使用的服务器，若该服务器是可用的且没有超载，将请求发送到该服务器；若服务器不存在，或者该服务器超载且有服务器处于一半的工作负载，则使用'最少连接'的原则选出一个可用的服务器，将请求发送到服务器。

#### 6.带复制的基于局部性的最少连接

​	带复制的基于局部性的最少连接（Locality-Based Least Connections with Replication  简称'LBLCR'）算法也是针对目标IP地址的负载均衡，目前主要用于Cache集群系统，它与LBLC算法不同之处是它要维护从一个目标IP地址到一组服务器的映射，而LBLC算法维护从一个目标IP地址到一台服务器的映射。按'最小连接'原则从该服务器组中选出一一台服务器，若服务器没有超载，将请求发送到该服务器；若服务器超载，则按'最小连接'原则从整个集群中选出一台服务器，将该服务器加入到这个服务器组中，将请求发送到该服务器。同时，当该服务器组有一段时间没有被修改，将最忙的服务器从服务器组中删除，以降低复制的程度。

#### 7.目标地址散列调度

​	目标地址散列调度（Destination Hashing 简称'DH'）算法先根据请求的目标IP地址，作为散列键（Hash Key）从静态分配的散列表找出对应的服务器，若该服务器是可用的且并未超载，将请求发送到该服务器，否则返回空。

#### 8.源地址散列调度U

​	源地址散列调度（Source Hashing  简称'SH'）算法先根据请求的源IP地址，作为散列键（Hash Key）从静态分配的散列表找出对应的服务器，若该服务器是可用的且并未超载，将请求发送到该服务器，否则返回空。它采用的散列函数与目标地址散列调度算法的相同，它的算法流程与目标地址散列调度算法的基本相似。

#### 9.最短的期望的延迟

​	最短的期望的延迟调度（Shortest Expected Delay 简称'SED'）算法基于WLC算法。举个例子吧，ABC三台服务器的权重分别为1、2、3 。那么如果使用WLC算法的话一个新请求进入时它可能会分给ABC中的任意一个。使用SED算法后会进行一个运算

A：（1+1）/1=2   B：（1+2）/2=3/2   C：（1+3）/3=4/3   就把请求交给得出运算结果最小的服务器。

#### 10.最少队列调度

​	最少队列调度（Never Queue 简称'NQ'）算法，无需队列。如果有realserver的连接数等于0就直接分配过去，不需要在进行SED运算。

### ④LVS 的原理图

![10.lvs原理与](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\10.lvs原理与.png)

### ⑤Keepalived 简介

​	Keepalived 是Linux 下一个轻量级别的高可用解决方案。高可用：广义来讲，是指整个系统的高可用行；狭义的讲就是主机的冗余和接管。

​	它与HeartBeat 实现类似的功能，都可以实现服务或者网络的高可用，但是又有差别，HeartBeat 是一个专业的、功能完善的高可用软件，**它提供HA 软件所需的基本功能，比如：心跳检测，资源接管，检测集群中的服务，在集群节点转移共享IP 地址的所有者等等**。HeartBeat 功能强大，但是部署和使用相对比较麻烦，与 HeartBeat 相比，Keepalived 主要是通过虚拟路由冗余来实现高可用功能，虽然它没有HeartBeat 功能强大，但是Keepalived 部署和使用非常的简单，所有配置只需要一个配置文件即可以完成。

### ⑥Keepalived 是什么？

​	Keepalived 起初是为LVS 设计的，专门用来监控集群系统中各个服务节点的状态，它根据TCP 、IP 参考模型的第三、第四层、第五层交换机制检测每个服务节点的状态，如果某个服务器节点出现异常，或者工作出现故障，keepalived 将检测到，并将出现的故障服务器节点从集群系统中剔除，这些工作全部都是自动完成的，不需要人工干涉，需要人工完成的只是出现故障的服务节点。

​	后来**Keepalived 又加入了VRRP 的功能，** VRRP（VirtualRouterRedundancyProtocol，**虚拟路由冗余协议**）出现的目的是解决静态路由出现的单点故障问题，通过VRRP 可以实现网络不断稳定运行，因此Keepalived 一方面具有服务器状态监测和故障隔离功能，另一方面也有HAcluster 功能。

​	健康检查和失败切换是keepalived 的两大核心功能。所谓的健康检查，就是采用TCP 三次握手，icmp 请求，http请求，udp echo 请求等方式堆负载均衡器后面的实际的服务器（通常是承载真实业务的服务器）进行保活；而失败切换主要是应用于配置了**主备模式** 的负载均衡器，利用VRRP 维持主备负载均衡器的心跳，当主负载均衡器出现问题时，由被负载均衡器承载对应的业务，从而在最大限度上减少流量损失，并提供服务的稳定性。

### ⑦ VRRP协议与工作原理

​	在现实的网络环境中。主机之间的通信都是通过配置静态路由或者(默认网关)来完成的，而主机之间的路由器一旦发生故障，通信就会失效，因此这种通信模式当中，路由器就成了一个单点瓶颈，为了解决这个问题，就引入了VRRP协议。

​	VRRP协议是一种容错的主备模式的协议，保证当主机的下一跳路由出现故障时，由另一台路由器来代替出现故障的路由器进行工作，通过VRRP可以在网络发生故障时透明的进行设备切换而不影响主机之间的数据通信。 

![keepalived 工作原理](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\keepalived 工作原理.png)

**虚拟路由器**：虚拟路由器是VRRP备份组中所有路由器的集合，它是一个逻辑概念，并不是正真存在的。从备份组外面看备份组中的路由器，感觉组中的所有路由器就像一个 一样，可以理解为在一个组中： 主路由器+所有备份路由器=虚拟路由器。虚拟路由器有一个虚拟的IP地址和MAC地址。主机将虚拟路由器当作默认网关。虚拟MAC地址的格式为00-00-5E-00-01-{VRID}。通常情况下，虚拟路由器回应ARP请求使用的是虚拟MAC地址，只有虚拟路由器做特殊配置的时候，才回应接口的真实MAC地址。

**主路由器**（MASTER）：虚拟路由器通过虚拟IP对外提供服务，而在虚拟路由器内部同一时间只有一台物理路由器对外提供服务，这台提供服务的物理路由器被称为主路由器。一般情况下Master是由选举算法产生，它拥有对外服务的虚拟IP，提供各种网络功能，如：ARP请求，ICMP数据转发等。

**备份路由器**（BACKUP）：虚拟路由器中的其他物理路由器不拥有对外的虚拟IP，也不对外提供网络功能，仅接受MASTER的VRRP状态通告信息，这些路由器被称为备份路由器。当主路由器失败时，处于BACKUP角色的备份路由器将重新进行选举，产生一个新的主路由器进入MASTER角色，继续提供对外服务，整个切换对用户来说是完全透明的。

### ⑧ VRRP选举机制

VRRP路由器在运行过程中有三种状态： 

1. **Initialize状态： 系统启动后就进入Initialize，此状态下路由器不对VRRP报文做任何处理；** 
2. **Master状态；** 
3. **Backup状态；** 

**一般主路由器处于Master状态，备份路由器处于Backup状态。**

VRRP使用选举机制来确定路由器的状态，优先级选举： 
​	1.VRRP组中IP拥有者。如果虚拟IP地址与VRRP组中的某台VRRP路由器IP地址相同，则此路由器为IP地址拥有者，这台路由器将被定位主路由器。 
​	2.比较优先级。如果没有IP地址拥有者，则比较路由器的优先级，优先级的范围是0~255，优先级大的作为主路由器 。
​	3.比较IP地址。在没有Ip地址拥有者和优先级相同的情况下，IP地址大的作为主路由器。

如下图所示，虚拟IP为10.1.1.254，在VRRP组中没有IP地址拥有者，则比较优先级，很明显RB和RA的优先级要大于RC，则比较RA和RB的IP地址，RB的IP地址大。所以RB为组中的主路由器。 

![keepalived 选取机制](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\keepalived 选取机制.png)

### ⑨工作过程

​	**路由器使用VRRP 功能后，会根据优先级确定自己在备份组中的角色。优先级高的路由器成为Master 路由器，优先级低的成为Backup 路由器。Master 拥有对外服务的虚拟IP，提供各种网络功能，并定期发送VRRP 报文，通知备份组内的其他设备自己工作正常；Backup 路由器只接收Master 发来的报文信息，用来监控Master 的运行状态。当Master 失效时，Backup 路由器进行选举，优先级高的Backup 将成为新的Master 。**

​	**在抢占方式下，当Backup 路由器收到VRRP 报文后，会将自己的优先级与报文中的优先级进行比较。如果大于通告报文中的优先级，则成为Master 路由器；否则将保持Backup状态；**

​	**在非抢占方式下，只要Master 路由器没有出现故障，备份组中的路由器始终保持Master 或Backup 状态，Backup 路由器即使随后被配置了更高的优先级也不会成为Master 路由器；**

​	**如果Backup 路由器的定时器超时后仍未收到Master 路由器发送来的VRRP报文，则认为Master 路由器已经无法正常工作，此时Backup 路由器会认为自己是Master 路由器，并对外发送VRRP报文。备份组内的路由器根据优先级选举出Master 路由 器，承担报文的转发功能。**

### ⑩Keepalived 的工作原理

**Keepalived对服务器运行状态和故障隔离的工作原理**： 
Keepalived工作在TCP/IP参考模型的三层、四层、五层（物理层，链路层）： 
**网络层（3）：**Keepalived通过ICMP协议向服务器集群中的每一个节点发送一个ICMP数据包(有点类似与Ping的功能)，如果某个节点没有返回响应数据包，那么认为该节点发生了故障，Keepalived将报告这个节点失效，并从服务器集群中剔除故障节点。

**传输层（4）：**Keepalived在传输层里利用了TCP协议的端口连接和扫描技术来判断集群节点的端口是否正常，比如对于常见的WEB服务器80端口。或者SSH服务22端口，Keepalived一旦在传输层探测到这些端口号没有数据响应和数据返回，就认为这些端口发生异常，然后强制将这些端口所对应的节点从服务器集群中剔除掉。

**应用层（5）：**Keepalived的运行方式也更加全面化和复杂化，用户可以通过自定义Keepalived工作方式，例如：可以通过编写程序或者脚本来运行Keepalived，而Keepalived将根据用户的设定参数检测各种程序或者服务是否允许正常，如果Keepalived的检测结果和用户设定的不一致时，Keepalived将把对应的服务器从服务器集群中剔除。

keepalived运行时，会启动3个进程，分别为：core(核心进程)，check和vrrp 

- core：负责主进程的启动，维护和全局配置文件的加载； 
- check：负责健康检查 
- vrrp：用来实现vrrp协议

## 二、搭建

### ①基本架构图

![keepalived+lvs+Nginx 架构图](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\keepalived+lvs+Nginx 架构图.png)

### ②准备了4台虚拟机，用于测试

| 主机   | ip                | 作用                |
| ---- | ----------------- | ----------------- |
| 主机1  | **192.168.1.128** | Keepalived Master |
| 主机2  | **192.168.1.129** | Keepalived Backup |
| 主机3  | 192.168.1.130     | Nginx1            |
| 主机4  | 192.168.1.131     | Nginx2            |

虚拟机搭建可参考: [基于VirtualBox搭建Linux(CentOS 7)虚拟机环境（学习必备技能）](https://blog.lupf.cn/articles/2020/04/04/1586001434581.html)https://blog.lupf.cn/articles/2020/04/04/1586001434581.html

###③ 软件安装

在**192.168.1.128**及**192.168.1.129**上安装**keepalived**
在**192.168.1.130**及**192.168.1.131**上安装**nginx**

#### 3.1 KeepAlived 安装

**基础安装：**

```xml
yum install -y gcc
yum install -y openssl-devel
yum install -y libnl libnl-devel
yum install -y libnfnetlink-devel
yum install -y net-tools
yum install -y vim wget
```

**yum安装方式：**

```
yum install -y keepalived
```

**服务启动、重启、关闭**

这里只是测试服务是否能正常启动，后续还需要更改keepalived的配置之后才能正常的使用：

- 启动

  ```
  /etc/init.d/keepalived start
  ```

- 重启

  ```
  /etc/init.d/keepalived restart
  ```

- 暂停

  ```
  /etc/init.d/keepalived stop
  ```

#### 3.2 安装ipvsadm

用于查看lvs转发及代理情况的工具
只需要在192.168.1.128及192.168.1.129上安装即可

```
yum install ipvsadm -y
```

#### 3.3 nginx安装

只需要在192.168.1.130及192.168.1.131上安装nginx即可
请参考[基于CentOS 7 web服务环境搭建（包含JDK+Nginx+Tomcat+Mysql+Redis）](https://blog.csdn.net/lupengfei1009/article/details/77969514)中nginx的安装部分
或者
请参考[OpenResty(Nginx+Lua)高并发最佳实践](https://blog.csdn.net/lupengfei1009/article/details/86062644)直接安装OpenResty即可包含了nginx部分，**这里选用的是这种方式**

#### 3.4 防火墙（iptables）

- 停用firewalld

  ```
  systemctl stop firewalld.service
  systemctl disable firewalld.service
  systemctl mask firewalld.service
  123
  ```

- 安装iptables防火墙

  ```
  #查看iptables相关的安装包
  yum list iptables*
  #安装
  yum install -y iptables-services
  1234
  ```

##### 防火墙配置(方式一)

- 编辑防火墙，增加端口

  - keepalived服务器下的配置

    ​

    192.168.1.128和192.168.1.129下的添加以下配置

    ​

    vi /etc/sysconfig/iptables

    ```
    #允许vrrp多播心跳(如果防火墙开启，这里不配置这个，就会出现裂脑)
    -A INPUT -p vrrp -j ACCEPT
    #开启80端口的访问(如果防火墙开启，不配置这个，vip的80端口将无法正常访问)
    -I INPUT -p tcp --dport 80 -j ACCEPT
    1234
    ```

  - nginx服务器下配置

    ​

    192.168.1.130和192.168.1.131下的添加以下配置

    ​

    vi /etc/sysconfig/iptables

    ```
    #nginx默认监听的80端口 这里直接开启80端口的外网访问(不开启外网将无法正常反问对应服务器的nginx)
    -A INPUT -p tcp -m state --state NEW -m tcp --dport 80 -j ACCEPT
    12
    ```

  - 重启防火墙

    ```
    systemctl restart iptables.service
    1
    ```

##### 防火墙配置(方式二)

直接关闭所有防火墙，这种方式仅仅用于测试；不推荐用于实际项目

```
systemctl stop iptables.service
```

#### 3.5 配置nginx服务器(>>这里很重要!<<)

以下操作需要在角色为Web服务器的两台中进行
即**192.168.1.130和192.168.1.131这两台服务器上配置即可**

- 启动nginx服务

  确保nginx已经正常运行了

  ```
  ps -ef|grep nginx
  ```

编辑realserver脚本文件

两台机器都要搞

- 进入init文件夹
  cd /etc/init.d/

- 编辑脚本
  vim realserver
  添加以下脚本

  ```python
  #虚拟的vip 根据自己的实际情况定义
  SNS_VIP=192.168.1.200
  /etc/rc.d/init.d/functions
  case "$1" in
  start)
         ifconfig lo:0 $SNS_VIP netmask 255.255.255.255 broadcast $SNS_VIP
         /sbin/route add -host $SNS_VIP dev lo:0
         echo "1" >/proc/sys/net/ipv4/conf/lo/arp_ignore
         echo "2" >/proc/sys/net/ipv4/conf/lo/arp_announce
         echo "1" >/proc/sys/net/ipv4/conf/all/arp_ignore
         echo "2" >/proc/sys/net/ipv4/conf/all/arp_announce
         sysctl -p >/dev/null 2>&1
         echo "RealServer Start OK"
         ;;
  stop)
         ifconfig lo:0 down
         route del $SNS_VIP >/dev/null 2>&1
         echo "0" >/proc/sys/net/ipv4/conf/lo/arp_ignore
         echo "0" >/proc/sys/net/ipv4/conf/lo/arp_announce
         echo "0" >/proc/sys/net/ipv4/conf/all/arp_ignore
         echo "0" >/proc/sys/net/ipv4/conf/all/arp_announce
         echo "RealServer Stoped"
         ;;
  *)
         echo "Usage: $0 {start|stop}"
         exit 1
  esac
  exit 0
  ```

- 保存并设置脚本的执行权限

  ```
  chmod 755 /etc/init.d/realserver
  // 因为realserver脚本中用到了/etc/rc.d/init.d/functions，所以一并设置权限
  chmod 755 /etc/rc.d/init.d/functions
  ```

- 执行脚本

  ```
  service realserver start
  ```

  查看执行结果
  ip a
  如果看到以下效果，说明脚本已经执行成功了

  ![配置运行成功_](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\配置运行成功_.png)

#### 3.6 最后关键一步，配置keepalived

##### 1、配置MASTER

- 进入192.168.1.128服务器

  ```
  cd /etc/keepalived
  #备份默认的keepalived配置
  mv keepalived.conf keepalived-back.conf
  vim keepalived.conf
  1234
  ```

- 添加以下配置:

  ```
  global_defs {
     notification_email {
           edisonchou@hotmail.com
     }
     notification_email_from sns-lvs@gmail.com
     smtp_server 192.168.80.1
     smtp_connection_timeout 30
     router_id LVS_DEVEL  # 设置lvs的id，在一个网络内应该是唯一的
  }
  vrrp_instance VI_1 {
      state MASTER   #指定Keepalived的角色，MASTER为主，BACKUP为备 记得大写
      interface eno16777736  #网卡id 不同的电脑网卡id会有区别 可以使用:ip a查看
      virtual_router_id 51  #虚拟路由编号，主备要一致
      priority 100  #定义优先级，数字越大，优先级越高，主DR必须大于备用DR
      advert_int 1  #检查间隔，默认为1s
      authentication {   #这里配置的密码最多为8位，主备要一致，否则无法正常通讯
          auth_type PASS
          auth_pass 1111
      }
      virtual_ipaddress {
          192.168.1.200  #定义虚拟IP(VIP)为192.168.1.200，可多设，每行一个
      }
  }
  # 定义对外提供服务的LVS的VIP以及port
  virtual_server 192.168.1.200 80 {
      delay_loop 6 # 设置健康检查时间，单位是秒
      lb_algo rr # 设置负载调度的算法为wlc
      lb_kind DR # 设置LVS实现负载的机制，有NAT、TUN、DR三个模式
      nat_mask 255.255.255.0
      persistence_timeout 0
      protocol TCP
      real_server 192.168.1.130 80 {  # 指定real server1的IP地址
          weight 3   # 配置节点权值，数字越大权重越高
          TCP_CHECK {
          connect_timeout 10
          nb_get_retry 3
          delay_before_retry 3
          connect_port 80
          }
      }
      real_server 192.168.1.131 80 {  # 指定real server2的IP地址
          weight 3  # 配置节点权值，数字越大权重越高
          TCP_CHECK {
          connect_timeout 10
          nb_get_retry 3
          delay_before_retry 3
          connect_port 80
          }
       }
  }
  ```

##### 2、配置BACKUP

- 进入192.168.1.129服务器

  ```
  cd /etc/keepalived
  #备份默认的keepalived配置
  mv keepalived.conf keepalived-back.conf
  vim keepalived.conf
  ```

- 添加以下配置:

  ```
  global_defs {
     notification_email {
           edisonchou@hotmail.com
     }
     notification_email_from sns-lvs@gmail.com
     smtp_server 192.168.80.1
     smtp_connection_timeout 30
     router_id LVS_DEVEL  # 设置lvs的id，在一个网络内应该是唯一的
  }
  vrrp_instance VI_1 {
      state BACKUP #指定Keepalived的角色，MASTER为主，BACKUP为备 记得大写
      interface eno16777736  #网卡id 不同的电脑网卡id会有区别 可以使用:ip a查看
      virtual_router_id 51  #虚拟路由编号，主备要一致
      priority 50  #定义优先级，数字越大，优先级越高，主DR必须大于备用DR
      advert_int 1  #检查间隔，默认为1s
      authentication {   #这里配置的密码最多为8位，主备要一致，否则无法正常通讯
          auth_type PASS
          auth_pass 1111
      }
      virtual_ipaddress {
          192.168.1.200  #定义虚拟IP(VIP)为192.168.1.200，可多设，每行一个
      }
  }
  # 定义对外提供服务的LVS的VIP以及port
  virtual_server 192.168.1.200 80 {
      delay_loop 6 # 设置健康检查时间，单位是秒
      lb_algo rr # 设置负载调度的算法为wlc
      lb_kind DR # 设置LVS实现负载的机制，有NAT、TUN、DR三个模式
      nat_mask 255.255.255.0
      persistence_timeout 0
      protocol TCP
      real_server 192.168.1.130 80 {  # 指定real server1的IP地址
          weight 3   # 配置节点权值，数字越大权重越高
          TCP_CHECK {
          connect_timeout 10
          nb_get_retry 3
          delay_before_retry 3
          connect_port 80
          }
      }
      real_server 192.168.1.131 80 {  # 指定real server2的IP地址
          weight 3  # 配置节点权值，数字越大权重越高
          TCP_CHECK {
          connect_timeout 10
          nb_get_retry 3
          delay_before_retry 3
          connect_port 80
          }
       }
  }
  ```

##### 3、配置注意项

- **router_id**
  后面跟的自定义的ID在同一个网络下是一致的
- **state**
  state后跟的MASTER和BACKUP必须是大写；否则会造成配置无法生效的问题
- **interface**
  网卡ID；**这个值不能完全拷贝我的配置，要根据自己的实际情况来看**，可以使用以下方式查询
  ip a
  ![img](https://img-blog.csdnimg.cn/20190117165709448.?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x1cGVuZ2ZlaTEwMDk=,size_16,color_FFFFFF,t_70)
- **priority**
  主备优先级
  MASTER中配置的priority必须比BACKUP大；差值最好>=50
- **authentication**
  主备之间的认证方式
  一般使用PASS即可；主备的配置必须一致；否则无法通讯，会导致裂脑；密码不能大于8位
- **virtual_ipaddress**
  配置的VIP；允许配置多个

#### 3.7 启动Keepalived

在192.168.1.128和192.168.1.129下分别执行以下指令启动keepalived

```
/etc/init.d/keepalived start
```

![启动成功](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\启动成功.jpg)

**检查主keepalived 启动后的配置情况**

```
ip a
// 如果网卡下出现192.168.1.200（VIP）说明主已经启动成功
```

![检查网卡设置情况](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\检查网卡设置情况.png)

检查备keepalived 启动后的配置情况

```
ip a
```

备服务器的网卡下

没有出现

192.168.1.200（VIP）的ip，说明备服务正常

注:如果这里也出现了VIP，那么说明裂脑了，需要检查防火墙是否配置正确；是否允许了vrrp的多播通讯

![是否出现脑裂问题](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\是否出现脑裂问题.png)

#### 3.8 LVS管理工具-ipvsadm

##### 1、安装

```
yum install ipvsadm -y
```

##### 2、查看统计

- 查看当前配置的虚拟服务和各个RS的权重

  ```
  ipvsadm -Ln
  ```

- 查看当前ipvs模块中记录的连接（可用于观察转发情况）

  ```
  ipvsadm -lnc
  ```

- 查看ipvs模块的转发情况统计

  ```
  ipvsadm -Ln --stats | --rate
  1
  ```

##### 3、lvs超时配置

- 查看lvs的超时时间

  ```
  ipvsadm -L --timeout
  ```

##### 4、优化连接超时时间

```
ipvsadm --set 1 10 300
```

- 这里的TCP的连接超时时间最好和keepalived中的persistence_timeout超时时间保持一致；persistence_timeout的超时时间表示指定时间内，同ip的请求会转发到同一个服务；
- 更多ipvsadm的操作请参考以下文章
  <https://www.cnblogs.com/lipengxiang2009/p/7353373.html>

### ④测试

#### 1、正常代理转发

使用我linux虚拟机的windows宿主机进行测试

- 测试vip

  ```
  ping 192.168.1.200
  ```

  测试vip监听的端口

```
telnet 192.168.1.200 80
```

请求虚拟IP查看转发的服务

```
192.168.1.200
```

#### 2、KeepAlived高可用测试

- 停掉主keepalived

  ```
  /etc/init.d/keepalived stop
  ```

![测试](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\测试.png)

**vip漂移至备服务器**

![VIP 漂移](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\VIP 漂移.png)

- 此时网页访问:192.168.1.200依然能够正常访问；却分发依然正常
- 重启主keepalived
  主服务恢复之后；vip又会自动漂移回主服务

#### 3、LVS监控真实服务测试

查看最新的虚拟ip对应的RealServer的情况

```
ipvsadm -l
```

![lvs 测试](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\lvs 测试.jpg)

- 可以看出192.168.1.130和192.168.1.131两台正式服务都还在
- 测试停掉192.168.1.130

![测试lvs关闭一台](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\测试lvs关闭一台.jpg)

**再次查看虚拟ip对应的RealServer的情况**

![查看lvs 运行状态](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\查看lvs 运行状态.jpg)

- 可以看出192.168.1.130这台已经挂掉的服务器已经被移除了
- 测试访问虚拟ip
- 所有的访问都只会转发到131的真实服务器
- 恢复192.168.1.130
  lvs又会自动监控并加入192.168.1.130

![重启一台lvs](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\重启一台lvs.jpg)

![查看2](C:\Users\Administrator\Desktop\笔记\java网络高并发 高性能知识\查看2.jpg)

#### 常见问题

- 裂脑
  主备keepalived服务器同时出现了VIP；导致vip无法正常使用
  常见原因为防火墙配置所致导致多播心跳失败
- vip能ping通，但是vip监听的端口不通
  - 第一个原因:nginx1和nginx2两台服务器的服务没有正常启动
  - 第二个原因:请参考上面**Nginx服务器**那一大项中所说的配置，可能没有配置好
- vip ping不通
  核对是否出现裂脑
  核对keepalived的配置是否正确

感谢以下资料对我的帮助
【参考资料】
[Keepalived源码安装](http://blog.51cto.com/xiaozhagn/2058174)
[高可用解决方案–keepalived](http://blog.51cto.com/13570193/2161637)
[Centos7.2下基于Nginx+Keepalived搭建高可用负载均衡(一.基于Keepalived搭建HA体系)](https://www.cnblogs.com/GreedyL/p/7519969.html)
[keepalived介绍和配置](http://blog.51cto.com/8844414/2171226)
[【大型网站技术实践】初级篇：借助LVS+Keepalived实现负载均衡](https://www.cnblogs.com/edisonchou/p/4281978.html)

【解决问题参考资料】
[咨询个lvs的问题，有时访问VIP会出现SYN_RECV](http://zh.linuxvirtualserver.org/node/2621)
[怎么样让 LVS 和 realserver 工作在同一台机器上](http://www.linuxde.net/2012/05/10652.html)
[两台服务器既做LVS主备又做realserver的配置方法](https://blog.csdn.net/wzyzzu/article/details/47277533)
[keepalived+lvs无法访问vip或访问超时](https://blog.csdn.net/Gmoon23/article/details/75379863?utm_source=blogxgwz6)
[lvs中ipvsadm的ActiveConn和InActConn的深入理解](http://blog.51cto.com/tonychiu/950822)











