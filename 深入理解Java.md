# 面试题

## 一、JMM 内存模型 - java对象模型 - JVM 内存结构

<https://juejin.im/post/5b42c01ee51d45194e0b819a>

### ①JMM（java内存模型）：

JMM 保证了： 可见性、原子性、有序性。

```
	1.Volatile保证了可见性。如果共享数据不加Volatile关键字，则线程A从主内存上读取到自己的工作内存后进行修改完写会主内存，其他线程不会刷新共享数据，读取到的还是之前的老数据。如果加上，修改完后写会主内存，其他线程会读取到并且刷新。
    2. 验证原子性： 不可分割，完整性。 也即某个线程正在做某个具体业务时，中间不可被阻塞或分割。需要整体完整。要么同时成功，要么同时失败。
    如何解决原子性：
    	1. 加synchronized
    	2.使用我们juc下的AtomicInteger
   	3. 有序性： 计算机在执行程序时，为了提高性能，编译器和处理器常常会对指令做重拍，一般情况下分3钟情况：
   	代码源--> 编译器优化的重排--> 指令并行的重排--> 内存系统的重排--> 最终执行的指令
   	1.单线程环境里面确保程序最终执行结果和代码顺序执行的结果一致。
   	2.处理器在进行重排序时必须要考虑指令之间的数据依赖性。
   	3.多线程环境中线程交替执行，由于编译器优化重排的存在，两个线程中使用的变量能否保证一致性是无法确认的，结果是无法预测的。

```

java内存模型，Java的多线程之间是通过共享内存进行通信的，而由于采用共享内存进行通信，在通信过程中会存在一系列如可见性、原子性、顺序性等问题，而JMM就是围绕着多线程通信以及与其相关的一系列特性而建立的模型。JMM定义了一些语法集，这些语法集映射到Java语言中就是volatile、synchronized等关键字。

![11](C:\Users\Administrator\Desktop\笔记\面试笔记  高级\11.png)

JMM关于同步的规定：

1. 线程解锁前，必须把共享变量的值刷新回主内存
2. 线程加锁前，必须读取主内存的最新值到自己的工作内存
3. 加锁解锁是同一把锁

### ②Java对象模型：

Java是一种面向对象的语言，而Java对象在JVM中的存储也是有一定的结构的。而这个关于Java对象自身的存储模型称之为Java对象模型。

HotSpot虚拟机中，设计了一个OOP-Klass Model。OOP（Ordinary Object Pointer）指的是普通对象指针，而Klass用来描述对象实例的具体类型。

每一个Java类，在被JVM加载的时候，JVM会给这个类创建一个`instanceKlass`，保存在方法区，用来在JVM层表示该Java类。当我们在Java代码中，使用new创建一个对象的时候，JVM会创建一个`instanceOopDesc`对象，这个对象中包含了对象头以及实例数据。

![20170615230126453](C:\Users\Administrator\Desktop\笔记\面试笔记  高级\20170615230126453.jpg)

这就是一个简单的Java对象的OOP-Klass模型，即Java对象模型。

### ③JVM内存结构

我们都知道，Java代码是要运行在虚拟机上的，而虚拟机在执行Java程序的过程中会把所管理的内存划分为若干个不同的数据区域，这些区域都有各自的用途。其中有些区域随着虚拟机进程的启动而存在，而有些区域则依赖用户线程的启动和结束而建立和销毁。在《[Java虚拟机规范（Java SE 8）](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.5.4)》中描述了JVM运行时内存区域结构如下：

![QQ20180624-150918](C:\Users\Administrator\Desktop\笔记\面试笔记  高级\QQ20180624-150918.png)

1、以上是Java虚拟机规范，不同的虚拟机实现会各有不同，但是一般会遵守规范。

2、规范中定义的方法区，只是一种概念上的区域，并说明了其应该具有什么功能。但是并没有规定这个区域到底应该处于何处。所以，对于不同的虚拟机实现来说，是由一定的自由度的。

3、不同版本的方法区所处位置不同，上图中划分的是逻辑区域，并不是绝对意义上的物理区域。因为某些版本的JDK中方法区其实是在堆中实现的。

4、运行时常量池用于存放编译期生成的各种字面量和符号应用。但是，Java语言并不要求常量只有在编译期才能产生。比如在运行期，String.intern也会把新的常量放入池中。

5、除了以上介绍的JVM运行时内存外，还有一块内存区域可供使用，那就是直接内存。Java虚拟机规范并没有定义这块内存区域，所以他并不由JVM管理，是利用本地方法库直接在堆外申请的内存区域。

6、堆和栈的数据划分也不是绝对的，如HotSpot的JIT会针对对象分配做相应的优化。

## 1.volatile 是什么？

Volatile是java虚拟机提供的轻量级的同步机制。

保证可见性、不保证原子性、禁止指令重排。



线程安全性获得保证：

​	工作内存与主内存同步延迟现象导致的可见性问题？ 可以使用synchronized 或Volatile关键字解决，他们都可以使一个线程修改后的变量立即对其他线程可见。

​	对于指令重排导致的可见性问题和有序性问题？ 可以利用Volatile 关键字解决，因为Volatile的另外一个作用就是禁止重排序优化。

## 2.你在那些地方用到过Volatile？

2.1 单例模式DCL代码：

​	代码看SingletonDemo

2.2 单例模式Volatile分析：

DCL（双端检锁机制）：就是我去上厕所，然后先看看门有没有锁，没有锁进去后，把门锁上，处于不放心，又推了推门。

DCL（双端检锁机制）机制不一定线程安全，原因是有指令重排序的存在，加入Volatile可以禁止指令重排。

原因在于某一个线程执行到第一次检测，读取到的instance不为null时，instance的引用对象可能没有完成初始化。

instance = new SingletonDemo(); 可以分为以下3步完成（伪代码）：

memory = allocate() ;  //1.分配对象内存空间

instance(memory) ;    //2. 初始化对象

instance = memory;  //3. 设置instance指向刚分配的内存地址，此时instance ！= null

步骤2和步骤3不存在数据依赖关系，而且无论重排前还是重拍后程序的执行结果在单线程中并没有改变，因此这种重排序优化是允许的。



memory = allocate() ;  //1.分配对象内存空间

instance = memory;  //3. 设置instance指向刚分配的内存地址，此时instance ！= null ，但是对象还没有初始化完成！

instance(memory) ;    //2. 初始化对象。

但是指令重排只会保证串行语义的执行的一致性（单线程），但并不会关系多线程间的语义一致性。

所以当一条线程访问instance不为null时，由于instance实例化未必已初始化完成，也就造成了线程安全问题。。



## 3.CAS是什么？

CAS是什么？   ===> compareAndSet  即比较并交换。

意思： 就是多个线程从主内存（物理内存）中获取共享数据到自己的工作内存中，一个线程准备修改共享数据的时候，会先将 期望值 和初始值进行比较，如果相当就更新为新的值，并写会主内存。

### 3.1CAS底层原理

底层主要用到了UnSafe类。

1.UnSafe 是CAS的核心类，由于java方法无法直接访问底层系统，需要通过本地（native）方法来访问，UnSafe相当于一个后门，基于该类可以直接操作特定内存的数据。UnSafe类存在于sun.misc包下，其内部方法操作可以像C的指针一样直接操作内存，因为java中CAS操作的执行依赖于UnSafe类的方法。

注意UnSafe类中的所有方法都是native修饰的，也就是说UnSafe类中的方法都直接调用操作系统底层资源执行相应任务。

2.变量valueOffset，表示该变量值在内存中的偏移地址，因为UnSafe就是根据内存偏移地址获取数据的。

3.变量value用Volatile修饰，保证了多线程之间的内存可见性。



CAS的全称为Compare-And-Swap，它是一条CPU并发原语。

它的功能是判断内存某个位置的值是否为预期值，如果是则更改为新的值，这个过程是原子的。

CAS并发原语体现在java语言中就是sun.misc.Unsafe类中的各个方法。调用UnSafe类中的CAS方法，jvm会帮我们实现出CAS汇编指令。这是一种完全依赖于硬件的功能，通过它实现了原子操作。再次强调，由于CAS是一种系统原语，原语属于操作系统用语范畴，是由若干条指令组成的，用于完成某个功能的一个过程，并且原语的执行必须是连续的，在执行过程中不允许被中断，也就是说CAS是一条CPU的原子指令，不会造成所谓的数据不一致问题。

```java
AtomicInteger.java
	
    public final int getAndIncrement(){
    	return unsafe.getAndAddInt(this,valueOffset,1);    //第一参数为：该前的类，
    }												   //第二个参数为：这个对象的内存地址偏移量
    
UnSafe.class
  public fainal int getAndAddInt(Object var1, long var2, int var4）{
  	int var5;
    do{
      var5 = this.getIntVolatile(var1,var2);
    }while(!this.compareAndSwapInt(var1,var2,var5,var5+var4));
  }
  // compareAndSwapInt(var1,var2,var5,var5+var4) 意思为：该类的真实值和期望值是否相等，如果相等，则期望值+1，并交换                              
```

CAS（CompareAndSwap）：比较当前工作内存中的值和主内存中的值，如果相同则执行规定操作，否则继续比较直到主内存和工作内存中的值一致为止。

CAS应用：CAS有3 个操作数，内存值V，旧的预期值A，要修改的更新值B。当且仅当预期值A和内存值V相同时，将内存值V修改为B，否则什么都不做。

### 3.2 CAS缺点

①循环时间长开销很大。

②只能保证一个共享变量操作。

③引出来ABA问题。

### 3.3 ABA问题怎么产生的？

CAS会导致 ”ABA问题“。

​	CAS算法实现一个重要前提需要取出内存中某时刻的数据并在当下时刻比较并替换，那么在这个时间差类会导致数据的变化。比如说一个线程one从内存位置V中取出A，这时候另一个线程two也从内存中取出A，并且线程two进行了一些操作将值变成了B，然后线程two又将V位置的数据变成A，这时候线程one进行CAS操作发现内存中仍然是A，然后线程one操作成功。

​	尽管线程one的CAS操作成功，但是不代表这个过程就是没有问题。

原子引用： AtomicReference

时间戳原子引用：AtomicStampedReference

时间戳原子引用可以解决 “ABA问题”，因为时间戳版本号原子引用每次修改都带一个版本号，修改过程中版本号不同，则不允许修改。

## 4.ArrayList是线程不安全的，请编码写一个不安全的案例并给出解决方案？

```java
public class ContainerNotSafeDemo {
    
  public static void main(String[] args) {
    
        List<String> list = new CopyOnWriteArrayList<>();

        for (int i = 1; i<=30;i++){
            new Thread(()->{
                list.add(UUID.randomUUID().toString().substring(0,8));
                System.out.println(list);
            },String.valueOf(i)).start();
        }
    }
  
    /**
     * 1. 故障现象
     *  java.util.ConcurrentModificationException
     *
     *  2. 导致原因
     *      并发争抢修改导致
     *      一个人正在写入，另一个同学过来争抢，导致数据不一致异常，并发修改异常。
     *
     *  3. 解决原因
     *   3.1 new Vector<>()
     *   3.2 Collections.synchronizedList(new ArrayList())
     *   3.3 new CopyOnWriteArrayList();
     *
     *   4. 优化建议
     */

    /**
     * 写时复制
     * CopyOnWrite容器即写时复制的容器，往一个容器添加元素的时候，不直接往当前容器Object[]添加，而是先将当前容器Object[]进行Copy,
     * 复制出一个新的容器object[] newElements, 然后新的容器object[] newElements 里添加元素，添加完元素之后，
     * 再将原容器的引用指向新的容器setArray(newElements).这样做的好处是一种读写分离的思想，读和写不同的容器
     * 
     * public boolean add(E e) {
*         final ReentrantLock lock = this.lock;
*         lock.lock();
*         try {
*             Object[] elements = getArray();
*             int len = elements.length;
*             Object[] newElements = Arrays.copyOf(elements, len + 1);
*             newElements[len] = e;
*             setArray(newElements);
*             return true;
*         } finally {
*             lock.unlock();
*         }
*     }
     */
}
```

5.HashSet 是线程不安全的，HashSet底层是HashMap，HashSet的add方法使用的是HashMap.put 方法，key存的是这个对象，而value存的是一个Object对象。

```java
new HashSet<>();


HashSet.java
private static final Object PRESENT = new Object();
public HashSet() {
        map = new HashMap<>();
    }  
public boolean add(E e) {
        return map.put(e, PRESENT)==null;
    }

```

## 6.锁

① 公平锁  ： 是指多个线程按照申请锁的顺序来获取锁，类似排队打饭先来后到。

②非公平锁： 是指多个线程获取锁的顺序并不是按照申请锁的顺序，有可能后申请的线程比先申请的线程优先获取锁在高并发的情况下，有可能会造成优先级反转或者饥饿现象。

公平锁/非公平锁： 并发包中ReentrantLock 的创建可以指定构造函数的boolean类型来得到公平锁或非公平锁，默认是非公平锁。

关于两者的区别：  公平锁是很公平的，在并发环境下，每个线程在获取锁会先查看此锁维护的等待队列，如果为空，或者当前线程是等待队列的第一个，就占有锁，否则就会加入到等待队列中，以后会按照FIFO的规则从队列中取到自己。          非公平锁：  非公平锁比较粗鲁，上来就直接尝试占有锁，如果尝试失败，就再采用类似公平锁那种方式。

③可重入锁（又叫做递归锁）

​	指的是在同一线程外层函数获得锁之后，内层递归函数仍然能获取该锁的代码。在同一线程在外层方法获取锁的时候，在进入内层方法会自动获取锁。  也就是说，线程可以进入任何一个它已经拥有的锁所同步着的代码块。

 可重入锁最大的是避免死锁。

synchronized  和 ReenterLock 就是典型的可重入锁。

④自旋锁（spinlock）

​	是指尝试获取锁的线程不会立即阻塞，而是采用循环的方式去尝试获取锁，这样的好处是减少线程上下文切换的消耗，缺点是循环会消耗CPU。

​	unsafe.getAndAndInt 采用的就是自旋锁。   即CAS

```java
//手写自旋锁
public class SpinLockDemo {

    //原子引用线程
    AtomicReference<Thread> atomicReference= new AtomicReference<>();

    public void myLock(){

        Thread thread = Thread.currentThread();
        System.out.println(Thread.currentThread().getName()+"\t comin in");
        //循环直到获取到锁
        while (!atomicReference.compareAndSet(null,thread)){
            System.out.println(Thread.currentThread().getName());
        }
    }

    public void unMyLock(){

        Thread thread = Thread.currentThread();
        atomicReference.compareAndSet(thread,null);
        System.out.println(Thread.currentThread().getName()+"\t unMyLock!");
    }

    public static void main(String[] args) {

        SpinLockDemo spinLockDemo = new SpinLockDemo();

        new Thread(()->{
            spinLockDemo.myLock();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            spinLockDemo.unMyLock();
        },"A").start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(()->{
            spinLockDemo.myLock();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            spinLockDemo.unMyLock();
        },"B").start();

    }
}
```

⑤独占锁、共享锁

独占锁：指该锁一次只能被一个线程所持有。对ReentrantLock 和 Synchronized 而言都是独占锁。

共享锁：指该锁可被多个线程所持有。

对ReentrantReadWriteLock 其读锁是共享锁，其写锁是独占锁。

读锁的共享锁可保证并发读是非常高效的，读写，写读，写写的过程是互斥的。

总结：	

​		多个线程同时读一个资源类没有任何问题，所以为了满足并发量，读取共享资源应该可以同时进行。但是，如果有一个线程想去写共享资源来，就不应该再有其他线程可以对资源进行读或写。

​		读---读能共存。

​		读---写不能共存。

​		写---写不能共存。

写操作：原子+独占，整个过程必须是一个完整的统一体，中间不许被分割，被打断。

```java
class MyCache{  //资源类

    private volatile Map<String,Object> map = new HashMap<>();
    private ReentrantReadWriteLock rrwl = new ReentrantReadWriteLock();

    public void put(String key, Object value){

        rrwl.writeLock().lock();   //写锁
        try{

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            rrwl.writeLock().unlock();
        }

        System.out.println(Thread.currentThread().getName()+"\t 正在写入："+ key);
        try{
            Thread.sleep(100);
        }catch(Exception e){
            e.printStackTrace();
        }
        map.put(key,value);
        System.out.println(Thread.currentThread().getName()+"\t 写入完成：");
    }

    public void get(String key){

        rrwl.readLock().lock();  //读锁
        try{

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            rrwl.readLock().unlock();
        }

        System.out.println(Thread.currentThread().getName()+"\t 正在读取：");
        try{
            Thread.sleep(100);
        }catch(Exception e){
            e.printStackTrace();
        }
        Object result = map.get(key);
        System.out.println(Thread.currentThread().getName()+"\t 读取完成");
    }
}

public class ReadWriteLockDemo {

    public static void main(String[] args) {
        MyCache myCache = new MyCache();

        for (int i = 1; i<= 5; i++){
            final int thri = i;
            new Thread(()->{
               myCache.put(thri + "",thri + "");
            },String.valueOf(i)).start();
        }
        for (int i = 1; i<= 5; i++){
            final int thri = i;
            new Thread(()->{
                myCache.get(thri + "");
            },String.valueOf(i)).start();
        }
    }
}
```



## ☆使用过CountDownLatch /CyclicBarrier/Semaphore吗？

⑥ CountDownLatch 锁

- 让一些线程阻塞直到另一些线程完成一系列操作后才被唤醒。

- CountDownLatch主要有两个方法，当一个或多个线程调用await方法时，调用线程会被阻塞。其他线程调用countDown方法会将计数器减1（调用countDown方法的线程不会阻塞），当计数器的值变为0时，因调用await方法被阻塞的线程会被唤醒，继续执行。
- 相当于做减法，减完之后才执行下面语句。

是传入一个值递减完为1后才能执行await下面的代码。

```java
public class CountDownLatchDemo {

    public static void main(String[] args) {

        CountDownLatch countDownLatch = new CountDownLatch(6);

        for (int i = 1; i <= 6; i++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+"\t 国："+ "被灭");
                countDownLatch.countDown();
                //获取枚举类的字段，减少if判断，解耦
            },CountEnum.forEach_CountEnum(i).getRetMessage()).start();
        }

        try {
            countDownLatch.await();
            System.out.println(Thread.currentThread().getName()+"\t *****秦帝国:"+ "，一统天下");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

//枚举类  CountEnum  相当于库名
public enum CountEnum {
	// ONE TWO 等相当于表名 
    ONE(1, "吴"), TWO(2, "楚"), THREE(3, "宋"), FOUR(4, "燕"), FIVE(5, "魏"), SIX(6, "韩");
	// 相当于一个表中的字段
    private Integer retCode;
    private String retMessage;

    CountEnum(Integer retCode, String retMessage) {
        this.retCode = retCode;
        this.retMessage = retMessage;
    }
    public Integer getRetCode() {
        return retCode;
    }
    public String getRetMessage() {
        return retMessage;
    }
	//利用枚举类的values()方法获取 该对象的一个数组，然后再遍历
    public static CountEnum forEach_CountEnum(int index){
        CountEnum[] enums = CountEnum.values();
        for (CountEnum countEnum : enums) {
            if (countEnum.retCode == index){
                return countEnum;
            }
        }
        return null;
    }
}

```

⑦CyclicBarrier 锁

​		CyclicBarrier 的字面意思是可循环（Cyclic）使用的屏障（Barrier）。它要做的事情是，让一组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续干活，线程进入屏障通过CyclicBarrier的await() 方法。

​     相当于做加法，到了先等。

```java
public class CyclicBarrierDemo {

    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7,()->{
            System.out.println("******召唤神龙");
        });
        for (int i = 1; i <= 7; i++) {
            final int threadi = i;
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+"\t 收集到第："+threadi+"个龙珠");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            },String.valueOf(i)).start();
         }
    }
}
```

⑧Semaphore

​		信号量主要用于两个目的，一个是用于多个共享资源的互斥使用，另一个用于并发线程数的控制。

```java
public class SemaphoreDemo {

    public static void main(String[] args) {

        Semaphore semaphore = new Semaphore(3); //模拟3个停车位

        for (int i = 1; i <= 6; i++) { //模拟6部汽车
            new Thread(()->{
                try {
                    semaphore.acquire();      //抢到后，停车位减1
                    System.out.println(Thread.currentThread().getName()+"\t 抢到车位：");
                    Thread.sleep(3000);
                    System.out.println(Thread.currentThread().getName()+"\t 停车3秒后离开车位");
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    semaphore.release();    //离开后，停车位加1
                }
            },String.valueOf(i)).start();
        
    }
```

7 阻塞队列

ArrayBlockingQueue： 是一个基于数组结构的有界阻塞队列，此队列按FIFO（先进先出）原则对元素进行排序。

LinkBlockingQueue：一个基于链表结构的阻塞队列，此队列按FIFO（先进先出）排序元素，吞吐量通常要高于										ArrayBlockingQueue。

SynchronousQueue：一个不存储元素的阻塞队列。每个插入操作必须等到另一个线程调用移除操作，否则插入操作一致处于阻塞状态，吞吐量通常要高。   每一个put操作必须要等待一个take操作，否则不能继续添加元素。



当阻塞队列为空时，从队列中获取元素的操作将会被阻塞。

当阻塞队列是满时，从队列里添加元素的操作将会被阻塞。

试图从空的阻塞队列中获取元素的线程将会被阻塞，直到其他的线程往空的队列插入新的元素。

同样试图往已满的阻塞队列中添加新元素的线程同样也会被阻塞，直到其他的线程从列中移除一个或者多个元素或者完全清空队列后使队列重新变得空闲起来并后续新增。

7.1 阻塞队列的方法：

![阻塞队列API](C:\Users\Administrator\Desktop\笔记\面试笔记  高级\阻塞队列API.png)

![阻塞队列API解释](C:\Users\Administrator\Desktop\笔记\面试笔记  高级\阻塞队列API解释.png)

```java
//   使用SynchronousQueue
public class SynchronousQueueDemo {

    public static void main(String[] args) {

        SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>();

        new Thread(()->{
            try {
                System.out.println(Thread.currentThread().getName()+"\t 存入：a");
                synchronousQueue.put("a");
                System.out.println(Thread.currentThread().getName()+"\t 存入：b");
                synchronousQueue.put("b");
                System.out.println(Thread.currentThread().getName()+"\t 存入：c");
                synchronousQueue.put("c");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"AAA").start();

        new Thread(()->{
            try {
                Thread.sleep(5000);
                System.out.println(Thread.currentThread().getName()+"\t"+synchronousQueue.take());
                Thread.sleep(5000);
                System.out.println(Thread.currentThread().getName()+"\t"+synchronousQueue.take());
                Thread.sleep(5000);
                System.out.println(Thread.currentThread().getName()+"\t"+synchronousQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"BBB").start();

    }
}
```



7.2 synchronized 和 Lock 有什么区别？用新的Lock 有什么好处？

​	① 原始构造	

​		synchronized是关键字属于jvm层面。minitorenter (底层是通过monitor对象来完成，其实wait/notify等方法也依赖于monitor对象只有在同步块或方法中才能调wait/notify等方法)  minitorexit

​		Lock 是具体类（java.util.concurrent.locks.lock）是api层面的锁。

​	②使用方法

​		synchronized 不需要用户去手动释放锁，当synchronized 代码执行完成后系统会自动让线程释放对锁的占用。

​	ReentrantLock 需要用户去手动释放锁，若没有主动释放锁，就有可能导致出现死锁现象。

​	需要lock() 和 unlock() 方法配合 try / finally 语句块来完成。

​	③等待是否可中断

​		synchronized 不可中断，除非抛出异常或正常执行完成。

​		ReentrantLock 可中断，1. 设置超时方法 tryLock( long timeout , TimeUnit unit)

​                                                 2.lockInterruptibly() 放代码块中，调用interrupt() 方法可中断。

​	④加锁是否公平

​		synchronized 非公平锁

​		ReentrantLock两则都可以，默认非公平锁，构造方法可以传入boolean值，true为公平锁，false为非公平锁，	

​	⑤锁绑定多个条件Condition

​		synchronized没有

​		ReentrantLock 用来实现分组唤醒需要唤醒的线程们，可以精确唤醒，而不是像synchronized 要么随机唤醒一个线程要么唤醒全部线程。



7.3传统版的生成者和消费者 和 阻塞版的生成者个消费者

```java
class ShareData{

    private int number = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void increment(){

        lock.lock();
        try{
            while (number != 0){
                //等待 生成
               condition.await();
            }
            number++;
            System.out.println(Thread.currentThread().getName()+"\t "+number);
            condition.signalAll();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }

    public void descement(){

        lock.lock();
        try{
            while (number == 0){
                //等待 生成
                condition.await();
            }
            number--;
            System.out.println(Thread.currentThread().getName()+"\t "+number);
            condition.signalAll();

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }
}

public class ProConsumer_TraditionDemo {

    public static void main(String[] args) {
        ShareData shareData = new ShareData();

        new Thread(()->{
           for(int i = 1; i <= 5; i++){
               shareData.increment();
           }
        },"AAA").start();

        new Thread(()->{
           for(int i = 1; i <= 5; i++){
               shareData.increment();
           }
        },"BBB").start();
    }
}
```

```java
class MyResource{

    //共享数据保证可见性
    public volatile boolean FAGE = true;
    // 保证原子性
    private AtomicInteger atomicInteger = new AtomicInteger();

    BlockingQueue<String> blockingQueue = null;
    //构造方法参数填阻塞队列的接口，根据多态，可以实现7个实现类，更加的解耦
    public MyResource(BlockingQueue<String> blockingQueue){
        this.blockingQueue = blockingQueue;
        System.out.println(blockingQueue.getClass().getName());
    }

    //生产者
    public void myProd() throws Exception {

        String data = null;
        boolean retValue;
        while (FAGE){
            //生产者开始生产蛋糕
           data = atomicInteger.incrementAndGet()+"";
           retValue = blockingQueue.offer(data,2L, TimeUnit.SECONDS);
           if (retValue){
               System.out.println(Thread.currentThread().getName()+"\t 插入队列：" + data+" 成功。");
           }else {
               System.out.println(Thread.currentThread().getName()+"\t 插入队列：" + " 失败。");
           }
           Thread.sleep(1000);
        }
        System.out.println(Thread.currentThread().getName()+" 大老板叫听了，生产结束，表示FAGE=false");
    }

    //消费者
    public void myConsumer() throws Exception{

        String retValue = null;
        while (FAGE){
            //消费者开始消费
            retValue = blockingQueue.poll(2L,TimeUnit.SECONDS);
            if (retValue == null || retValue.equalsIgnoreCase("")){
                FAGE = false;
                System.out.println(Thread.currentThread().getName()+"\t 超过2s钟没有取到蛋糕，消费退出。");
                System.out.println();
                System.out.println();
                return;
            }
            System.out.println(Thread.currentThread().getName()+"\t 消费队列"+retValue+"成功");
        }
    }
}

public class ProdConsumer_BlockQueueDemo {

    public static void main(String[] args) throws InterruptedException {
        MyResource myResource = new MyResource(new ArrayBlockingQueue<>(5));

        new Thread(()->{
            System.out.println(Thread.currentThread().getName()+"\t 生产线程启动！");
            try {
                myResource.myProd();
            }catch (Exception e){
                e.printStackTrace();
            }
        },"Prod").start();

        new Thread(()->{
            System.out.println(Thread.currentThread().getName()+"\t 消费线程启动！");
            System.out.println();
            System.out.println();
            try {
                myResource.myConsumer();
            }catch (Exception e){
                e.printStackTrace();
            }
        },"Cons").start();

        Thread.sleep(5000);
        System.out.println();
        System.out.println();
        myResource.FAGE = false;
    }
}
```

⑨第三种实现多线程的方式

```java
/**
 * 第三种获取多线程的方式 Callable.  带返回值
 *
 */
class MyThread implements Callable<Integer> {
    
    @Override
    public Integer call() throws Exception {
        System.out.println("comin in Callable**************");
        return 1024;
    }
}

public class CallableDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //这里用FutureTask 主要是适配，因为Thread 构造方法中没有直接传Callable ，
        FutureTask<Integer> futureTask = new FutureTask<>(new MyThread());
        new Thread(futureTask,"AA").start();

        int result = 100;
        int result2 = futureTask.get(); //要求获得Callable线程的计算结果，如果没有计算完成就要去强求，会导致堵塞，值得计算完成后才能往下执行，所以建议将它放到最后。

        System.out.println("****"+(result+result2));
    }
}
```



⑩线程池的优势

​	线程池做的工作主要是控制运行的线程的数量，处理过程中将任务放入队列，然后在线程创建后启动这些任务，如果线程数量超过了最大数量的线程排队等候，等其他线程执行完毕，再从队列中取出任务来执行。

​	它的主要特点为：线程复用，控制最大并发数，管理线程。

第一：降低资源消耗。通过重复利用已创建的线程降低线程创建和销毁造成的消耗。

第二：提高响应速度。当任务到达时，任务可以不需要的等到线程创建就能立即执行。

第三：提高线程的可管理性。线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定			性，使用线程池可以进行统一的分配，调优和监控。

​	

```java
线程池常使用的3个方式
//1.Executors.newFixedThreadPool(5);//一池5个处理线程  。。执行长期的任务，性能比较好
//2.Executors.newSingleThreadExecutor();//一池1个处理线程。。一个任务一个任务执行的场景
//3.Executors.newCachedThreadPool();//一池 N 个处理线程。。执行很多短期异步的小程序或者负载较轻的服                                                                                      务器
//第4种获取/使用java线程池的方式
public class ThreadPoolExecutorDemo {

    public static void main(String[] args) {

        ExecutorService threadPool1 = Executors.newFixedThreadPool(5);//一池5个处理线程
        ExecutorService threadPool2 = Executors.newSingleThreadExecutor();//一池1个处理线程
        ExecutorService threadPool3 = Executors.newCachedThreadPool();//一池 N 个处理线程

        try {
            //模拟10个用户来办理业务，每个用户就是来自一个线程。
            for (int i = 0; i <=10 ; i++) {
                threadPool1.execute(()->{
                    System.out.println(Thread.currentThread().getName()+"\t 办理业务");
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            threadPool1.shutdown();
        }
    }
}


//1. 源码分析
 public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
    }
/**
	主要特点如下:
		1. 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
		2. newFixedThreadPool 创建 线程池corePoolSize 和 maximumPoolSize 值是相等的，它使用的LinkedBlockingQueue；
*/

//2. 源码分析
public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
    }
/**
	主要特点如下:
		1. 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序执行。
		2. newSingleThreadPool 创建 线程池corePoolSize 和 maximumPoolSize 值都设置为1，它使用的LinkedBlockingQueue；
*/

//3. 源码分析
public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }
/**
	主要特点如下:
		1. 创建一个可缓存的线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
		2. newCachedThreadPool 创建 线程池corePoolSize设置为0， ,将 maximumPoolSize 值设置为Integer.MAX_VALUE，使用的SynchronousQueue；也就是说来了任务就创建线程执行，当线程空闲超过60秒，就销毁线程。
*/    
```

10.1 线程池的七大参数：

```
1. corePoolSize： 线程池中的常驻核心线程数。（在创建了线程池后，当有请求任务来了之后，就会安排池中的线程            去执行请求任务。当线程池中的线程数目达到corePoolSize后，就会把达到的任务放到缓存队列当中。）
2.maximumPoolSize： 线程池能够容纳同时执行的最大线程数，此值必须大于等于1
3.keepAliveTime： 多余的空闲线程的存活时间。当前线程池数量超过corePoolSize时，当空闲时间达到	                       keepAliveTime值时，多余空闲线程会被注销直到只剩下corePoolSize个线程为止。
4.unit: keepAliveTime的单位。
5.workQueue：任务队列，被提交但尚未被执行的任务。
6.threadFactory： 表示生成线程池中工作线程的线程工厂，用于创建线程一般用默认的即可。
7.handler：拒绝策略，表示队列满了并且工作线程大于等于线程池的最大线程数（maximumPoolSize ）时如何来拒绝请求执行的runnable的策略。
```

10.2 线程池底层工作原理

![](C:\Users\Administrator\Desktop\笔记\面试笔记  高级\线程池的工作原理.png)

10.3 拒绝策略

```java
1. AbortPolicy(默认): 直接抛出RejectedExecutionException 异常阻止系统正常运行。
2. CallerRunsPolicy: “调用者运行”一种调节机制，该策略即不会抛弃任务，也不会抛出异常，而是将某些任务回退                       到调用者，从而降低新任务的流量。
3. DiscardOldestPolicy: 抛弃队列中等待最久的任务，然后把当前任务加入队列中尝试再次提交当前任务。
4. DiscardPolicy：直接丢弃任务，不予任何处理也不抛出异常。如果允许任务丢失，这是最好的一种方案。
```

```java
//手写线程池
public class ThreadPoolExecutorDemo {

    public static void main(String[] args) {
		
       	//七个参数，具体意思同上面
        ExecutorService executorService = new ThreadPoolExecutor(
                2,
                5,
                1L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        try {
            //模拟10个用户来办理业务，每个用户就是来自一个线程。
            for (int i = 0; i <=10 ; i++) {
                executorService.execute(()->{
                    System.out.println(Thread.currentThread().getName()+"\t 办理业务!");
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            executorService.shutdown();
        }
    }
```

10.4 合理配置线程池数你是如何考虑的？

 ①CPU密集型

```java
///查看CPU的核数
Runtime.getRuntime().availableProcessors();
```

CPU密集的意思是该任务需要大量的运算，而没有阻塞，CPU一直全速运行。

CPU密集任务只有在真正的多核CPU上才可能得到加速（通过多线程）。

而在单核CPU上，无论你开几个模拟的多线程该任务都不可能得到加速，因为CPU总的运算能力就那些。

CPU密集型任务配置尽可能少的线程数量。

一般公式：CPU核数 + 1 个线程的线程池。

② IO密集型

由于IO密集型任务线程并不是一直在执行任务，则应配置尽可能多的线程，如   CPU核数 * 2。

IO密集型，即该任务需要大量的IO，即大量的阻塞。

在单线程上运行IO密集型的任务会导致浪费大量的CPU运算能力浪费在等待。

所以在IO密集型任务中使用多线程可以大大的加速程序运行，即使在单核CPU上，这种加速主要就是利用了被浪费掉的阻塞时间。

IO密集型时，大部分线程都阻塞，故需要多配置线程数：

参考公式：CPU核数/ 1-阻塞系数                                            阻塞系数在0.8  ----0.9 之间

比如8核CPU： 8/1-0.9 = 80 个线程数。



11. 死锁编码及定位分析

    ①产生死锁的主要原因： 系统资源不足， 进程运行推进的顺序不合适，资源分配不当。



```java
//死锁的代码
class DeadLock implements Runnable{

    private String lockA;
    private String lockB;

    public DeadLock(String lockA, String lockB) {
        this.lockA = lockA;
        this.lockB = lockB;
    }
    
    @Override
    public void run() {

        synchronized (lockA){
            System.out.println(Thread.currentThread().getName()+"\t 自己持有"+ lockA+"\t 尝试获得： "+ lockB);
            try {
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            synchronized (lockB){
                System.out.println(Thread.currentThread().getName()+"\t 自己持有"+lockB+"\t 尝试获得: "+lockA);
            }
        }
    }
}

/**
 * 死锁  两个线程争夺同一资源，导致双方不释放导致死锁。
 */
public class DeadLockDemo {

    public static void main(String[] args) {

        String lockA = "lockA";
        String lockB = "lockB";

        new Thread(new DeadLock(lockA,lockB),"Thread AAA").start();
        new Thread(new DeadLock(lockB,lockA),"Thread BBB").start();
    }
}
```

②定位分析

 在IDEA 的Windows 窗口 输入： 

jsp命令定位进程号：  jps -l   

jstack 找到死锁查看： jstack ID(要查询的id号)



二 GC

1.如何判断一个对象是否可以被回收？

①枚举根节点做可达性分析（根搜索路径）

​	java中可以作为GC Roots 的对象：虚拟机栈（栈帧中的局部变量区，也叫局部变量表）中引用的对象。方法区中的类静态属性引用的对象。方法区中常量引用的对象。本地方法栈中JNI（Native方法）引用的对象。

![](C:\Users\Administrator\Desktop\笔记\面试笔记  高级\GC可达.png)



2.强引用，软引用，弱引用，虚引用分别是什么？

①强引用

​	当内存不足，jvm 开始垃圾回收，对于强引用的对象，就算是出现了OOM也不会对该对象进行回收，死都不会。

​	强引用是我们最常见的普通对象引用，只有还有强引用指向一个对象，就能表明对象还 “活着”，垃圾收集器不会碰这种对象。在java中最常见的就是强引用，把一个对象赋给一个引用变量，这个引用变量就是一个强引用。当一个对象被强引用变量引用时，它处于可达状态，它是不可能被垃圾回收机制回收的，即使该对象以后永远不会被用到jvm也不会回收。因此强引用是造成java内存泄露的主要原因之一。

​	对于一个普通的对象，如果没有其他的引用关系，只要超过了引用的作用域或者显式地将相应（强）引用赋值为null，一般认为就是可以被垃圾收集的了（当然具体回收时机还是要看垃圾收集策越）。

②软引用

​	是一种相对强引用弱化了一些的引用，需要用java.lang.ref.SoftReference 类来实现，可以让对象豁免一些垃圾收集。对于只有软引用的对象来说，当系统内存充足时，它不会被回收，当系统内存不足时它会被回收。

软引用通常用在对内存敏感的程序中，比如高速缓存就有用到软引用，内存够用的时候就保留，不够用就回收。

③弱引用

​	弱引用需要用java.lang.ref.WeakReference 类来实现，它比软引用的生存期更短，对于只有弱引用的对象来说，要用垃圾回收机制一运行，不管jvm 的内存空间是否足够，都会回收该对象占用的内存。

④软引用和弱引用的适用场景

​	加入有一个应用需要读取大量的本地图片： 如果每次读取图片都从硬盘读取则会严重影响性能，如果一次性全部加载到内存中又可能造成内存溢出。

​    此时使用软引用可以解决这个问题。 设计思路是： 用一个HashMap 来保存图片的路径和响应图片对象关联的软引用之间的映射关系，在内存不足时，JVM会自动回收这些缓存图片对象所占用的空间，从而有效地避免了OOM的问题。

Map<String,SoftReference<Bitmap>>     imageCache = new HashMap<String,SoftReference<Bitmap>>();

⑤虚引用

​	虚引用需要java.lang.ref.PhantomReference 类来实现。就是形同虚设，与其它几种引用都不同，虚引用并不会决定对象的生命周期。如果一个对象仅持有虚引用，那么它就和没有任何引用一样，在任何时候都可能被垃圾回收器回收，它不能单独使用也不能通过它访问对象，虚引用必须和引用队列（ReferenceQueue）联合使用。

​	虚引用的主要作用是跟踪对象被垃圾回收的状态。仅仅是提供了一种确保对象被finalize以后，做某些事情的机制。PhantomReference的get方法总是返回null，因此无法访问对应的引用对象。其意义在于说明一个对象已经进入finalization阶段，可以被gc回收，用来实现比finalization 机制更灵活的回收操作。

​	换句话说，设置虚引用关联的唯一目的，就是在这个对象被收集器回收的时候收到一个系统通知或者后续添加进一步的处理，java技术允许使用finalize()方法在垃圾收集器将对象从内存中清除出去之前做必要的清理工作。



总结：

![](C:\Users\Administrator\Desktop\笔记\面试笔记  高级\GC垃圾回收.png)



3.

①java.lang.StackOverflowError异常

②java.lang.OutOfMemoryError:  java heap space

③java.lang.OutOfMemoryError:  GCIverhead 异常

​	GC回收时间长时会抛出OutOfMemeroyError。过长的定义是，超过98%的时间用来做GC并且回收了不到2%的堆内存，连续多次GC都只回收了不到2%的极端情况下才会抛出。假如不抛出GC overhead limit 错误会发生什么情况呢？ 那就是GC清理的这么点内存很快会再次填满，迫使GC再次执行，这样就形成恶性循环，CPU使用率一直是100%，而GC却没有任何成果。

































































