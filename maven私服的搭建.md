# **一、Maven介绍**

​	Apache Maven是一个创新的软件项目管理和综合工具。
​	Maven提供了一个基于项目对象模型（POM）文件的新概念来管理项目的构建，可以从一个中心资料片管理项目构建，报告和文件。
​	Maven最强大的功能就是能够自动下载项目依赖库。
​	Maven提供了开发人员构建一个完整的生命周期框架。开发团队可以自动完成项目的基础工具建设，Maven使用标准的目录结构和默认构建生命周期。
​	在多个开发团队环境时，Maven可以设置按标准在非常短的时间里完成配置工作。由于大部分项目的设置都很简单，并且可重复使用，Maven让开发人员的工作更轻松，同时创建报表，检查，构建和测试自动化设置。
​	Maven项目的结构和内容在一个XML文件中声明，pom.xml 项目对象模型（POM），这是整个Maven系统的基本单元。

# **二、私服介绍**

​	私服是指私有服务器，是架设在局域网的一种特殊的远程仓库，目的是代理远程仓库及部署第三方构建。有了私服之后，当 Maven 需要下载构件时，直接请求私服，私服上存在则下载到本地仓库；否则，私服请求外部的远程仓库，将构件下载到私服，再提供给本地仓库下载。

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161220162726057-620806393.png)

# **三、Nexus介绍**

​	Nexus是一个强大的Maven仓库管理器，它极大地简化了本地内部仓库的维护和外部仓库的访问。
如果使用了公共的Maven仓库服务器，可以从Maven中央仓库下载所需要的构件（Artifact），但这通常不是一个好的做法。
​	正常做法是在本地架设一个Maven仓库服务器，即利用Nexus私服可以只在一个地方就能够完全控制访问和部署在你所维护仓库中的每个Artifact。
​	Nexus在代理远程仓库的同时维护本地仓库，以降低中央仓库的负荷,节省外网带宽和时间，Nexus私服就可以满足这样的需要。
​	Nexus是一套“开箱即用”的系统不需要数据库，它使用文件系统加Lucene来组织数据。
​	Nexus使用ExtJS来开发界面，利用Restlet来提供完整的REST APIs，通过m2eclipse与Eclipse集成使用。
​	Nexus支持WebDAV与LDAP安全身份认证。
​	Nexus还提供了强大的仓库管理功能，构件搜索功能，它基于REST，友好的UI是一个extjs的REST客户端，它占用较少的内存，基于简单文件系统而非数据库。

## 3.1为什么要构建Nexus私服？

​	如果没有Nexus私服，我们所需的所有构件都需要通过maven的中央仓库和第三方的Maven仓库下载到本地，而一个团队中的所有人都重复的从maven仓库下载构件无疑加大了仓库的负载和浪费了外网带宽，如果网速慢的话，还会影响项目的进程。很多情况下项目的开发都是在内网进行的，连接不到maven仓库怎么办呢？开发的公共构件怎么让其它项目使用？这个时候我们不得不为自己的团队搭建属于自己的maven私服，这样既节省了网络带宽也会加速项目搭建的进程，当然前提条件就是你的私服中拥有项目所需的所有构件。

总之，在本地构建nexus私服的好处有：
1）加速构建；
2）节省带宽；
3）节省中央maven仓库的带宽；
4）稳定（应付一旦中央服务器出问题的情况）；
5）控制和审计；
6）能够部署第三方构件；
7）可以建立本地内部仓库；
8）可以建立公共仓库
这些优点使得Nexus日趋成为最流行的Maven仓库管理器。

# **四、Maven的安装**

1、JDK环境

2、安装maven

# **五、Nexus安装**

Nexus的安装有两种实现方式：
1）war包安装方式
下载地址：https://sonatype-download.global.ssl.fastly.net/nexus/oss/nexus-2.14.2-01.war
直接将war包放在tomcat的根目录下，启动tomcat就可以用了

2）源码安装方式（之前在用的是2.14.4版本，这里是新版本）
下载地址：https://www.sonatype.com/download-oss-sonatype           （云盘下载：http://pan.baidu.com/s/1miKFm5a）

[root@master-node ~]# cd /usr/local/src/
[root@master-node src]# wget https://sonatype-download.global.ssl.fastly.net/nexus/3/nexus-3.2.0-01-unix.tar.gz
[root@master-node src]# tar -zvxf nexus-3.2.0-01-unix.tar.gz
[root@master-node src]# mv nexus-3.2.0-01 /usr/local/nexus

启动nexus（默认端口是8081）
[root@master-node src]# /usr/local/nexus/bin/nexus
WARNING: ************************************************************
WARNING: Detected execution as "root" user. This is NOT recommended!
WARNING: ************************************************************
Usage: /usr/local/nexus/bin/nexus {start|stop|run|run-redirect|status|restart|force-reload}
[root@master-node src]# /usr/local/nexus/bin/nexus start
WARNING: ************************************************************
WARNING: Detected execution as "root" user. This is NOT recommended!
WARNING: ************************************************************
Starting nexus
上面在启动过程中出现告警：不推荐使用root用户启动。这个告警不影响nexus的正常访问和使用。
去掉上面WARNING的办法：
[root@master-node src]# vim /etc/profile
......
export RUN_AS_USER=root
[root@master-node src]# source /etc/profile
[root@master-node src]# lsof -i:8081            //nexus服务启动成功后，需要稍等一段时间，8081端口才起来
COMMAND PID USER FD TYPE DEVICE SIZE/OFF NODE NAME
java 1486 root 859u IPv4 23504303 0t0 TCP *:tproxy (LISTEN)

在部署机上的iptables里打开8081端口
[root@master-node src]# vim /etc/sysconfig/iptables
....
-A INPUT -p tcp -m state --state NEW -m tcp --dport 8081 -j ACCEPT
[root@master-node src]# /etc/init.d/iptables restart

访问nexus，即http://localhost:8081    （如果出现404，就访问http://localhost:8081/nexus）

点击右上角“Log in”，
输入默认用户名(admin)和默认密码（admin123）登录。

# **六、Nexus说明**

## 6.1、component name的一些说明：

​    1）maven-central：maven中央库，默认从https://repo1.maven.org/maven2/拉取jar
​    2）maven-releases：私库发行版jar
​    3）maven-snapshots：私库快照（调试版本）jar
​    4）maven-public：仓库分组，把上面三个仓库组合在一起对外提供服务，在本地maven基础配置settings.xml中使用。

## 6.2、Nexus默认的仓库类型有以下四种：

​    1）group(仓库组类型)：又叫组仓库，用于方便开发人员自己设定的仓库；
​    2）hosted(宿主类型)：内部项目的发布仓库（内部开发人员，发布上去存放的仓库）；
​    3）proxy(代理类型)：从远程中央仓库中寻找数据的仓库（可以点击对应的仓库的Configuration页签下Remote Storage Location属性的值即被代理的远程仓库的路径）；
​    4）virtual(虚拟类型)：虚拟仓库（这个基本用不到，重点关注上面三个仓库的使用）；

## 6.3、Policy(策略):表示该仓库为发布(Release)版本仓库还是快照(Snapshot)版本仓库；

## 6.4、Public Repositories下的仓库

   1）3rd party: 无法从公共仓库获得的第三方发布版本的构件仓库，即第三方依赖的仓库，这个数据通常是由内部人员自行下载之后发布上去；
   2）Apache Snapshots: 用了代理ApacheMaven仓库快照版本的构件仓库
   3）Central: 用来代理maven中央仓库中发布版本构件的仓库
   4）Central M1 shadow: 用于提供中央仓库中M1格式的发布版本的构件镜像仓库
   5）Codehaus Snapshots: 用来代理CodehausMaven 仓库的快照版本构件的仓库
   6）Releases: 内部的模块中release模块的发布仓库，用来部署管理内部的发布版本构件的宿主类型仓库；release是发布版本；
   7）Snapshots:发布内部的SNAPSHOT模块的仓库，用来部署管理内部的快照版本构件的宿主类型仓库；snapshots是快照版本，也就是不稳定版本
所以自定义构建的仓库组代理仓库的顺序为：Releases，Snapshots，3rd party，Central。也可以使用oschina放到Central前面，下载包会更快。

## 6.5、Nexus默认的端口是8081，可以在etc/nexus-default.properties配置中修改。

## 6.6、Nexus默认的用户名密码是admin/admin123

## 6.7、当遇到奇怪问题时，重启nexus，重启后web界面要1分钟左右后才能访问。

## 6.8、Nexus的工作目录是sonatype-work（路径一般在nexus同级目录下）

[root@master-node local]# pwd
/usr/local
[root@master-node local]# ls nexus/
bin deploy etc lib LICENSE.txt NOTICE.txt public system
[root@master-node local]# ls sonatype-work/
nexus3
[root@master-node local]# ls sonatype-work/nexus3/
backup blobs cache db elasticsearch etc generated-bundles health-check instances keystores lock log orient port tmp

# **七、Nexus仓库分类的概念**

1）Maven可直接从宿主仓库下载构件,也可以从代理仓库下载构件,而代理仓库间接的从远程仓库下载并缓存构件
2）为了方便,Maven可以从仓库组下载构件,而仓库组并没有时间的内容(下图中用虚线表示,它会转向包含的宿主仓库或者代理仓库获得实际构件的内容).

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161220164452667-1588016524.png)

## **7.1、Nexus的web界面功能介绍**

1.Browse Server Content

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221105344026-1095529496.png)

1.1  Search
这个就是类似Maven仓库上的搜索功能，就是从私服上查找是否有哪些包。
注意：
1）在Search这级是支持模糊搜索的，如图所示：

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221105547167-2017264888.png)

2）如果进入具体的目录，好像不支持模糊搜索，如图所示：

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221105634245-19314456.png)

1.2  Browse

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221105808261-1531078817.png)

1）Assets
这是能看到所有的资源，包含Jar，已经对Jar的一些描述信息。
2）Components
这里只能看到Jar包。

**2.Server Adminstration And configuration**

看到这个选项的前提是要进行登录的，如上面已经介绍登陆方法，右上角点击“Sign In”的登录按钮，输入admin/admin123,登录成功之后，即可看到此功能，如图所示：

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221110207479-318443915.png)

2.1 Blob Stores
文件存储的地方，创建一个目录的话，对应文件系统的一个目录，如图所示：

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221110317932-1418604671.png)

2.2 Repositories

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221110524073-153249455.png)

1）Proxy
这里就是代理的意思，代理中央Maven仓库，当PC访问中央库的时候，先通过Proxy下载到Nexus仓库，然后再从Nexus仓库下载到PC本地。
这样的优势只要其中一个人从中央库下来了，以后大家都是从Nexus私服上进行下来，私服一般部署在内网，这样大大节约的宽带。
创建Proxy的具体步骤
1--点击“Create Repositories”按钮

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221110640089-1389328954.png)

2--选择要创建的类型

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221110758542-1042948386.png)

3--填写详细信息
Name：就是为代理起个名字
Remote Storage: 代理的地址，Maven的地址为: https://repo1.maven.org/maven2/
Blob Store: 选择代理下载包的存放路径

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221111233651-1321037653.png)

2）Hosted
Hosted是宿主机的意思，就是怎么把第三方的Jar放到私服上。
Hosted有三种方式，Releases、SNAPSHOT、Mixed
Releases: 一般是已经发布的Jar包
Snapshot: 未发布的版本
Mixed：混合的
Hosted的创建和Proxy是一致的，具体步骤和上面基本一致。如下：

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221111325104-723208432.png)

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221111344573-1949446719.png)

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221111419870-2005652543.png)

**注意事项：**
Deployment Pollcy: 需要把策略改成“Allow redeploy”。

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221111455589-1303191395.png)

3）Group
能把两个仓库合成一个仓库来使用，目前没使用过，所以没做详细的研究。

2.3 Security
这里主要是用户、角色、权限的配置（上面已经提到了在这里添加用户和角色等）

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221111747745-1991046629.png)

2.4 Support
包含日志及数据分析。

![img](https://images2015.cnblogs.com/blog/907596/201612/907596-20161221111920261-269936853.png)

2.5 System
主要是邮件服务器，调度的设置地方
这部分主要讲怎么和Maven做集成,集成的方式主要分以下种情况：代理中央仓库、Snapshot包的管理、Release包的管理、第三方Jar上传到Nexus上。

## 7.2、POM文件配置

```java
 <!-- 如果jar没有 会通过私服代理去中央仓库下载 -->
    <repositories>
        <repository>
            <id>nexus-proxy-jay</id>
            <name>nexus-proxy-jay</name>
            <url>http://47.92.193.183:8081/nexus/content/repositories/nexus-proxy-jay/</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
            <releases>
                <enabled>true</enabled>
            </releases>
        </repository>
    </repositories>

    <distributionManagement>
        <snapshotRepository>
            <!--id的名字可以任意取，但是在setting文件中的属性<server>的ID与这里一致-->
            <id>nexus</id>
            <name>Nexus Snapshot</name>
            <!--指向仓库类型为host(宿主仓库）的储存类型为Snapshot的仓库-->
            <url>http://47.92.193.183:8081/nexus/content/repositories/snapshots/</url>
        </snapshotRepository>
        <repository>
            <id>nexus</id>
            <!--指向仓库类型为host(宿主仓库）的储存类型为Release的仓库-->
            <name>Nexus releases</name>
            <url>http://47.92.193.183:8081/nexus/content/repositories/releases/</url>
        </repository>
    </distributionManagement>
```

## 7.3、setting文件的配置

```java
<mirrors>
	<mirror>
        <id>nexus</id>
        <name>Public Repositories</name>
        <!--镜像采用配置好的组的地址-->
        <url>http://47.92.193.183:8081/nexus/content/groups/public/</url>
        <mirrorOf>*</mirrorOf>
	</mirror>
<mirrors>

<!-- 这个是配置私服登录权限 -->
<servers>
	<server>
      <id>nexus</id>
      <username>admin</username>
      <password>admin123</password>
     </server>
<servers>
```

上面修改的Pom文件如截图中的名字要跟settings.xml文件中的名字(nexus)一定要对应上。

3）上传到Nexus上

1--项目编译成的jar是Snapshot(POM文件的头部)

```java
`<groupId>com.woasis<``/groupId``>``<artifactId>``test``-nexus<``/artifactId``>``<version>1.0.0-<span style=``"color: #ff0000;"``>SHAPSHOT<``/span``><``/version``>``<packaging>jar<``/packaging``>`
```

2--使用mvn deploy命令运行即可（运行结果在此略过）

3--因为Snapshot是快照版本，默认他每次会把Jar加一个时间戳，做为历史备份版本。

**Releases包的管理**

1）与Snapshot大同小异，只是上传到私服上的Jar包不会自动带时间戳
2）与Snapshot配置不同的地方，就是工程的PMO文件，加入repository配置

```java
`<distributionManagement>``        ``<repository>``            ``<``id``>nexus<``/id``>``            ``<name>Nexus Snapshot<``/name``>``            ``<url>http:``//192``.168.1.14:8081``/repository/maven-releases/``<``/url``>``        ``<``/repository``>`
```

3）打包的时候需要把Snapshot去掉

```java
`<groupId>com.woasis<``/groupId``>``    ``<artifactId>``test``-nexus<``/artifactId``>``    ``<version>1.0.0<``/version``>``<packaging>jar<``/packaging``>`
```

**第三方Jar上传到Nexus**

[root@master-node src]# mvn deploy:deploy-file -DgroupId=org.jasig.cas.client -DartifactId=cas-client-core -Dversion=3.1.3 -Dpackag
**注意事项：**
-DrepositoryId=nexus 对应的就是Maven中settings.xml的认证配的名字。

# **八、Nexus库被删除的恢复方法** 

​	在整理Maven私服的时候，不小心把Release库删掉了。瞬间冒出冷汗来了！脑子里闪过第一个办法就是看是否有回收站，恰好在Nexus UI中看到了一个叫Trash...的功能。可是我点击后发现只有Empty Trash的功能，这要按下去还得了啊。

![img](https://img2018.cnblogs.com/blog/907596/201907/907596-20190723120600389-1969163511.png)

幸好！找到了被删除文件恢复的办法。最后按照官方所提供的办法成功地恢复了被删Release库下所有的数据。操作步骤如下：

**1）首先找到sonatype-work/nexus/trash 下找到你删除的库，并保存到其他地方;**

![img](https://img2018.cnblogs.com/blog/907596/201907/907596-20190723120741962-309638239.png)

**2）然后通过nexus控制台点击Add，选择Hosted Repository，然后输入被删除的Repository信息；**

![img](https://img2018.cnblogs.com/blog/907596/201907/907596-20190723121116376-614786369.png)

**3）把刚才保存的库文件copy到指定的sonatype-work/nexus/storage/[releases]下即可;**
**4）点击列表中的Public Repositories，然后在下方的Configuration标签下将Releases添加到Ordered Group Repositories中;**

![img](https://img2018.cnblogs.com/blog/907596/201907/907596-20190723121124448-1899144020.png)

**5）最后Save保存就可以了。**



# 九、私服release库和snapshot库的区别

## **1、release库（发布库）使用规则及场景：**

**release库是存放稳定版本包的仓库，线上发布的程序都应从release库中引用正确版本进行使用**

release库仓库名中带有“releases”标识，包括libs-releases-local，plugins-releases-local两个仓库。私服中release库使用规则如下：

a) release库不允许删除jar；

b) release库不允许同版本更新jar包（即同一个版本jar包只存在一个）；

c) release库上传的jar包版本号（version）不能以“-SNAPSHOT”结束（版本号中的SNAPSHOT是release版和snapshot版区别的唯一标识）；

d) 第三方包（非公司内部开发）仅可引用release版

e) 如可能，请提供接口对应源码，方便引用方使用。

**release库使用场景：**

根据上述release库的使用规则可知，若在开发过程中引用的**不是release版的库，很有可能因为jar包更新后引用方不知道而引起代码错误**，所以在如下场景场景中，请使用release仓库：

**上传/发布：**①当**代码构建出的jar需要给其他程序提供服务**时；当第**三方提供的jar包或其他类型的依赖包不在远程中央仓库中**时；请将正式发布版的jar用命令deploy或者在web端手动上传至私服对应的release仓库。

②如果是jar包提供方，请在**代码变动时及时更新私服中对应的jar包版本**，并联系管理员将淘汰jar包下线，以免引起引用事故；并及时更新发布包releasenotes（推荐在CF中维护），使引用方及时获取版本更新信息。

**下载/依赖：**①当代码需要使用第三方包时，除非对方代码仍在开发过程中，否则请选**择三方包的正式release（发布）版本**。

②如提供方是公司内部其他系统，并且该系统未提供release版本包，请主动要**求该系统负责人提供release版本包**，以保证开发代码版本稳定，并且关注该包的版本升级情况。

## **2、snapshot库（快照库）使用使用规则及场景：**

**snapshot库是存放中间版本包的仓库，代表该库中jar包的程序处于不稳定状态。当代码在开发过程中有其他程序需要引用时，可以提供snapshot版jar包用于调试和测试。由于snapshot库的包依然处于测试状态，所以随时可以上传同版本最新包来替换旧包，基于这种不稳定状态，maven允许snapshot库中的包被编译时随时更新最新版，这就可能会导致每次打包编译时同一个版本jar会包含不同的内容，所以snapshot库中的包是不能用来发布的；**

snapshot库仓库名中带有“snapshots”标识，包括libs-snapshots-local，plugins- snapshots-local两个仓库。私服中snapshot库使用规则如下：

a) 快照库可以删除jar；

b) 快照库可以同版本更新jar包；

c) 第三方包（非公司内部开发）不允许引用快照版

**d) 快照库仅可用来联调测试环节使用，不建议用于线上的稳定发布版本**

e) 快照库上传的jar包版本号（version）必须以“-SNAPSHOT”结束，并上传至私服后系统将自动将“-SNAPSHOT”替换为时间戳串（本地代码引用时依然用“-SNAPSHOT”结束的版本号，无需替换时间戳），一个快照包线上将存在至少两个版本。

**snapshot库使用场景：**

根据上述snapshot库的使用规则可知，snapshot版的包仅供中间过程以供临时引用，若在最终发布过程中引用，**很有可能因为jar包被更新或者未更新而引用方不知道而引起代码错误**，所以请**仅在**如下场景场景中使用snapshot仓库：

**上传/发布：**当**开发中的代码构建出的jar**需要给其他程序提供服务时，请将snapshot版的jar用命令deploy或者在web端手动上传至私服对应的snapshot仓库。

**下载/依赖：**当代码需要使用其他开发过程中代码的jar包时，请依赖该包的snapshot（快照）版本。maven类型工程使用过程中请**使用“-U”强制更新命令**，来获取最新版本包。

## **3、常见问题**

①    上传jar包后，编译时发现代码找parent包，但是私服找不到jar，而且代码中也不需要这个parent

**原因**：问题多发于web端上传jar包时，自动生成pom.xml 文件中自带<parent/>节点

**解决**：使用web端上传时，请注意自行删除节点配置。

② 下载不了最新snapshot版本的jar包

**原因**：发布snapshot版本未连同对应的pom一起发布，只有jar包，没有pom，在maven编译时候会直接报错pom找不到，或者因jar包未更新编译报错。

**解决**：重新连同jar包一起发布即可解决。

③ 译后jar包变多或变少

**原因**：编译环境中传递依赖引用的快照包版本低，引用了错误版本的包进入代码。

**解决**：1.使用如下命令，检查依赖树，确定是哪个包引用到错的版本：**mvn  dependency:tree**；2.清除编译环境未更新的错误包，重新编译。







## 最后将idea 配置成：

![idea](C:\Users\81442\Desktop\笔记\maven 私服的搭建\idea.png)

这样 每次发现私服上有jar 包更新，会自动替代之前的。