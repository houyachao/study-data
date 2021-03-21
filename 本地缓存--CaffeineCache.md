# 一、Caffeine Cache 进程缓存之王

## 1、 前言

​	互联网软件神速发展，用户的体验度是判断一个软件好坏的重要原因，所以缓存就是必不可少的一个神器。在多线程高并发场景中往往是离不开cache的，需要根据不同的应用场景来需要选择不同的cache，比如分布式缓存如redis、memcached，还有本地（进程内）缓存如ehcache、GuavaCache、Caffeine。

​	说起Guava Cache，很多人都不会陌生，它是Google Guava工具包中的一个非常方便易用的本地化缓存实现，基于LRU算法实现，支持多种缓存过期策略。由于Guava的大量使用，Guava Cache也得到了大量的应用。但是，Guava Cache的性能一定是最好的吗？也许，曾经，它的性能是非常不错的。但所谓长江后浪推前浪，总会有更加优秀的技术出现。今天，我就来介绍一个比Guava Cache性能更高的缓存框架：Caffeine。

## 2、 比较

① Google Guava工具包中的一个非常方便易用的本地化缓存实现，基于LRU算法实现，支持多种缓存过期策略。

② EhCache 是一个纯Java的进程内缓存框架，具有快速、精干等特点，是Hibernate中默认的CacheProvider。

③ Caffeine是使用Java8对Guava缓存的重写版本，在Spring Boot 2.0中将取代，基于LRU算法实现，支持多种缓存过期策略。

##3、 Caffine Cache 在算法上的优点-W-TinyLFU

说到优化，Caffine Cache到底优化了什么呢？我们刚提到过LRU，常见的缓存淘汰算法还有FIFO，LFU：

1. FIFO：先进先出，在这种淘汰算法中，先进入缓存的会先被淘汰，会导致命中率很低。
2. LRU：最近最少使用算法，每次访问数据都会将其放在我们的队尾，如果需要淘汰数据，就只需要淘汰队首即可。仍然有个问题，如果有个数据在 1 分钟访问了 1000次，再后 1 分钟没有访问这个数据，但是有其他的数据访问，就导致了我们这个热点数据被淘汰。
3. LFU：最近最少频率使用，利用额外的空间记录每个数据的使用频率，然后选出频率最低进行淘汰。这样就避免了 LRU 不能处理时间段的问题。

上面三种策略各有利弊，实现的成本也是一个比一个高，同时命中率也是一个比一个好。Guava Cache虽然有这么多的功能，但是本质上还是对LRU的封装，如果有更优良的算法，并且也能提供这么多功能，相比之下就相形见绌了。

**LFU的局限性**：在 LFU 中只要数据访问模式的概率分布随时间保持不变时，其命中率就能变得非常高。比如有部新剧出来了，我们使用 LFU 给他缓存下来，这部新剧在这几天大概访问了几亿次，这个访问频率也在我们的 LFU 中记录了几亿次。但是新剧总会过气的，比如一个月之后这个新剧的前几集其实已经过气了，但是他的访问量的确是太高了，其他的电视剧根本无法淘汰这个新剧，所以在这种模式下是有局限性。

**LRU的优点和局限性**：LRU可以很好的应对突发流量的情况，因为他不需要累计数据频率。但LRU通过历史数据来预测未来是局限的，它会认为最后到来的数据是最可能被再次访问的，从而给与它最高的优先级。

在现有算法的局限性下，会导致缓存数据的命中率或多或少的受损，而命中略又是缓存的重要指标。HighScalability网站刊登了一篇文章，由前Google工程师发明的W-TinyLFU——一种现代的缓存 。Caffine Cache就是基于此算法而研发。Caffeine 因使用 **Window TinyLfu** 回收策略，提供了一个**近乎最佳的命中率**。

> 当数据的访问模式不随时间变化的时候，LFU的策略能够带来最佳的缓存命中率。然而LFU有两个缺点：
>
> 首先，它需要给每个记录项维护频率信息，每次访问都需要更新，这是个巨大的开销；
>
> 其次，如果数据访问模式随时间有变，LFU的频率信息无法随之变化，因此早先频繁访问的记录可能会占据缓存，而后期访问较多的记录则无法被命中。
>
> 因此，大多数的缓存设计都是基于LRU或者其变种来进行的。相比之下，LRU并不需要维护昂贵的缓存记录元信息，同时也能够反应随时间变化的数据访问模式。然而，在许多负载之下，LRU依然需要更多的空间才能做到跟LFU一致的缓存命中率。因此，一个“现代”的缓存，应当能够综合两者的长处。

TinyLFU维护了近期访问记录的频率信息，作为一个过滤器，当新记录来时，只有满足TinyLFU要求的记录才可以被插入缓存。如前所述，作为现代的缓存，它需要解决两个挑战：

一个是如何避免维护频率信息的高开销；

另一个是如何反应随时间变化的访问模式。

首先来看前者，TinyLFU借助了数据流Sketching技术，Count-Min Sketch显然是解决这个问题的有效手段，它可以用小得多的空间存放频率信息，而保证很低的False Positive Rate。但考虑到第二个问题，就要复杂许多了，因为我们知道，任何Sketching数据结构如果要反应时间变化都是一件困难的事情，在Bloom Filter方面，我们可以有Timing Bloom Filter，但对于CMSketch来说，如何做到Timing CMSketch就不那么容易了。TinyLFU采用了一种基于滑动窗口的时间衰减设计机制，借助于一种简易的reset操作：每次添加一条记录到Sketch的时候，都会给一个计数器上加1，当计数器达到一个尺寸W的时候，把所有记录的Sketch数值都除以2，该reset操作可以起到衰减的作用 。

W-TinyLFU主要用来解决一些稀疏的突发访问元素。在一些数目很少但突发访问量很大的场景下，TinyLFU将无法保存这类元素，因为它们无法在给定时间内积累到足够高的频率。因此W-TinyLFU就是结合LFU和LRU，前者用来应对大多数场景，而LRU用来处理突发流量。

在处理频率记录的方案中，你可能会想到用hashMap去存储，每一个key对应一个频率值。那如果数据量特别大的时候，是不是这个hashMap也会特别大呢。由此可以联想到 Bloom Filter，对于每个key，用n个byte每个存储一个标志用来判断key是否在集合中。原理就是使用k个hash函数来将key散列成一个整数。

## 4、使用

```java
1、 与SpringBoot整合需要加入：
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>2.6.2</version>
</dependency>

2、与spring整合需要加入：
 <dependency>
	 <groupId>org.springframework</groupId>
	 <artifactId>spring-context-support</artifactId>
	 <version>5.1.2.RELEASE</version>
 </dependency>
 <dependency>
	 <groupId>com.github.ben-manes.caffeine</groupId>
	 <artifactId>caffeine</artifactId>
</dependency>
```

### 4.1 缓存填充策略

	#### ① 手动加载

在每次get key 的时候指定一个同步的函数，如果key 不存在就调用这个函数生成一个值。

```java
/**
     * 手动加载
     * @param key
     * @return
     */
public Object manulOperator(String key) {
    Cache<String, Object> cache = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.SECONDS)
        .expireAfterAccess(1, TimeUnit.SECONDS)
        .maximumSize(10)
        .build();
    //如果一个key不存在，那么会进入指定的函数生成value
    Object value = cache.get(key, t -> setValue(key).apply(key));
    cache.put("hello",value);

    //判断是否存在如果不存返回null
    Object ifPresent = cache.getIfPresent(key);
    //移除一个key
    cache.invalidate(key);
    return value;
}

public Function<String, Object> setValue(String key){
    return t -> key + "value";
}
```

#### ② 同步加载

构造Cache时候，build方法传入一个CacheLoader实现类。实现load方法，通过key加载value。

```java
/**
     * 同步加载
     * @param key
     * @return
     */
public Object syncOperator(String key){
    LoadingCache<String, Object> cache = Caffeine.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build(k -> setValue(key).apply(key));
    return cache.get(key);
}

public Function<String, Object> setValue(String key){
    return t -> key + "value";
}
```

#### ③ 异步加载

AsyncLoadingCache是继承自LoadingCache类的，异步加载使用Executor去调用方法并返回一个CompletableFuture。异步加载缓存使用了响应式编程模型。

如果要以同步方式调用时，应提供CacheLoader。要以异步表示时，应该提供一个AsyncCacheLoader，并返回一个CompletableFuture。

```java
/**
     * 异步加载
     *
     * @param key
     * @return
     */
public Object asyncOperator(String key){
    AsyncLoadingCache<String, Object> cache = Caffeine.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .buildAsync(k -> setAsyncValue(key).get());

    return cache.get(key);
}

public CompletableFuture<Object> setAsyncValue(String key){
    return CompletableFuture.supplyAsync(() -> {
        return key + "value";
    });
}

```

### 4.2 回收策略

Caffeine提供了3种回收策略：基于大小回收，基于时间回收，基于引用回收。

#### ①基于大小的过期方式

基于大小的回收策略有两种方式：一种是基于缓存大小，一种是基于权重。

```java
// 根据缓存的计数进行驱逐
LoadingCache<String, Object> cache = Caffeine.newBuilder()
    .maximumSize(10000)
    .build(key -> function(key));


// 根据缓存的权重来进行驱逐（权重只是用于确定缓存大小，不会用于决定该缓存是否被驱逐）
LoadingCache<String, Object> cache1 = Caffeine.newBuilder()
    .maximumWeight(10000)
    .weigher(key -> function1(key))
    .build(key -> function(key))
```

maximumWeight与maximumSize不可以同时使用。

#### ②基于时间的过期方式

```java
// 基于固定的到期策略进行退出
LoadingCache<String, Object> cache = Caffeine.newBuilder()
    .expireAfterAccess(5, TimeUnit.MINUTES)
    .build(key -> function(key));
LoadingCache<String, Object> cache1 = Caffeine.newBuilder()
    .expireAfterWrite(10, TimeUnit.MINUTES)
    .build(key -> function(key));

// 基于不同的到期策略进行退出
LoadingCache<String, Object> cache2 = Caffeine.newBuilder()
    .expireAfter(new Expiry<String, Object>() {
        @Override
        public long expireAfterCreate(String key, Object value, long currentTime) {
            return TimeUnit.SECONDS.toNanos(seconds);
        }

        @Override
        public long expireAfterUpdate(@Nonnull String s, @Nonnull Object o, long l, long l1) {
            return 0;
        }

        @Override
        public long expireAfterRead(@Nonnull String s, @Nonnull Object o, long l, long l1) {
            return 0;
        }
    }).build(key -> function(key));
```

Caffeine提供了三种定时驱逐策略：

1、expireAfterAccess(long, TimeUnit):	在最后一次访问或者写入后开始计时，在指定的时间后过期。假如一直有请求访问该key，那么这个缓存将一直不会过期。
2、expireAfterWrite(long, TimeUnit): 	在最后一次写入缓存后开始计时，在指定的时间后过期。
3、expireAfter(Expiry):    自定义策略，过期时间由Expiry实现独自计算。
缓存的删除策略使用的是惰性删除和定时删除。这两个删除策略的时间复杂度都是O(1)。

#### ③基于引用的过期方式

Java中四种引用类型

| 引用类型                  | 被垃圾回收时间 | 用途                                       | 生存时间       |
| --------------------- | ------- | ---------------------------------------- | ---------- |
| 强引用 Strong Reference  | 从来不会    | 对象的一般状态                                  | JVM停止运行时终止 |
| 软引用 Soft Reference    | 在内存不足时  | 对象缓存                                     | 内存不足时终止    |
| 弱引用 Weak Reference    | 在垃圾回收时  | 对象缓存                                     | gc运行后终止    |
| 虚引用 Phantom Reference | 从来不会    | 可以用虚引用来跟踪对象被垃圾回收器回收的活动，当一个虚引用关联的对象被垃圾收集器回收之前会收到一条系统通知 | JVM停止运行时终止 |

```java
// 当key和value都没有引用时驱逐缓存
LoadingCache<String, Object> cache = Caffeine.newBuilder()
    .weakKeys()
    .weakValues()
    .build(key -> function(key));

// 当垃圾收集器需要释放内存时驱逐
LoadingCache<String, Object> cache1 = Caffeine.newBuilder()
    .softValues()
    .build(key -> function(key));
```

**注意：AsyncLoadingCache不支持弱引用和软引用。**

Caffeine.weakKeys()： 使用弱引用存储key。如果没有其他地方对该key有强引用，那么该缓存就会被垃圾回收器回收。由于垃圾回收器只依赖于身份(identity)相等，因此这会导致整个缓存使用身份 (==) 相等来比较 key，而不是使用 equals()。

Caffeine.weakValues() ：使用弱引用存储value。如果没有其他地方对该value有强引用，那么该缓存就会被垃圾回收器回收。由于垃圾回收器只依赖于身份(identity)相等，因此这会导致整个缓存使用身份 (==) 相等来比较 key，而不是使用 equals()。

Caffeine.softValues() ：使用软引用存储value。当内存满了过后，软引用的对象以将使用最近最少使用(least-recently-used ) 的方式进行垃圾回收。由于使用软引用是需要等到内存满了才进行回收，所以我们通常建议给缓存配置一个使用内存的最大值。 softValues() 将使用身份相等(identity) (==) 而不是equals() 来比较值。

**Caffeine.weakValues()和Caffeine.softValues()不可以一起使用。**

### 4.3 移除事件监听

```java
Cache<String, Object> cache = Caffeine.newBuilder()
    .removalListener((String key, Object value, RemovalCause cause) ->
                     System.out.printf("Key %s was removed (%s)%n", key, cause))
    .build();
```

### 4.4 写入外部存储

```java
CacheWriter 方法可以将缓存中所有的数据写入到第三方。

LoadingCache<String, Object> cache2 = Caffeine.newBuilder()
    .writer(new CacheWriter<String, Object>() {
        @Override public void write(String key, Object value) {
            // 写入到外部存储
        }
        @Override public void delete(String key, Object value, RemovalCause cause) {
            // 删除外部存储
        }
    })
    .build(key -> function(key));
如果你有多级缓存的情况下，这个方法还是很实用。

注意：CacheWriter不能与弱键或AsyncLoadingCache一起使用。
```

### 4.5 统计

```java
与Guava Cache的统计一样。

Cache<String, Object> cache = Caffeine.newBuilder()
    .maximumSize(10_000)
    .recordStats()
    .build();
通过使用Caffeine.recordStats(), 可以转化成一个统计的集合. 通过 Cache.stats() 返回一个CacheStats。CacheStats提供以下统计方法：

hitRate(): 返回缓存命中率

evictionCount(): 缓存回收数量

averageLoadPenalty(): 加载新值的平均时间
```



## 5、SpringBoot 中默认Cache-Caffine Cache

SpringBoot 1.x版本中的默认本地cache是Guava Cache。在2.x（**Spring Boot 2.0(spring 5)** ）版本中已经用Caffine Cache取代了Guava Cache。毕竟有了更优的缓存淘汰策略。

### 5.1 引入依赖

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>2.6.2</version>
</dependency>
```

### 5.2 添加注解开启缓存支持

```java
@SpringBootApplication
@EnableCaching
public class SingleDatabaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SingleDatabaseApplication.class, args);
    }
}
```

### 5.3 使用Bean的方式来注入、实例化Cache

```java
@Configuration
public class CacheConfig {


    /**
     * 创建基于Caffeine的Cache Manager
     * 初始化一些key存入
     * @return
     */
    @Beand("cacheManager")
    @Primary
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        ArrayList<CaffeineCache> caches = Lists.newArrayList();
        List<CacheBean> list = setCacheBean();
        for(CacheBean cacheBean : list){
            caches.add(new CaffeineCache(cacheBean.getKey(),
                    Caffeine.newBuilder().recordStats()
                            .expireAfterWrite(cacheBean.getTtl(), TimeUnit.SECONDS)
                            .maximumSize(cacheBean.getMaximumSize())
                            .build()));
        }
        cacheManager.setCaches(caches);
        return cacheManager;
    }


    /**
     * 初始化一些缓存的 key
     * @return
     */
    private List<CacheBean> setCacheBean(){
        List<CacheBean> list = Lists.newArrayList();
        CacheBean userCache = new CacheBean();
        userCache.setKey("userCache");
        userCache.setTtl(60);
        userCache.setMaximumSize(10000);

        CacheBean deptCache = new CacheBean();
        deptCache.setKey("userCache");
        deptCache.setTtl(60);
        deptCache.setMaximumSize(10000);

        list.add(userCache);
        list.add(deptCache);

        return list;
    }

    class CacheBean {
        private String key;
        private long ttl;
        private long maximumSize;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public long getTtl() {
            return ttl;
        }

        public void setTtl(long ttl) {
            this.ttl = ttl;
        }

        public long getMaximumSize() {
            return maximumSize;
        }

        public void setMaximumSize(long maximumSize) {
            this.maximumSize = maximumSize;
        }
    }

}
```

创建了一个`SimpleCacheManager`作为Cache的管理对象，然后初始化了两个Cache对象，分别存储user，dept类型的缓存。当然构建Cache的参数设置我写的比较简单，你在使用的时候酌情根据需要配置参数。

### 5.4 **Caffeine常用配置说明**

```java
initialCapacity=[integer]: 初始的缓存空间大小

maximumSize=[long]: 缓存的最大条数

maximumWeight=[long]: 缓存的最大权重

expireAfterAccess=[duration]: 最后一次写入或访问后经过固定时间过期

expireAfterWrite=[duration]: 最后一次写入后经过固定时间过期

refreshAfterWrite=[duration]: 创建缓存或者最近一次更新缓存后经过固定的时间间隔，刷新缓存

weakKeys: 打开key的弱引用

weakValues：打开value的弱引用

softValues：打开value的软引用

recordStats：开发统计功能

注意：

expireAfterWrite和expireAfterAccess同时存在时，以expireAfterWrite为准。

maximumSize和maximumWeight不可以同时使用

weakValues和softValues不可以同时使用
```

### 5.5 使用注解来对cache 增删改查

我们可以使用spring提供的 `@Cacheable`、`@CachePut`、`@CacheEvict`等注解来方便的使用caffeine缓存。

如果使用了多个cahce，比如redis、caffeine等，必须指定某一个CacheManage为@primary，在@Cacheable注解中没指定 cacheManager 则使用标记为primary的那个。

cache方面的注解主要有以下5个：

- @Cacheable 触发缓存入口（这里一般放在创建和获取的方法上，`@Cacheable`注解会先查询是否已经有缓存，有会使用缓存，没有则会执行方法并缓存）
- @CacheEvict 触发缓存的eviction（用于删除的方法上）
- @CachePut 更新缓存且不影响方法执行（用于修改的方法上，该注解下的方法始终会被执行）
- @Caching 将多个缓存组合在一个方法上（该注解可以允许一个方法同时设置多个注解）
- @CacheConfig 在类级别设置一些缓存相关的共同配置（与其它缓存配合使用）

说一下`@Cacheable` 和 `@CachePut`的区别：

@Cacheable：它的注解的方法是否被执行取决于Cacheable中的条件，方法很多时候都可能不被执行。

@CachePut：这个注解不会影响方法的执行，也就是说无论它配置的条件是什么，方法都会被执行，更多的时候是被用到修改上。

简要说一下Cacheable类中各个方法的使用：

```java
public @interface Cacheable {

    /**
     * 要使用的cache的名字
     */
    @AliasFor("cacheNames")
    String[] value() default {};

    /**
     * 同value()，决定要使用那个/些缓存
     */
    @AliasFor("value")
    String[] cacheNames() default {};

    /**
     * 使用SpEL表达式来设定缓存的key，如果不设置默认方法上所有参数都会作为key的一部分
     */
    String key() default "";

    /**
     * 用来生成key，与key()不可以共用
     */
    String keyGenerator() default "";

    /**
     * 设定要使用的cacheManager，必须先设置好cacheManager的bean，这是使用该bean的名字
     */
    String cacheManager() default "";

    /**
     * 使用cacheResolver来设定使用的缓存，用法同cacheManager，但是与cacheManager不可以同时使用
     */
    String cacheResolver() default "";

    /**
     * 使用SpEL表达式设定出发缓存的条件，在方法执行前生效
     */
    String condition() default "";

    /**
     * 使用SpEL设置出发缓存的条件，这里是方法执行完生效，所以条件中可以有方法执行后的value
     */
    String unless() default "";

    /**
     * 用于同步的，在缓存失效（过期不存在等各种原因）的时候，如果多个线程同时访问被标注的方法
     * 则只允许一个线程通过去执行方法
     */
    boolean sync() default false;

}
```

- 还有一个条件参数unless，与condition的用法恰好相反。
- 使用了条件式缓存后，哪怕哪怕缓存里已经有数据了，也依然会跳过缓存。比如我们在其他方法中将“小九九”添加到了缓存中，但通过该方法获取小九九的数据时，依然是从数据库中取值。
- @Cacheable注解不仅仅可以标记在一个方法上，还可以标记在一个类上，表示该类所有的方法都是支持缓存的。
- 我们除了使用参数作为key之外，Spring还为我们提供了一个root对象可以用来生成key，比如 #root.methodName（当前方法名）， #root.target（当前被调用的对象）， #root.args[0]（ #root.args[0]）等等。

```java
@Service
public class UserCacheService {
    /**
     * 查找
     * 先查缓存，如果查不到，会查数据库并存入缓存
     * @param id
     */
    @Cacheable(value = "userCache", key = "#id", sync = true)
    public void getUser(long id){
        //查找数据库
    }
、
    /**
     * 更新/保存
     * @param user
     */
    @CachePut(value = "userCache", key = "#user.id")
    public void saveUser(User user){
        //todo 保存数据库
    }

    /**
     * 删除
     * @param user
     */
    @CacheEvict(value = "userCache",key = "#user.id")
    public void delUser(User user){
        //todo 保存数据库
    }
}
```

如果你不想使用注解的方式去操作缓存，也可以直接使用SimpleCacheManager获取缓存的key进而进行操作。

注意到上面的key使用了spEL 表达式。Spring Cache提供了一些供我们使用的SpEL上下文数据，下表直接摘自Spring官方文档：

| 名称            | 位置     | 描述                                       | 示例                     |
| ------------- | ------ | ---------------------------------------- | ---------------------- |
| methodName    | root对象 | 当前被调用的方法名                                | `#root.methodname`     |
| method        | root对象 | 当前被调用的方法                                 | `#root.method.name`    |
| target        | root对象 | 当前被调用的目标对象实例                             | `#root.target`         |
| targetClass   | root对象 | 当前被调用的目标对象的类                             | `#root.targetClass`    |
| args          | root对象 | 当前被调用的方法的参数列表                            | `#root.args[0]`        |
| caches        | root对象 | 当前方法调用使用的缓存列表                            | `#root.caches[0].name` |
| Argument Name | 执行上下文  | 当前被调用的方法的参数，如findArtisan(Artisan artisan),可以通过#artsian.id获得参数 | `#artsian.id`          |
| result        | 执行上下文  | 方法执行后的返回值（仅当方法执行后的判断有效，如 unless cacheEvict的beforeInvocation=false） | `#result`              |

**注意：**

1.当我们要使用root对象的属性作为key时我们也可以将“#root”省略，因为Spring默认使用的就是root对象的属性。 如

```java
@Cacheable(key = "targetClass + methodName +#p0")
```

2.使用方法参数时我们可以直接使用“#参数名”或者“#p参数index”。 如：

```java
@Cacheable(value="userCache", key="#id")
@Cacheable(value="userCache", key="#p0")
```

**SpEL提供了多种运算符**

| **类型** | **运算符**                                 |
| ------ | --------------------------------------- |
| 关系     | <，>，<=，>=，==，!=，lt，gt，le，ge，eq，ne       |
| 算术     | +，- ，* ，/，%，^                           |
| 逻辑     | &&，\|\|，!，and，or，not，between，instanceof |
| 条件     | ?: (ternary)，?: (elvis)                 |
| 正则表达式  | matches                                 |
| 其他类型   | ?.，?[…]，![…]，^[…]，$[…]                  |

### 5.6 高阶用法

#### ① 线程锁定

​	前面我们提到了@Cacheable可以添加缓存，当缓存过期之后如果多个线程同时请求过来，而该方法执行较慢时可能会导致大量请求堆积，甚至导致缓存瞬间被击穿，所有请求同时去到数据库，数据库瞬间负荷增高。所以该注解还提供了一个参数 sync：默认为false，如果为true时表示多个线程同时调用此时只有一个线程能够成功调用，其他线程直接取这次调用的返回值。不过它在代码注释上也写了，这仅仅是个hint，具体还是要看缓存提供者。

​	不管sync设置是true还是false，Caffeine默认使用的都是单线程 ：只允许一个线程去加载数据，其余线程阻塞。这样其实也会导致效率低下，用户等待。因此建议配合refreshAfterWrite一起使用：只阻塞加载数据的线程，其余线程返回旧数据。

#### ② 缓存失效

初始化缓存时，我们还可以设置3个参数：expireAfterAccess、expireAfterWrite、refreshAfterWrite。千万不要被这三个单词的表面意思误导，网上很多写法也是错的。比如expireAfterAccess，不是表示访问完多长时间就过期，而是多长时间没有访问就失效。

- expireAfterAccess=[duration]:指在指定时间内没有被读或写就回收
- expireAfterWrite=[duration]: 指在指定时间内没有被创建或覆盖就回收
- refreshAfterWrite=[duration]：指在指定时间内没有被创建/覆盖，则指定时间过后再次访问时会去刷新该缓存，在新值没有到来之前，始终返回旧值

我们以expireAfterWrite为例，配置如下，然后不停地访问，我们看到每隔5秒后就自动更新一次缓存。

#### ③ 查看缓存信息

在开发过程中，如果需要验证缓存是否生效或者我们的配置是否正确，除了看系统的运行行为，我们还可以直接去查看缓存的信息。

```java
private CacheManager cacheManager;   
@GetMapping("/cache/info")
public Object cacheData(String id) {
  Cache cache = cacheManager.getCache("USER");
  if (null == cache.get(id)) {
    return "cache is null";
  }
  Object obj = cache.get(id).get();
  if (null == obj) {
    return "null obj";
  } else {
    return "Object Info:" + obj.toString();
  }
}
```



## 6、高级思想

参考文章：<http://highscalability.com/blog/2016/1/25/design-of-a-modern-cache.html>

### 并发

对高速缓存的并发访问被视为一个难题，因为在**大多数策略中，每次访问都是对某些共享状态的写入**。传统解决方案是使用单个锁来保护高速缓存。然后，可以通过将高速缓存拆分为许多较小的独立区域来通过锁条来改善这一点。不幸的是，由于热条目导致某些锁比其他锁更容易满足，因此这种方法的收益往往有限。当竞争成为瓶颈时，下一步的经典步骤是**仅更新每个条目元数据，**并使用[随机采样](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.110.8469&rep=rep1&type=pdf)或基于[FIFO的](https://en.wikipedia.org/wiki/Page_replacement_algorithm#Second-chance)驱逐策略。这些技术可能具有出色的读取性能，较差的写入性能以及难以选择好的受害者的能力。

一种[替代方法](http://web.cse.ohio-state.edu/hpcs/WWW/HTML/publications/papers/TR-09-1.pdf)是**从数据库理论中借用一个想法，在该理论中，使用提交日志来缩放写操作**。与其立即**更改**数据结构，不如将**更新写入日志并以异步批处理方式重播**。可以通过执行哈希表操作，将该操作记录到缓冲区以及在认为必要时针对该策略安排重放活动，来将相同的思想应用于缓存。该策略仍由锁（或更精确地说是try锁）来保护，但是将争用转移到附加到日志缓冲区上。

在Caffeine中，**单独的缓冲区用于缓存读取和写入**。访问记录在**带状环形缓冲区中**，在该**缓冲区**中，通过特定于线程的哈希值来选择条带，并且在检测到竞争时条带的数量会增加。当环形缓冲区已满时，将调度异步耗用，并丢弃对该缓冲区的后续添加，直到空间可用为止。当由于缓冲区已满而未记录访问时，仍会将缓存的值返回给调用方。策略信息的丢失不会产生有意义的影响，因为W-TinyLFU能够识别我们希望保留的热门条目。通过使用特定于线程的哈希而不是键的哈希，缓存可以通过更平均地分散负载来避免流行条目引起争用。

### 过期政策

到期通常被实现为每个条目的变量，并且由于容量限制，到期条目被懒惰地逐出。这会用死项污染缓存，因此有时会使用清除线程来定期清除缓存并回收可用空间。这种策略往往比在O（lg n）优先级队列上按到期时间对条目进行排序更好地工作，这是因为向用户隐藏了开销，而不是对每次读取或写入操作都造成了损失。

Caffeine采取了另一种方法，**观察到大多数情况下固定时间是首选的**。此约束允许组织O（1）按时间排序的队列上的条目。生存时间是写顺序队列，空闲时间是访问顺序队列。缓存可以重新使用逐出策略的队列和以下所述的并发机制，以便在缓存的维护阶段丢弃过期的条目。









































