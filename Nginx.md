# 一、Nginx

## 1、Nginx 相对于 Apache httpd 的优点

​	①、轻量级，同样起web 服务，比 Apache 占用更少的内存及资源。

​	②、抗并发，Nginx 处理请求时异步非阻塞的，而Apache 则是阻塞型的，在高并发下Nginx 能保持资源低消耗高性能。

​	③、高度模块化的设计，编写模块相对简单。

## 2、Apache Httpd 相对于Nginx 优点

​	①、rewrite、比Nginx 的rewrite 强大。

​	②、模块超多，基本想到的都可以找到。

​	③、bug 少，Nginx 的bug 相对较多。

## 3、 最核心的区别

​	①、Nginx 配置简洁，Apache 复杂。

​	②、最核心的区别在于Apache 是同步多进程模型，一个连接对应一个进程。Nginx 是异步的，多个连接（万级别）可以对应一个进程。

## 4、Nginx 简单介绍

​	Nginx 是一个高性能的Http 和 反向代理服务器，也是一个 IMAP、POP3、SMTP 代理服务器。

官方文档：<http://tengine.taobao.org/nginx_docs/cn/docs/http/ngx_http_core_module.html>

### 4.1 Nginx 的进程模型

#### ① master 进程 ：主进程

#### ② worker 进程 ： 工作进程

![5.Nginx 进程模型](C:\Users\Administrator\Desktop\笔记\Java直通车项目\5.Nginx 进程模型.png)

### 4.2 常用命令

```
信号：
	./nginx -s stop                   # 暴力关闭，直接关闭
	./nginx -s quit                   # 优雅的关闭Nginx，如果没有正在工作的进程，则直接关闭，如果有工作的进程，则不再接受新的请求，直到所有工作进程执行完后，在关闭
	./nginx -s reload                 # 重新加载配置文件，并启动
	./nginx -t                        # 配置文件有修改，可以检查语法是否正确
```

### 4.3 Nginx事件处理

![5.1 Nginx 事件处理](C:\Users\Administrator\Desktop\笔记\Java直通车项目\5.1 Nginx 事件处理.png)

### 4.4 Nginx 配置跨域

```xml
server {
	# 允许跨域请求的域，*代表所有
	add_header 'Access-Control-Allow-Origin' *;
	# 允许带上cookie 请求
	add_header 'Access-Control-Allow-Credentials' 'true';
	# 允许请求的方法，比如 GET、POST、PUT、DELETE
	add_header 'Access-Control-Allow-Methods' *;
	# 允许请求的header
	add_header 'Access-Control-Allow-Headers' *;
}
```

### 4.5 Nginx 日志切割---定时

​	随着时间的推移，文件会越来越大，我们需要将日志文件切割成多分不同的小文件作为日志。

```nginx
1、创建一个shell 可执行文件，cut_my_log.sh 内容为：

#!/bin/bash
LOG_PATH = "/var/log/nginx/"
RECORD_TIME=#(date -d "yesterday" +%Y-%m-%d+%H)
PID=/var/run/nginx/nginx.pid
mv ${LOG_PATH}/access.log ${LOG_PATH}/access.${RECORD_TIME}.log
mv ${LOG_PATH}/error.log ${LOG_PATH}/error.${RECORD_TIME}.log

# 向Nginx主进程发送信号，用于重新打开日志文件
kill -USR1 `cat $PID`

2、为cut_my_log.sh 添加可执行的权限
chmod +x cut_my_log.sh

```

1、安装定时任务

yum install crontabs

2. crontab -e 编辑并且添加一行新的任务；

*/1 * * * * /usr/local/nginx/sbin/cut_my_log.sh

 3、启动定时任务

service crond restart

4、 查看任务列表

crontab -l 

5、 编辑任务

crontab -e 

## 5、Nginx 安装

​	① 下载地址：<http://tengine.taobao.org/download.html>

```java
gcc opemssl-devel pcre-devel zlib-devel
安装： yum install gcc openssl-devel pcre-devel zlib-devel
```

​	②Nginx 下载地址：http://nginx.org

​	③将上传的tar 包解压缩，一般都安装在/usr/local 目录下。

​	④编译安装

```java
./configure --prefix=/安装路径
make && make install
```

## 6、启动服务

​	①脚本自启动，拷贝下面配置文件，到Nginx启动脚本内容复制到	/etc/init.d/nginx 目录下的文件里，如果没有需要手动创建

```java
#!/bin/bash
#
# nginx - this script starts and stops the nginx daemon
#
# chkconfig: - 85 15
# description: Nginx is an HTTP(S) server, HTTP(S) reverse
# proxy and IMAP/POP3 proxy server
# processname: nginx
# config: /etc/nginx/nginx.conf
# config: /etc/sysconfig/nginx
# pidfile: /var/run/nginx.pid

# Source function library.
. /etc/rc.d/init.d/functions

# Source networking configuration.
. /etc/sysconfig/network

# Check that networking is up.
[ "$NETWORKING" = "no" ] && exit 0

TENGINE_HOME="/usr/local/Tengine/"     #这个是你安装tengine目录
nginx=$TENGINE_HOME"sbin/nginx"
prog=$(basename $nginx)

NGINX_CONF_FILE=$TENGINE_HOME"conf/nginx.conf"

[ -f /etc/sysconfig/nginx ] && /etc/sysconfig/nginx

lockfile=/var/lock/subsys/nginx

start() {
    [ -x $nginx ] || exit 5
    [ -f $NGINX_CONF_FILE ] || exit 6
    echo -n $"Starting $prog: "
    daemon $nginx -c $NGINX_CONF_FILE
    retval=$?
    echo
    [ $retval -eq 0 ] && touch $lockfile
    return $retval
}

stop() {
    echo -n $"Stopping $prog: "
    killproc $prog -QUIT
    retval=$?
    echo
    [ $retval -eq 0 ] && rm -f $lockfile
    return $retval
    killall -9 nginx
}

restart() {
    configtest || return $?
    stop
    sleep 1
    start
}

reload() {
    configtest || return $?
    echo -n $"Reloading $prog: "
    killproc $nginx -HUP
    RETVAL=$?
    echo
}

force_reload() {
    restart
}

configtest() {
    $nginx -t -c $NGINX_CONF_FILE
}

rh_status() {
    status $prog
}

rh_status_q() {
    rh_status >/dev/null 2>&1
}

case "$1" in
start)
    rh_status_q && exit 0
    $1
;;
stop)
    rh_status_q || exit 0
    $1
;;
restart|configtest)
    $1
;;
reload)
    rh_status_q || exit 7
	$1
;;
force-reload)
    force_reload
;;
status)
    rh_status
;;
condrestart|try-restart)
    rh_status_q || exit 0
;;
*)

echo $"Usage: $0 {start|stop|status|restart|condrestart|try-restart|reload|force-reload|configtest}"
exit 2
esac

```

②修改可执行权限

​	chmod 777 nginx

③启动服务

```java
service Nginx start 启动服务
service Nginx stop 停止
service Nginx status 状态
service Nginx reload 动态重载配置文件
```

## 7、Nginx 配置文件

![5.Nginx 配置结构](C:\Users\Administrator\Desktop\笔记\Java直通车项目\5.Nginx 配置结构.png)

### ①、Nginx 文件结构

```nginx
...              #全局块

events {         #events块
   ...
}

http      #http块
{
    ...   #http全局块
    server        #server块
    { 
        ...       #server全局块
        location [PATTERN]   #location块
        {
            ...
        }
        location [PATTERN] 
        {
            ...
        }
    }
    server
    {
      ...
    }
    ...     #http全局块
}
```

- 1、**全局块**：配置影响nginx全局的指令。一般有运行nginx服务器的用户组，nginx进程pid存放路径，日志存放路径，配置文件引入，允许生成worker process数等。
- 2、**events块**：配置影响nginx服务器或与用户的网络连接。有每个进程的最大连接数，选取哪种事件驱动模型处理连接请求，是否允许同时接受多个网路连接，开启多个网络连接序列化等。( 开发总数是 worker_processes 和 worker_connections 的乘积，即 max_clients = worker_processes * worker_connections，在设置了反向代理的情况下，max_clients = worker_process * worker_connections /4 为什么上面反向代理要除以4，应该说是一个经验值根据以上条件，正常情况下 Nginx Server 可以应付的最大连接数为：4 * 8000 = 32000 worker_connections 值的 设置跟物理内存大小有关，因为并发受IO约束，max_clients 的值须小于系统可以打开的最大文件数），
- 3、**http块**：可以嵌套多个server，配置代理，缓存，日志定义等绝大多数功能和第三方模块的配置。如文件引入，mime-type定义，日志自定义，是否使用sendfile传输文件，连接超时时间，单连接请求数等。
- 4、**server块**：配置虚拟主机的相关参数，一个http中可以有多个server。
- 5、**location块**：配置请求的路由，以及各种页面的处理情况。

```nginx
########### 每个指令必须有分号结束。#################
#user administrator administrators;  #配置用户或者组，默认为nobody nobody。
#worker_processes 2;  #允许生成的进程数，默认为1
#pid /nginx/pid/nginx.pid;   #指定nginx进程运行文件存放地址
error_log log/error.log debug;  #制定日志路径，级别。这个设置可以放入全局块，http块，server块，级别以此为：debug|info|notice|warn|error|crit|alert|emerg
events {
    accept_mutex on;   #设置网路连接序列化，防止惊群现象发生，默认为on
    multi_accept on;  #设置一个进程是否同时接受多个网络连接，默认为off
    #use epoll;      #事件驱动模型，select|poll|kqueue|epoll|resig|/dev/poll|eventport
    worker_connections  1024;    #最大连接数，默认为512
}
http {
    include       mime.types;   #文件扩展名与文件类型映射表
    default_type  application/octet-stream; #默认文件类型，默认为text/plain
    #access_log off; #取消服务日志    
    log_format myFormat '$remote_addr–$remote_user [$time_local] $request $status $body_bytes_sent $http_referer $http_user_agent $http_x_forwarded_for'; #自定义格式
    access_log log/access.log myFormat;  #combined为日志格式的默认值
    sendfile on;   #允许sendfile方式传输文件，默认为off，可以在http块，server块，location块。
    sendfile_max_chunk 100k;  #每个进程每次调用传输数量不能大于设定的值，默认为0，即不设上限。
    keepalive_timeout 65;  #连接超时时间，默认为75s，可以在http，server，location块。

    upstream mysvr {   
      server 127.0.0.1:7878;
      server 192.168.10.121:3333 backup;  #热备
    }
    error_page 404 https://www.baidu.com; #错误页
    server {
        keepalive_requests 120; #单连接请求上限次数。
        listen       4545;   #监听端口
        server_name  127.0.0.1;   #监听地址       
        location  ~*^.+$ {       #请求的url过滤，正则匹配，~为区分大小写，~*为不区分大小写。
           #root path;  #根目录
           #index vv.txt;  #设置默认页
           proxy_pass  http://mysvr;  #请求转向mysvr 定义的服务器列表
           deny 127.0.0.1;  #拒绝的ip
           allow 172.18.5.54; #允许的ip           
        } 
    }
}
```

上面是nginx的基本配置，需要注意的有以下几点：

### ②、几个常见配置项：

- 1.$remote_addr 与 $http_x_forwarded_for 用以记录客户端的ip地址；
- 2.$remote_user ：用来记录客户端用户名称；
- 3.$time_local ： 用来记录访问时间与时区；
- 4.$request ： 用来记录请求的url与http协议；
- 5.$status ： 用来记录请求状态；成功是200；
- 6.$body_bytes_s ent ：记录发送给客户端文件主体内容大小；
- 7.$http_referer ：用来记录从那个页面链接访问过来的；
- 8.$http_user_agent ：记录客户端浏览器的相关信息；

2、惊群现象：一个网路连接到来，多个睡眠的进程被同时叫醒，但只有一个进程能获得链接，这样会影响系统性能。

3、每个指令必须有分号结束。

### 4、可以打开的文件句柄数是多少：

```nginx
cat /proc/sys/fs/file-max
输出：97320

并发连接总数小于系统可以打开的文件句柄总数，这样就在操作系统可以承受的范围之内，所以，worker_connections 的值需根据worker_processes 进程数目和系统可以打开的最大文件总数进行适当地进行设置，使得并发总数小于操作系统可以打开的最大文件数目。

--其实质也就是根据主机的物理CPU 和内存进行配置
当然，理论上的并发总数可能会和实际有所偏差，因为主机还有其他的工作进程需要消耗系统资源。

查看系统限制： ulimit -a 
```

### 5、sendfile 

```java
sendfile on; #开启高效文件传输模式，sendfile 指令指定Nginx 是否调用sendfile 函数来输出文件，对于善通应用设为ON，如果来进行下载等应用磁盘IO 重负载应用，可设置为off，以平衡磁盘与网络I/O 处理速度，降低系统的负载，注意：如果图片显示不正常把这个设为off、

sendfile() 还能够用来在两个文件夹之间移动数据
tcp_nopush 在liunx /unix 系统中优化tcp 数据传输，仅在sendfile 开启时才有效。
autoindex on； 开启目录列表访问，适合下载服务器，默认是关闭。
keepalive_timeout 120 ，长链接超时时间 ，单位是秒
```

### 6、虚拟主机

​	虚拟主机是一种特殊的软硬件技术，它可以将网络上的每一台计算机分成多个虚拟机主机，每个虚拟主机可以独立对外提供www服务，这样就可以实现一台主机对外提供多个web 服务，每个虚拟主机之间是独立的，互不影响的。可以通过配置多个server 来实现。

```
通过Nginx 可以实现虚拟主机的配置： Nginx 支持三种类型的虚拟主机配置
	1、基于ip 的虚拟主机，（一块主机绑定多个ip地址）
	2、基于域名的虚拟主机（servername）
	3、基于端口的虚拟主机（listen 如果不写ip端口模式）

http {
   server {
       #表示一个虚拟主机
   }
}
```

### 7、gzip

```
gzip ON ： 开启gizp 压缩输出
gzip_min_length 1k; 设置允许压缩的页面最小字节数，页面字节数从header 头得content-length 中进行获取，默认值是0，，不管页面多大都压缩，建议设置成大于2k 的字节数，小于2k 可能会越压越大。

gzip_buffers 4 16k; 设置系统获取几个单位的缓存用于存储gzip 的压缩结果数据流，例如 4 4k 代表以4K 为单位，按照原始数据大小以4k 为单位的4倍申请内存， 4 8k 代表以8k 为单位，按照原始数据大小以8k 为单位的4倍申请内存。 如果没有设置，默认值是申请跟原始数据相同大小的内存空间去存储gzip 压缩结果。

gzip_http_version 1.0; 压缩版本（默认1.1，前端如果是squid2.5 使用1.9）、
gzip_comp_level 2; 压缩级别，1-10 数字越大压缩的越好，也越占用CPU时间。

```

### 8、location匹配规则

```nginx
映射/ 虚拟目录
location = /{
	[configuration A]
}

location / {
	[configuration B]
}

location /documents/ {
	[configuration C]
}

location ^~ /images/ {
  [configuration D ]
}

location ~* \.(gif|jpg|jpeg)$ {
  [configuration E]
}

location [= | - | -* | ^-]url {...}
location URI {} 对当前路径及子路径下的所有对象都生效。
location = URI {} 注意URL 最好为具体路径，精确匹配指定的路径，不包括子路径，因此，只对当前资源生效。
location ~ URI {}   location ~* URI {} 模式匹配URL，此处的URL可使用正则表达式，~区分字符大小写，~*不区分大小写。
location ^~ URI {} 禁用正则表达式。
优先级 = > ^~ > - > ~* > |/die/

location 配置原则
location 的执行逻辑跟location 的编辑顺序无关。“普通location” 的匹配规则是“最大前缀”，因此 “普通location”的确与location编辑顺序无关。
```

### 9、用户认证访问

模块ngx_http_auth_basic_module 允许使用 “http 基本认证”协议验证用户名和密码来限制堆资源的访问。

```java
location ~(.*)\.avi$ {
  auth_basic "closed site";
  auth_basic_user_file conf/users;
}
```

**http-tools 下载**

```java
yum install httpd
htpasswd -c -d /usr/local/users houyachao
```

### 10、nginx 访问状态监控

```java
location /basic_status {
  stub_status on;
}
```



## 8、Nginx开机启动

chkconfig --list

chkconfig --add nginx

chkconfig nginx on

## 9、Nginx 反向代理

​	通常的代理服务器，只用于代理内部网络堆Internet 的链接请求，客户机必须指定代理服务器，并将本来要直接发送到web 服务器上的http 请求发送到代理服务器中由代理服务器向Internet 上的web 服务器发起请求，最终达到客户机上网的目的。

​	反向代理方式是指以代理服务器来接受Internet 上的链接请求，然后将请求转发给内部网络上的服务器，并将服务器上得到的结果返回给Internet 上请求链接的客户端，此时代理服务器对外就表现为一个反向代理服务器。

```java
Proxy_pass http://39.100.93.90/

301重定向问题：  是客户端发送一个请求，Nginx拦截到这个请求到Proxy_pass 指定的位置，然后请求完后，服务器返回给Nginx一个请求302重定向链接给Nginx，Nginx 将这个连接返回给客户端，客户端拿到这个连接会去再去转向直接调用服务器端。（如果访问的不是https 的链接，而是http的链接，就不会出现302重定向问题，因为客户端发送一个请求到Nginx ，Nginx 去访问服务器，最后服务器直接将结果返回给客户端，省去了返回给Nginx这一个过程）。

upstream 反向代理配合upstream 使用。

upstream httpds {
	server 30.100.98.90:80;   //tomcat 
	server 30.100.98.91:81;   
}
```

### ①weight  (权重)

​	指定轮询几率，weight 和 访问比率成正比，用户后端服务器性能不均的情况。

```java
upstream httpds {
  server 127.0.0.1:8081    weight=10 down;    //weight=10之间不能有空格
  server 127.0.0.1:8082    weight=1;
  server 127.0.0.1:8083    weight=1 backup;
}
```

down :  表示当前的server 暂时不参与负载。

weight：默认为1，weight 越大，负载的权重就越大，就被打倒服务器的次数也就越多。

backup：其它所有的非backup 机器down或者忙的时候，请求backup机器。

### ②max_conns

​	可以根据服务的好坏来设置最大连接数，防止挂掉，比如1000，我们可以设置为800；

```java
 xxxxxxxxxx upstream httpds {  
   server 127.0.0.1:8081    weight=10 down;    //weight=10之间不能有空格  
   server 127.0.0.1:8082    weight=1 max_conns=800; 
 }
```

### ③max_fails 、fail_timeout

max_fails ：失败多少次认为主机已挂掉则踢出，公司资源少的一般设置2-3次，多的话就设置1次。

max_fails=3  fail_timeout=30s 代表在30秒内请求某一应用失败3次，认为该应用宕机，后等待30秒，这期间内不会再把心情求发送到宕机应用，而是直接发送正常的那一台，时间到后再有请求进来继续尝试连接宕机应用且仅尝试1次，如果还是失败，则继续等待30秒。。。依次循环，直到恢复。

```java
 upstream httpds { 
   server 127.0.0.1:8081    weight=10 down;    //weight=10之间不能有空格 
   server 127.0.0.1:8082    weight=1  max_fails=1  fail_timeout=20;  
   server 127.0.0.1:8083    weight=1 backup;
 }
```

### ④负载均衡算法

①轮询+weight

②ip_hash （算法）， 是根据  ip地址  “。” 去做分割，然后取出前三项进行hash的。

③url_hash

④least_conn

⑤least_time

### ⑤健康检查模块

配置一个status的location

```java
location /status {
  check_status;
}
在upstream 配置如下：
check interbal=3000 rise=2 fall=5 timeout=1000 type=http;
check_http_send "HEAD / HTTP/1.0\r\n\r\n";
check_http_expect_alive http_2xx http_3xx;
```

### ⑥session 共享

①Memcached

②安装 libevent

③安装memcached

 	可以用yum 安装方式 yum -y install memcached

④启动memcached

​	memcached -d -m 128 -u root -l 39.100.98.90 -p 112211 -c 256 -P /tmp/memcached.pid 

​	memcached-tool 192.168.2.51  112211

​	参数解释： -d 后台启动，-m：缓存大小，-p：端口，-l ：IP，-p：服务器启动后的系统进程ID，存储的文件， -u：服务器启动时以哪个用户名作为管理用户。

### ⑦Tomcat 配置

到Tomcat 的lib 下，每个Tomcat 里面的context.xml 中加入。

```java
<Manager className="de.javakaffee.web.msm.MemcachedBackupSessionManager"
  memcachedNodes="n1:39.100.98.90:11211"
  sticky="false"
  lockingMode="auto"
  sessionBackAsync="false"
  requestUriIgnorePattern=".*\.(ico|png|gif|jpg|css|js)$"
  sessionBackupTimeout="1000"
  transcoderFactoryClass="de.javakaffee.web.msm.serializer.kryo.kryoTranscoderFactory"
  />
```

### ⑧http_proxy 本地磁盘缓存

```nginx
proxy_cache_path /path/to/cache levels=1:2 keys_zone=my_cache:10m max_size=10g inactive=60m use_temp_path=off
server {
	set $upstream http://ip:port
    location / {
    	proxy_cache my_cache;    # 开启并使用缓存
    	proxy_pass $upstream; 
    }
}

/path/to/cache	#本地路径，用来设置Nginx 缓存资源的存放地址。
levels	#默认所有缓存文件都放在同一个/path/to/cache 下，但是会影响缓存的性能，因此通常会在/path/to/cache下面建立了子目录用来存放不同的文件，假设levels=1:2，Nginx 为将要缓存的资源生成的key 为f4cd0f.........，那么key 的最后一位0，以及倒数第2-3为作为两级的子目录，也就是该资源最终会被缓存到/path/to/cache/0/6d目录中。
key_zone	#在共享内存中设置一块存储区域来存放缓存的key 和 metadata（类似使用次数），这样ngin可以快速判断一个Request 是否命中缓存，1m 可以存储8000个key，10m 可以存储80000个key,
max_size	#最大cache 空间，如果不指定，会使用掉所有disk space ，当达到配额后，会删除最少使用的cache文件。
inactive	#未被访问文件在缓存中保留时间，本配置中如果60分钟未被访问则不论状态是否为expired，缓存控制程序会删掉文件，inactive 默认是10分钟，需要注意的是，inactive 和 expired 配置项的含义是不同的，expired 只是缓存过期，但不会被删除，，inactive 是删除指定时间内未被访问的缓存文件。
use_temp_path 	#如果设置off，则Nginx 会将缓存文件指定的Cahce文件中，而不是使用temp_path存储，official 建议为off, 避免文件在不同文件系统中不必要的拷贝。
```

## 10、虚拟目录

```java
location /www {
  alias /var/data/www1;
  index index.html index.htm a.html;
}
```

## 11、自动索引

```java
location /art {
  alias /bar/data/www1/;
  autoindex on;
}
```

## 12、动静分离

```java
location / {
  proxy_pass http://30.100.93.90 8080;
}

location ~ .*\.(git|jpg|jpeg|png|bmp|swf|html|htm|css|js)$ {
  root /var/data/www1/;
}
```

## 13、时间问题

```java
service ntpd status
```

## 14、SSL

​	SSL 能够帮助系统在客户端和服务器之间建立一条安全通信通道，SSL 安全协议是由Netscape Communication 公司 在1994年开发的。SSL依赖于加密算法 ，极难窃听，有高效的安全性，因此SSL协议已经成为网络上最常用的安全保密通信协议，该安全协议主要用于用来提供堆用户和服务器的认证，对传送的数据进行加密和隐藏，确保数据在传送中不被改变，即数据的完整性，现已成为该领域中全球化的标准。

**需要在服务器上配置SSL证书，也就是域名需要配置的。**



### 14.1、SSL 和 TLS

所有的X.509 证书包含以下数据：

① X.509 版本号：指出该证书使用了那种版本的S.509的标准，版本号会影响证书中的一些特定的信息，目前的版本是3.

②证书持有人的公钥：包含证书持有人的公钥，算法（指明秘钥属于那种密码系统）的标识符和其他相关的密钥参数。

③证书的序列号：由CA 给予每一个证书分配的唯一的数字型编号，当证书被取消时，实际上是将此证书序列号放入由CA签发的CRL（CRL 证书作废表，或证书黑名单）中，这也是序列号唯一的原因。

④主题信息：证书持有人唯一的标识符.

⑤证书的有效期: 证书起始日期和时间以及终止日期和时间,指明证书在这两个时间内有效.

⑥认证机构：证书发布者，是签发该证书的实体唯一的CA的X.509名字，使用该证书意味着信任签发证书的实体。

⑦发布者的数字签名：这是使用发布者私钥生成的签名，以确保这个证书在发放之后没有被篡改过。

⑧签名算法标识符：用来指定CA签署证书时使用的签名算法，算法标识符用来指定CA 签发证书所使用的公开密钥算法和HASH算法。

### 14.2、抓包工具

**青花瓷：**https://www.charlesproxy.com/latest-release/download.do

### 14.3 对称加密与非对称加密

非对称加密算法需要两个密钥：公开密钥 和 私有密钥，公开密钥与私有密钥是一对，如果用公开密钥对数据进行加密，只有用对应的私有密钥才能解密，如果用私有密钥对数据进行加密，那么只有用对应的公开密钥才能解密，因为加密和解密使用的是两个不同的密钥，所以这种算法叫做非对称加密算法。

**CA**

​	CA 是负责签发证书，认证证书，管理已颁发证书的机关，它要制定政策和具体步骤来验证，识别用户身份，并对用户证书进行签名，以确保证书持有者的身份和公钥的拥有权。

**证书的种类：**

![证书的种类](C:\Users\Administrator\Desktop\笔记\Nginx\证书的种类.png)

### 15.6、OPenSSL 自签名

       	1. key 私钥 = 明文 自己生成的
        	2. csr 公钥 = 由私钥生成
         	3. crt 证书 = 公钥 + 签名



下载

http://slproweb.com/prodducts/Win32OpenSSL.html

**生成私钥**

```
genrsa
```

控制台输入genrsa, 会默认生成一个2048位的私钥。  需在 /bin 目录下 .exe 文件 cmd

```
openssl genras -des3 -out server.key 1024
```

**由私钥生成证书**

```
openssl req -new -key c:/dev/my.key -out c:/dev/my.csr
openssl req -new -key server.key -out server.csr
```

 查看证书![签名](C:\Users\Administrator\Desktop\笔记\Nginx\签名.png)