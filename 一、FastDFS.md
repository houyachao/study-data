#  一、FastDFS

## 1、简介

​	[FastDFS](https://code.google.com/p/fastdfs/)是一个开源的轻量级分布式文件系统，**由跟踪服务器（tracker server）、存储服务器（storage server）和客户端（client）三个部分组成，**主要解决了海量数据存储问题，特别适合以中小文件（建议范围：4KB < file_size <500MB）为载体的在线服务。

## 2、Storage server

Storage server（后简称storage）以组（卷，group或volume）为单位组织，一个group内包含多台storage机器，数据互为备份，存储空间以group内容量最小的storage为准，所以建议group内的多个storage尽量配置相同，以免造成存储空间的浪费。

以group为单位组织存储能方便的进行应用隔离、负载均衡、副本数定制（group内storage server数量即为该group的副本数），比如将不同应用数据存到不同的group就能隔离应用数据，同时还可根据应用的访问特性来将应用分配到不同的group来做负载均衡；缺点是group的容量受单机存储容量的限制，同时当group内有机器坏掉时，数据恢复只能依赖group内地其他机器，使得恢复时间会很长。

group内每个storage的存储依赖于本地文件系统，storage可配置多个数据存储目录，比如有10块磁盘，分别挂载在/data/disk1-/data/disk10，则可将这10个目录都配置为storage的数据存储目录。

storage接受到写文件请求时，会根据配置好的规则（后面会介绍），选择其中一个存储目录来存储文件。为了避免单个目录下的文件数太多，在storage第一次启动时，会在每个数据存储目录里创建2级子目录，每级256个，总共65536个文件，新写的文件会以hash的方式被路由到其中某个子目录下，然后将文件数据直接作为一个本地文件存储到该目录中。

## 3、Tracker server

Tracker是FastDFS的协调者，负责管理所有的storage server和group，每个storage在启动后会连接Tracker，告知自己所属的group等信息，并保持周期性的心跳，tracker根据storage的心跳信息，建立group==>[storage server list]的映射表。

Tracker需要管理的元信息很少，会全部存储在内存中；另外tracker上的元信息都是由storage汇报的信息生成的，本身不需要持久化任何数据，这样使得tracker非常容易扩展，直接增加tracker机器即可扩展为tracker cluster来服务，cluster里每个tracker之间是完全对等的，所有的tracker都接受stroage的心跳信息，生成元数据信息来提供读写服务。

## 4、Upload file

FastDFS向使用者提供基本文件访问接口，比如upload、download、append、delete等，以客户端库的方式提供给用户使用。

## 5、如何选择

配置详解：<https://www.jianshu.com/p/920f35e9363b>

**选择tracker server**

```
当集群中不止一个tracker server时，由于tracker之间是完全对等的关系，客户端在upload文件时可以任意选择一个trakcer。tracker server时，由于tracker之间是完全对等的关系，客户端在upload文件时可以任意选择一个trakcer。
```

**选择存储的group**

```
当tracker接收到upload file的请求时，会为该文件分配一个可以存储该文件的group，支持如下选择group的规则： 1. Round robin，所有的group间轮询 2. Specified group，指定某一个确定的group 3. Load balance，剩余存储空间多多group优先tracker接收到upload file的请求时，会为该文件分配一个可以存储该文件的group，支持如下选择group的规则： 1. Round robin，所有的group间轮询 2. Specified group，指定某一个确定的group 3. Load balance，剩余存储空间多多group优先
```

**选择storage server**

```
当选定group后，tracker会在group内选择一个storage server给客户端，支持如下选择storage的规则： 1. Round robin，在group内的所有storage间轮询 2. First server ordered by ip，按ip排序 3. First server ordered by priority，按优先级排序（优先级在storage上配置）group后，tracker会在group内选择一个storage server给客户端，支持如下选择storage的规则： 1. Round robin，在group内的所有storage间轮询 2. First server ordered by ip，按ip排序 3. First server ordered by priority，按优先级排序（优先级在storage上配置）
```

**选择storage path**

```
当分配好storage server后，客户端将向storage发送写文件请求，storage将会为文件分配一个数据存储目录，支持如下规则： 1. Round robin，多个存储目录间轮询 2. 剩余存储空间最多的优先storage server后，客户端将向storage发送写文件请求，storage将会为文件分配一个数据存储目录，支持如下规则： 1. Round robin，多个存储目录间轮询 2. 剩余存储空间最多的优先
```

**生成Fileid**

```
选定存储目录之后，storage会为文件生一个Fileid，由storage server ip、文件创建时间、文件大小、文件crc32和一个随机数拼接而成，然后将这个二进制串进行base64编码，转换为可打印的字符串。storage会为文件生一个Fileid，由storage server ip、文件创建时间、文件大小、文件crc32和一个随机数拼接而成，然后将这个二进制串进行base64编码，转换为可打印的字符串。
```

**选择两级目录**

```
当选定存储目录之后，storage会为文件分配一个fileid，每个存储目录下有两级256*256的子目录，storage会按文件fileid进行两次hash（猜测），路由到其中一个子目录，然后将文件以fileid为文件名存储到该子目录下。storage会为文件分配一个fileid，每个存储目录下有两级256*256的子目录，storage会按文件fileid进行两次hash（猜测），路由到其中一个子目录，然后将文件以fileid为文件名存储到该子目录下。
```

**生成文件名**

```
当文件存储到某个子目录后，即认为该文件存储成功，接下来会为该文件生成一个文件名，文件名由group、存储目录、两级子目录、fileid、文件后缀名（由客户端指定，主要用于区分文件类型）拼接而成。
group、存储目录、两级子目录、fileid、文件后缀名（由客户端指定，主要用于区分文件类型）拼接而成。
```

**文件同步**

写文件时，客户端将文件写至group内一个storage server即认为写文件成功，storage server写完文件后，会由后台线程将文件同步至同group内其他的storage server。

每个storage写文件后，同时会写一份binlog，binlog里不包含文件数据，只包含文件名等元信息，这份binlog用于后台同步，storage会记录向group内其他storage同步的进度，以便重启后能接上次的进度继续同步；进度以时间戳的方式进行记录，所以最好能保证集群内所有server的时钟保持同步。

storage的同步进度会作为元数据的一部分汇报到tracker上，tracke在选择读storage的时候会以同步进度作为参考。

比如一个group内有A、B、C三个storage server，A向C同步到进度为T1 (T1以前写的文件都已经同步到B上了），B向C同步到时间戳为T2（T2 > T1)，tracker接收到这些同步进度信息时，就会进行整理，将最小的那个做为C的同步时间戳，本例中T1即为C的同步时间戳为T1（即所有T1以前写的数据都已经同步到C上了）；同理，根据上述规则，tracker会为A、B生成一个同步时间戳。

**Download file**

客户端upload file成功后，会拿到一个storage生成的文件名，接下来客户端根据这个文件名即可访问到该文件。

跟upload file一样，在download file时客户端可以选择任意tracker server。

tracker发送download请求给某个tracker，必须带上文件名信息，tracke从文件名中解析出文件的group、大小、创建时间等信息，然后为该请求选择一个storage用来服务读请求。由于group内的文件同步时在后台异步进行的，所以有可能出现在读到时候，文件还没有同步到某些storage server上，为了尽量避免访问到这样的storage，tracker按照如下规则选择group内可读的storage。

```
1. 该文件上传到的源头storage - 源头storage只要存活着，肯定包含这个文件，源头的地址被编码在文件名中。 
2. 文件创建时间戳==storage被同步到的时间戳 且(当前时间-文件创建时间戳) > 文件同步最大时间（如5分钟) - 文件创建后，认为经过最大同步时间后，肯定已经同步到其他storage了。 
3. 文件创建时间戳 < storage被同步到的时间戳。 - 同步时间戳之前的文件确定已经同步了 
4. (当前时间-文件创建时间戳) > 同步延迟阀值（如一天）。 - 经过同步延迟阈值时间，认为文件肯定已经同步了。 

1.该文件上传到的源头storage - 源头storage只要存活着，肯定包含这个文件，源头的地址被编码在文件名中。 
2. 文件创建时间戳==storage被同步到的时间戳 且(当前时间-文件创建时间戳) > 文件同步最大时间（如5分钟) - 文件创建后，认为经过最大同步时间后，肯定已经同步到其他storage了。 
3. 文件创建时间戳 < storage被同步到的时间戳。 - 同步时间戳之前的文件确定已经同步了 
4. (当前时间-文件创建时间戳) > 同步延迟阀值（如一天）。 - 经过同步延迟阈值时间，认为文件肯定已经同步了。
```

**小文件合并存储**

将[小文件合并存储](http://blog.yunnotes.net/index.php/losf_problem/)主要解决如下几个问题：

```
1. 本地文件系统inode数量有限，从而存储的小文件数量也就受到限制。 2. 多级目录+目录里很多文件，导致访问文件的开销很大（可能导致很多次IO） 3. 按小文件存储，备份与恢复的效率低 本地文件系统inode数量有限，从而存储的小文件数量也就受到限制。 2. 多级目录+目录里很多文件，导致访问文件的开销很大（可能导致很多次IO） 3. 按小文件存储，备份与恢复的效率低
```

FastDFS在V3.0版本里[引入小文件合并存储](http://www.open-open.com/doc/view/ab5701d57e5b49a8b6255df1ae7d5a97)的机制，可将多个小文件存储到一个大的文件（trunk file），为了支持这个机制，FastDFS生成的文件fileid需要额外增加16个字节

```
1. trunk file id 2. 文件在trunk file内部的offset 3. 文件占用的存储空间大小 （字节对齐及删除空间复用，文件占用存储空间>=文件大小） trunk file id 2. 文件在trunk file内部的offset 3. 文件占用的存储空间大小 （字节对齐及删除空间复用，文件占用存储空间>=文件大小）
```

每个trunk file由一个id唯一标识，trunk file由group内的trunk server负责创建（trunk server是tracker选出来的），并同步到group内其他的storage，文件存储合并存储到trunk file后，根据其offset就能从trunk file读取到文件。

文件在trunk file内的offset编码到文件名，决定了其在trunk file内的位置是不能更改的，也就不能通过compact的方式回收trunk file内删除文件的空间。但当trunk file内有文件删除时，其删除的空间是可以被复用的，比如一个100KB的文件被删除，接下来存储一个99KB的文件就可以直接复用这片删除的存储空间。

HTTP访问支持

FastDFS的tracker和storage都内置了http协议的支持，客户端可以通过http协议来下载文件，tracker在接收到请求时，通过http的redirect机制将请求重定向至文件所在的storage上；除了内置的http协议外，FastDFS还提供了通过[apache或nginx扩展模块](http://wenku.baidu.com/view/145b4d6ab84ae45c3b358c57)下载文件的支持。

其他特性

FastDFS提供了设置/获取文件扩展属性的接口（setmeta/getmeta)，扩展属性以key-value对的方式存储在storage上的同名文件（拥有特殊的前缀或后缀），比如/group/M00/00/01/some_file为原始文件，则该文件的扩展属性存储在/group/M00/00/01/.some_file.meta文件（真实情况不一定是这样，但机制类似），这样根据文件名就能定位到存储扩展属性的文件。

以上两个接口作者不建议使用，额外的meta文件会进一步“放大”海量小文件存储问题，同时由于meta非常小，其存储空间利用率也不高，比如100bytes的meta文件也需要占用4K（block_size）的存储空间。

FastDFS还提供appender file的支持，通过upload_appender_file接口存储，appender file允许在创建后，对该文件进行append操作。实际上，appender file与普通文件的存储方式是相同的，不同的是，appender file不能被合并存储到trunk file。

问题讨论

从FastDFS的整个设计看，基本上都已简单为原则。比如以机器为单位备份数据，简化了tracker的管理工作；storage直接借助本地文件系统原样存储文件，简化了storage的管理工作；文件写单份到storage即为成功、然后后台同步，简化了写文件流程。但简单的方案能解决的问题通常也有限，FastDFS目前尚存在如下问题（欢迎探讨）。

数据安全性

- 写一份即成功：从源storage写完文件至同步到组内其他storage的时间窗口内，一旦源storage出现故障，就可能导致用户数据丢失，而数据的丢失对存储系统来说通常是不可接受的。
- 缺乏自动化恢复机制：当storage的某块磁盘故障时，只能换存磁盘，然后手动恢复数据；由于按机器备份，似乎也不可能有自动化恢复机制，除非有预先准备好的热备磁盘，缺乏自动化恢复机制会增加系统运维工作。
- 数据恢复效率低：恢复数据时，只能从group内其他的storage读取，同时由于小文件的访问效率本身较低，按文件恢复的效率也会很低，低的恢复效率也就意味着数据处于不安全状态的时间更长。
- 缺乏多机房容灾支持：目前要做多机房容灾，只能额外做工具来将数据同步到备份的集群，无自动化机制。

存储空间利用率

- 单机存储的文件数受限于inode数量
- 每个文件对应一个storage本地文件系统的文件，平均每个文件会存在block_size/2的存储空间浪费。
- 文件合并存储能有效解决上述两个问题，但由于合并存储没有空间回收机制，删除文件的空间不保证一定能复用，也存在空间浪费的问题

负载均衡

- group机制本身可用来做负载均衡，但这只是一种静态的负载均衡机制，需要预先知道应用的访问特性；同时group机制也导致不可能在group之间迁移数据来做动态负载均衡。



## 6、FastDFS 介绍

### 6.1 FastDFS 架构

FastDFS服务有三个角色:跟踪服务器(tracker server)、存储服务器(storage server)和客户端(client)

​    **多个group之间的存储方式有3种策略:round robin(轮询)、load balance(选择最大剩余空 间的组上传文件)、specify group(指定group上传)**
​    group 中 storage 存储依赖本地文件系统,storage 可配置多个数据存储目录,磁盘不做 raid, 直接分别挂载到多个目录,将这些目录配置为 storage 的数据目录即可
​    storage 接受写请求时,会根据配置好的规则,选择其中一个存储目录来存储文件;为避免单 个目录下的文件过多,storage 第一次启时,会在每个数据存储目录里创建 2 级子目录,每级 256 个,总共 65536 个,新写的文件会以 hash 的方式被路由到其中某个子目录下,然后将文件数据直 接作为一个本地文件存储到该目录中

![fastDFS 架构图](C:\Users\Administrator\Desktop\笔记\fastDFS\fastDFS 架构图.png)

总结:1.高可靠性:无单点故障 2.高吞吐性:只要Group足够多,数据流量是足够分散的

### 6.2 FastDFS 工作流程 

### 6.3 上传

![fastDFS 上传流程](C:\Users\Administrator\Desktop\笔记\fastDFS\fastDFS 上传流程.png)



**FastDFS 提供基本的文件访问接口,如 upload、download、append、delete 等**
选择tracker server
​    集群中 tracker 之间是对等关系,客户端在上传文件时可用任意选择一个 tracker
选择存储 group
​    当tracker接收到upload file的请求时,会为该文件分配一个可以存储文件的group,目前支持选择 group 的规则为:
1. Round robin,所有 group 轮询使用
\2. Specified group,指定某个确定的 group
\3. Load balance,剩余存储空间较多的 group 优先
选择storage server
​    当选定group后,tracker会在group内选择一个storage server给客户端,目前支持选择server 的规则为:
\1. Round robin,所有 server 轮询使用(默认)
\2. 根据IP地址进行排序选择第一个服务器(IP地址最小者)
\3. 根据优先级进行排序(上传优先级由storage server来设置,参数为upload_priority)
选择storage path(磁盘或者挂载点)
​    当分配好storage server后,客户端将向storage发送写文件请求,storage会将文件分配一个数据存储目录,目前支持选择存储路径的规则为:
￼1. round robin,轮询(默认)
\2. load balance,选择使用剩余空间最大的存储路径
选择下载服务器
​    目前支持的规则为:
\1. 轮询方式,可以下载当前文件的任一storage server 

\2. 从源storage server下载

生成 file_id

​    选择存储目录后,storage 会生成一个 file_id,采用 Base64 编码,包含字段包括:storage server ip、文件创建时间、文件大小、文件 CRC32 校验码和随机数;每个存储目录下有两个 256*256 个子目录,storage 会按文件 file_id 进行两次 hash,路由到其中一个子目录,,然后将文件以 file_id 为文件名存储到该子目录下,最后生成文件路径:group 名称、虚拟磁盘路径、数据两级目录、file_id

![fastDFS 上传后的内容](C:\Users\Administrator\Desktop\笔记\fastDFS\fastDFS 上传后的内容.png)

其中,

​    组名:文件上传后所在的存储组的名称,在文件上传成功后由存储服务器返回,需要客户端自行保存

​    虚拟磁盘路径:存储服务器配置的虚拟路径,与磁盘选项 store_path*参数对应 

​    数据两级目录:存储服务器在每个虚拟磁盘路径下创建的两级目录,用于存储数据文件

### 6.4 同步机制

**storage server的7种状态:**
​    通过命令 fdfs_monitor /etc/fdfs/client.conf 可以查看 ip_addr 选项显示 storage server 当前状态
INIT : 初始化,尚未得到同步已有数据的源服务器 

WAIT_SYNC : 等待同步,已得到同步已有数据的源服务器 

SYNCING : 同步中
DELETED : 已删除,该服务器从本组中摘除
OFFLINE :离线
ONLINE : 在线,尚不能提供服务
ACTIVE : 在线,可以提供服务

**组内增加storage serverA状态变化过程:**
\1. storage server A 主动连接 tracker server,此时 tracker server 将 storageserverA 状态设置为 INIT
\2. storage server A 向 tracker server 询问追加同步的源服务器和追加同步截止时间点(当前时间),若组内只有storage server A或者上传文件数为0,则告诉新机器不需要数据同步,storage server A 状态设置为 ONLINE ;若组内没有 active 状态机器,就返回错误给新机器,新机器睡眠尝试;否则 tracker 将其状态设置为 WAIT_SYNC
\3. 假如分配了 storage server B 为同步源服务器和截至时间点,那么 storage server B会将截至时间点之前的所有数据同步给storage server A,并请求tracker设置 storage server A 状态为SYNCING;到了截至时间点后,storage server B向storage server A 的同步将由追加同步切换为正常 binlog 增量同步,当取不到更多的 binlog 时,请求tracker将storage server A设置为OFFLINE状态,此时源同步完成
\4. storage server B 向 storage server A 同步完所有数据,暂时没有数据要同步时, storage server B请求tracker server将storage server A的状态设置为ONLINE
\5. 当 storage server A 向 tracker server 发起心跳时,tracker sercer 将其状态更改为 ACTIVE,之后就是增量同步(binlog)

![fastDFS 同步机制](C:\Users\Administrator\Desktop\笔记\fastDFS\fastDFS 同步机制.png)

注释:

1.整个源同步过程是源机器启动一个同步线程,将数据 push 到新机器,最大达到一个磁盘的 IO,不能并发
2.由于源同步截止条件是取不到 binlog,系统繁忙,不断有新数据写入的情况,将会导致一直无法完成源同步过程

### 6.5 下载

![fastDFS 下载](C:\Users\Administrator\Desktop\笔记\fastDFS\fastDFS 下载.png)

client 发送下载请求给某个 tracker,必须带上文件名信息,tracker 从文件名中解析出文件的 group、大小、创建时间等信息,然后为该请求选择一个 storage 用于读请求;由于 group 内的文件同步在后台是异步进行的,可能出现文件没有同步到其他storage server上或者延迟的问题, 后面我们在使用 nginx_fastdfs_module 模块可以很好解决这一问题。

![fast下载详细图](C:\Users\Administrator\Desktop\笔记\fastDFS\fast下载详细图.png)

### 6.6 文件合并原理

​     FastDFS 提供合并存储功能,默认创建的大文件为 64MB,然后在该大文件中存储很多小文件; 大文件中容纳一个小文件的空间称作一个 Slot,规定 Slot 最小值为 256 字节,最大为 16MB,即小于 256 字节的文件也要占用 256 字节,超过 16MB 的文件独立存储;
​    为了支持文件合并机制,FastDFS生成的文件file_id需要额外增加16个字节;每个trunk file 由一个id唯一标识,trunk file由group内的trunk server负责创建(trunk server是tracker 选出来的),并同步到group内其他的storage,文件存储合并存储到trunk file后,根据其文件偏移量就能从trunk file中读取文件

一、软件包准备：

1、FastDFS_v5.05.tar.gz

2、libfastcommonV1.0.7.tar.gz

3、 fastdfs-nginx-module_v1.16.tar.gz

4、nginx-1.10.2.tar.gz

二、确认环境端口是否可用，也可更换fastdfs默认端口

tracker使用：22122    

storage使用：23000               

远程连接端口（xshell）：80   、22  

# 二、FastDFS 集群搭建

## 1、参看链接：

1、[https://blog.csdn.net/u012453843/article/details/68957209?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522159869154719724839214494%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=159869154719724839214494&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_click~default-2-68957209.pc_first_rank_v2_rank_v28&utm_term=fastDFS%E9%9B%86%E7%BE%A4%E6%90%AD%E5%BB%BA&spm=1018.2118.3001.4187](https://blog.csdn.net/u012453843/article/details/68957209?ops_request_misc=%7B%22request%5Fid%22%3A%22159869154719724839214494%22%2C%22scm%22%3A%2220140713.130102334..%22%7D&request_id=159869154719724839214494&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_click~default-2-68957209.pc_first_rank_v2_rank_v28&utm_term=fastDFS集群搭建&spm=1018.2118.3001.4187)

2、<https://blog.csdn.net/u012453843/article/details/69055570>

3、[https://blog.csdn.net/u012453843/article/details/69172423?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522159869154719724839214494%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=159869154719724839214494&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~first_rank_v2~rank_v28-3-69172423.pc_first_rank_v2_rank_v28&utm_term=fastDFS%E9%9B%86%E7%BE%A4%E6%90%AD%E5%BB%BA&spm=1018.2118.3001.4187](https://blog.csdn.net/u012453843/article/details/69172423?ops_request_misc=%7B%22request%5Fid%22%3A%22159869154719724839214494%22%2C%22scm%22%3A%2220140713.130102334..%22%7D&request_id=159869154719724839214494&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~first_rank_v2~rank_v28-3-69172423.pc_first_rank_v2_rank_v28&utm_term=fastDFS集群搭建&spm=1018.2118.3001.4187)

## 2、架构图

下面我们便来搭建一个FastDFS集群，我们搭建一个如下图所示的集群，总共需要8台虚拟机。

![11.fastDFS 架构图](C:\Users\Administrator\Desktop\笔记\fastDFS\11.fastDFS 架构图.png)

## 3、操作步骤

1.安装8台虚拟机（最小化安装，大家可以参考：http://blog.csdn.net/u012453843/article/details/68947589这篇博客进行安装）

2.给这8台虚拟机配置静态IP并且要能上网，大家可以参考：http://blog.csdn.net/u012453843/article/details/52839105这篇博客进行配置，不过由于现在是最小化安装，是没有安装vim命令的，因此需要使用"vi"命令来修改文件。

3.配置好静态IP之后，我们使用XShell工具来操作虚拟机（因为真实环境中我们是不大可能直接去操作服务器的，都是通过远程连接工具来进行操作的）。如下图所示，我使用的虚拟机分别是192.168.156.5、192.168.156.6、192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10、192.168.156.11、192.168.156.12。其中，192.168.156.5、192.168.156.6分别作为tracker1和tracker2。192.168.156.7、192.168.156.8作为group1，192.168.156.9、192.168.156.10作为group2。我把192.168.156.11、192.168.156.12作为Nginx集群多层负载均衡。多层负载均衡会生成一个虚拟IP，我们最终会通过虚拟IP来访问我们的集群。我们先安装tracker和group的六台设备。

4.在192.168.156.5的"/usr/local/"目录下新建一个"software"目录，用来存放我们的安装文件。这样做的好处是容易管理。

5.我们一次性上传我们所需要的所有安装文件，大家可以到：这个地址进行下载。我们使用XShell结合Xftp5进行上传，关于如何结合大家可以参考：http://blog.csdn.net/u012453843/article/details/68951776这篇文件进行操作。如下图所示。

![11.所需要的包](C:\Users\Administrator\Desktop\笔记\fastDFS\11.所需要的包.png)

6.我们将192.168.156.5这台设备上刚上传的文件复制到其它设备上。我们使用的命令是：scp -r /usr/local/software/ root@192.168.156.6:/usr/local/，这句命令的意思是，使用scp的方式将softWare及其下的文件都复制到192.168.156.6的/usr/local目录下，如果192.168.156.6的/usr/local目录下没有softWare目录，那么会自动创建这么一个目录。"root@"的意思是指定传到哪个用户组下面。由于当前都是操作的root用户，因此也可以不用写"root@"。输入命令并按回车后，会让我们输入是否继续，我们输入"yes"并回车，之后会让我们输入192.168.156.6的root用户的密码，我们输入之后便开始上传操作了（**如果输入scp命令后很久才能到提示 让我们输入yes/no，那么我们可以在/etc/hosts文件中配置下所有要参与互相通信的ip和名称的映射关系**），如下图所示。

![11.3将包拷贝到其他虚拟机下](C:\Users\Administrator\Desktop\笔记\fastDFS\11.3将包拷贝到其他虚拟机下.png)

上传完之后，我们到192.168.156.6的/usr/local目录下查看一下，发现自动多了softWare目录。 我们再进入softWare目录内，发现确实已经复制过来了。**同理，我们再向192.168.156.6、192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10这几台虚拟机复制这些安装文件**。

7.安装gcc，使用的命令：yum install make cmake gcc gcc-c++。**在192.168.156.6、192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10虚拟机也都安装下gcc环境**。

8.最小化安装是没有zip和unzip命令的，所以需要安装，安装命令：yum install zip unzip。**同样，为192.168.156.6、192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10也安装zip和unzip命令**。

9.解压libfastcommon-master.zip。使用命令：unzip libfastcommon-master.zip -d /usr/local/fast/。解压到/usr/local/fast/是为了便于管理。**同理解压192.168.156.6、192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10虚拟机中的该文件**。

![11.4解压](C:\Users\Administrator\Desktop\笔记\fastDFS\11.4解压.png)

10.安装vim，之所以安装vim是因为安装vim会自动帮我们安装perl，否则我们使用./make.sh来编译的时候会报错，如下图所示。（如果有这个命令就无需安装了）。

​		安装vim所使用命令：yum install vim-enhanced。**同理在192.168.156.6、192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10虚拟机也安装vim命令**。

11.编译libfastcommon-master，我们到/usr/local/fast/libfastcommon-master目录下，使用命令：./make.sh来进行编译。

![11.5编译](C:\Users\Administrator\Desktop\笔记\fastDFS\11.5编译.png)

​	接着执行./make.sh install命令。如下图所示。（**我们把./make.sh和./make.sh install命令在192.168.156.6、192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10虚拟机的相同目录下也执行一遍**）。

![11.6](C:\Users\Administrator\Desktop\笔记\fastDFS\11.6.png)

12.创建软链接，我们从上图中可以看到执行./make.sh install命令后把fastcommon安装在了/usr/lib64下面，这个目录不是我们常用的目录，如果不实用软链接的话，将来安装的东西多了，它们的安装目录我们将比较难找到，不便于管理操作，为了在我们熟悉的目录下看到我们安装的目录，我们使用软链接来实现。

​       创建第一条软链接的命令：ln -s /usr/lib64/libfastcommon.so /usr/local/lib/libfastcommon.so，如下图所示。  

![11.7](C:\Users\Administrator\Desktop\笔记\fastDFS\11.7.png)

 接着再创建三条软链接，命令依次如下：

[root@itcast05 lib]# ln -s /usr/lib64/libfastcommon.so /usr/lib/libfastcommon.so
[root@itcast05 lib]# ln -s /usr/lib64/libfdfsclient.so /usr/local/lib/libfdfsclient.so
[root@itcast05 lib]# ln -s /usr/lib64/libfdfsclient.so /usr/lib/libfdfsclient.so

​      创建完软链接之后，我们再查看软链接，如下图所示，发现有一条软链接是红色的，一闪一闪的，这是由于fastclient还未安装造成的，随着后续的安装，这个报警会自动消失。**同理，我们在192.168.156.6、192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10上也创建同样的软链接**。

![11.8](C:\Users\Administrator\Desktop\笔记\fastDFS\11.8.png)

13.安装FastDFS，我们先到/usr/local/softWare/目录下，然后使用命令：tar -zxvf FastDFS_v5.05.tar.gz -C /usr/local/fast/进行解压，如下图所示。

![11.9](C:\Users\Administrator\Desktop\笔记\fastDFS\11.9.png)

​		解压完后，我们进入到/usr/local/fast/fastDFS/目录下，依次执行./make.sh和./make.sh install命令进行安装。如下图所示，**同理，我们在192.168.156.6、192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10上也进行与第13步同样的安装操作。**

![11.10](C:\Users\Administrator\Desktop\笔记\fastDFS\11.10.jpg)

14.因为FastDFS服务脚本设置的bin目录为/usr/local/bin/下,但是实际我们安装在了/usr/bin/下面。所以我们需要修改FastDFS配置文件中的路径，也就是需要修改俩 个配置文件： 命令：vim /etc/init.d/fdfs_storaged 输入一个":"，然后输入全局替换命令：%s+/usr/local/bin+/usr/bin并按回车即可完成替换，替换完之后，保存退出该文件，然后再打开看一下是否都已经将/usr/local/bin替换成/usr/bin了。**同样的步骤，输入第二条命令：vim /etc/init.d/fdfs_trackerd 进行全局替换，替换命令：%s+/usr/local/bin+/usr/bin**。**同样为192.168.156.6、192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10进行替换操作。**

![11.11](C:\Users\Administrator\Desktop\笔记\fastDFS\11.11.png)

15.前面做的都是公共部分的配置，下面我们来配置tracker1(192.168.156.5)和tracker2(192.168.156.6)。我们先到/etc/fdfs/目录下，使用cp tracker.conf.sample tracker.conf复制一份tracker.conf.sample并更名为tracker.conf。

![11.12](C:\Users\Administrator\Desktop\笔记\fastDFS\11.12.png)

​      编辑tracker.conf文件，需要修改的地方有两处，第一处：base_path，将默认的路径修改为/fastdfs/tracker。第二处：store_lookup，该值默认是2（即负载均衡策略），现在把它修改为0（即轮询策略，修改成这样方便一会儿我们进行测试，当然，最终还是要改回到2的。如果值为1的话表明要始终向某个group进行上传下载操作，这时下图中的"store_group=group2"才会起作用，如果值是0或2，则"store_group=group2"不起作用）。

![11.13](C:\Users\Administrator\Desktop\笔记\fastDFS\11.13.png)

​		由于192.168.156.6和192.168.156.5的tracker配置是一样的，因此我们只需要将192.168.156.5上配置好的tracker.conf文件复制一份到192.168.156.6上。使用的命令：scp tracker.conf 192.168.156.6:/etc/fdfs/

​        复制完之后，我们到192.168.156.6的/etc/fdfs/目录下查看一下是否已经有tracker.conf文件了，如下图所示，我们发现已经有该文件并且配置完全一样。

![11.14](C:\Users\Administrator\Desktop\笔记\fastDFS\11.14.png)

​      由于我们给base_path配置的路径/fastdfs/tracker当前并不存在，因此我们需要在192.168.156.5和192.168.156.6上创建一下该目录，创建命令：mkdir -p /fastdfs/tracker，其中-p表示递归创建目录。![11.15](C:\Users\Administrator\Desktop\笔记\fastDFS\11.15.png)

​      配置完了tracker1和tracker2，现在我们来启动两个tracker。我们先到我们刚创建的/fastdfs/tracker/目录下，发现当前该目录下什么也没有，如下图所示。

​       **在启动前，我们需要先在192.168.156.5和192.168.156.6这两台设备上配置一下防火墙，添加端口22122，从而可以让其它设备可以访问22122端口。添加的内容：-A INPUT -m state --state NEW -m tcp -p tcp --dport 22122 -j ACCEPT，如下图所示（注意所有的例子都是以一个为例，其它照着操作就可以了）**。

![11.16](C:\Users\Administrator\Desktop\笔记\fastDFS\11.16.jpg)

​       添加完之后，我们重启防火墙，如下图所示。

![11.17](C:\Users\Administrator\Desktop\笔记\fastDFS\11.17.jpg)

​       下面我们便使用命令：/etc/init.d/fdfs_trackerd start 进行启动，启动之后，我们再查看该目录，发现多了两个目录data和logs，我们可以通过命令：ps -ef | grep fdfs来查看tracker是否正常启动，如下图所示。**同理，我们启动一下192.168.156.6上的tracker。**

![11.18](C:\Users\Administrator\Desktop\笔记\fastDFS\11.18.png)

​      如果想要停止tracker的话，就使用命令/etc/init.d/fdfs_trackerd stop。

16.配置storage，按照我们的规划，192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10四台虚拟机将作为我们的存储节点，四个节点中同一组的配置应该是相同的，也就是192.168.156.7、192.168.156.8一组（组名为group1），192.168.156.9、192.168.156.10一组（组名为group2）。

​      首先，进入4台机器的配置文件目录/etc/fdfs，然后进行复制storage.conf.sample并更名为storage.conf，使用的命令：cp storage.conf.sample storage.conf，如下图所示

![11.19](C:\Users\Administrator\Desktop\笔记\fastDFS\11.19.png)

接下来我们编辑storage.conf文件，对于group1的192.168.156.7、192.168.156.8这两台设备需要修改的配置如下。

​       base_path=/fastdfs/storage

​       store_path0=/fastdfs/storage

​       tracker_server=192.168.156.5:22122
​       tracker_server=192.168.156.6:22122

​       修改完后，如下图所示。

![11.20](C:\Users\Administrator\Desktop\笔记\fastDFS\11.20.jpg)

   	配置完了192.168.156.7，接下来我们把storage.conf文件复制到192.168.156.8、192.168.156.9、192.168.156.10这三台设备上。其中192.168.156.8这台设备与192.168.156.7同属于group1，因此把配置文件放到它的/etc/fdfs/目录后不用做任何修改。**但是192.168.156.9和192.168.156.10这两台设备需要修改一下，修改也非常简单，只需要把group_name由group1改为group2就可以了**，如下图所示。

![11.21](C:\Users\Administrator\Desktop\笔记\fastDFS\11.21.png)

​     **由于四个配置文件的base_path=/fastdfs/storage和store_path0=/fastdfs/storage都配置成了/fastdfs/storage，但是目前我们这四台虚拟机还未创建过该目录，因此我们需要为这四台虚拟机都创建一下该目录,命令：mkdir -p /fastdfs/storage**，如下图所示。

![11.22](C:\Users\Administrator\Desktop\笔记\fastDFS\11.22.png)

**启动storage之前，我们需要先对192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10这四台虚拟机的防火墙做下配置，添加端口2300，添加语句：-A INPUT -m state --state NEW -m tcp -p tcp --dport 23000 -j ACCEPT，如下图所示。**

![11.23](C:\Users\Administrator\Desktop\笔记\fastDFS\11.23.png)

​     配置完防火墙之后，我们重启下这四台虚拟机的防火墙，如下图所示。

![11.24](C:\Users\Administrator\Desktop\笔记\fastDFS\11.24.png)

​      配置完防火墙之后，我们到storage所在的四台虚拟机的/fastdfs/storage目录下，使用命令：/etc/init.d/fdfs_storaged start进行启动，然后使用命令：tailf logs/storaged.log来查看启动信息，如下图所示（192.168.156.7这台虚拟机的操作图）。可以看到，192.168.156.7这台设备成功与两个tracker设备连接了，其中选举了192.168.156.6作为tracker集群的leader。192.168.156.7和192.168.156.8这两台虚拟机同属一个分组(group1)，因此可以从下图的信息中看到成功与192.168.156.8连接的信息。

![11.26](C:\Users\Administrator\Desktop\笔记\fastDFS\11.26.png)

​      下图是192.168.156.8的启动信息。看到信息几乎一样，只是最后一行显示的是成功与192.168.156.7连接（因为它们同属一组）

![11.27](C:\Users\Administrator\Desktop\笔记\fastDFS\11.27.png)

​       下图是192.168.156.9的启动信息。看到的tracker的信息与group1的两台设备一样，只是这台设备与192.168.156.10属于group2，所以它连接的设备是192.168.156.10。

![11.28](C:\Users\Administrator\Desktop\笔记\fastDFS\11.28.png)下图是192.168.156.10的启动信息，从下面可以看到成功与tracker还有192.168.156.9连接了。

![11.29](C:\Users\Administrator\Desktop\笔记\fastDFS\11.29.png)

​      看到上面的信息，说明我们的tracker和storage集群搭建好了，下面我们测试一下tracker的高可用性，从上图我们可以看到192.168.156.6这台设备是作为tracker的leader的，我们现在人为关掉192.168.156.6的tracker。如下图所示。

![11.30](C:\Users\Administrator\Desktop\笔记\fastDFS\11.30.png)

​     关掉192.168.156.6的tracker之后，我们再看看storage所在的四台虚拟机的日志信息。如下图所示，可以看到192.168.156.5自动切换成leader角色了，而且显示连不上192.168.156.6了（因为已经关了tracker了，所以都连不上了）。

![11.31](C:\Users\Administrator\Desktop\笔记\fastDFS\11.31.png)

​        下面我们再启动192.168.156.6上的tracker，如下图所示。

![11.32](C:\Users\Administrator\Desktop\笔记\fastDFS\11.32.png)

​      启动后，再看看四台设备的日志信息，如下图所示，可以看到，都重新连接上该tracker了。只不过此时tracker的leader依然是192.168.156.5。

![11.35](C:\Users\Administrator\Desktop\笔记\fastDFS\11.35.png)

​      当我们所有的tracker和storage节点都启动成功之后，我们可以在任意的一个存储节点上查看存储集群的信息，命令：/usr/bin/fdfs_monitor /etc/fdfs/storage.conf，可以看到如下图所示的集群信息。可以看到tracker Server有两个，当前提供服务的是192.168.156.5，group的数量是2，第一组的IP有192.168.156.7和192.168.156.8，第二组的IP有192.168.156.9和192.168.156.10，与我们规划的集群完全一致。

![11.36](C:\Users\Administrator\Desktop\笔记\fastDFS\11.36.png)

​        可以使用命令cd /usr/bin/ && ls | grep fdfs来查看fdfs所有的命令，如下图所示。

![11.37](C:\Users\Administrator\Desktop\笔记\fastDFS\11.37.png)



**一、tracker和storage集群上传图片测试**

​        由于目前还没有搭建完集群，因此我们暂且在tracker的一台设备上（我们就选择192.168.156.5这台设备）使用client来上传图片进行测试。

​        1.进入到/etc/fdfs/目录下，我们可以看到client.conf.sample这么一个配置文件，如下图所示。

![12.1](C:\Users\Administrator\Desktop\笔记\fastDFS\12.1.png)

​        2.我们使用命令：cp client.conf.sample client.conf复制一份该文件并命名为client.conf，如下图所示。

![12.2](C:\Users\Administrator\Desktop\笔记\fastDFS\12.2.png)

        3.修改client.conf配置文件，使用命令：vim client.conf，需要修改的配置有base_path=/fastdfs/tracker、tracker_server=192.168.156.5:22122和tracker_server=192.168.156.6:22122，如下图所示。其中/fastdfs/tracker这个目录我们已经创建过了，因此这时不需要再创建了。

![12.3](C:\Users\Administrator\Desktop\笔记\fastDFS\12.3.png)

​       4.下面我们来上传一张图片，我把/usr/local目录下一张3.jpg图片上传，使用的命令：/usr/bin/fdfs_upload_file  /etc/fdfs/client.conf /usr/local/3.jpg，可以看到这条命令由3部分组成，第一部分是/usr/bin/fdfs_upload_file，意思是指定要进行上传文件操作，第二部分是/etc/fdfs/client.conf，意思是指定上传操作使用的配置文件，这个配置文件就是我们上面刚配置过的client.conf文件，第三部分是/usr/local/3.jpg，意思是指定要上传哪个目录下的哪个文件。按回车执行上传命令后，会返回一个串：group1/M00/00/00/wKicB1jjiFmAOUdkAAHk-VzqZ6w720.jpg，其中group1表示这张图片被保存在了哪个组当中，M00代表磁盘目录，如果电脑只有一个磁盘那就只有M00， 如果有多个磁盘，那就M01、M02...等等。00/00代表磁盘上的两级目录，每级目录下是从00到FF共256个文件夹，两级就是256*256个。wKicB1jjiFmAOUdkAAHk-VzqZ6w720.jpg表示被存储到storage上的3.jpg被重命名的名字，这样做的目的是为了防止图片名字重复。

![12.4](C:\Users\Administrator\Desktop\笔记\fastDFS\12.4.png)

​       我们到两组group所在的四台设备的/fastdfs/storage/data/00/00目录下查看一下是否有我们刚才上传的图片，发现192.168.156.7和192.168.156.8两台设备上有该图片，而192.168.156.9和192.168.156.10两台设备上没有该图片。这是由于192.168.156.7和192.168.156.8两台设备属于group1，而192.168.156.9和192.168.156.10属于group2，返回的图片信息中明确说明了存储在了group1下面，因此可group2下面是没有该图片的。

![12.5](C:\Users\Administrator\Desktop\笔记\fastDFS\12.5.png)

​        我们在搭建集群的时候，配置的策略是轮询策略，那么我们现在再上传一次该图片，看是否会存储到group2下面。如下图所示，发现这次返回的路径信息中显示存储到了group2下面。

![12.6](C:\Users\Administrator\Desktop\笔记\fastDFS\12.6.png)

​        下面再到四台设备下的/fastdfs/storage/data/00/00目录下查看下图片信息，发现192.168.156.7和192.168.156.8这两台设备的图片还是刚才那一张，并没有新增图片。而192.168.156.9和192.168.156.10这两台设备上新增了我们刚才上传的图片，这说明第二次上传确实是存储到了group2上面。

![12.7](C:\Users\Administrator\Desktop\笔记\fastDFS\12.7.png)

​         大家可以多次上传图片，就可以看到存储确实是一次group1，一次group2，轮询进行存储的，如下图所示。

![12.8](C:\Users\Administrator\Desktop\笔记\fastDFS\12.8.png)

​      这里说一个情况，那就是同处一个组的某台设备如果发生了故障，那么这时上传的文件将只能存放到其它同组设备上，等故障设备修复后，同组的其它设备会自动将数据同步到该故障设备上，这个不用我们干预。

**二、配置Ngnix**

​        1.到目前为止，我们还是不能够使用http方式来上传或下载文件的，因此我们需要配置下nginx来达到这个目的。我们先关闭除两个组所在的四台之外的其它所有设备，然后把剩下的这四台设备切换到/usr/local/software目录下，如下图所示。

![13.1](C:\Users\Administrator\Desktop\笔记\fastDFS\13.1.png)

​       2.这四台设备都需要执行同一个操作，那就是解压fastdfs-nginx-module_v1.16.tar.gz，我们把命令：tar -zxvf fastdfs-nginx-module_v1.16.tar.gz -C /usr/local/fast/复制到下面那个输入框中，默认情况下，在这个输入框中输入命令并按回车，命令执行的范围是当前会话，为了同时在四台设备上执行同样的命令，我们可以切换到"全部Xshell(X)"。如下图所示。

![13.2](C:\Users\Administrator\Desktop\笔记\fastDFS\13.2.png)

​      切换到"全部Xshell(X)"后，如下图所示，可以看到输入框左边的那个图标切换成了多个窗口的图标。这时我们把鼠标放到输入框中，按回车便可同时在四台设备上执行相同的命令。

![13.4](C:\Users\Administrator\Desktop\笔记\fastDFS\13.4.png)

​       解压信息如下图所示，其它设备我就不一一截图了。

![13。5](C:\Users\Administrator\Desktop\笔记\fastDFS\13。5.png)

​     3.四台设备都需要进入到/usr/local/fast/fastdfs-nginx-module/src/这个目录下，因此我们把cd /usr/local/fast/fastdfs-nginx-module/src/命令放到下面的输入框中并按回车。

![13.6](C:\Users\Administrator\Desktop\笔记\fastDFS\13.6.png)

​       我们可以看到在/usr/local/fast/fastdfs-nginx-module/src/这个目录下有个config配置文件，我们需要修改下该文件。

![13.7](C:\Users\Administrator\Desktop\笔记\fastDFS\13.7.png)

​       我们在下面的全局输入框中输入"vim config"同时打开四台设备的该配置文件，如下图所示，该配置文件的第四行我们需要做下修改，这是因为我们前面为了查看方便而把东西放到了/usr/local/include下，但是实际执行make install安装时却安装在了/usr/include下面，也就是我们多了一层local目录，因此我们需要把该行的两个local目录去掉。

![13.8](C:\Users\Administrator\Desktop\笔记\fastDFS\13.8.png)

​      去掉local目录后如下图所示。**同样的，我们把其它三台设备的这行配置也都去掉local目录。**

![13.9](C:\Users\Administrator\Desktop\笔记\fastDFS\13.9.png)

​       4.安装nginx依赖包

​        把下面四条命令执行一遍。

​        yum install pcre

​        yum install pcre-devel

​        yum install zlib

​        yum install zlib-devel

​      5.安装nginx

​       四台设备都进入到/usr/local/software目录下，该目录下nginx-1.6.2.tar.gz是nginx安装包。

![13.10](C:\Users\Administrator\Desktop\笔记\fastDFS\13.10.png)

​     把解压命令：tar -zxvf nginx-1.6.2.tar.gz -C /usr/local/输入到下面的输入框中，回车即可在这四台设备上同时执行解压操作。

![13.11](C:\Users\Administrator\Desktop\笔记\fastDFS\13.11.png)

​        安装完之后，我们都进入到/usr/local/nginx-1.6.2/目录下，然后在下面的输入框中输入命令：./configure --add-module=/usr/local/fast/fastdfs-nginx-module/src/并按回车，在四台设备上都加入下模块并进行检查。

![13.12](C:\Users\Administrator\Desktop\笔记\fastDFS\13.12.png)

​      检查信息如下图所示。

![13.13](C:\Users\Administrator\Desktop\笔记\fastDFS\13.13.png)

​      检查完之后，我们在下面输入框中输入命令make && make install，回车，四台设备同时执行编译安装操作。

![13.14](C:\Users\Administrator\Desktop\笔记\fastDFS\13.14.png)

​       安装信息中没有出现错误信息，说明我们的安装成功，安装完之后，我们到/usr/local/目录下，可以看到多了一个nginx的文件夹，如下图所示。

![13.15](C:\Users\Administrator\Desktop\笔记\fastDFS\13.15.png)

​       下面我们到/usr/local/fast/fastdfs-nginx-module/src/目录下，我们还是在输入框中输入cd /usr/local/fast/fastdfs-nginx-module/src/并回车，四台设备都进入到该目录下。

![13.16](C:\Users\Administrator\Desktop\笔记\fastDFS\13.16.png)

​        我们将cp mod_fastdfs.conf /etc/fdfs/复制到/etc/fdfs/目录下，我们在下面输入框中输入命令cp mod_fastdfs.conf /etc/fdfs/并按回车，四台设备同时进行复制操作。

​        复制完之后，我们修改/etc/fdfs/目录下的mod_fastdfs.conf，我们使用命令vim /etc/fdfs/mod_fastdfs.conf来编辑192.168.156.7上的该文件。

​        需要修改的地方如下：

connect_timeout=10
tracker_server=192.168.156.5:22122
tracker_server=192.168.156.6:22122
storage_server_port=23000//默认就是2300，不用做修改
url_have_group_name=true
store_path0=/fastdfs/storage
group_name=group1
group_count=2


[group1]
group_name=group1
storage_server_port=23000
store_path_count=1
store_path0=/fastdfs/storage


[group2]
group_name=group2
storage_server_port=23000
store_path_count=1
store_path0=/fastdfs/storage

​         修改后如下图所示。

![13.17](C:\Users\Administrator\Desktop\笔记\fastDFS\13.17.png)

​       我们把192.168.156.7上的这个配置文件复制到其它三台设备上，先到/etc/fdfs/目录下，如下图所示。

![13.18](C:\Users\Administrator\Desktop\笔记\fastDFS\13.18.png)

​       使用命令：scp  mod_fastdfs.conf 192.168.156.8:/etc/fdfs/、scp  mod_fastdfs.conf 192.168.156.9:/etc/fdfs/、scp  mod_fastdfs.conf 192.168.156.10:/etc/fdfs/进行复制。

​       复制完之后，我们到8、9、10这三台设备上看下配置文件是否就是我们刚才复制过去的文件。由于192.168.156.8这台设备与192.168.156.7这台设备同属group1，因此192.168.156.8这台设备不用做修改。我们只需把192.168.156.9和192.168.156.10这两台设备的这个配置文件的group名称改为group2即可。192.168.156.9的修改如下图所示，192.168.156.10与之一样。

![13.19](C:\Users\Administrator\Desktop\笔记\fastDFS\13.19.png)

​       接下来，我们需要把/usr/local/fast/FastDFS/conf/目录下的http.conf和mime.types两个文件复制到/etc/fdfs/目录下，由于这四台设备的这步操作都一样，因此我们在下面的输入框中输入cd /usr/local/fast/FastDFS/conf/并回车，都进入到该目录下，然后输入命令cp http.conf mime.types /etc/fdfs/并回车，在四台设备同时进行复制操作。

![13.20](C:\Users\Administrator\Desktop\笔记\fastDFS\13.20.png)

​       复制完之后，我们给四个节点都创建一下软链接，由于步骤都一样，因此在下面输入框中输入创建软链接命令：ln -s /fastdfs/storage/data/ /fastdfs/storage/data/M00并同时执行就可以了。如下图所示。

![13.21](C:\Users\Administrator\Desktop\笔记\fastDFS\13.21.png)

​       创建完软链接之后，我们来配置下nginx，由于四个节点操作一样，我们都进入到/usr/local/nginx/conf/这个目录下，并使用命令vim nginx.conf来编辑该文件，我们需要修改的地方有listen端口，把它由80改成8888，至于原因，是因为我们在上篇博客搭建storage的时候使用的端口是8888，因此这里也需要使用8888。另一个修改的地方是location，修改代码如下，路径采用正则表达式来匹配，匹配group0到group9下的M00前缀。由于fastdfs与nginx的模块结合，因此需要在location当中添加该模块。

```html
location ~/group([0-9])/M00 {

              ngx_fastdfs_module; 

        }
```

![13.22](C:\Users\Administrator\Desktop\笔记\fastDFS\13.22.png)

​       修改完192.168.156.7之后，我们把这个配置文件再复制到其它三台设备上。在用scp复制文件到其它设备的时候，反应很慢，加上-o GSSAPIAuthentication=no会快点，但还是不够快，解决办法是我们可以在/etc/hosts文件当中配置所有要通信的设备的IP和名称的映射，这样scp就很快了。

![13.23](C:\Users\Administrator\Desktop\笔记\fastDFS\13.23.png)

​      6.四台设备都启动nginx，我们在下面的输入框中输入/usr/local/nginx/sbin/nginx并按回车，四台设备同时启动nginx。

![13.24](C:\Users\Administrator\Desktop\笔记\fastDFS\13.24.png)

​       7.启动完nginx之后，我们现在便可以通过http的方式访问上传到FastDFS上的文件了，比如我们现在再把那个3.jpg文件上传一次并拿回显地址去访问，如下所示（**注意：上传是在192.168.156.5上进行的**）。

```html
[root@itcast05 local]# /usr/bin/fdfs_upload_file  /etc/fdfs/client.conf /usr/local/3.jpg
group1/M00/00/00/wKicCFjj1xqAcN8EAAHk-VzqZ6w619.jpg
[root@itcast05 local]# 
```

​       我们在地址栏输入：http://192.168.156.7:8888/group1/M00/00/00/wKicCFjj1xqAcN8EAAHk-VzqZ6w619.jpg，这时我们访问不到图片，如下图所示。

![13.25](C:\Users\Administrator\Desktop\笔记\fastDFS\13.25.png)

​        原因是我们的虚拟机的防火墙把端口8888给阻拦了，禁止外界访问，解决方法有两个：

​       1.关闭虚拟机的防火墙，并禁止开启自启动，大家可以参考：http://blog.csdn.net/u012453843/article/details/52411019这篇博客进行防火墙的关闭操作。

​       2.不关闭防火墙，只是让外界可以访问8888端口，这在实际环境中更实用，因此，我们采用方法2。

方法2的操作方法如下：

​       使用命令：vim /etc/sysconfig/iptables打开编辑界面，如下图所示，我们添加的一行内容是：-A INPUT -m state --state NEW -m tcp -p tcp --dport 8888 -j ACCEPT

![13.26](C:\Users\Administrator\Desktop\笔记\fastDFS\13.26.png)

​        编辑完之后，保存退出，之后需要重新启动防火墙，重启的命令：service iptables restart，如下图所示。

![13.27](C:\Users\Administrator\Desktop\笔记\fastDFS\13.27.png)

​        这样192.168.156.7这台设备的防火墙我们便配置好了，其它三台设备我们也都配置一下防火墙。

​        配置过防火墙之后，我们再访问http://192.168.156.7:8888/group1/M00/00/00/wKicCFjj1xqAcN8EAAHk-VzqZ6w619.jpg，这时我们便可以看到图片了，如下图所示：就成功了。

**一、配置反向代理**

​       我们需要在两个跟踪器上安装nginx（也就是192.168.156.5和192.168.156.6）以提供反向代理服务，目的是使用统一的一个IP地址对外提供服务。为了避免一些不必要的错误，我们先把其它四台虚拟机的窗口关掉。

​       1.解压ngx_cache_purge-2.3.tar.gz，解压命令：tar -zxvf ngx_cache_purge-2.3.tar.gz -C /usr/local/fast/，如下图所示（另一台设备就不粘贴图片了）。

![14.1](C:\Users\Administrator\Desktop\笔记\fastDFS\14.1.png)

 解压完之后我们在/usr/local/fast/目录下可以看到多了一个ngx_cache_purge-2.3文件夹。如下图所示。

![14.2](C:\Users\Administrator\Desktop\笔记\fastDFS\14.2.png)

  2.下载需要的依赖库，在两台设备上依次执行下面四条命令。

```html
yum install pcre
yum install pcre-devel
yum install zlib
yum install zlib-devel
```

​     3.为两台设备都安装nginx，我们在XShell的下方的输入框中输入命令：cd /usr/local/software/并敲回车，两个窗口都会进入到/usr/local/software目录下，然后在下面的输入框再输入"ll"来查看/usr/local/software目录下的文件，如下图所示（只有输入框左边的图标是多窗口的情况下才能一次作用所有窗口，如果当前是单窗口图标，就如下图那样选择全部XShell）。

![14.3](C:\Users\Administrator\Desktop\笔记\fastDFS\14.3.png)

​      接着，我们在下面的输入框中输入：tar -zxvf nginx-1.6.2.tar.gz -C /usr/local/并按回车，会在两个窗口同时执行解压操作。如下图所示。

![14.4](C:\Users\Administrator\Desktop\笔记\fastDFS\14.4.png)

​      接下来我们在下面的输入框中输入：cd /usr/local并按回车，两台设备都进入到/usr/local/nginx-1.6.2目录下。如下图所示。

![14.5](C:\Users\Administrator\Desktop\笔记\fastDFS\14.5.png)

​        接着，在下面的输入框中加入模块命令：./configure --add-module=/usr/local/fast/ngx_cache_purge-2.3，回车就会在两台设备上都执行添加cache模块并会检查环境。

![14.6](C:\Users\Administrator\Desktop\笔记\fastDFS\14.6.png)

​        接着在下面的输入框中输入命令：make && make install，回车就会在两台设备上都执行编译安装。如下图所示。

![14.7](C:\Users\Administrator\Desktop\笔记\fastDFS\14.7.png)

​       下面我们需要修改下两台设备/usr/local/nginx/conf/目录下的nginx.conf文件，大家可以直接把下面代码替换这个目录下的该文件，也可以直接到：http://download.csdn.net/detail/u012453843/9803673这个地址下载nginx.conf文件来替换。不过由于我们搭建环境的虚拟机IP可能不一样，因此，我们需要根据实际情况修改下IP等信息。（**注意192.168.156.5和192.168.156.6这两台设备的/usr/local/nginx/conf/目录下的nginx.conf都要修改**）

```
#user  nobody;
worker_processes  1;
 
#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;
 
#pid        logs/nginx.pid;
 
 
events {
    worker_connections  1024;
    use epoll;
}
 
 
http {
    include       mime.types;
    default_type  application/octet-stream;
 
    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';
 
    #access_log  logs/access.log  main;
 
    sendfile        on;
    tcp_nopush      on;
    #tcp_nopush     on;
 
    #keepalive_timeout  0;
    keepalive_timeout  65;
 
    #gzip  on;
    #设置缓存
    server_names_hash_bucket_size 128;
    client_header_buffer_size 32k;
    large_client_header_buffers 4 32k;
    client_max_body_size 300m;
 
    proxy_redirect off;
    proxy_set_header Host $http_host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_connect_timeout 90;
    proxy_send_timeout 90;
    proxy_read_timeout 90;
    proxy_buffer_size 16k;
    proxy_buffers 4 64k;
    proxy_busy_buffers_size 128k;
    proxy_temp_file_write_size 128k;
    #设置缓存存储路径，存储方式，分别内存大小，磁盘最大空间，缓存期限
    proxy_cache_path /fastdfs/cache/nginx/proxy_cache levels=1:2
    keys_zone=http-cache:200m max_size=1g inactive=30d;
    proxy_temp_path /fastdfs/cache/nginx/proxy_cache/tmp;
    #group1的服务设置
    upstream fdfs_group1 {
         server 192.168.156.7:8888 weight=1 max_fails=2 fail_timeout=30s;
         server 192.168.156.8:8888 weight=1 max_fails=2 fail_timeout=30s;
    }
    #group2的服务设置
    upstream fdfs_group2 {
         server 192.168.156.9:8888 weight=1 max_fails=2 fail_timeout=30s;
         server 192.168.156.10:8888 weight=1 max_fails=2 fail_timeout=30s;
    }
 
    server {
        listen       8000;
        server_name  localhost;
 
        #charset koi8-r;
 
        #access_log  logs/host.access.log  main;
        #group1的负载均衡配置
        location /group1/M00 {
            proxy_next_upstream http_502 http_504 error timeout invalid_header;
            proxy_cache http-cache;
            proxy_cache_valid 200 304 12h;
            proxy_cache_key $uri$is_args$args;
            #对应group1的服务设置
            proxy_pass http://fdfs_group1;
            expires 30d;
        }
 
        location /group2/M00 {
            proxy_next_upstream http_502 http_504 error timeout invalid_header;
            proxy_cache http-cache;
            proxy_cache_valid 200 304 12h;
            proxy_cache_key $uri$is_args$args;
            #对应group2的服务设置
            proxy_pass http://fdfs_group2;
            expires 30d;
         }
 
        location ~/purge(/.*) {
            allow 127.0.0.1;
            allow 192.168.156.0/24;
            deny all;
            proxy_cache_purge http-cache $1$is_args$args;
        }
 
        location / {
            root   html;
            index  index.html index.htm;
        }
 
        #error_page  404              /404.html;
 
        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
 
        # proxy the PHP scripts to Apache listening on 127.0.0.1:80
        #
        #location ~ \.php$ {
        #    proxy_pass   http://127.0.0.1;
        #}
 
        # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
        #
        #location ~ \.php$ {
        #    root           html;
        #    fastcgi_pass   127.0.0.1:9000;
        #    fastcgi_index  index.php;
        #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
        #    include        fastcgi_params;
        #}
 
        # deny access to .htaccess files, if Apache's document root
        # concurs with nginx's one
        #
        #location ~ /\.ht {
        #    deny  all;
        #}
    }
 
 
    # another virtual host using mix of IP-, name-, and port-based configuration
    #
    #server {
    #    listen       8000;
    #    listen       somename:8080;
    #    server_name  somename  alias  another.alias;
 
    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}
 
 
    # HTTPS server
    #
    #server {
    #    listen       443 ssl;
    #    server_name  localhost;
 
    #    ssl_certificate      cert.pem;
    #    ssl_certificate_key  cert.key;
 
    #    ssl_session_cache    shared:SSL:1m;
    #    ssl_session_timeout  5m;
 
    #    ssl_ciphers  HIGH:!aNULL:!MD5;
    #    ssl_prefer_server_ciphers  on;
 
    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}
 
}                             
```

​      修改完nginx.conf文件之后，我们下面需要创建/fastdfs/cache/nginx/proxy_cache和/fastdfs/cache/nginx/proxy_cache/tmp目录，这是因为我们在nginx.conf文件中配置缓存路径时指定了该目录，但是这两个目录目前还不存在，因此我们需要在192.168.156.5和192.168.156.6这两台设备上都创建下这两个目录，由于涉及到多级，因此需要递归创建目录，使用命令：mkdir -p /fastdfs/cache/nginx/proxy_cache和mkdir -p /fastdfs/cache/nginx/proxy_cache/tmp，如下图所示。

![14.8](C:\Users\Administrator\Desktop\笔记\fastDFS\14.8.png)

​       **由于我们配置了两个tracker的访问端口是8000，而我们的防火墙是不允许访问该端口的，因此我们需要修改下防火墙，使其允许访问8000端口，这个操作我在上篇和中篇都介绍过了，这里就不啰嗦了。**

​       下面我们便来启动这两台设备上的nginx。启动所使用的命令是/usr/local/nginx/sbin/nginx。启动完之后，可以使用ps -ef | grep nginx命令来查看nginx是否正常启动，如果看到root       4027      1  0 08:18 ?        00:00:00 nginx: master process /usr/local/nginx/sbin/nginx这条信息，说明正常启动了。

```html
[root@itcast05 conf]# /usr/local/nginx/sbin/nginx
[root@itcast05 conf]# ps -ef | grep nginx
root       4027      1  0 08:18 ?        00:00:00 nginx: master process /usr/local/nginx/sbin/nginx
nobody     4028   4027  0 08:18 ?        00:00:00 nginx: worker process      
nobody     4029   4027  0 08:18 ?        00:00:00 nginx: cache manager process
nobody     4030   4027  0 08:18 ?        00:00:00 nginx: cache loader process
root       4032   1522  0 08:18 pts/0    00:00:00 grep nginx
```

​        两台设备都启动完nginx之后，我们再在192.168.156.5上上传两次次图片，第一次返回的路径是在group1下，第二次返回的路径是在group2下。

```html
[root@itcast05 conf]# /usr/bin/fdfs_upload_file  /etc/fdfs/client.conf /usr/local/3.jpg
group1/M00/00/00/wKicCFjkOVGAMlQvAAHk-VzqZ6w757.jpg
[root@itcast05 conf]# /usr/bin/fdfs_upload_file  /etc/fdfs/client.conf /usr/local/3.jpg
group2/M00/00/00/wKicCVjkOeaAVb0dAAHk-VzqZ6w123.jpg
[root@itcast05 conf]# 
```

​        由于我们在192.168.156.5和192.168.156.6上配置了代理，代理端口是8000，所以我们可以访问这两个IP的8000端口来访问我们刚才上传的图片，如下图所示（我们访问http://192.168.156.5:8000/group1/M00/00/00/wKicCFjkOVGAMlQvAAHk-VzqZ6w757.jpg也能访问到该图片）。这说明我们配置的代理完全没问题。

​    我们知道，nginx对外提供服务有可能碰到服务挂掉的时候，这时候高可用就显得异常重要了，因此现在我们搭建一个nginx和keepalived结合实现的nginx集群高可用的环境，大家可以参考http://blog.csdn.net/u012453843/article/details/69668663这篇博客进行学习。

​       我们现在要把keepalived实现的nginx集群高可用应用到我们的FastDFS集群当中，现在用于搭建nginx集群高可用的设备是192.168.156.11和192.168.156.12，我们只需要修改下这两台设备的nginx.conf文件，配置文件如下

```html
#user  nobody;
worker_processes  1;

#error_log  logs/error.log;

#error_log  logs/error.log  notice;

#error_log  logs/error.log  info;

#pid        logs/nginx.pid;

events {
    worker_connections  1024;
}

http {

    include       mime.types;

    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '

    #                  '$status $body_bytes_sent "$http_referer" '

    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;

    #tcp_nopush     on;

    #keepalive_timeout  0;

    keepalive_timeout  65;

    #gzip  on;

	upstream fastdfs_tracker {

	   server 192.168.156.5:8000 weight=1 max_fails=2 fail_timeout=30s;
	   server 192.168.156.6:8000 weight=1 max_fails=2 fail_timeout=30s;
	}

    server {

        listen       80;

        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location /fastdfs {
           root html;
           index index.html index.htm;
           proxy_pass http://fastdfs_tracker/;
           proxy_set_header Host $http_host;
           proxy_set_header Cookie $http_cookie;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
           client_max_body_size 300m;
        }
        #error_page  404              /404.html;
        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}
```

​      我们对配置文件做了两处修改，一处是添加了负载均衡upstream fastdfs_tracker，如下所示。我们是把192.168.156.5和192.168.156.6两台设备作为tracker，现在我们加了一层nginx来代理这两个tracker。

```html
upstream fastdfs_tracker {
   server 192.168.156.5:8000 weight=1 max_fails=2 fail_timeout=30s;
   server 192.168.156.6:8000 weight=1 max_fails=2 fail_timeout=30s;
}
```

​      第二处修改是添加了一个location并且匹配规则是路径当中有fastdfs。如下所示。

```html
location /fastdfs {
           root html;
           index index.html index.htm;
           proxy_pass http://fastdfs_tracker/;
           proxy_set_header Host $http_host;
           proxy_set_header Cookie $http_cookie;
           proxy_set_header X-Real-IP $remote_addr;
		   proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
           client_max_body_size 300m;
        }
```

​       做好了修改之后，我们只需重新启动192.168.156.11和192.168.156.12这两台设备的nginx即可

```html
[root@nginx1 conf]#  /usr/local/nginx/sbin/nginx -s reload
[root@nginx1 conf]#
[root@nginx2 conf]# /usr/local/nginx/sbin/nginx -s reload
[root@nginx2 conf]#
```

​       这样我们便配置好了虚拟IP，现在我们从192.168.156.5再上传一张图片，如下所示

```html
[root@itcast05 conf]# /usr/bin/fdfs_upload_file /etc/fdfs/client.conf /usr/local/3.jpg 
group1/M00/00/00/wKicB1jqnPqARiT6AAHk-VzqZ6w956.jpg
[root@itcast05 conf]# 
```

​       我们现在就用虚拟IP192.168.156.110来访问我们刚才上传的图片，只是注意在地址栏中要记得输入fastdfs（这是我们nginx.conf文件中location /fastdfs{}规则规定的）。如下图所示，发现，我们通过虚拟IP便可以访问我们上传的图片了。这样的好处是，对用户来说，只需要访问这个虚拟IP就可以了，不用关心FastDFS集群内部的转发机制。

​       这样我们的FastDFS集群便搭建完了，搭建完后的集群图如下图所示。这个集群当中192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10这四台设备以8888端口对外提供服务，我们使用Tracker跟踪器管理这四台storage设备，两个Tracker的nginx对外提供的端口号是8000，也就是我们可以使用两台Tracker的任何一台设备的IP并且使用端口8000来访问存储在storage上的资源文件。其实我们完全可以在两台Tracker设备上搭建keepalived和nginx相结合的高可用环境并且对外提供虚拟IP192.168.156.110和端口80来访问资源文件。只不过这里为了展示多层nginx负载均衡所以才在192.168.156.11和192.168.156.12上专门搭建了keepalived和nginx相结合的高可用环境，由这两台设备对外提供虚拟IP服务，由于端口使用了默认的80，因此我们在使用虚拟IP192.168.156.110访问图片的时候才不用输入端口号的。

![15](C:\Users\Administrator\Desktop\笔记\fastDFS\15.png)

   备注：启动集群步骤

​        1.启动6台设备（192.168.156.5、192.168.156.6、192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10）的nginx（其中192.168.156.11和192.168.156.12配置了keepalived开机自启动，顺带会启动nginx，因此这两台设备不用启动nginx）

​        2.启动tracker（192.168.156.5和192.168.156.6，启动命令：/etc/init.d/fdfs_trackerd start）

​        3.启动storage（192.168.156.7、192.168.156.8、192.168.156.9、192.168.156.10，启动命令：/etc/init.d/fdfs_storaged start）

​        这样FastDFS集群便都启动完了。



