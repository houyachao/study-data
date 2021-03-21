## 一、Redis缓存击穿

### ①什么是缓存击穿

![redis--缓存击穿1](C:\Users\Administrator\Desktop\笔记\redis\redis--缓存击穿1.png)

​	当redis中有一条热门数据时，假如给它的过期时间为 1天，当有请求来到时，直接去redis中去查找。如果一天后，这条热门数据失效了，则大量的请求会去mysql数据库中查找。 缓存击穿是缓存穿透的一种。

​	如果是中小型公司，不需要解决缓存击穿问题，因为没有一条那么大的热门数据，导致mysql宕机。

### ②什么时候需要加锁

​	1、共享资源

​	2、共享资源互斥

​	3、多任务环境

### ③ 缓存击穿问题解决方案

​	1、可以加分布式锁： clent 发送请求到Nginx，Nginx 转发到不同的Tomcat上，Tomcat 获取分布式锁后，会去读取mysql数据库，将查询到的数据写回redis，这样其他的访问直接访问redis就好了。加分布式锁，会影响性能问题。

​	2、可以将mysql 中查询出来的数据，缓存到redis中。 clent 发送请求到Nginx，Nginx 转发到不同的Tomcat上，Tomcat会直接去redis中去查询数据，如果没有查询到，直接访问数据库，将查询出来的数据再写会redis。其他的Tomcat也会直接去访问redis，可能第一次访问数据库返回的数据还没有写会redis，其他的Tomcat访问请求会直接访问数据库，这样只让mysql 抵挡一下压力，但是比加分布式锁性能好。

### ④ 缓存雪崩

![redis--缓存雪崩1](C:\Users\Administrator\Desktop\笔记\redis\redis--缓存雪崩1.png)

​	当redis中有多条热门数据时，假如给有效期一天，一天后，这些数据都失效了，就会有大量的请求去访问mysql数据库，那一时刻导致mysql需要承受无比巨大的压力。

​	缓存雪崩和缓存击穿都是缓存穿透的特殊表现形式。。

### ⑤ 缓存雪崩产生的原因

​	1、redis中设置的热点数据有效期都一致。   解决方案：给每条设置随机有效期

​	2、redis挂了。    解决方案：redis集群。如果热门数据比较多，可以使用redis切片搭建集群，每台redis上存三分之一的数据，这样即使有一台redis挂了，也就损失三分之一的数据。如果再增加一台redis的话，会导致数据倾斜，需要将前三台的数据平均到第四台上。。可以利用一致hash算法实现（环型糙，顺时针去存取数据），即使增加redis，也就最多两台redis去同步数据就行了。

​		如果热门数据不是太多，可以使用主从复制，每个redis上都保留了百分之百的数据。

- **二级缓存**：对于热点数据进行二级缓存，并对于不同[级别的缓存设定](http://mp.weixin.qq.com/s?__biz=MzU5NTAzNjM0Mw==&mid=2247485424&idx=3&sn=34a5bfd6373a156e02a676c42d2ccb4d&chksm=fe795628c90edf3ec6d1bcf44b6d20b8202ca8265233914366bfcd6ebfcf0c56250efe7fa7b0&scene=21#wechat_redirect)不同的失效时间，则请求不会直接击穿缓存层到达数据库。

- 这里参考了阿里双11万亿流量的缓存击穿解决方案，解决此问题的关键在于热点访问。由于热点可能随着时间的变化而变化，针对固定的数据进行特殊缓存是不能起到治本作用的，结合LRU算法能够较好的帮我们解决这个问题。那么LRU是什么，下面粗略的介绍一下，有兴趣的可以点击上面的链接查看.

- - **LRU**（Least recently used，最近最少使用）算法根据数据的历史访问记录来进行淘汰数据，其核心思想是“如果数据最近被访问过，那么将来被访问的几率也更高”。最常见的实现是使用一个链表保存[缓存数据](http://mp.weixin.qq.com/s?__biz=MzU5NTAzNjM0Mw==&mid=2247484731&idx=2&sn=932f690ee1b775e864d3e113739a421a&chksm=fe7954e3c90eddf5d2f7de72909a43b5151797df7212b10311c497c3d1708d9a0613279f5734&scene=21#wechat_redirect)。

- 这个链表即是我们的缓存结构，缓存处理步骤为

- - - 首先将新数据放入链表的头部
    - 在进行数据插入的过程中，如果检测到链表中有数据被再次访问也就是有请求再次访问这些数据，那么就其插入的链表的头部，因为它们相对其他数据来说可能是热点数据，具有保留时间更久的意义
    - 最后当链表数据放满时将底部的数据淘汰，也就是不常访问的数据

- - ####  **LRU-K算法 ，其实上面的算法也是该算法的特例情况即LRU-1，上面的算法存在较多的不合理性，在实际的应用过程中采用该算法进行了改进，例如偶然的数据影响会造成命中率较低，比如某个数据即将到达底部即将被淘汰，但由于一次的请求又放入了头部，此后再无该数据的请求，那么该数据的继续存在其实是不合理的，针对这类情况LRU-K算法拥有更好的解决措施。结构图如下所示：**

    #### 

  - ![img](https://mmbiz.qpic.cn/mmbiz_png/bcPwoCALib9JZ9WrHX461dSiaG63jtbIgNIZn2B6hI35gOearps4WDMqulJJVT28ftuFVHpv20ZMGic2XsZPYnD9g/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

  - LRU-K需要多维护一个队列或者更多，用于记录[所有缓存](http://mp.weixin.qq.com/s?__biz=MzU5NTAzNjM0Mw==&mid=2247484719&idx=2&sn=1af585427deecd336d4e38676f485ec4&chksm=fe7954f7c90edde1b4433f7d60cddc6f9cd13dc6461ff6bdf9cebfc89b1b32e6317ac5bc139c&scene=21#wechat_redirect)数据被访问的历史。只有当数据的访问次数达到K次的时候，才将数据放入缓存。当需要淘汰数据时，LRU-K会淘汰第K次访问时间距当前时间最大的数据。

- - - 第一步添加数据照样放入第一个队列的头部
    - 如果数据在该队列里访问没有达到K次（该数值根据具体系统qps来定）则会继续到达链表底部直至淘汰；如果该数据在队列中时访问次数达到了K次，那么它会被加入到接下来的2级(具体需要几级结构也同样结合系统分析)链表中，按照时间顺序在2级链表中排列
    - 接下来2级链表中的操作与上面算法相同，链表中的数据如果再次被访问则移到头部，链表满时，底部数据淘汰



相比LRU，LRU-K需要多维护一个[队列，](http://mp.weixin.qq.com/s?__biz=MzU5NTAzNjM0Mw==&mid=2247484705&idx=3&sn=a92401c2e99375866fc5a18dfa8f62b1&chksm=fe7954f9c90eddef5e6a0f011378fb890fca9f008e87e22d612d1db192881ba4a7463f314830&scene=21#wechat_redirect)用于记录所有缓存数据被访问的历史，所以需要更多的内存空间来用来构建缓存，但优点也很明显，较好的降低了数据的污染率提高了缓存的命中率，对于系统来说可以用一定的硬件成本来换取系统性能也不失为一种办法。当然还有更为复杂的缓存结构算法，点击LRU算法即可学习，例如Two Queues和Mutil Queues等等，本文不过多赘述，只为读者提供一种解决思路。



### ⑥缓存穿透

![缓存穿透](C:\Users\Administrator\Desktop\笔记\redis\缓存穿透.png)

​		缓存穿透：缓存穿透是指查询一个一定不存在的数据，因为缓存中也无该数据的信息，则会直接去数据库层进行查询，从系统层面来看像是穿透了缓存层直接达到db，从而称为缓存穿透，没有了缓存层的保护，这种查询一定不存在的数据对系统来说可能是一种危险，如果有人恶意用这种一定不存在的数据来频繁请求系统，不，准确的说是攻击系统，请求都会到达数据库层导致db瘫痪从而引起系统故障。

### ⑦缓存穿透解决方案

​	1、如果数据不存在，这直接设置为一个 空值 存在redis中，并设置一个过期时间。(最简单的方式)

​	1、可以用布隆过滤器

​	2、 可以使用分布式锁

​	3、 可以不加分布式锁，或者布隆过滤器， 直接查询数据，然后将查询的数据直接存入redis中，这样也会存在一个问题，就是请求mysql的数据不可能是一条，有可能是一批数据同时去访问mysql数据库。

![缓存穿透](C:\Users\Administrator\Desktop\笔记\redis\缓存穿透.png)

 可以使用布隆算法。bloomfilter就类似于一个hash set，用于快速判某个元素是否存在于集合中，其典型的应用场景就是快速判断一个key是否存在于某容器，不存在就直接返回。布隆过滤器的关键就在于hash算法和容器大小。

布隆过滤器是用来判断一个元素是否出现在给定集合中的重要工具，具有快速，比哈希表更节省空间等优点，而缺点在于有一定的误识别率（false-positive，假阳性），亦即，它可能会把不是集合内的元素判定为存在于集合内，不过这样的概率相当小，在大部分的生产环境中是可以接受的；

![布隆算法](C:\Users\Administrator\Desktop\笔记\redis\布隆算法.png)

**布隆过滤器实现原理：**

redis 可以集成redis--布隆过滤器。<http://redis.cn/clients.html#java>

![redis布隆2](C:\Users\Administrator\Desktop\笔记\redis\redis布隆2.png)

1. 其原理比较简单，如下图所示，S集合中有n个元素，利用k个哈希函数，将S中的每个元素映射到一个长度为m的位（bit）数组B中不同的位置上，这些位置上的二进制数均置为1，如果待检测的元素经过这k个哈希函数的映射后，发现其k个位置上的二进制数不全是1，那么这个元素一定不在集合S中，反之，该元素可能是S中的某一个元素（参考1）；

[![img](https://img2018.cnblogs.com/blog/1775037/201910/1775037-20191008173752927-1989369488.png)](https://img2018.cnblogs.com/blog/1775037/201910/1775037-20191008173752927-1989369488.png)

1. 综上描述，那么到底需要多少个哈希函数，以及创建长度为多少的bit数组比较合适，为了估算出k和m的值，在构造一个布隆过滤器时，需要传入两个参数，即可以接受的误判率fpp和元素总个数n（不一定完全精确）。至于参数估计的方法，有兴趣的同学可以参考维基英文页面，下面直接给出公式：
2. 哈希函数的要求尽量满足平均分布，这样既降低误判发生的概率，又可以充分利用bit数组的空间；
3. 根据论文《Less Hashing, Same Performance: Building a Better Bloom Filter》提出的一个技巧，可以用2个哈希函数来模拟k个哈希函数，即gi(x) = h1(x) + ih2(x) ，其中0<=i<=k-1；
4. 在吴军博士的《数学之美》一书中展示了不同情况下的误判率，例如，假定一个元素用16位比特，8个哈希函数，那么假阳性的概率是万分之五，这已经相当小了。

目前已经有相应实现的开源类库，如Google的Guava类库，Twitter的Algebird类库，和ScalaNLP breeze等等，其中Guava 11.0版本中增加了BloomFilter类，它使用了Funnel和Sink的设计，增强了泛化的能力，使其可以支持任何数据类型，其利用murmur3 hash来做哈希映射函数，不过它底层并没有使用传统的java.util.BitSet来做bit数组，而是用long型数组进行了重新封装，大部分操作均基于位的运算，因此能达到一个非常好的性能；下面我们就Guava类库中实现布隆过滤器的源码作详细分析，最后出于灵活性和解耦等因素的考虑，我们想要把布隆过滤器从JVM中拿出来，于是利用了Redis自带的Bitmaps作为底层的bit数组进行重构，另外随着插入的元素越来越多，当实际数量远远大于创建时设置的预计数量时，布隆过滤器的误判率会越来越高，因此在重构的过程中增加了自动扩容的特性，最后通过测试验证其正确性。



布隆算法的错误率主要体现在：

​	1、它告诉你数据存在，那么实际情况下不存在（由于hash碰撞导致的）。

​	2、但是它告诉你数据不存在，那么一定不存在。

影响布隆算法错误率主要原因：

​	1、数组长度影响。

​	2、hash函数的个数影响。

### ⑧ 布隆算法redis的bitmap 实现

```java

/**
 * @author HouYC
 * @create 2020-07-15-22:04
 */
public class BloomFilter_Test {

    private JedisPool jedisPool = null;
    private Jedis jedis = null;
    /**
     * 要存储的数据量··
     */
    private static long n = 10000;
    /**
     * 所能容忍错误率
     */
    private static double fpp = 0.01F;

    /**
     * bit数组长度
     */
    private static long numBits = optimalNumOfBits(n, fpp);
    /**
     * hash函数个数
     */
    private int numHashFunctions = optimalNumOfHashFunctions(n, numBits);


    public static void main(String[] args) {
        System.out.println(numBits);
//        long[] indexs = new BloomFilter_Test().getIndexs("hello");
        BloomFilter_Test filterTest = new BloomFilter_Test();
        filterTest.init();

        int ex_count = 0;
        int ne_count = 0;
        /**
         * 存在： 不一定存在
         * 不存在：一定不存在
         */
        for (int i = 0; i < 20000; i++) {
//            filterTest.put("bf",100 + i + "");
            boolean exist = filterTest.isExist("bf", 100 + i + "");
            if(exist){
                ex_count++;
            }else{
                ne_count++;
            }
        }
        //ex_count:6729	ne_count 3271
        System.out.println("ex_count:" + ex_count + "\t" + "ne_count " + ne_count);
    }

    public void init(){
        //测试连接redis
        jedisPool = new JedisPool("192.168.150.111", 6379);
        jedis = jedisPool.getResource();
    }

    private long getCount(){
        Pipeline pipeline = jedis.pipelined();
        Response<Long> bf = pipeline.bitcount("bf");
        pipeline.sync();
        Long count = bf.get();
        pipeline.close();
        return count;
    }

    /**
     * 判断keys是否存在于集合where中
     */
    public boolean isExist(String where, String key) {
        long[] indexs = getIndexs(key);
        boolean result;
        //这里使用了Redis管道来降低过滤器运行当中访问Redis次数 降低Redis并发量
        Pipeline pipeline = jedis.pipelined();
        try {
            for (long index : indexs) {
                pipeline.getbit(where, index);
            }
            result = !pipeline.syncAndReturnAll().contains(false);
        } finally {
            pipeline.close();

        }
//        if (!result) {
//            put(where, key);
//        }
        return result;
    }

    /**
     * 将key存入redis bitmap
     */
    private void put(String where, String key) {
        long[] indexs = getIndexs(key);
        //这里使用了Redis管道来降低过滤器运行当中访问Redis次数 降低Redis并发量
        Pipeline pipeline = jedis.pipelined();
        try {
            for (long index : indexs) {
                pipeline.setbit(where, index, true);
            }
            pipeline.sync();
            /**
             * 把数据存储到mysql中
             */
        } finally {
            pipeline.close();
        }
    }
    
    /**
     *  根据key获取bitmap下标方法来自guava
     */
    public long[] getIndexs(String key) {
        long hash1 = hash(key);
        long hash2 = hash1 >>> 16;
        long[] result = new long[numHashFunctions];
        for (int i = 0; i < numHashFunctions; i++) {
            long combinedHash = hash1 + i * hash2;
            if (combinedHash < 0) {
                combinedHash = ~combinedHash;
            }
            result[i] = combinedHash % numBits;
        }
        return result;
    }

    /**
     * 获取一个hash值 方法来自guava
     */
    private long hash(String key) {
        Charset charset = Charset.forName("UTF-8");
        return Hashing.murmur3_128().hashObject(key, Funnels.stringFunnel(charset)).asLong();
    }


    private static int optimalNumOfHashFunctions(long n, long m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }


    private static long optimalNumOfBits(long n, double p) {
        if (p == 0) {
            p = Double.MIN_VALUE;
        }
        return (long) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }
}

```

布隆过滤器主要做两件事情： 标记 和 过滤。



### ⑨ Zookeeper 实现分布式锁

#### 	1、Zookeeper特点：

​	① 可以用文件存储数据

​	②创建多种类型的目录：（持久目录，临时目录，持久有序目录，临时有顺序目录，）

​	③事件回调机制。（客户端发送操作请求,比如删除 或者新增一个文件下的子目录，Zookeeper会给客户端发送一个回调机制）。

#### 2、Zookeeper实现分布式锁原理

![Zookeeper 实现分布式锁](C:\Users\Administrator\Desktop\笔记\redis\Zookeeper 实现分布式锁.png)

1、因为Zookeeper 可以创建临时有顺序目录，所以在大量请求来向Zookeeper注册的时候，会依次根据自己的访问数据来向Zookeeper注册，比如向 /lock/目录下注册，会依次/ lock001   /lock002 /lock003 等依次累加。再另一个集群Zookeeper中也会同步过去，也是会依次累加的。

2、并且后面注册的会像前一个目录注册事件，等到前一个执行完后会通知注册时间的clent。

3、 Zookeeper会根据注册的顺序开始执行。

4、加入jvm1 注册的事件挂了，Zookeeper会直接通知jvm2 注册的事件，将 jvm1 注册的事件直接移除。 所以不会出现死锁的问题。



```java

/**
 * @author HouYC
 * @create 2020-07-15-22:04
 */
public class DistributedLock implements Lock, Watcher {

    private ZooKeeper zk = null;
    // 根节点
    private String ROOT_LOCK = "/lock_msb";
    // 竞争的资源
    private String lockName;
    // 等待的前一个锁
    private String WAIT_LOCK;
    // 当前锁
    private String CURRENT_LOCK;
    // 计数器
    private CountDownLatch countDownLatch;
    private int sessionTimeout = 3000000;
    private List<Exception> exceptionList = new ArrayList<Exception>();

    /**
     * 配置分布式锁
     * @param config 连接的url
     * @param lockName 竞争资源
     */
    public DistributedLock(String config, String lockName) {
        this.lockName = lockName;
        try {
            // 连接zookeeper
            zk = new ZooKeeper(config, sessionTimeout, this);
            Stat stat = zk.exists(ROOT_LOCK, false);
            if (stat == null) {
                // 如果根节点不存在，则创建根节点
                zk.create(ROOT_LOCK, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    // 节点监视器
    public void process(WatchedEvent event) {
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }

    public void lock() {
        if (exceptionList.size() > 0) {
            throw new LockException(exceptionList.get(0));
        }
        try {
            if (this.tryLock()) {
                System.out.println(Thread.currentThread().getName() + " " + lockName + "获得了锁");
                return;
            } else {
                // 等待锁
                waitForLock(WAIT_LOCK, sessionTimeout);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public boolean tryLock() {
        try {
            String splitStr = "_lock_";
            if (lockName.contains(splitStr)) {
                throw new LockException("锁名有误");
            }
            // 创建临时有序节点
            CURRENT_LOCK = zk.create(ROOT_LOCK + "/" + lockName + splitStr, new byte[0],
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(CURRENT_LOCK + " 已经创建");
            // 取所有子节点
            List<String> subNodes = zk.getChildren(ROOT_LOCK, false);
            // 取出所有lockName的锁
            List<String> lockObjects = new ArrayList<String>();
            for (String node : subNodes) {
                String _node = node.split(splitStr)[0];
                if (_node.equals(lockName)) {
                    lockObjects.add(node);
                }
            }
            Collections.sort(lockObjects);
            System.out.println(Thread.currentThread().getName() + " 的锁是 " + CURRENT_LOCK);
            // 若当前节点为最小节点，则获取锁成功
            if (CURRENT_LOCK.equals(ROOT_LOCK + "/" + lockObjects.get(0))) {
                return true;
            }

            // 若不是最小节点，则找到自己的前一个节点
            String prevNode = CURRENT_LOCK.substring(CURRENT_LOCK.lastIndexOf("/") + 1);
            WAIT_LOCK = lockObjects.get(Collections.binarySearch(lockObjects, prevNode) - 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean tryLock(long timeout, TimeUnit unit) {
        try {
            if (this.tryLock()) {
                return true;
            }
            return waitForLock(WAIT_LOCK, timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 等待锁
    private boolean waitForLock(String prev, long waitTime) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(ROOT_LOCK + "/" + prev, true);

        if (stat != null) {
            System.out.println(Thread.currentThread().getName() + "等待锁 " + ROOT_LOCK + "/" + prev);
            this.countDownLatch = new CountDownLatch(1);
            // 计数等待，若等到前一个节点消失，则precess中进行countDown，停止等待，获取锁
            this.countDownLatch.await(waitTime, TimeUnit.MILLISECONDS);
            this.countDownLatch = null;
            System.out.println(Thread.currentThread().getName() + " 等到了锁");
        }
        return true;
    }

    public void unlock() {
        try {
            System.out.println("释放锁 " + CURRENT_LOCK);
            zk.delete(CURRENT_LOCK, -1);
            CURRENT_LOCK = null;
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public Condition newCondition() {
        return null;
    }

    public void lockInterruptibly() throws InterruptedException {
        this.lock();
    }

    public class LockException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public LockException(String e){
            super(e);
        }
        public LockException(Exception e){
            super(e);
        }
    }
}

```

```java

public class Test {
    //100张票
    private Integer n = 100;
//    private Lock lock = new ReentrantLock();

    public void printInfo() {
        System.out.println(Thread.currentThread().getName() +
                "正在运行,剩余余票:" + --n);
    }

    public class TicketThread implements Runnable {
        public void run() {
            Lock lock = new DistributedLock("192.168.150.111:2181,192.168.150.112:2181,192.168.150.113:2181", "zk");
            lock.lock();
            try {
                if (n > 0) {
                    printInfo();
                }
                }finally{
                    lock.unlock();
                }
            }
    }

        public void ticketStart() {
            TicketThread thread = new TicketThread();
            for (int i = 0; i < 30; i++) {
                Thread t = new Thread(thread, "mem" + i);
                t.start();
            }
        }

        public static void main(String[] args) {
            new Test().ticketStart();
        }
    }
```



### 10、redis分布式锁存在的问题

![redis--分布式锁 存在的问题](C:\Users\Administrator\Desktop\笔记\redis\redis--分布式锁 存在的问题.png)

## 二、Redis 单线程

![redis 多路复用原理](C:\Users\Administrator\Desktop\笔记\redis\redis 多路复用原理.png)

### ① redis    worker 单线程  串行化

​	现在有多个客户端连接redis，redis从客户端读取数据，然后去处理，处理完成后，再返回给客户端，然后才会去读第二条在重复上面操作。

### ② redis IO-多路复用

![redis IO 多线程](C:\Users\Administrator\Desktop\笔记\redis\redis IO 多线程.png)

大概解释为：

​	1、现在有多个客户端连接redis，当C1 发送一个连接请求时，CPU 会去进行处理，在IO Thread 上进行读取，然后在 worker 上进行处理，处理完后 再由之前那个CPU写会。

​	2、 IO Thread 可以读取多个客户端发送的数据，但是在处理数据的时候 只能在worker上进行串行化处理，处理完后再交由IOThread 进行返回。

这样IO串行化 减少了时间片的开销。



关于I/O多路复用(又被称为“事件驱动”)，首先要理解的是，操作系统为你提供了一个功能，当你的某个socket可读或者可写的时候，它可以给你一个通知。这样当配合非阻塞的socket使用时，只有当系统通知我哪个描述符可读了，我才去执行read操作，可以保证每次read都能读到有效数据而不做纯返回-1和EAGAIN的无用功，写操作类似。

操作系统的这个功能是通过select/poll/epoll/kqueue之类的系统调用函数来实现，这些函数都可以同时监视多个描述符的读写就绪状况，这样，多个描述符的I/O操作都能在一个线程内并发交替地顺序完成，这就叫I/O多路复用，这里的“多路”指的是多个网络连接，“复用”指的是复用同一个Redis处理线程。（正如上图所示）

采用多路 I/O 复用技术可以让单个线程高效的处理多个连接请求（尽量减少网络 I/O 的时间消耗），且 Redis 在内存中操作数据的速度非常快，也就是说内存内的操作不会成为影响Redis性能的瓶颈，所有 Redis 具有很高的吞吐量。

Redis基于Reactor模式开发了自己的网络事件处理器——文件事件处理器，文件事件处理器使用I/O多路复用程序来同时监听多个socket（I/O多路复用技术下面有介绍），并根据socket目前执行的任务来为socket关联不同的事件处理器。当被监听的socket准备好执行连接应答、读取、写入、关闭等操作时，与操作相对应的文件事件就会产生，这时文件事件处理器就会调用socket之前已关联好的事件处理器来处理这些事件。

**内部实现采用epoll，采用了epoll+自己实现的简单的事件框架。** epoll中的读、写、关闭、连接都转化成了事件，**然后利用epoll的多路复用特性，** 绝不在io上浪费一点时间

### ③多路复用发展

![2、多路复用1](C:\Users\Administrator\Desktop\笔记\redis\2、多路复用1.png)

kernel：为操作系统的内核，而线程/ 进程，为用户态，在BIO 时期，用户态只能一个一个的去操作系统的内核态去获取要处理的数据，因此效率是非常低的。new了很多的用户线程去处理。

![2、多路复用2](C:\Users\Administrator\Desktop\笔记\redis\2、多路复用2.png)

而在这个阶段，轮询循环发生在用户空间，用户空间不断的循环去调用操作系统内核，这样导致用户需要调用1000次内核，成本问题是很大的。

![2、多路复用3](C:\Users\Administrator\Desktop\笔记\redis\2、多路复用3.png)

而这种模式是用户端将一批一批的数据直接发送给操作系统内核，这样操作系统内核去循环处理这些数据，将处理的告诉用户线程，用户线程再去内核态中去查找自己想要的数据。

![2、多路复用4](C:\Users\Administrator\Desktop\笔记\redis\2、多路复用4.png)



而这种模式是用户端将一批一批的数据直接发送给操作系统内核，这样操作系统内核去循环处理这些数据，将处理的告诉用户线程，mmap 为内核态和用户空间共同维护的共享空间，用户线程再去共享空间中去查找数据..

### ④这是一篇非常好的文章关于多路复用

[https://blog.csdn.net/armlinuxww/article/details/92803381?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522159877940719725219958858%2522%252C%2522scm%2522%253A%252220140713.130102334.pc%255Fall.%2522%257D&request_id=159877940719725219958858&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~first_rank_v2~rank_v28-2-92803381.pc_first_rank_v2_rank_v28&utm_term=epoll+%E8%BF%9B%E5%8C%96%E8%BF%87%E7%A8%8B&spm=1018.2118.3001.4187](https://blog.csdn.net/armlinuxww/article/details/92803381?ops_request_misc=%7B%22request%5Fid%22%3A%22159877940719725219958858%22%2C%22scm%22%3A%2220140713.130102334.pc%5Fall.%22%7D&request_id=159877940719725219958858&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~first_rank_v2~rank_v28-2-92803381.pc_first_rank_v2_rank_v28&utm_term=epoll+进化过程&spm=1018.2118.3001.4187)

## 三、Redis 5中基本数据类型用法

### ①String（bitmap）

![bitmap 牛逼用法](C:\Users\Administrator\Desktop\笔记\redis\bitmap 牛逼用法.png)

```java
1、bitmap 用法

① 如果统计用户登录的天数，且窗口随机。
	思路： 可以使用setbit，将用户作为key, 值为日期，哪一天登录了，就将该天对应的槽值设为1，因为redis 是用字节来存储数据的，一个字节占8位，假如统计一年的登录情况，这样一个用户只需要 365÷ 8 = 46个字节，大约46B，如果用户过多，可以节省大量的空间。
	
② 假如京东618做活动，送礼物，要为用户准备礼物，假如数据用户为2亿，因为用户分为僵尸用户，忠诚用户。我们只需要统计用户的活跃用户就可以，，比如1号--3号，连续登陆的 要去重。
	思路：  可以用日期 作为key， 值为每一个用户，假如数据库中id为1的用户登录了，就将该0槽设为1，假如用户id为6的登录的，就将5号槽为1，依次类推，最后将日期或运算，就是有1就为1。最后求出登录的用户即可。
```

### ②hash（哈希）

![redis 数据结构](C:\Users\Administrator\Desktop\笔记\redis\redis 数据结构.png)

 hash存储的为key--vlaue键值对，可以对filed 进行数值操作。点赞，收藏，详情页等。

### ③ Set

​	set 是无序，且去重的。应用场景 抽奖。

### ④sorted set

![redis 数据结构3](C:\Users\Administrator\Desktop\笔记\redis\redis 数据结构3.png)

	#### 跳跃表：

<https://blog.csdn.net/universe_ant/article/details/51134020?utm_medium=distribute.pc_relevant.none-task-blog-OPENSEARCH-2.channel_param&depth_1-utm_source=distribute.pc_relevant.none-task-blog-OPENSEARCH-2.channel_param>

### ⑤List

![redis数据类型](C:\Users\Administrator\Desktop\笔记\redis\redis数据类型.png)



## 四、Redis持久化

![redis rdb持久化](C:\Users\Administrator\Desktop\笔记\redis\redis rdb持久化.png)



![redis AOF持久化](C:\Users\Administrator\Desktop\笔记\redis\redis AOF持久化.png)

## 五、Redis 事务

![redis 事务](C:\Users\Administrator\Desktop\笔记\redis\redis 事务.png)

**Redis事务的概念：**

　　Redis 事务的本质是一组命令的集合。事务支持一次执行多个命令，一个事务中所有命令都会被序列化。在事务执行过程，会按照顺序串行化执行队列中的命令，其他客户端提交的命令请求不会插入到事务执行命令序列中。

　　总结说：redis事务就是一次性、顺序性、排他性的执行一个队列中的一系列命令。　　

**Redis事务没有隔离级别的概念：**

　　批量操作在发送 EXEC 命令前被放入队列缓存，并不会被实际执行，也就不存在事务内的查询要看到事务里的更新，事务外查询不能看到。

**Redis不保证原子性：**

　　Redis中，单条命令是原子性执行的，但事务不保证原子性，且没有回滚。事务中任意命令执行失败，其余的命令仍会被执行。

**Redis事务的三个阶段：**

- 开始事务
- 命令入队
- 执行事务

**Redis事务相关命令：**

　　watch key1 key2 ... : 监视一或多个key,如果在事务执行之前，被监视的key被其他命令改动，则事务被打断 （ 类似乐观锁 ）

　　multi : 标记一个事务块的开始（ queued ）

　　exec : 执行所有事务块的命令 （ 一旦执行exec后，之前加的监控锁都会被取消掉 ）　

　　discard : 取消事务，放弃事务块中的所有命令

　　unwatch : 取消watch对所有key的监控

**Redis事务使用案例：**

**（1）正常执行**

 ![img](https://img2018.cnblogs.com/blog/1659331/201904/1659331-20190416204151947-1999193750.png)

**（2）放弃事务**

![img](https://img2018.cnblogs.com/blog/1659331/201904/1659331-20190416204558119-2028373874.png)

**（3）若在事务队列中存在命令性错误（类似于java编译性错误），则执行EXEC命令时，所有命令都不会执行**

![img](https://img2018.cnblogs.com/blog/1659331/201904/1659331-20190416205137740-1887538258.png)

**（4）若在事务队列中存在语法性错误（类似于java的1/0的运行时异常），则执行EXEC命令时，其他正确命令会被执行，错误命令抛出异常。**

 ![img](https://img2018.cnblogs.com/blog/1659331/201904/1659331-20190416205714294-77806844.png)

**（5）使用watch**

案例一：使用watch检测balance，事务期间balance数据未变动，事务执行成功

![img](https://img2018.cnblogs.com/blog/1659331/201904/1659331-20190416210530600-1167641209.png)

案例二：使用watch检测balance，在开启事务后（标注1处），在新窗口执行标注2中的操作，更改balance的值，模拟其他客户端在事务执行期间更改watch监控的数据，然后再执行标注1后命令，执行EXEC后，事务未成功执行。

![img](https://img2018.cnblogs.com/blog/1659331/201904/1659331-20190416211144923-1469436233.png)

![img](https://img2018.cnblogs.com/blog/1659331/201904/1659331-20190416211149567-1618751187.png)

一但执行 EXEC 开启事务的执行后，无论事务使用执行成功， WARCH 对变量的监控都将被取消。

故当事务执行失败后，需重新执行WATCH命令对变量进行监控，并开启新的事务进行操作。

 **总结：**

　　watch指令类似于乐观锁，在事务提交时，如果watch监控的多个KEY中任何KEY的值已经被其他客户端更改，则使用EXEC执行事务时，事务队列将不会被执行，同时返回Nullmulti-bulk应答以通知调用者事务执行失败。

## 六、Redis 集群

![redis 集群1](C:\Users\Administrator\Desktop\笔记\redis\redis 集群1.png)

![redis 集群2](C:\Users\Administrator\Desktop\笔记\redis\redis 集群2.png)

![redis 集群4](C:\Users\Administrator\Desktop\笔记\redis\redis 集群4.png)



![redis 集群5](C:\Users\Administrator\Desktop\笔记\redis\redis 集群5.png)

## 七、Redis 高可用集群

![redis 高可用集群1](C:\Users\Administrator\Desktop\笔记\redis\redis 高可用集群1.png)

![redis 高可用集群2](C:\Users\Administrator\Desktop\笔记\redis\redis 高可用集群2.png)

![redis 高可用集群3](C:\Users\Administrator\Desktop\笔记\redis\redis 高可用集群3.png)

![redis 高可用集群4](C:\Users\Administrator\Desktop\笔记\redis\redis 高可用集群4.png)

![redis 高可用集群5](C:\Users\Administrator\Desktop\笔记\redis\redis 高可用集群5.png)

这里 可以用twemproxy或者predixy或Codis 建立主要是为了减少与后端缓存服务器的连接数，这以及协议流水线和分片使的水平扩展分布式缓存体系结构。

github 地址：

<https://github.com/joyieldInc/predixy/blob/master/README_CN.md>

<https://github.com/twitter/twemproxy>



[https://blog.csdn.net/mawming/article/details/52171116?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522159940063719724835815608%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=159940063719724835815608&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~first_rank_v2~rank_v28-2-52171116.pc_first_rank_v2_rank_v28&utm_term=twemproxy&spm=1018.2118.3001.4187](https://blog.csdn.net/mawming/article/details/52171116?ops_request_misc=%7B%22request%5Fid%22%3A%22159940063719724835815608%22%2C%22scm%22%3A%2220140713.130102334..%22%7D&request_id=159940063719724835815608&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~first_rank_v2~rank_v28-2-52171116.pc_first_rank_v2_rank_v28&utm_term=twemproxy&spm=1018.2118.3001.4187)

