# JPA

## 1.Jpa

### ①JPA 和hibernate的关系

•JPA 是hibernate的一个抽象（就像JDBC和JDBC驱动的关系）：

 –JPA 是规范：JPA本质上就是一种  ORM 规范，不是ORM框架—— 因为 JPA 并未提供ORM 实现，它只是制订了     一些规范，提供了一些编程的API 接口，但具体实现则由ORM厂商提供实现

–Hibernate 是实现：Hibernate除了作为ORM 框架之外，它也是一种 JPA 实现

•从功能上来说， JPA是Hibernate 功能的一个子集

### ②JPA 的供应商

•JPA 的目标之一是制定一个可以由很多供应商实现的API，目前Hibernate3.2+、TopLink10.1+以及OpenJPA都提供了JPA 的实现

•Hibernate

–JPA 的始作俑者就是Hibernate 的作者

–Hibernate 从3.2 开始兼容 JPA

•OpenJPA

–OpenJPA  是Apache组织提供的开源项目

•TopLink

–TopLink 以前需要收费，如今开源了

### ③JPA的优势

•标准化:  提供相同的API，这保证了基于JPA开发的企业应用能够经过少量的修改就能够在不同的JPA框架下运行。

•简单易用，集成方便:  JPA 的主要目标之一就是提供更加简单的编程模型，在JPA 框架下创建实体和创建Java  类一样简单，只需要使用javax.persistence.Entity进行注释；JPA的框架和接口也都非常简单，

•可媲美JDBC的查询能力:  JPA的查询语言是面向对象的，JPA定义了独特的JPQL，而且能够支持批量更新和修改、JOIN、GROUPBY、HAVING等通常只有SQL 才能够提供的高级查询特性，甚至还能够支持子查询。

•支持面向对象的高级特性:JPA 中能够支持面向对象的高级特性，如类之间的继承、多态和类之间的复杂关系，最大限度的使用面向对象的模型

### ④JPA 包括3方面的技术

•ORM 映射元数据：JPA支持XML 和  JDK5.0 注解两种元数据的形式，元数据描述对象和表之间的映射关系，框架据此将实体对象持久化到数据库表中。  

•JPA 的API：用来操作实体对象，执行CRUD操作，框架在后台完成所有的事情，开发者从繁琐的JDBC和SQL代码中解脱出来。  

•查询语言（JPQL）：这是持久化操作中很重要的一个方面，通过面向对象而非面向数据库的查询语言查询数据，避免程序和具体的  SQL 紧密耦合。

## 2.使用JPA持久化对象的步骤

•创建 persistence.xml,在这个文件中配置持久化单元

​	–需要指定跟哪个数据库进行交互;

​	–需要指定 JPA 使用哪个持久化的框架以及配置该框架的基本属性

•创建实体类, 使用annotation 来描述实体类跟数据库表之间的映射关系.

•使用 JPAAPI 完成数据增加、删除、修改和查询操作

​	–创建 EntityManagerFactory(对应 Hibernate中的SessionFactory);

​	–创建 EntityManager (对应Hibernate中的Session);

## 3.Jpa常用注解

### ①@Entity

​	标注用于实体类声明语句之前，指出该Java类为实体类，将映射到指定的数据库表。如声明一个实体Customer，它将映射到数据库中的customer表上。

```java
@Entity
public class Customer{
  
}
```

### ②@Table

•当实体类与其映射的数据库表名不同名时需要使用@Table 标注说明，该标注与 @Entity标注并列使用，置于实体类声明语句之前，可写于单独语句行，也可与声明语句同行。

•@Table标注的常用选项是name，用于指明数据库的表名

•@Table标注还有一个两个选项catalog和schema用于设置表所属的数据库目录或模式，通常为数据库名。uniqueConstraints选项用于设置约束条件，通常不须设置。

```java
@Table(name="customer_table")
@Entity
public class Customer{
  
}
```

### ③@Id

•@Id 标注用于声明一个实体类的属性映射为数据库的主键列。该属性通常置于属性声明语句之前，可与声明语句同行，也可写在单独行上。

•@Id标注也可置于属性的getter方法之前。

```java
@Column(name="PERSON_ID")
@GeneratedValue(strategy=GenerationType.AUTO)
@Id
public Integer getPersonId(){
  return personId;
}
```

### ④@Generated Value

•@GeneratedValue  用于标注主键的生成策略，通过 strategy属性指定。默认情况下，JPA自动选择一个最适合底层数据库的主键生成策略：SqlServer对应identity，MySQL对应autoincrement。

•在 javax.persistence.GenerationType中定义了以下几种可供选择的策略：

–IDENTITY：采用数据库ID自增长的方式来自增主键字段，Oracle不支持这种方式；

–AUTO：JPA自动选择合适的策略，是默认选项；

–SEQUENCE：通过序列产生主键，通过@SequenceGenerator 注解指定序列名，MySql不支持这种方式

–TABLE：通过表产生主键，框架借由表模拟序列产生主键，使用该策略可以使应用更易于数据库移植。

```java
@Column(name="PERSON_ID")
@GeneratedValue(strategy=GenerationType.AUTO)
@Id
public Integer getPersonId(){
  return personId;
}
```

### ⑤Basic

•@Basic 表示一个简单的属性到数据库表的字段的映射,对于没有任何标注的 getXxxx() 方法,默认即为@Basic

•fetch: 表示该属性的读取策略,有EAGER 和 LAZY两种,分别表示主支抓取和延迟加载,默认为EAGER.

optional: 表示该属性是否允许为null,默认为true

### ⑥Column

•当实体的属性与其映射的数据库表的列不同名时需要使用@Column标注说明，该属性通常置于实体的属性声明语句之前，还可与@Id标注一起使用。

•@Column标注的常用属性是name，用于设置映射数据库表的列名。此外，该标注还包含其它多个属性，如：unique 、nullable、length 等。

•@Column 标注的columnDefinition属性:表示该字段在数据库中的实际类型.通常ORM框架可以根据属性类型自动判断数据库中字段的类型,但是对于Date类型仍无法确定数据库中字段类型究竟是DATE,TIME还是TIMESTAMP.此外,String的默认映射类型为VARCHAR,如果要将String类型映射到特定数据库的BLOB或TEXT字段类型.

•@Column标注也可置于属性的getter方法之前

```java
@Column(name="PERSON_ID")
@GeneratedValue(strategy=GenerationType.AUTO)
@Id
public Integer getPersonId(){
  return personId;
}
```

### ⑦Transient

表示该属性并非一个到数据库表的字段的映射,ORM框架将忽略该属性.

如果一个属性并非数据库表的字段映射,就务必将其标示为@Transient,否则,ORM框架默认其注解为@Basic

### ⑧@Temporal

•在核心的 JavaAPI 中并没有定义Date类型的精度(temporalprecision).  而在数据库中,表示Date类型的数据有DATE,TIME, 和 TIMESTAMP三种精度(即单纯的日期,时间,或者两者兼备). 在进行属性映射时可使用@Temporal注解来调整精度.

```java
@Temporal(Temporal.DATE)
public Integer getBrity(){
  return brity;
}
```

## 3.EntityManager

```java
/**
	 * 同 hibernate 中 Session 的 refresh 方法. 
	 */
	@Test
	public void testRefresh(){
		Customer customer = entityManager.find(Customer.class, 1);
		customer = entityManager.find(Customer.class, 1);
		
		entityManager.refresh(customer);
	}
	
	/**
	 * 同 hibernate 中 Session 的 flush 方法. 
	 */
	@Test
	public void testFlush(){
		Customer customer = entityManager.find(Customer.class, 1);
		System.out.println(customer);
		
		customer.setLastName("AA");
		
		entityManager.flush();
	}
	
	//若传入的是一个游离对象, 即传入的对象有 OID. 
	//1. 若在 EntityManager 缓存中有对应的对象
	//2. JPA 会把游离对象的属性复制到查询到EntityManager 缓存中的对象中.
	//3. EntityManager 缓存中的对象执行 UPDATE. 
	@Test
	public void testMerge4(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("dd@163.com");
		customer.setLastName("DD");
		
		customer.setId(4);
		Customer customer2 = entityManager.find(Customer.class, 4);
		
		entityManager.merge(customer);
		
		System.out.println(customer == customer2); //false
	}
	
	//若传入的是一个游离对象, 即传入的对象有 OID. 
	//1. 若在 EntityManager 缓存中没有该对象
	//2. 若在数据库中也有对应的记录
	//3. JPA 会查询对应的记录, 然后返回该记录对一个的对象, 再然后会把游离对象的属性复制到查询到的对象中.
	//4. 对查询到的对象执行 update 操作. 
	@Test
	public void testMerge3(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("ee@163.com");
		customer.setLastName("EE");
		
		customer.setId(4);
		
		Customer customer2 = entityManager.merge(customer);
		
		System.out.println(customer == customer2); //false
	}
	
	//若传入的是一个游离对象, 即传入的对象有 OID. 
	//1. 若在 EntityManager 缓存中没有该对象
	//2. 若在数据库中也没有对应的记录
	//3. JPA 会创建一个新的对象, 然后把当前游离对象的属性复制到新创建的对象中
	//4. 对新创建的对象执行 insert 操作. 
	@Test
	public void testMerge2(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("dd@163.com");
		customer.setLastName("DD");
		
		customer.setId(100);
		
		Customer customer2 = entityManager.merge(customer);
		
		System.out.println("customer#id:" + customer.getId());
		System.out.println("customer2#id:" + customer2.getId());
	}
	
	/**
	 * 总的来说: 类似于 hibernate Session 的 saveOrUpdate 方法.
	 */
	//1. 若传入的是一个临时对象
	//会创建一个新的对象, 把临时对象的属性复制到新的对象中, 然后对新的对象执行持久化操作. 所以
	//新的对象中有 id, 但以前的临时对象中没有 id. 
	@Test
	public void testMerge1(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("cc@163.com");
		customer.setLastName("CC");
		
		Customer customer2 = entityManager.merge(customer);
		
		System.out.println("customer#id:" + customer.getId());
		System.out.println("customer2#id:" + customer2.getId());
	}
	
	//类似于 hibernate 中 Session 的 delete 方法. 把对象对应的记录从数据库中移除
	//但注意: 该方法只能移除 持久化 对象. 而 hibernate 的 delete 方法实际上还可以移除 游离对象.
	@Test
	public void testRemove(){
//		Customer customer = new Customer();
//		customer.setId(2);
		
		Customer customer = entityManager.find(Customer.class, 2);
		entityManager.remove(customer);
	}
	
	//类似于 hibernate 的 save 方法. 使对象由临时状态变为持久化状态. 
	//和 hibernate 的 save 方法的不同之处: 若对象有 id, 则不能执行 insert 操作, 而会抛出异常. 
	@Test
	public void testPersistence(){
		Customer customer = new Customer();
		customer.setAge(15);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("bb@163.com");
		customer.setLastName("BB");
		customer.setId(100);
		
		entityManager.persist(customer);
		System.out.println(customer.getId());
	}
	
	//类似于 hibernate 中 Session 的 load 方法
	@Test
	public void testGetReference(){
		Customer customer = entityManager.getReference(Customer.class, 1);
		System.out.println(customer.getClass().getName());
		
		System.out.println("-------------------------------------");
//		transaction.commit();
//		entityManager.close();
		
		System.out.println(customer);
	}
	
	//类似于 hibernate 中 Session 的 get 方法. 
	@Test
	public void testFind() {
		Customer customer = entityManager.find(Customer.class, 1);
		System.out.println("-------------------------------------");
		
		System.out.println(customer);
	}
```

### ①find

•find(Class<T> entityClass,Object primaryKey)：返回指定的OID对应的实体类对象，如果这个实体存在于当前的持久化环境，则返回一个被缓存的对象；否则会创建一个新的Entity,并加载数据库中相关信息；若OID不存在于数据库中，则返回一个null。第一个参数为被查询的实体类类型，第二个参数为待查找实体的主键值。

### ②getReference

•getReference (Class<T>entityClass,Object primaryKey)：与find()方法类似，不同的是：如果缓存中不存在指定的Entity,EntityManager会创建一个Entity类的代理，但是不会立即加载数据库中的信息，只有第一次真正使用此Entity的属性才加载，所以如果此OID在数据库不存在，getReference()不会返回null值,而是抛出EntityNotFoundException

### ③persist

•persist(Object entity)：用于将新创建的Entity纳入到EntityManager的管理。该方法执行后，传入persist()方法的Entity对象转换成持久化状态。

–如果传入 persist()方法的Entity对象已经处于持久化状态，则persist()方法什么都不做。

–如果对删除状态的Entity进行persist()操作，会转换为持久化状态。

–如果对游离状态的实体执行persist()操作，可能会在persist()方法抛出EntityExistException(也有可能是在flush或事务提交后抛出)。



























































