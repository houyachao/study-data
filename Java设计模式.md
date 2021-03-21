# 一、Java 设计模式

## 1、面向对象设计原则

### ① 单一职责原则

​	一个类只负责一个功能领域中的相应职责，或者可以定义为：就一个类而言，应该只有一个引起它变化的原因。

​	单一职责原则告诉我们：一个类不能太累！一个类承担的职责越多，它被复用的可能性就越小，而且一个类承担的职责就越多，相当于将这些职责耦合在一起，当其中一个职责变化时，可能会影响其他职责的运作，因此要将这些职责进行分离，将不同的职责封装在不同的类中，即将不同的变化原因封装在不同的类中。

​	单一职责 是实现高内聚，低耦合 的指导方针。

我们对下面一个例子进行说明，单一职责原则：

![1.单一职责](C:\Users\Administrator\Desktop\笔记\java设计模式\1.单一职责.png)

1、在CustomerDataChart 类中 的方法说明：

​	getConnection（）方法用于连接数据库。 findCustomer（）用于查询所有的客户信息，createChart（）用于创建图表，displayChart（）用于显示图表。

​	在上图CustomerDataChart 类承担了太多的职责，即包含于数据库相关的方法，又包含了与图表生成和显示相关的方法。如果在其他类中也需要连接数据库或者使用findCustomer（）方法查询客户信息，则难以实现代码的重用。修改任何一个方法，它不止一个引起它变化的原因，违背了单一职责原则。

2、现使用单一职责原则对其进行重构：

​	![1.单一职责抽取](C:\Users\Administrator\Desktop\笔记\java设计模式\1.单一职责抽取.png)

 DBUtil：负责连接数据库，CustomerDAO：负责操作数据库中的Customer 表，CustomerDataChart ：负责图表生成和显示

### ② 开闭原则

​	一个软件实体应当对扩展开放，对修改关闭。 即 软件实体应尽量在不修改原有代码的情况下进行扩展。

​	在开闭原则中，软件实体可以指一个软件模块、一个由多个类组成的局部结构或一个独立的类。

​	为了满足开闭原则，需要对系统进行抽象化设计，抽象化是开闭原则的关键。可以为系统定义一个相对稳定的抽象层，而将不同的实现行为移至具体的实现层中完成。在很多面向对象编程语言中都提供了接口、抽象类等机制，可以通过他们定义系统的抽象层，再通过具体类来进行扩展。如果需要修改系统的行为，无需对抽象层进行任何改动，只需要增加新的具体类来实现新的业务功能即可，实现在不修改已有代码的基础上扩展系统的功能，达到开闭原则的要求。

![2.开闭原则1](C:\Users\Administrator\Desktop\笔记\java设计模式\2.开闭原则1.png)

```java
if (type.equals("pie")) {
  PieChar chart = new PieChart();
  chart.display();
} else if (type.equals("bar")) {
  BarChart chart = new BarChart();
  chart.display();
}
..
```

​	在该代码中，如果需要增加一个新的图表类，就需要修改此类代码，新增判断逻辑，就违反了开闭原则。

**我们对其改进，使用开闭原则：**

![2.开闭原则2](C:\Users\Administrator\Desktop\笔记\java设计模式\2.开闭原则2.png)

我们引入了抽象图表类 AbstractChart，且 ChartDisplay 针对抽象图表类进行编程，并通过setChart() 方法由客户端来设置实例化的具体图表对象，在ChartDisplay的display（）方法中调用chart对象的display（）方法显示图表，如果需要增加一种新的图表，如折线图，只需要将该类作为AbstractChart的子类，在客户端向ChartDisplay 中注入对象即可，无需修改现有类库的源代码。

### ③ 里氏替换原则

​	**里氏替换原则：所有引用基类（父类）的地方必须能透明地使用其子类的对象。**

​	里氏替换原则告诉我们，在软件中将一个基类对象替换成它的子类对象，程序将不会产生任何错误和异常，反过来则不成立，如果一个软件实体使用的是一个子类对象的话，那么它不一定能够使用基类对象。例如：我喜欢动物，那我一定喜欢狗，因为狗是动物的子类；但是我喜欢狗，不能断定我喜欢动物，因为我并不喜欢老鼠，虽然它是动物。

​	例如有两个类，一个类为BaseClass，另一个是SubClass类，并且SubClass类是BaseClass 类的子类，那么一个方法如果可以接受一个BaseClass 类型的基类对象base的话，如：method1（base），那么它必然可以接受一个BaseClass类型的子类对象sub, method1（sub） 能够正常运行。反过来的代换不成立。

​	里氏替换原则是实现开闭原则的重要方式之一，由于使用基类对象的地方都可以使用子类对象，因此在程序中尽量使用基类类型来对对象进行定义，而在运行时再确定其子类类型，用子类对象来替换父类对象。

**在使用里氏替换原则时需要注意如下几个问题：**

​	1、子类的所有方法必须在父类中声明，或子类必须实现父类中声明的所有方法。根据里氏替换原则，为了保证系统的扩展性，在程序中通常使用父类来进行定义，如果一个方法只存在子类中，在父类中不提供相应的声明，则无法再以父类定义的对象中使用该方法。

​	2、我们在运用里氏替换原则时，尽量把父类设计为抽象类或接口，让子类继承父类或实现父接口，并实现在父类中声明的方法，运行时，子类实例替换父类实例，我们可以很方便地扩展系统的功能，同时无须修改原有子类的代码，增加新的功能可以通过增加一个新的子类来实现。里氏替换原则是开闭原则的具体实现手段之一。

​	3、Java 语言中，在编译阶段，Java编译器会检查一个程序是否符合里氏替换原则，这是一个与实现无关的，纯语法意义上的检查，但java编译器的检查是有局限的。

### ④ 依赖倒转原则

​	如果说开闭原则是面向对象设计的目标的话，那么依赖倒转原则就是面向对象设计的主要实现机制之一，它是系统抽象化的具体实现。

​	**依赖倒转原则：抽象不应该依赖于细节，细节应当依赖于抽象。换言之，要针对接口编程，而不是针对实现编程。**

​	依赖倒转原则要求我们在程序代码中传递参数时或在关联关系中，尽量引用层次高的抽象层类，即使使用接口和抽象类进行变量类型声明、参数类型声明、方法返回类型声明，以及数据类型的转换等，而不是要用具体类来做这些事情。为了确保该原则的应用，一个具体类应当只实现接口或抽象类中声明过的方法，而不要给出多余的方法，否则将无法调用到在子类中增加的新方法。

​	在引用抽象层后，系统将具有很好的灵活性，在程序中尽量使用抽象层进行编程，而将具体类写在配置文件中，这样一来，如果系统行为发生变化，只需要对抽象层进行扩展，并修改配置文件，而无须修改原有系统的源代码，在不修改的情况下来扩展系统的功能，满足开闭原则的要求。

​	在实现一来倒转原则时，我们需要针对抽象层编程，而将具体类的对象通过依赖注入的方式注入到其他对象中，依赖注入是指一个对象要与其他对象发生依赖关系时，通过抽象来注入所依赖的对象。 **常用的注入方式有：构造器注入，设置注入（Setting注入）和接口注入。**

### ⑤ 接口隔离原则

​	接口隔离原则：使用多个专门的接口，而不使用单一的总接口，即客户端不应该依赖那些它不需要的接口。

​	根据接口隔离原则，当一个接口太大时，我们需要将它分割成一些更细小的接口，使用该接口的客户端仅需知道与之相关的方法即可。

**这里的“接口”往往有两种不同的含义：**

​	一种是指一个类型所具有的方法特征的集合，仅仅是一种逻辑上的抽象。

​	另外一种是指某种语言具体的“接口”定义，有严格的定义和结构，比如Java语言中的interface。

对于这两种不同的含义，ISP的表达方式以及含义都有所不同：
​	(1) 当把“接口”理解成一个类型所提供的所有方法特征的集合的时候，这就是一种逻辑上的概念，接口的划分将直接带来类型的划分。可以把接口理解成角色，一个接口只能代表一个角色，每个角色都有它特定的一个接口，此时，这个原则可以叫做“角色隔离原则”。
​	(2) 如果把“接口”理解成狭义的特定语言的接口，那么ISP表达的意思是指接口仅仅提供客户端需要的行为，客户端不需要的行为则隐藏起来，应当为客户端提供尽可能小的单独的接口，而不要提供大的总接口。在面向对象编程语言中，实现一个接口就需要实现该接口中定义的所有方法，因此大的总接口使用起来不一定很方便，为了使接口的职责单一，需要将大接口中的方法根据其职责不同分别放在不同的小接口中，以确保每个接口使用起来都较为方便，并都承担某一单一角色。接口应该尽量细化，同时接口中的方法应该尽量少，每个接口中只包含一个客户端（如子模块或业务逻辑类）所需的方法即可，这种机制也称为“定制服务”，即为不同的客户端提供宽窄不同的接口。

### ⑥ 合成复用原则

​	**合成复用原则：尽量使用对象组合，而不是继承来达到复用的目的。**

​	合成复用原则就是在一个新的对象里通过关联关系（包括组合关系和聚合关系）来使用一些已有的对象，使之成为新对象的一部分；新对象通过委派调用已有对象的方法达到复用功能的目的。**简言之：复用时要尽量使用组合\聚合关系（关联关系）少用继承。**

​	在面向对象设计中，可以通过两种方法在不同的环境中复用已有的设计和实现，即通过组合\聚合关系或通过继承，但首先应该考虑使用组合\聚合，组合\聚合可以使系统更加灵活，降低类与类之间的耦合度，一个类的变化堆其他类造成的影响相对较少，其次才考虑继承，在使用继承时，需要严格遵循里氏替换原则，有效使用继承会有助于对问题的理解，降低复杂度，而滥用继承反而会增加系统构建和维护的难度以及系统的复杂度，因此需要慎用使用继承复用。

​	由于组合或聚合关系可以将已有的对象（也可称为成员对象）纳入到新对象中，使之成为新对象的一部分，因此新对象可以调用已有对象的功能，这样做可以使得成员对象的内部实现细节对于新对象不可见，所以这种复用又称为 “黑箱”复用，相对继承关系而言，其耦合度相对较低，成员对象的变化对新对象的影响不大，可以在新对象中根据实际需要有选择性地调用成员对象的操作；合成复用可以在运行时动态进行，新对象可以动态地引用与成员对象类型相同的其他对象。

### ⑦ 迪米特法则

​	**迪米特法则：一个软件实体应当尽可能少地与其他实体发生相互作用。**

​	如果一个系统符合迪米特法则，那么当其中某一个模块发生修改时，就会尽力少地影响其他模块，扩展会相对容易，这是对软件实体之间通信的限制，迪米特法则要求限制软件实体之间通信的宽度和深度。迪米特法则可降低系统的耦合度，使类与类之间保持松散的耦合关系。

​	**迪米特法则还有几种定义形式，包括：不要和 “陌生人”说话，只与你的直接朋友通信等，在迪米特法则中，对于一个对象，其朋友包括以下几类：**

- 当前对象本身（this）；
- 以参数形式传入到当前对象方法中的对象
- 当前对象的成员对象
- 如果当前对象的成员对象是一个集合，那么集合中的元素也都是朋友
- 当前对象所创建的对象

任何一个对象，如果满足上面的条件之一，就是当前对象的 “朋友”，否则就是 “陌生人”。在应用迪米特法则时，一个对象只能与直接朋友发送交互，不要与 “陌生人”发生直接交互，这样做可以降低系统的耦合度，一个对象的改变不会给太多其他对象带来影响。

​	迪米特法则要求我们在设计系统时，应该尽量减少对象之间的交互，如果两个对象之间不彼此直接通信，那么这两个对象就不应当发生任何直接的相互作用，如果其中的一个对象需要调用另一个对象的某一个方法的话，可以通过第三者转发这个调用。**简言之，就是通过引入一个合理的第三者来降低现有对象之间的耦合度。**

![3.迪米特法则2](C:\Users\Administrator\Desktop\笔记\java设计模式\3.迪米特法则2.png)

 我们通过引入一个专门用于控制界面控件交互的中间类（Mediator）来降低界面控件之间的耦合度，引入中间类之后，界面控件之间不再发生直接引用，而是将请求先转发给中间类，再由中间类来完成堆其他控件的调用。当需要增加或删除新的空间时，只需要修改中间类即可，无须修改新增空间或已有控件的源代码。

## 2、6种创建型模式

### ① 简单工厂模式

#### 1、介绍

​	在所有的工厂模式中，我们都强调一点：两个类A 和 B 之间的关系仅仅是A创建B 或是 A 使用B，而不能两种关系都有。将对象的创建和使用分离，也使得系统更加符合“单一职责原则”，有利于对功能的复用和系统的维护。

​	工厂类 的引入，将降低因为产品 或工厂类改变所造成的维护工作量。如果 抽象类的某个子类的构造函数发送改变或者需要添加或移除不同的子类，主要维护 工厂的代码，而不会影响 客户端。如果 抽象类的接口发生改变，例如添加或者移除方法，主需要在客户端修改代码即可，不会给 工厂带来任何影响。

​	此外，将对象的创建和使用分离还有一个好处：防止用来实例化一个类的数据和代码在多个类中到处都是，可以将有关创建的知识都搬到工厂类中。因为有时候我们创建一个对象不只是简单调用其构造函数，还需要设置一些参数，可能还需要配置环境，如果将这些代码散落在每一个创建对象的客户类中，势必会出现代码重复，创建蔓延的问题，而这些客户类其实无需承担对象的创建工作，他们只需使用已创建好的对象就可以了。此时可以引入工厂类来封装对象的创建逻辑和客户代码的实例化/ 配置选项。

#### 2、主要优点

- [ ]  工厂类包含必要的判断逻辑，可以决定在什么时候创建哪一个产品类的实例，客户端可以免除直接创建产品对象的职责，而仅仅 “消费”产品，简单工厂模式实现了对象创建和使用的分离。
- [ ]  客户端无须知道所创建的具体产品类的类名，只需要知道具体产品类对应的参数即可，对于一些复杂的类名，通过工厂模式可以在一定程度减少使用者的记忆量。
- [ ] 通过引入配置文件，可以在不修改任何客户端代码的情况下更换和增加新的具体产品类，在一定程度上提高了系统的灵活性。

#### 3、主要缺点

- [ ]  由于工厂类集中了所有产品的创建逻辑，职责过重，一旦不能正常工作，整个系统都要受到影响。
- [ ]   使用简单工厂模式势必会增加系统中类的个数（引入了新订单工厂类），增加了系统的复杂度和理解程度。
- [ ]  系统扩展困难，一旦添加新产品就不得不修改工厂逻辑，在产品类型较多时，有可能造成工厂逻辑过于复杂，不利于系统的扩展和维护。
- [ ]  简单工厂模式由于使用了静态工厂方法，造成工厂角色无法形成基于继承的等级结构。

#### 4、适用场景

- [ ]  工厂类负责创建的对象比较少，由于创建的对象较少，不会造成工厂方法中的业务逻辑太过复杂。
- [ ]  客户端只知道传入工厂类的参数，对于如何创建对象并不关心。

```java
/**
 * @author HouYC
 * @create 2020-11-07-13:26
 * 这是一个工厂，根据客户端传入不同的参数，创建不同的对象
 */
public class Factory {

    public static Product getProduct(String type) {
        Product product = null;
        if ("A".equals(type)) {
           product = new ConcreateProduct();
           // 实现自己的业务
        } else if ("B".equals(type)) {
            product = new ConcreateProductB();
            // 实现自己的业务
        }

        return product;
    }
}


/**
 * @author HouYC
 * @create 2020-11-07-13:27
 */
public abstract class Product {
    /**
     * 所有产品类的公共业务方法
     */
    public void methodSame() {

    }

    /**
     * 声明抽象类业务方法
     */
    public abstract void methodDiff();
}


/**
 * @author HouYC
 * @create 2020-11-07-13:29
 */
public class ConcreateProduct extends Product {

    @Override
    public void methodDiff() {
        System.out.println("ConcreateProduct 我实现了 product类   methodDiff（） 方法");
    }
}


/**
 * @author HouYC
 * @create 2020-11-07-13:30
 */
public class ConcreateProductB extends Product {
    @Override
    public void methodDiff() {

        System.out.println("ConcreateProductB 是实现了 Product  methodDiff（） 方法");
    }
}


/**
 * @author HouYC
 * @create 2020-11-07-13:34
 */
public class Client {

    public static void main(String[] args) {

        // 通过工厂模式创建对象
        Product product = Factory.getProduct("A");
        product.methodDiff();
        Product productB = Factory.getProduct("B");
        productB.methodDiff();
    }
}

```

### ② 工厂方法模式

#### 1、介绍

​	简单工厂虽然简单，但是存在一个很严重的问题。当系统中需要引入新产品时，由于静态工厂方法通过传入参数不同来创建不同的产品，这必定要修改工厂类的源代码，将违背 “开闭原则”。

​	**工厂方式模式，增加新产品 不影响已有代码。定义一个用于创建对象的接口，让子类决定将哪一个类实例化。工厂方法模式让一个类的实例化延迟到其子类。工厂方法模式又简称为工厂模式，又可称为虚拟构造器模式或多态工厂模式。 工厂方法模式是一种类创建型模式。**

![4.工厂方法模式](C:\Users\Administrator\Desktop\笔记\java设计模式\4.工厂方法模式.png)

在工厂方法模式结构图中包含如下几个角色：
​	● Product（抽象产品）：它是定义产品的接口，是工厂方法模式所创建对象的超类型。是产品对象的公共父类。
​	● ConcreteProduct（具体产品）：它实现了抽象产品接口，某种类型的具体产品由专门的具体工厂创建，具体工厂和具体产品之间一一对应。
​	● Factory（抽象工厂）：在抽象工厂类中，声明了工厂方法(Factory Method)，用于返回一个产品。抽象工厂是工厂方法模式的核心，所有创建对象的工厂类都必须实现该接口。
​	● ConcreteFactory（具体工厂）：它是抽象工厂类的子类，实现了抽象工厂中定义的工厂方法，并可由客户端调用，返回一个具体产品类的实例。

#### 2、主要优点

- [ ]  在工厂方法模式中，工厂方法用来创建客户所需要的产品，同时还向客户隐藏了哪种具体产品类将被实例化这一细节，用户只需要关心所需产品对应的工厂，无须关心创建细节，甚至无须知道具体产品类的类名。
- [ ]  基于工厂角色和产品角色的多态性设计时工厂方法模式的关键。 它能够让工厂可以自主确定创建何种产品对象，而如何创建这个对象的细节则完全封装在具体工厂内聚。工厂方法模式之所以又被称为多态工厂模式，就正是因为所有的具体工厂类都具有统一抽象父类。
- [ ]  使用工厂方式模式的另一有点事在系统中加入新产品时，无须修改抽象工厂和抽象产品提供的接口，无须修改客户端，也无须修改其他的具体工厂和具体产品，而只要添加一个具体工厂和具体产品就可以了，这样系统的可扩展也就变得非常好，完全符合 “开闭原则”。

#### 3、主要缺点

- [ ]  在添加新产品时，需要编写新的具体产品类，而且还有提供与之对应的具体工厂类，系统中类的个数将成对增加，在一定程度上增加了系统的复杂度，有更多的类需要编译和运行，会给系统带来一些额外的开销。
- [ ]  由于考虑到系统的可扩展型，需要引入抽象层，在客户端代码中均使用抽象层进行定义，增加了系统的抽象性和理解难度，且在实现时可能需要用到DOM，反射等技术，增加了系统的实现难度。

#### 4、适用场景

- [ ]  客户端不知道它所需要的对象的类。在工厂方法模式中，客户端不需要知道具体产品类的类名，只需要知道所对应的工厂即可，具体的产品对象由具体工厂类创建，可将具体工厂类的类名存储在配置文件或数据库中。
- [ ]  抽象工厂类通过其子类来指定创建哪个对象。在工厂方法模式中，对于抽象工厂类只需要提供一个创建产品的接口，而由其子类来确定具体要创建的对象，利用面向对象的多态性和里氏替换原则，在程序运行时，子类对象将覆盖父类对象，从而使得系统更容易扩展。

```java

/**
 * @author HouYC
 * @create 2020-11-07-14:45
 *
 *  这是图片工厂，提供抽象方法，让其子类具体实现，创建不同的工厂对象
 */
public interface PictureFactory {

    /**
     * 图片解析
     */
    Picture createPicture();
}

/**
 * @author HouYC
 * @create 2020-11-07-14:48
 * JPG 格式 工厂
 */
public class JPGPictureFactory implements PictureFactory {
    @Override
    public Picture createPicture() {
        return new JPGPicture();
    }
}


/**
 * @author HouYC
 * @create 2020-11-07-14:47
 * GIF 图片工厂
 */
public class GIFPictureFactory implements PictureFactory {
    @Override
    public Picture createPicture() {
        return new GIFPicture();
    }
}

/**
 * @author HouYC
 * @create 2020-11-07-14:50
 *
 *  图片解析 提供公共的方法和类型
 */
public interface Picture {

    /**
     * 读取图片
     */
    void readPicture();
}


/**
 * @author HouYC
 * @create 2020-11-07-14:52
 */
public class JPGPicture implements Picture {
    @Override
    public void readPicture() {
        // 实现自己的业务代码
        System.out.println("JPGPicture 读取");
    }
}

/**
 * @author HouYC
 * @create 2020-11-07-14:53
 */
public class GIFPicture implements Picture {
    @Override
    public void readPicture() {
        // 实现自己的业务代码
        System.out.println("GIF 读取图片信息");
    }
}

/**
 * @author HouYC
 * @create 2020-11-07-14:56
 */
public class Client {

    public static void main(String[] args) {
        // 在客戶端不用new，直接使用配置文件
        PictureFactory factory = (PictureFactory) XMLUtil.getBean();
        Picture picture = factory.createPicture();
        picture.readPicture();
    }
}

<?xml version="1.0"?>
<config>
    <className>JPGPictureFactory</className>
</config>
```

### ③ 抽象工厂模式

#### 1、简介

​	抽象工厂模式为创建一组对象提供了一种解决方案。与工厂方法模式相比，抽象工厂模式中的具体工厂不只是创建一种产品，它负责创建一族产品。

​	抽象工厂模式：提供一个创建一系列相关或相互依赖对象的接口，而无需指定它们具体的类。抽象工厂模式又称为 Kit 模式，它是一种对象创建型模式。

![5.抽象工厂模式1](C:\Users\Administrator\Desktop\笔记\java设计模式\5.抽象工厂模式1.png)

​	每一个具体工厂可以生产属于一个产品族的所有产品，例如生产颜色相同的正方形、圆形和椭圆形，所生产的产品又位于不同的产品等级结构中。如果使用工厂方法模式，图4所示结构需要提供15个具体工厂，而使用抽象工厂模式只需要提供5个具体工厂，极大减少了系统中类的个数。

![5.抽象工厂模式2](C:\Users\Administrator\Desktop\笔记\java设计模式\5.抽象工厂模式2.png)

​	抽象工厂模式无法解决该问题，这也是抽象工厂模式最大的缺点。在抽象工厂模式中，增加新的产品族很方便，但是增加新的产品等级结构很麻烦，抽象工厂模式的这种性质称为“开闭原则”的倾斜性。“开闭原则”要求系统对扩展开放，对修改封闭，通过扩展达到增强其功能的目的，对于涉及到多个产品族与多个产品等级结构的系统，其功能增强包括两方面：
​	(1) 增加产品族：对于增加新的产品族，抽象工厂模式很好地支持了“开闭原则”，只需要增加具体产品并对应增加一个新的具体工厂，对已有代码无须做任何修改。
​	(2) 增加新的产品等级结构：对于增加新的产品等级结构，需要修改所有的工厂角色，包括抽象工厂类，在所有的工厂类中都需要增加生产新产品的方法，违背了“开闭原则”。正因为抽象工厂模式存在“开闭原则”的倾斜性，它以一种倾斜的方式来满足“开闭原则”，为增加新产品族提供方便，但不能为增加新产品结构提供这样的方便，因此要求设计人员在设计之初就能够全面考虑，不会在设计完成之后向系统中增加新的产品等级结构，也不会删除已
有的产品等级结构，否则将会导致系统出现较大的修改，为后续维护工作带来诸多麻烦。

​	抽象工厂模式是工厂方法模式的进一步延伸，由于它提供了功能更为强大的工厂类并且具备较好的可扩展性，在软件开发中得以广泛应用，尤其是在一些框架和API类库的设计中，例如在Java语言的AWT（抽象窗口工具包）中就使用了抽象工厂模式，它使用抽象工厂模式来实现在不同的操作系统中应用程序呈现与所在操作系统一致的外观界面。抽象工厂模式也是在软件开发中最常用的设计模式之一。

#### 2、主要优点

(1) 抽象工厂模式隔离了具体类的生成，使得客户并不需要知道什么被创建。由于这种隔离，更换一个具体工厂就变得相对容易，所有的具体工厂都实现了抽象工厂中定义的那些公共接口，因此只需改变具体工厂的实例，就可以在某种程度上改变整个软件系统的行为。
(2) 当一个产品族中的多个对象被设计成一起工作时，它能够保证客户端始终只使用同一个产品族中的对象。
(3) 增加新的产品族很方便，无须修改已有系统，符合“开闭原则”。

#### 3、主要缺点

​	增加新的产品等级结构麻烦，需要对原有系统进行较大的修改，甚至需要修改抽象层代码，这显然会带来较大的不便，违背了“开闭原则”。

#### 4、适用场景

- [ ]  一个系统不应当依赖于产品类实例如何被创建、组合 和 表达的细节，这对于所有类型的工厂模式都是很重要的，用户无须关心对象的创建过程，将对象的创建和使用解耦。
- [ ]  系统中有多于一个的产品族，而每次只使用其中某一个产品族。可以通过配置文件等方式来使得用户可以动态改变产品族，也可以很方便地增加新的产品族。
- [ ]  属于同一个产品族的产品将在一起使用，这一约束必须在系统的设计中体现出来。同一个产品族中的产品可以是没有任何关系的对象，但是他们都具有一些共同的约束，如同一操作系统下的按钮和文本框，按钮与文本框之间没有直接关系，但他们都属于某一操作系统的，此时具有一个共同的约束条件：操作系统的类型。
- [ ]  产品等级结构稳定，设计完成之后，不会向系统中增加新的产品等级结构或者删除已有的产品等级结构。

```java
/**
 * @author HouYC
 * @create 2020-11-07-16:21
 * 创建工厂
 */
public interface SkinaFactory {

    Button createButton();

    TextField createTextField();
}


/**
 * @author HouYC
 * @create 2020-11-07-16:27
 */
public class SanXingSkinaFactory implements SkinaFactory {
    @Override
    public Button createButton() {
        return new SanXingButton();
    }

    @Override
    public TextField createTextField() {
        return new SanXingTextField();
    }
}


/**
 * @author HouYC
 * @create 2020-11-07-16:26
 */
public class SpringSkinaFactory implements SkinaFactory {
    @Override
    public Button createButton() {
        return new SpringButton();
    }

    @Override
    public TextField createTextField() {
        return new SpringTextField();
    }
}

/**
 * @author HouYC
 * @create 2020-11-07-16:22
 * 具体产品实现类  spring 风格的按钮
 */
public class SpringButton implements Button {
    @Override
    public void display() {
        System.out.println("Spring 风格的按钮");
    }
}


/**
 * @author HouYC
 * @create 2020-11-07-16:24
 *
 * 文字字体 产品接口
 */
public interface TextField {

    void display();
}

/**
 * @author HouYC
 * @create 2020-11-07-16:22
 * 具体产品实现类  spring 风格的按钮
 */
public class SpringButton implements Button {
    @Override
    public void display() {
        System.out.println("Spring 风格的按钮");
    }
}

/**
 * @author HouYC
 * @create 2020-11-07-16:23
 * 按钮具体实现类 三星风格
 */
public class SanXingButton implements Button {
    @Override
    public void display() {

        System.out.println("三星 风格的按钮");
    }
}

/**
 * @author HouYC
 * @create 2020-11-07-16:25
 */
public class SanXingTextField implements TextField {
    @Override
    public void display() {
        System.out.println("三星风格字体");
    }
}

/**
 * @author HouYC
 * @create 2020-11-07-16:24
 *
 * 字体具体实现类
 */
public class SpringTextField implements TextField {
    @Override
    public void display() {
        System.out.println("Spring 风格的字体");
    }
}

/**
 * @author HouYC
 * @create 2020-11-07-16:28
 */
public class Client {

    public static void main(String[] args) {
        // 可以使用配置文件创建
        SpringSkinaFactory factory = new SpringSkinaFactory();
        factory.createButton().display();
        factory.createTextField().display();
    }
}
```

### ④ 单例模式---确保对象的唯一性

### ⑤ 原型模式----对象的克隆

#### 1、简介

​	在使用原型模式时，我们需要首先创建一个原型对象，再通过复制这个原型对象来创建更多同类型的对象。 

​	原型模式：使用原型实例指定创建对象的种类，并且通过拷贝这些原型创建新的对象，原型模式是一种对象创建型模式。

​	原型模式的工作原理很简单：将一个原型对象传给那个要发动创建的对象，这个要发动创建的对象通过请求原型对象拷贝自己来实现创建过程。由于在软件系统中我们经常会遇到需要创建多个相同或者相似的对象的情况，因此原型模式在真实开发中的使用频率还是非常高的。

​	通过克隆方法所创建的对象是全新的对象，它们在内存中拥有新的地址，通常堆克隆所产生的对象进行修改堆原型对象不会造成任何影响，每一个克隆对象都是相互独立的。通过不同的方式修改可以得到一系列相似但不完全相同的对象。

1、java 语言提供的clone（）方法

​	学过Java语言的人都知道，所有的Java类都继承自java.lang.Object。事实上，Object类提供一个clone()方法，可以将一个Java对象复制一份。因此在Java中可以直接使用Object提供的clone()方法来实现对象的克隆，Java语言中的原型模式实现很简单。需要注意的是能够实现克隆的Java类必须实现一个标识接口Cloneable，表示这个Java类支持被复制。如果一个类没有实现这个接口但是调用了clone()方法，Java编译器将抛出一个CloneNotSupportedException异常。

一般而言，Java语言中的clone()方法满足：
(1) 对任何对象x，都有x.clone() != x，即克隆对象与原型对象不是同一个对象；
(2) 对任何对象x，都有x.clone().getClass() == x.getClass()，即克隆对象与原型对象的类型一样；
(3) 如果对象x的equals()方法定义恰当，那么x.clone().equals(x)应该成立。

为了获取对象的一份拷贝，我们可以直接利用Object类的clone()方法，具体步骤如下：
(1) 在派生类中覆盖基类的clone()方法，并声明为public；
(2) 在派生类的clone()方法中，调用super.clone()；
(3)派生类需实现Cloneable接口。此时，Object类相当于抽象原型类，所有实现了Cloneable接口的类相当于具体原型类。

#### 2、浅克隆

​	在浅克隆中，如果原型对象的成员变量是值类型，将复制一份给克隆对象；如果原型对象的成员变量是引用类型，则将引用对象的地址复制一份给克隆对象，也就是说原型对象和克隆对象的成员变量指向相同的内存地址。简单来说，**在浅克隆中，当对象被复制时只复制它本身和其中包含的值类型的成员变量，而引用类型的成员对象并没有复制。**

![6.浅克隆](C:\Users\Administrator\Desktop\笔记\java设计模式\6.浅克隆.png)

#### 3、深克隆

​	在深克隆中，无论原型对象的成员变量是值类型还是引用类型，都将复制一份给克隆对象，深克隆将原型对象的所有引用对象也复制一份给克隆对象。简单来说，在深克隆中，除了对象本身被复制外，对象所包含的所有成员变量也将复制。

![6.深克隆](C:\Users\Administrator\Desktop\笔记\java设计模式\6.深克隆.png)

​	在Java语言中，如果需要实现深克隆，可以通过序列化(Serialization)等方式来实现。序列化就是将对象写到流的过程，写到流中的对象是原有对象的一个拷贝，而原对象仍然存在于内存中。通过序列化实现的拷贝不仅可以复制对象本身，而且可以复制其引用的成员对象，因此通过序列化将对象写到一个流中，再从流里将其读出来，可以实现深克隆。需要注意的是能够实现序列化的对象其类必须实现Serializable接口，否则无法实现序列化操作。下面我们使用深克隆技术来实现工作周报和附件对象的复制，由于要将附件对象和工作周报对象都写入流
中，因此两个类均需要实现Serializable接口。

![6.举例说明](C:\Users\Administrator\Desktop\笔记\java设计模式\6.举例说明.png)

```java
//附件类
class Attachment implements Serializable{
  
	private String name; //附件名
	public void setName(String name){
		this.name = name;
	}
    public String getName()
    {
    	return this.name;
    }
    public void download()
    {
    	System.out.println("下载附件，文件名为" + name);
    }
 }
//工作周报类WeeklyLog不再使用Java自带的克隆机制，而是通过序列化来从头实现对象的深克
//隆，我们需要重新编写clone()方法，修改后的代码如下：
import java.io.*;
//工作周报类
class WeeklyLog implements Serializable
{
  private Attachment attachment;
  private String name;
  private String date;
   private String content;
  public void setAttachment(Attachment attachment) {
  	this.attachment = attachment;
  }
  public void setName(String name) {
  	this.name = name;
  }
  public void setDate(String date) {
  	this.date = date;
  }
  public void setContent(String content) {
 	 this.content = content;
  }
  public Attachment getAttachment(){
 	 return (this.attachment);
  }
  public String getName() {
  	return (this.name);
  }
  public String getDate() {
  	return (this.date);
  }
  public String getContent() {
  	return (this.content);
  }
  //使用序列化技术实现深克隆
  public WeeklyLog deepClone() throws IOException, ClassNotFoundException, OptionalDataException
  {
    //将对象写入流中
    ByteArrayOutputStream bao=new ByteArrayOutputStream();
    ObjectOutputStream oos=new ObjectOutputStream(bao);
    oos.writeObject(this);
    //将对象从流中取出
    ByteArrayInputStream bis=new ByteArrayInputStream(bao.toByteArray());
    ObjectInputStream ois=new ObjectInputStream(bis);
    return (WeeklyLog)ois.readObject();
    }
  }
  客户端代码如下所示：
class Client{
  public static void main(String args[]){
    WeeklyLog log_previous, log_new = null;
    log_previous = new WeeklyLog(); //创建原型对象
    Attachment attachment = new Attachment(); //创建附件对象
    log_previous.setAttachment(attachment); //将附件添加到周报中
    try
  	{
  		log_new = log_previous.deepClone(); //调用深克隆方法创建克隆对象
  	}catch(Exception e)
  	{
 		 System.err.println("克隆失败！");
 	 }
 	 //比较周报
 		 System.out.println("周报是否相同？ " + (log_previous == log_new));
  	//比较附件
 	 System.out.println("附件是否相同？ " + (log_previous.getAttachment() == 			log_new.getAttachment()));
  }
}
编译并运行程序，输出结果如下：
周报是否相同？ false
附件是否相同？ false
从输出结果可以看出，由于使用了深克隆技术，附件对象也得以复制，因此用“==”比较原型
对象的附件和克隆对象的附件时输出结果均为false。深克隆技术实现了原型对象和克隆对象的
完全独立，对任意克隆对象的修改都不会给其他对象产生影响，是一种更为理想的克隆实现
方式。

```

#### 4、主要优点

原型模式的主要优点如下：
(1) 当创建新的对象实例较为复杂时，使用原型模式可以简化对象的创建过程，通过复制一个
已有实例可以提高新实例的创建效率。
(2) 扩展性较好，由于在原型模式中提供了抽象原型类，在客户端可以针对抽象原型类进行编
程，而将具体原型类写在配置文件中，增加或减少产品类对原有系统都没有任何影响。
(3) 原型模式提供了简化的创建结构，工厂方法模式常常需要有一个与产品类等级结构相同的
工厂等级结构，而原型模式就不需要这样，原型模式中产品的复制是通过封装在原型类中的
克隆方法实现的，无须专门的工厂类来创建产品。
(4) 可以使用深克隆的方式保存对象的状态，使用原型模式将对象复制一份并将其状态保存起
来，以便在需要的时候使用（如恢复到某一历史状态），可辅助实现撤销操作。

#### 5、主要缺点

原型模式的主要缺点如下：
(1) 需要为每一个类配备一个克隆方法，而且该克隆方法位于一个类的内部，当对已有的类进
行改造时，需要修改源代码，违背了“开闭原则”。
(2) 在实现深克隆时需要编写较为复杂的代码，而且当对象之间存在多重的嵌套引用时，为了
实现深克隆，每一层对象对应的类都必须支持深克隆，实现起来可能会比较麻烦。
3.适用场景 在以下情况下可以考虑使用原型模式：
(1) 创建新对象成本较大（如初始化需要占用较长的时间，占用太多的CPU资源或网络资
源），新的对象可以通过原型模式对已有对象进行复制来获得，如果是相似对象，则可以对
其成员变量稍作修改。
(2) 如果系统要保存对象的状态，而对象的状态变化很小，或者对象本身占用内存较少时，可
以使用原型模式配合备忘录模式来实现。
(3) 需要避免使用分层次的工厂类来创建分层次的对象，并且类的实例对象只有一个或很少的
几个组合状态，通过复制原型对象得到新实例可能比使用构造函数创建一个新实例更加方
便

### ⑥、建造者模式---复杂对象的组装与创建

#### 1、简介

​	建造者模式（Builder Pattern）： 将一个复杂对象的构建与它的表示分离，使得同样的构建过程可以创建不同的表示。建造者模式是一种对象创建型模式。

![7.建造者模式](C:\Users\Administrator\Desktop\笔记\java设计模式\7.建造者模式.png)	

**在建造者模式结构图中包含如下几个角色：**

- [ ]  Builder（抽象建造者）：它为创建一个产品Product 对象的各个部件指定抽象接口，在该接口中一般声明两类方法，一类方法时buildPartX（），他们用于创建复杂对象的各个部件；另一类方法是getResult（），它们用于返回复杂对象。BUilder即可以是抽象类，也可以是接口。
- [ ] ConcureteBuilder（具体建造者）：它实现了Builder 接口，实现各个部件的具体构造和装配方法，定义并明确它所创建的复杂对象，也可以提供一个方法返回创建好的复杂产品对象。
- [ ] Product（产品角色）：它是被构建的复杂对象，包含多个组成部件，具体建造者创建该产品的内部表示并定义它的装配过程。
- [ ]  Director（指挥者）：指挥者又称为导演类，它负责安排复杂对象的建造次序，指挥者与抽象建造者之间存在关联关系，可以在其construct（）建造方法中调用建造者对象的部件构造与装配方法，完成复杂对象的建造。  客户端一般只需要与指挥者进行交互，在客户端确定具体建造者类的构造函数或者Setter 方法将该对象传入指挥者类中。   （指挥者Director 可以省略，需要在抽象建造者 创建一个静态方法，用于组件返回建造次序）。

#### 2、主要优点

- [ ]  在建造者模式中，客户端不必知道产品内部组成的细节，将产品本身与产品的创建过程解耦，使得相同的创建过程可以创建不同的产品对象。
- [ ]  每一个具体建造者都相对独立，而与其他的具体建造者无关，因此可以很方便地替换具体建造者或增加新的具体建造者，用户使用不同的具体建造者即可得到不同的产品对象。由于指挥者类针对抽象建造者编程，增加新的具体建造者无须修改原有类库的代码，系统扩展方便，符合 “开闭原则”。
- [ ]  可以更加精细地控制产品的创建过程。将复杂产品的创建步骤分解在不同的方法中，使得创建过程更加清晰，也更方便使用程序来控制创建过程。

#### 3、主要缺点

- [ ]  建造者模式所创建的产品一般具有较多的共同点，其组成部分相似，如果产品之间的差异性很大，例如 很多组成部分都不相同，不适合使用建造者模式，因此其使用范围受到一定的限制。
- [ ]  如果产品的内部变化复杂，可能会导致需要定义很多具体建造者类来实现这种变化，导致系统变得很庞大，增加系统的理解难度和运行成本。

#### 4、 适用场景

- [ ]  需要生成的产品对象有复杂的内部结构，这些产品对象通常包含多个成员属性。
- [ ]  需要生成的产品对象的属性相互依赖，需要指定其生成顺序。
- [ ]  对象的创建过程独立于创建该对象的类，在建造者模式中通过引入了指挥者类，将创建过程封装在指挥类中，而不再建造者类和客户类中。
- [ ]  隔离复杂对象的创建和使用，并使得相同的创建过程可以创建不同的产品。

```java
/**
 * @author HouYC
 * @create 2020-11-08-10:38
 *
 *  抽象建造者
 */
public interface ProductBuilder {

    Product product = new Product();

    void builderProduct1();
    void builderProduct2();
    void builderProduct3();
    void builderProduct4();

    Product getResult();


    /**
     * 如果舍弃指挥者，需要添加如下方法
     */
    static Product construcr(ProductBuilder productBuilder) {
        productBuilder.builderProduct1();
        productBuilder.builderProduct2();
        productBuilder.builderProduct3();
        productBuilder.builderProduct4();
        return productBuilder.getResult();
    }
}


/**
 * @author HouYC
 * @create 2020-11-08-10:41
 *
 * 具体构建者
 */
public class ConcreateProductBuilder implements  ProductBuilder {

    @Override
    public void builderProduct1() {
        // 实现自己的业务
        System.out.println("builderProduct1");
    }

    @Override
    public void builderProduct2() {
        // 实现自己的业务
        System.out.println("builderProduct2");
    }

    @Override
    public void builderProduct3() {
        // 实现自己的业务
        System.out.println("builderProduct3");
    }

    @Override
    public void builderProduct4() {
        // 实现自己的业务
        System.out.println("builderProduct4");
    }

    @Override
    public Product getResult() {
        return product;
    }
}


/**
 * @author HouYC
 * @create 2020-11-08-10:40
 *
 * 产品角色
 */
@Data
public class Product {

    private Integer age;

    private String name;
}


/**
 * @author HouYC
 * @create 2020-11-08-10:45
 * 指挥者，指挥创建过程，客户端只需要跟这个指挥者打交道就可以了。。。 可省略，可以在建造者抽象类中实现
 */
public class Director {

    private ProductBuilder builder;

    public Director(ProductBuilder builder) {
        this.builder = builder;
    }

    public void setBuilder(ProductBuilder builder) {
        this.builder = builder;
    }

    public Product construcr() {
        builder.builderProduct1();
        builder.builderProduct2();
        builder.builderProduct3();
        builder.builderProduct4();
        Product product = builder.getResult();
        return product;
    }
}

/**
 * @author HouYC
 * @create 2020-11-08-10:48
 */
public class Client {
    public static void main(String[] args) {
        Director director = new Director(new ConcreateProductBuilder());
        director.construcr();
    }
}

```

## 3、结构性模式

​	结构型模式关注如何将现有类或对象组织在一起形成更加强大的功能。

​	结构型模式可以描述两种不同的东西---类与类的实例（即对象）。**结构型模式可以分为 类结构型模式 和 对象结构型模式。**

| 模式名称                    | 定义                                       | 使用频率   |
| ----------------------- | ---------------------------------------- | ------ |
| 适配器模式（Adapter Pattern）  | 将一个类的接口转换成客户希望的另一个接口。适配器模式让那些接口不兼容的类可以一起工作。 | ****   |
| 桥接模式（Bridge Pattern）    | 将抽象部分与它的实现部分解耦，使得两者都能够独立变化               | ***    |
| 组合模式（Composite Pattern） | 组合多个对象形成树形结构以表示具有部分--整体关系的层次结构，组合模式让客户端可以统一对待单个对象和组合对象 | ****   |
| 装饰模式（Decorator Pattern） | 动态地给一个对象增加一些额外的职责。就扩展功能而言，装饰模式提供了一种比使用子类更加灵活的替代方案 | ***    |
| 外观模式（Facade Pattern）    | 为子系统中的一组接口提供一个统一的入口。外观模式定义了一个高层接口，这个接口使得这一子系统更加容易使用 | ****** |
| 享元模式（Flyweight Pattern） | 运用共享技术有效地支持大量细粒度对象的复用                    | *      |
| 代理模式（Proxy Pattern）     | 给某一个对象提供一个代理或占位符，并由代理对象来控制对原对象的访问        | *****  |

### ① 适配器模式----不兼容的协调

#### 1、介绍

​	**适配器模式可以将一个类的接口和另一个类的接口匹配起来，而无须修改原来的适配者接口和抽象目标类接口。**

​	适配器模式（Adapter Pattern）：将一个接口转换成客户希望的另一个接口，使接口不兼容的哪些类可以一起工作，其别名为包装器（Wrapper）。适配器模式即可以作为类结构模型，也可以作为对象结构型模式。

#### 2、对象适配器模式

![8.适配器模式](C:\Users\Administrator\Desktop\笔记\java设计模式\8.适配器模式.png)

**在对象适配器模式结构图中包含如下几个角色：**

1.  Target （目标抽象类）：目标抽象类定义客户所需接口，可以是一个抽象类或接口，也可以是具体类。
2.  Adapter（适配器类）： 适配器可以调用另一个接口，作为一个转换器，对Adaptee 和 Target 进行适配，适配器类是适配器模式的核心，在对象适配器中，它通过继承Target 并关联一个Adaptee 对象使二者产生联系。
3.  Adaptee （适配者类）：适配者即被适配的角色，它定义了一个已经存在的接口，这个接口需要适配，适配者类一般是一个具体类，包含了客户希望使用的业务方法，在某些情况下可能没有适配者类的源代码。

根据对象适配器模式结构图，在对象适配器中，客户端需要调用request()方法，而适配者类Adaptee没有该方法，但是它所提供的specificRequest()方法却是客户端所需要的。为了使客户端能够使用适配者类，需要提供一个包装类Adapter，即适配器类。这个包装类包装了一个适配者的实例，从而将客户端与适配者衔接起来，在适配器的request()方法中调用适配者的specificRequest()方法。因为适配器类与适配者类是关联关系（也可称之为委派关系），所以这种适配器模式称为对象适配器模式。

```java
class Adapter extends Target {
  private Adaptee adaptee; // 维持一个对适配者对象的引用
  
  public Adapter(Adaptee adaptee) {
    this.adaptee = adaptee;
  }
  public void request() {
    adaptee.specificRequest(); // 转发调用
  }
}
```

#### 3、类适配器

​	除了对象适配器模式外，适配器模式还有一种形式，那就是类适配器模式，类适配器模式和对象适配器模式最大的区别在于适配器和适配者之间的关系不同，对象适配器模式中适配器和适配者之间是关联关系，而类适配器模式中适配器和适配者是继承关系。

![8.适配器模式2](C:\Users\Administrator\Desktop\笔记\java设计模式\8.适配器模式2.png)

根据类适配器模式结构图，适配器实现了抽象目标类接口Target，并继承了适配者类，在适配器类的Request（）方法中调用所继承的适配者类的specificRequest（）方法，实现了适配。

```java
class Adapter extends Adaptee implements Target {
  public void request() {
    specificRequest();
  }
}
```

由于Java 、C# 等语言不支持多重类继承，因此类适配器的使用受到很多限制，例如如果目标抽象类Target 不是接口，而是一个类，就无法使用类适配器； 此外，如果适配者Adapter 为最终（final）类，也无法使用类适配器。在Java 等面向对象编程语言中，大部分情况下我们使用的是对象适配器，类适配器较少使用。

#### 4、双向适配器

​	在对象适配器的使用过程中，如果在适配器中同时包含对目标类和适配者类的引用，适配者可以通过它调用目标类中的方法，目标类也可以通过它调用适配者类中的方法，那么该适配器就是一个双向适配器。

![8.双向适配器](C:\Users\Administrator\Desktop\笔记\java设计模式\8.双向适配器.png)

```java
class Adapter implements Target, Adaptee {
  // 同时维持对抽象目标类和适配者的引用
  private Target target;
  private Adaptee adaptee;
  
  public Adapter(Target target) {
    this.target = target;
  }
  public void request() {
    adaptee.specificRequest();
  }
  public void specificRequest() {
    target.request();
  }
}
```

#### 5、缺省适配器

​	缺省适配器模式（Default Adapter Pattern）: 当不需要实现一个接口所提供的所有方法时，可先设计一个抽象类实现该接口，并为接口中每个方法提供一个默认实现（空方法），那么该抽象类的子类可以选择性地覆盖父类的某些方法来实现需求，它适用于不想使用一个接口中的所有方法的情况，又称为单接口适配器模式。

![8.缺省适配器](C:\Users\Administrator\Desktop\笔记\java设计模式\8.缺省适配器.png)

**在缺省适配器模式中，包含如下三个角色：**

1.  ServiceInterface（适配者接口）：它是一个接口，通常在该接口中声明了大量的方法。
2.  AbstractServiceClass（缺省适配器类）：它是缺省适配器模式的核心类，使用空方法的形式实现了在ServiceInterface 接口中声明的方法。通常将它定义为抽象类，因为对他进行实例化没有任何意义。
3.  ConcreteServiceClass（具体业务类）：它是缺省适配器类的子类，在没有引入适配器之前，它需要实现适配者接口，因此需要实现在适配者接口定义的所有方法，而对于一些无须使用的方法也不得不提供空实现。在有了缺省适配器之后，可以直接继承该适配器类，根据需要有选择性地覆盖在适配器类中定义的方法。

#### 6、主要优点

​	无论是对象适配器模式还是类适配器模式都具有如下优点：

- [ ]  将目标类和适配者类解耦，通过引入一个适配器类来重用现有的适配者类，无须修改原有接口。
- [ ]  增加了类的透明性和复用性，将具体的业务实现过程封装在适配者类中，对于客户端类而言是透明的，而且提高了适配者的复用性，同一个适配者类可以在多个不同的系统中复用。
- [ ]  灵活性和扩展性都非常好，通过使用配置文件，可以很方便地更换适配器，也可以在不修改原有代码的基础上增加新的适配器类，完全符合 “开闭原则”、
- [ ]  由于适配器类是适配者类的子类，因此可以在适配器类中置换一些适配者的方法，使得适配器的灵活性更强。
- [ ]  一个对象适配器可以把多个不同的适配者适配到同一个目标；
- [ ]  可以适配一个适配者的子类，由于适配器和适配者之间是关联关系，根据 “里氏替换原则”，适配者的子类也可通过该适配器进行适配。

#### 7、主要缺点

 	**类适配器模式的缺点如下：**

- [ ]  对于Java 、C# 不支持多重类继承的语言，一次最多只能适配一个适配者类，不能同时适配多个适配者。
- [ ]  适配者类不能为最终类，如在Java 中不能为final 类。
- [ ]  在 Java 语言中，**类适配器模式**中的目标抽象类只能为接口，不能为类，其使用有一定的局限性。

#### 8、适用场景

​	1、系统需要使用一些现有的类，而这些类的接口（如方法名）不符合系统的需要，甚至没有这些类的源代码、

​	2、想创建一个可以重复使用的类，用于与一些彼此之间没有太大关联的一些类，包括一些可能将来引进的类一起工作。

### ② 桥接模式----处理多维度变化

#### 1、简介

​	在正式介绍桥接模式之前，我先跟大家谈谈两种常见文具的区别，它们是毛笔和蜡笔。假如我们需要大中小3种型号的画笔，能够绘制12种不同的颜色，如果使用蜡笔，需要准备3×12 =36支，但如果使用毛笔的话，只需要提供3种型号的毛笔，外加12个颜料盒即可，涉及到的对象个数仅为 3 + 12 = 15，远小于36，却能实现与36支蜡笔同样的功能。如果增加一种新型号的画笔，并且也需要具有12种颜色，对应的蜡笔需增加12支，而毛笔只需增加一支。为什么会这样呢？通过分析我们可以得知：在蜡笔中，颜色和型号两个不同的变化维度（即两个不同的变化原因）融合在一起，无论是对颜色进行扩展还是对型号进行扩展都势必会影响另一个维度；但在毛笔中，颜色和型号实现了分离，增加新的颜色或者型号对另一方都没有任何影响。如果使用软件工程中的术语，我们可以认为在蜡笔中颜色和型号之间存在较强的耦合性，而毛笔很好地将二者解耦，使用起来非常灵活，扩展也更为方便。

​	桥接模式是一种很实用的结构型设计模式，如果软件系统中某个类存在两个独立变化的维度，通过该模式可以将两个维度分离出来，是两者可以独立扩展，让系统更加符合 “单一职责原则”。与多层继承方案不同，它将两个独立变化的继承等级结构，并且在抽象层建立一个抽象关联，该关联关系类似一条连接两个独立继承结构的桥，故名桥接模式。

​	桥接模式用一种巧妙的方式处理多层继承存在的问题，用抽象关联取代了传统的多层继承，将类之间的静态继承关系转化为动态的对象组合关系，使得系统更加灵活，并易于扩展，同时有效控制了系统中类的个数。

​	桥接模式：将抽象部分与它的实现部分分离，使它们都可以独立地变化，它是一种对象结构型模式，又称为 柄体模式或接口模式。

![9.桥接模式1](C:\Users\Administrator\Desktop\笔记\java设计模式\9.桥接模式1.png)

**在桥接模式结构图中包含如下几个角色：**

1.  Abstraction（抽象类）：用于定义抽象类的接口，它一般是抽象类而不是接口，其中定义了一个 Implementor（实现类接口）类型的对象并可以维护该对象，它与 Implementor 之间具有关联关系，它既可以包含抽象业务方法，也可以包含具体业务方法。
2.  RefinedAbstraction（扩充抽象类）： 扩充由Abstraction 定义的接口，通常情况下它不再是抽象类而是具体实现类，它实现了在 Abstraction 中声明的抽象业务方法，在 RefinedAbstraction 中可以调用 Implementor 中定义的业务方法。
3.  Implementor（实现类接口）：定义实现类的接口，这个接口不一定要与Abstraction 的接口完全一致，事实上 这两个接口可以完全不同，一般而言，Implementor 接口仅提供基本操作，而 Abstraction 定义的接口可能会做更多复杂的操作。 Implementor 接口堆这些基本操作进行了声明，而具体实现交给其子类。通过关联关系，在 Abstraction 中不仅拥有自己的方法，还可以调用到Implementor 中定义的方法，使用关联关系来替代继承关系。
4.  ConcreateImplementor (具体实现类)：具体实现 Implementor 接口，在不同的 ConcreateImplementor 中 提供基本操作的不同实现，在程序运行时，ConcreateImplementor 对象将替换其父类对象，提供给抽象类具体的业务操作方法。

桥接模式是一个非常有用的模式，在桥接模式中 体现了很多面向对象设计原则的思想，包括 “单一职责原则”、“开闭原则”、“合成复用原则”“、”里氏替换原则“、”依赖倒转原则“等。

​	在使用桥接模式时，我们首先应该识别出一个类所具有的两个独立变化的维度，将它们设计为两个独立的继承等级结构，为两个维度都提供抽象层，并建立抽象耦合。通常情况下，我们将具有两个独立变化维度的类的一些普通业务方法和与之关系最密切的维度设计为“抽象类”层次结构（抽象部分），而将另一个维度设计为“实现类”层次结构（实现部分）。例如：对于毛笔而言，由于型号是其固有的维度，因此可以设计一个抽象的毛笔类，在该类中声明并部分实现毛笔的业务方法，而将各种型号的毛笔作为其子类；颜色是毛笔的另一个维度，由于它与毛笔之间存在一种“设置”的关系，因此我们可以提供一个抽象的颜色接口，而将具体的颜色作为实现该接口的子类。此，型号可认为是毛笔的抽象部分，而颜色是毛笔的实现部分、

![9.桥接模式2](C:\Users\Administrator\Desktop\笔记\java设计模式\9.桥接模式2.png)

​	如果需要增加一种新型号的毛笔，只需扩展左侧的“抽象部分”，增加一个新的扩充抽象类；如果需要增加一种新的颜色，只需扩展右侧的“实现部分”，增加一个新的具体实现类。扩展非常方便，无须修改已有代码，且不会导致类的数目增长过快。在具体编码实现时，由于在桥接模式中存在两个独立变化的维度，为了使两者之间耦合度降
低，首先需要针对两个不同的维度提取抽象类和实现类接口，并建立一个抽象关联关系。对于“实现部分”维度，典型的实现类接口代码如下所示：

```java
interface Implementor {

public void operationImpl();

}

```

在实现Implementor接口的子类中实现了在该接口中声明的方法，用于定义与该维度相对应的一些具体方法。对于另一“抽象部分”维度而言，其典型的抽象类代码如下所示：

```java
abstract class Abstraction {

protected Implementor impl; //定义实现类接口对象

public void setImpl(Implementor impl) {

this.impl=impl;

}

public abstract void operation(); //声明抽象业务方法

}
```

​	在抽象类Abstraction中定义了一个实现类接口类型的成员对象impl，再通过注入的方式给该对象赋值，一般将该对象的可见性定义为protected，以便在其子类中访问Implementor的方法，其子类一般称为扩充抽象类或细化抽象类(RefinedAbstraction)，典型的RefinedAbstraction类代码如下所示：

```java
class RefinedAbstraction extends Abstraction {

public void operation() {

//业务代码

impl.operationImpl(); //调用实现类的方法

//业务代码

}
}
```

​	对于客户端而言，可以针对两个维度的抽象层编程，在程序运行时再动态确定两个维度的子
类，动态组合对象，将两个独立变化的维度完全解耦，以便能够灵活地扩充任一维度而对另
一维度不造成任何影响。

#### 2、案例展示

​	Sunny软件公司欲开发一个跨平台图像浏览系统，要求该系统能够显示BMP、JPG、GIF、PNG等多种格式的文件，并且能够在Windows、Linux、Unix等多个操作系统上运行。系统首先将各种格式的文件解析为像素矩阵(Matrix)，然后将像素矩阵显示在屏幕上，在不同的操作系统中可以调用不同的绘制函数来绘制像素矩阵。系统需具有较好的扩展性以支持新的文件格式和操作系统。

![9.桥接模式案例展示](C:\Users\Administrator\Desktop\笔记\java设计模式\9.桥接模式案例展示.png)

​	Image充当抽象类，其子类JPGImage、PNGImage、BMPImage和GIFImage充当扩充抽象类；ImageImp充当实现类接口，其子类WindowsImp、LinuxImp和UnixImp充当具体实现类。

```java

/**
 * @author HouYC
 * @create 2020-11-08-21:14
 * 像素实体类
 */
@Data
public class Matrix {
    private String name;
    private Integer size;
}

/**
 * @author HouYC
 * @create 2020-11-08-21:11
 *  桥接模式 抽象类，图片可以在各个系统兼容。 图片展示 可以分两个维度
 *  具体图片类型 PNG JPG 等等
 *  具体图片在操作系统展示  Windows Linux 等等
 *
 *  桥接模式可以分为 抽象（图片类型） 和 实现（操作系统）
 */
public abstract class Image {

    /**
     * 关联实现对象
     */
    ImageImpl image;
    void setImageImpl(ImageImpl imageImpl) {
        this.image = imageImpl;
    }

    /**
     * 设置抽象接口，让其具体类去实现。这个必须设置为抽象方法，让子类必须实现
     * @param fileName
     */
    abstract void parseFile(String fileName);
}


/**
 * @author HouYC
 * @create 2020-11-08-21:14
 *
 *  图片具体实现接口
 */
public interface ImageImpl {

    void doPaint(Matrix matrix);
}

/**
 * @author HouYC
 * @create 2020-11-08-21:16
 * Linux 具体实现
 */
public class LinuxImpl implements ImageImpl {
    @Override
    public void doPaint(Matrix matrix) {
        // 具体业务代码
        System.out.println("在 Linux 系统绘制图片显示：。。。。");
    }
}


/**
 * @author HouYC
 * @create 2020-11-08-21:18
 */
public class UnixImpl implements ImageImpl {
    @Override
    public void doPaint(Matrix matrix) {
        // 具体业务代码
        System.out.println("在 Unix 系统上的图片显示。。。。");
    }
}


/**
 * @author HouYC
 * @create 2020-11-08-21:17
 */
public class WindowsImpl implements ImageImpl {
    @Override
    public void doPaint(Matrix matrix) {
        // 具体业务代码
        System.out.println("在 Windows 系统上进行图片显示。。。。");
    }
}

/**
 * @author HouYC
 * @create 2020-11-08-21:26
 */
public class BMPImage extends Image {
    @Override
    void parseFile(String fileName) {
        // 模拟BMP图片解析，并获得一个图像对象
        Matrix matrix = new Matrix();
        super.image.doPaint(matrix);
        System.out.println(fileName + ", BMP 格式。");
    }
}


/**
 * @author HouYC
 * @create 2020-11-08-21:25
 */
public class JPGImage extends Image {
    @Override
    void parseFile(String fileName) {
        // 模拟解析JPG 图片，获得一个图像矩阵
        Matrix matrix  = new Matrix();
        super.image.doPaint(matrix);
        System.out.println(fileName + ", JPG 格式");
    }
}

/**
 * @author HouYC
 * @create 2020-11-08-21:23
 */
public class PNGImage extends Image {


    @Override
    void parseFile(String fileName) {
        // 模拟在PNG解析文件，获得一个像素矩阵对象
        Matrix matrix = new Matrix();
        super.image.doPaint(matrix);
        System.out.println(fileName + ", 格式为PNG");
    }
}

/**
 * @author HouYC
 * @create 2020-11-08-21:27
 */
public class Client {
    public static void main(String[] args) {

        JPGImage jpgImage = new JPGImage();
        jpgImage.setImageImpl(new WindowsImpl());
        jpgImage.parseFile("JPG");

    }
}
```

#### 3、桥接模式和适配器模式混合使用

​	在软件开发中，适配器模式通常可以与桥接模式联合使用。适配器模式可以解决两个已有接口间不兼容问题，在这种情况下被适配的类往往是一个黑盒子，有时候我们不想也不能改变这个被适配的类，也不能控制其扩展。适配器模式通常用于现有系统与第三方产品功能的集成，采用增加适配器的方式将第三方类集成到系统中。桥接模式则不同，用户可以通过接口继承或类继承的方式来对系统进行扩展。

​	桥接模式和适配器模式用于设计的不同阶段，桥接模式用于系统的初步设计，对于存在两个独立变化维度的类可以将其分为抽象化和实现化两个角色，使它们可以分别进行变化；而在初步设计完成之后，当发现系统与已有类无法协同工作时，可以采用适配器模式。但有时候在设计初期也需要考虑适配器模式，特别是那些涉及到大量第三方应用接口的情况。

**下面通过一个实例来说明适配器模式和桥接模式的联合使用：**
​	在某系统的报表处理模块中，需要将报表显示和数据采集分开，系统可以有多种报表显示方式也可以有多种数据采集方式，如可以从文本文件中读取数据，也可以从数据库中读取数据，还可以从Excel文件中获取数据。如果需要从Excel文件中获取数据，则需要调用与Excel相关的API，而这个API是现有系统所不具备的，该API由厂商提供。使用适配器模式和桥接模式设计该模块。
​	在设计过程中，由于存在报表显示和数据采集两个独立变化的维度，因此可以使用桥接模式进行初步设计；为了使用Excel相关的API来进行数据采集则需要使用适配器模式。系统的完整设计中需要将两个模式联用，

![9.桥接模式和适配器混合使用](C:\Users\Administrator\Desktop\笔记\java设计模式\9.桥接模式和适配器混合使用.png)

#### 4、主要优点

​	桥接模式是设计java虚拟机和实现JDBC 等驱动程序的核心模式之一，应用较为广泛，在软件开发中如果有一个类或一个系统有多个变化维度时，都可以尝试使用桥接模式读其进行设计。桥接模式为多维度变化的系统提供了一套完整的解决方案，并且降低了系统的复杂度。

- [ ]  分离抽象接口及其实现部分，桥接模式使用 “对象间的关联关系” 解耦了抽象和实现直接固有的绑定关系，使得抽象和实现可以沿着各自的维度来变化。所谓抽象和实现沿着各自维度的变化，也就是说抽象和实现不再同一个继承层次结构中，而是 “子类化”他们，使他们各自都具有自己的子类，以便任何组合子类，从而获得多维度组合对象。
- [ ]  在很多情况下，桥接模式可以取代多层继承方案，多层继承方案违背了“单一职责原则”，复用性较差，且类的个数非常多，桥接模式是比多层继承方案更好的解决方法，它极大减少了子类的个数。
- [ ]  桥接模式提高了系统的可扩展性，在两个变化维度中任意扩展一个维度，都不需要修改原有系统，符合“开闭原则”。

#### 4、主要缺点

-  桥接模式的使用会增加系统的理解与设计难度，由于关联关系建立在抽象层，要求开发者一开始就针对抽象层进行设计与编程。
-  桥接模式要求正确识别出系统中两个独立变化的维度，因此其使用范围具有一定的局限性，如何正确识别两个独立维度也需要一定的经验积累。

#### 5、适用场景

-  如果一个系统需要在抽象化和具体化之间增加更多的灵活性，避免在两个层次之间建立静态的继承关系，通过桥接模式可以使他们在抽象层建立一个关联关系。


-  “抽象部分” 和 “实现部分” 可以以继承的方式独立扩展而互不影响，在程序运行时可以动态将一个抽象化子类的对象和一个实现化子类的对象进行组合，即系统需要堆抽象化角色和实现化角色进行动态解耦。
-  一个类存在两个（或多个）独立变化的维度，且这两个（或多个）维度都需要独立进行扩展。
-  对于那些不希望使用继承或因为多层继承导致系统类的个数急剧增加的系统，桥接模式尤为适用。 

### ③ 组合模式----树形结构处理

#### 1、简介

​	对于树形结构，当容器对象（如文件夹）的某一个方法被调用时，将遍历整个树形结构，寻找也包含这个方法的成员对象（可以是容器对象，也可以是叶子对象）并调用执行，牵一而动白，其中使用了递归调用的机制来对整个结构进行处理。由于容器对象和叶子对象在功能上的区别，在使用这些对象的代码中必须有区别地对待容器对象和叶子对象。

​	**组合模式：组合多个对象形成树形结构以表示具体 “整体--部分”关系的层次结构。组合模式对单个对象 （即叶子对象）和组合对象（即容器对象）的使用具有一致性，组合模式又可以称为 “整体--部分”模式，它是一种对象结构型模式。**

​	在组合模式中引入了抽象构建类 Component，它是所有容器类和叶子类的公共父类，客户端针对Component 进行编程。 组合模式结构如图：

![10.组合模式1](C:\Users\Administrator\Desktop\笔记\java设计模式\10.组合模式1.png)

**在组合模式结构图中包含了如下几个角色：**

1.  **Component（抽象构建）：它可以是接口或抽象类，为叶子构建和容器构建对象声明接口，在该角色中可以包含所有子类共有行为的声明和实现。在抽象构建中定义了访问及管理它的子构建的方法，如增加子构建，删除子构建，获取子构建等。**
2.  **Leaf（叶子构建）：它在组合结构中表示叶子节点对象，叶子节点没有子节点，它实现了在抽象构建中定义的行为。对于那些访问及管理子构建的方法，可以通过异常等方式进行处理。**
3.  **Composite（容器构件）：它在组合结构中表示容器节点对象，容器节点包含子节点，其子节点可以是叶子节点，也可以是容器节点，它提供一个集合用于存储子节点，实现了在抽象构件中定义的行为，包括哪些访问及管理子构件的方法，在其业务方法中可以递归调用其子节点的业务方法。**

组合模式的关键是定义了一个抽象构件类，它既可以代表叶子，又可以代表容器，而客户端针对该抽象构件类进行编程，无须知道它到底表示的是叶子还是容器，可以对其进行统一处理。同时容器对象与抽象构件类之间还建立一个聚合关联关系，在容器对象中既可以包含叶子，也可以包含容器，以此实现递归组合，形成一个树形结构。

```java

/**
 * @author HouYC
 * @create 2020-11-09-22:34
 */
public interface AbstractFile {

    /**
     * 增加成员
     * @param abstractFile
     */
    void add(AbstractFile abstractFile);

    /**
     * 删除成员
     * @param abstractFile
     */
    void remove(AbstractFile abstractFile);

    /**
     * 获取成员
     * @param i
     * @return
     */
    AbstractFile getChild(int i);

    /**
     * 业务代码
     */
    void operation();
}

```

​	 一般将抽象构件类设计为接口或抽象类，将所有子类共有方法的声明和实现放在抽象构件类中。对于客户端而言，将针对抽象构件编程，而无须关心其具体子类是容器构件还是叶子构件。

```java

/**
 * @author HouYC
 * @create 2020-11-09-22:36
 */
public class Leaf implements AbstractFile {
    @Override
    public void add(AbstractFile abstractFile) {
        // 叶子节点不能使用该方法，因为叶子节点下面没有可获取的节点
    }

    @Override
    public void remove(AbstractFile abstractFile) {
        // 叶子节点不能使用该方法，因为叶子节点下面没有可获取的节点
    }

    @Override
    public AbstractFile getChild(int i) {
        // 叶子节点不能使用该方法，因为叶子节点下面没有可获取的节点
        return null;
    }

    @Override
    public void operation() {
        // 业务代码
    }
}

```

​	作为抽象构件类的子类，在叶子构件中需要实现在抽象构件类中声明的所有方法，包括业务方法以及管理和访问子构件的方法，但是叶子构件不能再包含子构件，因此在叶子构件中实现子构件管理和访问方法时需要提供异常处理或错误提示。当然，这无疑会给叶子构件的实现带来麻烦。

```java

/**
 * @author HouYC
 * @create 2020-11-09-22:37
 */
public class Composite implements AbstractFile {

    /**
     * 管理对象类
     */
    private List<AbstractFile> fileList = new ArrayList<>();
    @Override
    public void add(AbstractFile abstractFile) {
        fileList.add(abstractFile);
    }

    @Override
    public void remove(AbstractFile abstractFile) {
        fileList.remove(abstractFile);
    }

    @Override
    public AbstractFile getChild(int i) {
        return fileList.get(i);
    }

    @Override
    public void operation() {
        // 递归调用业务方法
        for (AbstractFile abstractFile : fileList) {
            abstractFile.operation();
        }
    }
}

```

​	在容器构件中实现了在抽象构件中声明的所有方法，既包括业务方法，也包括用于访问和管理成员子构件的方法，如add()、remove()和getChild()等方法。需要注意的是在实现具体业务方法时，由于容器构件充当的是容器角色，包含成员构件，因此它将调用其成员构件的业务方法。在组合模式结构中，由于容器构件中仍然可以包含容器构件，因此在对容器构件进行处理时需要使用递归算法，即在容器构件的operation()方法中递归调用其成员构件的operation()方法。

#### 2、案例分析

​	Sunny软件公司欲开发一个杀毒(AntiVirus)软件，该软件既可以对某个文件夹(Folder)杀毒，也可以对某个指定的文件(File)进行杀毒。该杀毒软件还可以根据各类文件的特点，为不同类型的文件提供不同的杀毒方式，例如图像文件(ImageFile)和文本文件(TextFile)的杀毒方式就有所差异。现需要提供该杀毒软件的整体框架设计方案。

![10.组合模式-案例分析1](C:\Users\Administrator\Desktop\笔记\java设计模式\10.组合模式-案例分析1.png)

![10.组合模式-案例分析2](C:\Users\Administrator\Desktop\笔记\java设计模式\10.组合模式-案例分析2.png)

```java

/**
 * @author HouYC
 * @create 2020-11-09-21:36\
 * 这是一个抽象接口类，用于定义叶子 和容器的所有行为方法，客户端 和 叶子 、容器，只要面向这个接口编程即可
 */
public interface AbstractFile {

    void add(AbstractFile abstractFile);

    void remove(AbstractFile abstractFile);

    AbstractFile getChild(int i);

    void killVirus();
}


/**
 * @author HouYC
 * @create 2020-11-09-21:38
 *
 * 叶子构件类
 */
public class ImageFile implements AbstractFile {

    private String name;

    public ImageFile(String name) {
        this.name = name;
    }
    @Override
    public void add(AbstractFile abstractFile) {
        // 叶子构件类不能再实现添加操作，只能实现自己的业务代码
    }

    @Override
    public void remove(AbstractFile abstractFile) {
        // 叶子构件类不能再实现添加操作，只能实现自己的业务代码
    }

    @Override
    public AbstractFile getChild(int i) {
        // 叶子构件类不能再实现添加操作，只能实现自己的业务代码
        return null;
    }


    @Override
    public void killVirus() {
        System.out.println("---对图片文件" + name + "进行杀毒");
    }
}

/**
 * @author HouYC
 * @create 2020-11-09-21:40
 * 叶子构件类
 */
public class TxtFile implements AbstractFile {

    private String name;

    public TxtFile(String name) {
        this.name = name;
    }

    @Override
    public void add(AbstractFile abstractFile) {
        // 叶子构件类不能再实现添加操作，只能实现自己的业务代码
    }

    @Override
    public void remove(AbstractFile abstractFile) {
        // 叶子构件类不能再实现添加操作，只能实现自己的业务代码
    }

    @Override
    public AbstractFile getChild(int i) {
        // 叶子构件类不能再实现添加操作，只能实现自己的业务代码
        return null;
    }

    @Override
    public void killVirus() {
        System.out.println("---对文本文件" + name + "进行杀毒");
    }
}

/**
 * @author HouYC
 * @create 2020-11-09-21:40
 * 叶子构件类
 */
public class VideoFile implements AbstractFile {
    private String name;

    public VideoFile(String name) {
        this.name = name;
    }

    @Override
    public void add(AbstractFile abstractFile) {
        // 叶子构件类不能再实现添加操作，只能实现自己的业务代码
    }

    @Override
    public void remove(AbstractFile abstractFile) {
        // 叶子构件类不能再实现添加操作，只能实现自己的业务代码
    }

    @Override
    public AbstractFile getChild(int i) {
        // 叶子构件类不能再实现添加操作，只能实现自己的业务代码
        return null;
    }

    @Override
    public void killVirus() {
        System.out.println("---对视频文件" + name + "进行杀毒");
    }
}

/**
 * @author HouYC
 * @create 2020-11-09-21:44
 * 容器构件类
 */
public class Folder implements AbstractFile {

    /**
     * 定义集合，用于存放AbstractFile 对象
     */
    private List<AbstractFile> fileList = new ArrayList<>();
    private String name;

    public Folder(String name) {
        this.name = name;
    }
    @Override
    public void add(AbstractFile abstractFile) {
        fileList.add(abstractFile);
    }

    @Override
    public void remove(AbstractFile abstractFile) {
        fileList.remove(abstractFile);
    }

    @Override
    public AbstractFile getChild(int i) {
        return fileList.get(i);
    }

    @Override
    public void killVirus() {
        System.out.println("*********** 对文件夹" + name + " 进行杀毒");
        for (AbstractFile abstractFile : fileList) {
            abstractFile.killVirus();
        }
    }
}

/**
 * @author HouYC
 * @create 2020-11-09-21:47
 */
public class Client {
    public static void main(String[] args) {
        AbstractFile folder1 = new Folder("Sunny 的资料");
        AbstractFile folder2 = new Folder("图形文件夹");
        AbstractFile folder3 = new Folder("文本文件");
        AbstractFile folder4 = new Folder("视频文件");

        AbstractFile file1 = new ImageFile("小亚超.jpg");
        AbstractFile file2 = new ImageFile("侯亚超.jpg");
        AbstractFile file3 = new TxtFile("九阳真经.txt");
        AbstractFile file4 = new TxtFile("葵花宝典.txt");
        AbstractFile file5 = new VideoFile("哪吒.mp4");
        folder2.add(file1);
        folder2.add(file2);

        folder3.add(file3);
        folder3.add(file4);

        folder4.add(file5);
        folder1.add(folder2);
        folder1.add(folder3);
        folder1.add(folder4);

        // 开始杀毒
        folder1.killVirus();

    }
}

```

#### 3、透明组合模式 与 安全组合模式

​	Sunny公司设计的杀毒软件具有良好的可扩展性，在增加新的文件类型时，无须修改现有类库代码，只需增加一个新的文件类作为AbstractFile类的子类即可，但是由于在AbstractFile中声明了大量用于管理和访问成员构件的方法，例如add()、remove()等方法，我们不得不在新增的文件类中实现这些方法，提供对应的错误提示和异常处理。为了简化代码，我们有以下两个解决方案：

**解决方案一：**将叶子构件的add()、remove()等方法的实现代码移至AbstractFile类中，由AbstractFile提供统一的默认实现，代码如下所示：

```java
//提供默认实现的抽象构件类
abstract class AbstractFile {
	public void add(AbstractFile file) {
		System.out.println("对不起，不支持该方法！");
	}
	public void remove(AbstractFile file) {
		System.out.println("对不起，不支持该方法！");
	}
	public AbstractFile getChild(int i) {
		System.out.println("对不起，不支持该方法！");
		return null;
	}
	public abstract void killVirus();
}
```

​	如果客户端代码针对抽象类AbstractFile编程，在调用文件对象的这些方法时将出现错误提示。如果不希望出现任何错误提示，我们可以在客户端定义文件对象时不使用抽象层，而直接使用具体叶子构件本身，客户端代码片段如下所示：

```java
class Client {
public static void main(String args[]) {
	//不能透明处理叶子构件
	ImageFile file1,file2;
	TextFile file3,file4;
	VideoFile file5;
	AbstractFile folder1,folder2,folder3,folder4;
//其他代码省略
}
}
```

**解决方案二：**除此之外，还有一种解决方法是在抽象构件AbstractFile中不声明任何用于访问和管理成员构件的方法，代码如下所示：

```java
abstract class AbstractFile {
	public abstract void killVirus();
}
```

​	此时，由于在AbstractFile中没有声明add()、remove()等访问和管理成员的方法，其叶子构件子类无须提供实现；而且无论客户端如何定义叶子构件对象都无法调用到这些方法，不需要做任何错误和异常处理，容器构件再根据需要增加访问和管理成员的方法，但这时候也存在一个问题：客户端不得不使用容器类本身来声明容器构件对象，否则无法访问其中新增的add()、remove()等方法，如果客户端一致性地对待叶子和容器，将会导致容器构件的新增对客户端不可见，客户端代码对于容器构件无法再使用抽象构件来定义，客户端代码片段如下所示：

```java
class Client {
public static void main(String args[]) {
	AbstractFile file1,file2,file3,file4,file5;
	Folder folder1,folder2,folder3,folder4; //不能透明处理容器构件
//其他代码省略
}
}
```

在使用组合模式时，根据抽象构件类的定义形式，我们可将组合模式分为透明组合模式和安全组合模式两种形式：
**(1) 透明组合模式**

​	透明组合模式中，抽象构件Component中声明了所有用于管理成员对象的方法，包括add()、remove()以及getChild()等方法，这样做的好处是确保所有的构件类都有相同的接口。在客户端看来，叶子对象与容器对象所提供的方法是一致的，客户端可以相同地对待所有的对象。透明组合模式也是组合模式的标准形式，虽然上面的解决方案一在客户端可以有不透明的实现方法，但是由于在抽象构件中包含add()、remove()等方法，因此它还是透明组合模式，透明组合模式的完整结构。

![10.组合-透明模式](C:\Users\Administrator\Desktop\笔记\java设计模式\10.组合-透明模式.png)

​	透明组合模式的缺点是不够安全，因为叶子对象和容器对象在本质上是有区别的。叶子对象不可能有下一个层次的对象，即不可能包含成员对象，因此为其提供add()、remove()以及getChild()等方法是没有意义的，这在编译阶段不会出错，但在运行阶段如果调用这些方法可能会出错（如果没有提供相应的错误处理代码）。

**(2) 安全组合模式**
​	安全组合模式中，在抽象构件Component中没有声明任何用于管理成员对象的方法，而是在Composite类中声明并实现这些方法。这种做法是安全的，因为根本不向叶子对象提供这些管理成员对象的方法，对于叶子对象，客户端不可能调用到这些方法，这就是解决方案二所采用的实现方式。安全组合模式的结构

![10.组合--安全](C:\Users\Administrator\Desktop\笔记\java设计模式\10.组合--安全.png)

​	安全组合模式的缺点是不够透明，因为叶子构件和容器构件具有不同的方法，且容器构件中那些用于管理成员对象的方法没有在抽象构件类中定义，因此客户端不能完全针对抽象编程，必须有区别地对待叶子构件和容器构件。在实际应用中，安全组合模式的使用频率也非常高，在Java AWT中使用的组合模式就是安全组合模式。

#### 4、主要优点

​	组合模式使用面向对象的思想来实现树形结构的构建与处理，描述了如何将容器对象和叶子对象进行递归组合，实现简单，灵活性好。

-  组合模式可以清除地定义分层次的复杂对象，表示对象的全部或部分层次，它让客户端忽略了层次的差异，方便对整个层次结构进行控制。
-  客户端可以一致地使用一个组合结构或其中单个对象，不必关系处理的单个对象还是整个组合结构，简化了客户端代码。
-  在组合模式中 增加新的容器构件和叶子构件都很方便，无须对现有类库进行任何修改，符合 “开闭原则”。
-  组合模式为树形结构的面向对象实现提供了一种灵活的解决方案，通过叶子对象和容器对象的递归组合，可以形成复杂 的 树形结构，但对树形结构的控制却非常简单。

#### 5、主要缺点

​	在增加新构件时很难对容器中的构建类型进行限制。有时候我们希望一个容器中只能有某些特定类型的对象，例如在某个文件夹中只能包含文本文件，使用组合模式时，不能依赖类型系统来施加这些约束，因为他们都来自于相同的抽象层，在这种情况下，必须通过在运行时进行类型检查来实现，这个实现过程较为复杂。

#### 6、适用场景

-  在具体整体和部分的层次结构中，希望通过一种方式忽略整体与部分的差异，客户端可以一致地对待他们。
-  在一个使用面向对象语言开发的系统中需要处理一个树形结构。
-  在一个系统中能够分离出叶子对象和容器对象，而且他们的类型不固定，需要增加一些新的类型。

### ④ 装饰者模式---扩展系统功能

#### 1、简介

​	装饰模式可以在不改变一个对象本身功能的基础上给对象增加额外的新行为。

​	装饰模式是一种用于替代继承的技术，它通过一种无须定义子类的方式来给对象动态增加职责，使用对象之间的关联关系取代之间的继承关系。在装饰模式中引入了装饰类，在装饰类中既可以调用待装饰的原有类 的方法，还可以增加新的方法，以扩充原有类的功能。

​	装饰模式（Decorator Pattern）：动态地给一个对象增加一些额外的职责，就增加对象功能来说，装饰模式比生成子类实现更为灵活。装饰模式是一种对象结构型模式。

​	在装饰模式中，为了让系统具有更好的灵活性和可扩展性，我们通常会定义一个抽象装饰类，而将具体的装饰类作为它的子类。

![11. 装饰模式1](C:\Users\Administrator\Desktop\笔记\java设计模式\11. 装饰模式1.png)

**在装饰模式结构图中包含以下几个角色：**

1.  Component（抽象构建）： 它是具体构建和抽象装饰类的共同父类，声明了在具体构建中实现的业务方法，它的引入可以使客户端以一致的方式处理未被装饰的对象以及装饰之后的对象，实现客户端的透明操作。
2.  ConcreteComponent（具体构件）： 它是抽象构建类的子类，用于定义具体的构建对象，实现了在抽象构件中声明的方法，装饰器可以给它增加额外的职责（方法）。
3.  Decorator（抽象装饰类）： 它也是抽象构件类的子类，用于给具体构件增加职责，但是具体职责在其子类中实现。它维护一个指向抽象构件对象的引用，通过该引用可以调用装饰之前构件对象的方法，并通过其子类扩展该方法，以达到装饰的目的。
4.  ConcreateDecorator（具体装饰类）：它是抽象装饰类的子类，负责向构件添加新的职责。每一个具体装饰类都定义了一些新的行为，它可以调用在抽象装饰类中定义的方法，并可以增加新的方法用以扩充对象的行为。

​	由于具体构件类和装饰类都实现了相同的抽象构件接口，因此装饰模式以对客户透明的方式动态地给一个对象附加上更多的职责，换言之，客户端并不会觉得对象在装饰前和装饰后有什么不同，装饰模式可以在不需要创造更多子类的情况下，将对象的功能加以扩展。

​	装饰模式的核心在于抽象装饰类的设计，其典型代码如下图所示：

```java
class Decorator implements Component
{
	private Component component; //维持一个对抽象构件对象的引用
	public Decorator(Component component) //注入一个抽象构件类型的对象
	{
		this.component=component;
	}
	public void operation()
	{
		component.operation(); //调用原有业务方法
	}
}
```

​	在抽象装饰类Decorator中定义了一个Component类型的对象component，维持一个对抽象构件对象的引用，并可以通过构造方法或Setter方法将一个Component类型的对象注入进来，同时由于Decorator类实现了抽象构件Component接口，因此需要实现在其中声明的业务方法operation()，需要注意的是在Decorator中并未真正实现operation()方法，而只是调用原有component对象的operation()方法，它没有真正实施装饰，而是提供一个统一的接口，将具体装饰过程交给子类完成。
在Decorator的子类即具体装饰类中将继承operation()方法并根据需要进行扩展，典型的具体装饰类代码如下：

```java
class ConcreteDecorator extends Decorator
{
	public ConcreteDecorator(Component component)
	{
		super(component);
	}
 	public void operation()
	{
      super.operation(); //调用原有业务方法
      addedBehavior(); //调用新增业务方法
	}
	//新增业务方法
	public void addedBehavior()
	{
		……
	}
}
```

#### 2、透明模式

​	在具体装饰类通过新增成员变量或成员方法来扩充具体构建类的功能，在标准的装饰模式中，新增行为需在原有业务方法中调用，无论是具体构建对象还是装饰过的构建对象，对于客户端而言都是透明的，这种装饰模式称为  透明模式，但在某些情况下，有些新增行为需要单独被调用，此时客户端不能再一致性地处理装饰之前的对象和装饰之后的对象，这种模式被称为 半透明装饰模式。。

​	在透明装饰模式中要求客户端完全针对抽象编程，装饰模式的透明性要求客户端程序不应该将对象声明为具体构建类型或具体装饰类型，而应该全部声明为抽象构建类型。对于客户端而言，具体构建对象和具体装饰对象没有任何区别。

#### 3、半透明模式

​	半透明模式可以给系统带来更多的灵活性，设计相对简单，使用起来也非常方便。为了能够调用到新增方法，不得不具体装饰类型来定义装饰之后的对象，而具体构建类型仍然可以使用抽象构建类型来定义，这种装饰模式即为半透明装饰模式。

#### 4、优点

​	对应扩展一个对象的功能，，装饰模式比继承更加灵活，不会导致类的个数急剧增加。

​	可以通过一个动态的方式来扩展一个对象的功能，通过配置文件可以在运行时选择不同的具体装饰类，从而实现不同的行为。

​	可以对一个对象进行多次装饰，通过使用不同的具体装饰类以及这些装饰类的排列组合可以创造出很多不同行为的组合，得到功能更加强大的对象。

​	具体构件类与具体装饰类可以独立变化，用户可以根据需要增加新的具体构建类和具体装饰类，原有类库代码无须改变，符合开闭原则。

#### 5、缺点

​	在使用装饰模式进行系统设计时将产生很多小对象，这些对象的区别在于他们之间相互连接的方式有所不同，而不是他们的类或者属性值有所不同，大量小对象的产生势必会占用更多的系统资源，在一定程度上影响程序的性能。

​	装饰模式提供一种比继承更加灵活，机动的解决方案，但同时也意味着比继承更加易于出错，排除也更困难，对于多次装饰的对象，在调试时寻找错误可能需要逐级排查，较为繁琐。

#### 6、 适用环境

​	在不影响其他对象的情况下以动态，透明的方式给单个对象添加职责。

​	当不能采用继承的方式对系统进行扩展或者采用继承不利于系统扩展和维护时，可以使用装饰模式。

### ⑤ 外观模式

#### 1、简介

​	外观模式是一种使用频繁非常高的结构型设计模式，它通过引入一个外观角色来简化客户端与系统之间的交互，为复杂的子系统调用提供一个统一的入口，使子系统与客户端的耦合度降低，且客户端调用非常方便。

​	在外观模式中，一个子系统的外部与其内部的通信通过一个统一的外观类进行，外观类将客户类与子系统的内部复杂性分割开，使得客户类只需要与外观角色打交道，而不需要与子系统内部的很多对象打交道。

​	**外观模式：为子系统中的一组接口提供一个统一的入口，外观模式定义了一个高层接口，这个接口使得这一子系统更加容易使用。**

​	**外观模式又称为门面模式，它是一种对象结构型模式。外观模式是迪米特法则的一种具体实现，通过引入一个新的外观对象可以降低原有系统的复杂度，同时降低客户类与子系统的耦合度。**

#### 2、举例说明

​	不知道大家有没有比较过自己泡茶和去茶馆喝茶的区别，如果是自己泡茶需要自行准备茶叶、茶具和开水，如图1(A)所示，而去茶馆喝茶，最简单的方式就是跟茶馆服务员说想要一杯什么样的茶，是铁观音、碧螺春还是西湖龙井？正因为茶馆有服务员，顾客无须直接和茶叶、茶具、开水等交互，整个泡茶过程由服务员来完成，顾客只需与服务员交互即可，整个过程非常简单省事。

![12.外观模式1](C:\Users\Administrator\Desktop\笔记\java设计模式\12.外观模式1.png)

​	在软件开发中，有时候为了完成一项较为复杂的功能，一个客户类需要和多个业务类交互，而这些需要交互的业务类经常会作为一个整体出现，由于涉及到的类比较多，导致使用时代码较为复杂，此时，特别需要一个类似服务员一样的角色，由它来负责和多个业务类进行交互，而客户类只需与该类交互。外观模式通过引入一个新的外观类(Facade)来实现该功能，外观类充当了软件系统中的“服务员”，它为多个业务类的调用提供了一个统一的入口，简化了类与类之间的交互。在外观模式中，那些需要交互的业务类被称为子系统(Subsystem)。如果没有外观类，那么每个客户类需要和多个子系统之间进行复杂的交互，系统的耦合度将很大，如图2(A)所示；而引入外观类之后，客户类只需要直接与外观类交互，客户类与子系统之间原有的复杂引用关系由外观类来实现，从而降低了系统的耦合度，如图2(B)所示。

![12.外观模式2](C:\Users\Administrator\Desktop\笔记\java设计模式\12.外观模式2.png)

![12.外观模式3](C:\Users\Administrator\Desktop\笔记\java设计模式\12.外观模式3.png)

**由图3可知，外观模式包含如下两个角色：**
**(1) Facade（外观角色）**：在客户端可以调用它的方法，在外观角色中可以知道相关的（一个或者多个）子系统的功能和责任；在正常情况下，它将所有从客户端发来的请求委派到相应的子系统去，传递给相应的子系统对象处理。

**(2) SubSystem（子系统角色）**：在软件系统中可以有一个或者多个子系统角色，每一个子系统可以不是一个单独的类，而是一个类的集合，它实现子系统的功能；每一个子系统都可以被客户端直接调用，或者被外观角色调用，它处理由外观类传过来的请求；子系统并不知道外观的存在，对于子系统而言，外观角色仅仅是另外一个客户端而已。

​	外观模式的主要目的在于降低系统的复杂程度，在面向对象软件系统中，类与类之间的关系越多，不能表示系统设计得越好，反而表示系统中类之间的耦合度太大，这样的系统在维护和修改时都缺乏灵活性，因为一个类的改动会导致多个类发生变化，而外观模式的引入在很大程度上降低了类与类之间的耦合关系。引入外观模式之后，增加新的子系统或者移除子系统都非常方便，客户类无须进行修改（或者极少的修改），只需要在外观类中增加或移除对子系统的引用即可。从这一点来说，外观模式在一定程度上并不符合开闭原则，增加新的子系统需要对原有系统进行一定的修改，虽然这个修改工作量不大。外观模式中所指的子系统是一个广义的概念，它可以是一个类、一个功能模块、系统的一个组成部分或者一个完整的系统。子系统类通常是一些业务类，实现了一些具体的、独立的业务功能，其典型代码如下：

```java
class SubSystemA
{
	 public void MethodA()
	{
		//业务实现代码
	}
}

class SubSystemB
{
	public void MethodB()
	{
	//业务实现代码
	}
}

class SubSystemC
{
	public void MethodC()
	{
	//业务实现代码
	}
}
```

在引入外观类之后，与子系统业务类之间的交互统一由外观类来完成，在外观类中通常存在如下代码：

```java
class Facade
{
	private SubSystemA obj1 = new SubSystemA();
  	private SubSystemB obj2 = new SubSystemB();
	private SubSystemC obj3 = new SubSystemC();
public void Method()
{
obj1.MethodA();
obj2.MethodB();
obj3.MethodC();
}
}
```

#### 3、主要优点

 ① 它对客户端屏蔽了子系统组件，减少了客户端所需处理的对象数目，并使得子系统使用起来更加容易。通过引入外观模式，客户端代码将变得很简单，与之关联的对象也很少。
②  它实现了子系统与客户端之间的松耦合关系，这使得子系统的变化不会影响到调用它的客户端，只需要调整外观类即可。
 ③  一个子系统的修改对其他子系统没有任何影响，而且子系统内部变化也不会影响到外观对象。

#### 4、主要缺点

(1) 不能很好地限制客户端直接使用子系统类，如果对客户端访问子系统类做太多的限制则减少了可变性和灵活 性。
(2) 如果设计不当，增加新的子系统可能需要修改外观类的源代码，违背了开闭原则。

#### 5、适用场景

(1) 当要为访问一系列复杂的子系统提供一个简单入口时可以使用外观模式。
(2) 客户端程序与多个子系统之间存在很大的依赖性。引入外观类可以将子系统与客户端解耦，从而提高子系统的独立性和可移植性。
(3) 在层次化结构中，可以使用外观模式定义系统中每一层的入口，层与层之间不直接产生联系，而通过外观类建立联系，降低层之间的耦合度。

​	外观模式是一种使用频率非常高的设计模式，它通过引入一个外观角色来简化客户端与子系统之间的交互，为复杂的子系统调用提供一个统一的入口，使子系统与客户端的耦合度降低，且客户端调用非常方便。外观模式并不给系统增加任何新功能，它仅仅是简化调用接口。在几乎所有的软件中都能够找到外观模式的应用，如绝大多数B/S系统都有一个首页或者导航页面，大部分C/S系统都提供了菜单或者工具栏，在这里，首页和导航页面就是B/S系统的外观角色，而菜单和工具栏就是C/S系统的外观角色，通过它们用户可以快速访问子系统，降低了系统的复杂程度。所有涉及到与多个业务对象交互的场景都可以考虑使用外观模式进行重构。外观模式是一种使用频率非常高的设计模式，它通过引入一个外观角色来简化客户端与子系统之间的交互，为复杂的子系统调用提供一个统一的入口，使子系统与客户端的耦合度降低，且客户端调用非常方便。外观模式并不给系统增加任何新功能，它仅仅是简化调用接口。在几乎所有的软件中都能够找到外观模式的应用，如绝大多数B/S系统都有一个首页或者导航页面，大部分C/S系统都提供了菜单或者工具栏，在这里，首页和导航页面就是B/S系统的外观角色，而菜单和工具栏就是C/S系统的外观角色，通过它们用户可以快速访问子系统，降低了系统的复杂程度。所有涉及到与多个业务对象交互的场景都可以考虑使用外观模式进行重构。

### ⑥ 享元模式----实现对象的复用

​	当系统中存在大量相同或者类似的对象时，享元模式是一种值得考虑的解决方案，他通过共享技术实现相同或相似的细粒度对象的复用，从而节省内存空间，提高了系统性能。在享元模式中提供一个享元池用于存储已经创建好的享元对象，并通过享元工厂类将享元对象提供给客户端使用。

​	**享元模式通过共享技术实现或相似对象的重用，在逻辑上每一个出现的字符都有一个对象与之对应，然而在物理上他们却共享同一个享元对象，这个对象可以出现一个字符串的不同地方，相同的字符对象都指向同一个实例，在享元模式中这些共享实例对象的地方称为享元池。**

​	享元模式以共享的方式高效地支持大量细粒度对象的重用，享元对象能做到共享的关键是区分了**内部状态和外部状态。**

​	内部状态是存储在享元内部并且不会随环境改变而改变的状态，内部状态可以共享。	

​	外部状态是随环境改变而改变的，不可以共享的状态。享元对象的外部状态通常由客户端保存，并在享元对象被创建之后需要使用的时候再传入到享元对象内部。

​	**享元模式：运用共享技术有效地支持大量细粒度对象复用。**

![13.享元模式1](C:\Users\Administrator\Desktop\笔记\java设计模式\13.享元模式1.png)

**在享元模式结构图中包含如下几个角色：**

1、Flyweight（抽象享元类）：通常是一个接口或抽象类，在抽象享元类中具体享元类公共的方法，这些方法可以向外界提供享元对象的内部数据（内部状态），同时也可以通过这些方法来设置外部数据（外部状态）。

2、ConcreteFlyweight（具体享元类）：它实现了抽象享元类，其实例称为享元对象；在具体享元类中为内部状态提供了存储空间。通常我们可以结合单例模式来设计具体享元类，为每个具体享元类提供唯一的享元对象。

3、UnsharedConcreteFlyweight（非共享具体享元类）：并不是所有的抽象享元类的子类都需要被共享，不能被共享的子类可设计为非共享具体享元类；当需要一个非共享具体享元类的对象时可以直接通过实例化创建。

4、FlyweightFactory（享元工厂类）：享元工厂类用于创建并管理享元对象，它针对抽象享元类编程，将各种类型的具体享元对象存储在一个享元池中，享元池一般设计为一个存储 “键值对”的集合 （也可以是其他类型的集合）。可以结合工厂模式进行设计；当用户请求一个具体享元对象时，享元工厂提供一个存储在享元池中已创建的实例或者创建一个新的实例。返回新创建的实例并将其存储在享元池中。

```java
class FlyweightFactory {
	//定义一个HashMap用于存储享元对象，实现享元池
	private HashMap flyweights = newHashMap();
	public Flyweight getFlyweight(String key){
	//如果对象存在，则直接从享元池获取
	if(flyweights.containsKey(key)){
		return(Flyweight)flyweights.get(key);
	}
	//如果对象不存在，先创建一个新的对象添加到享元池中，然后返回
	else {
		Flyweight fw = newConcreteFlyweight();
		flyweights.put(key,fw);
		return fw;
	}
	}
}
```

​	享元类的设计是享元模式的要点之一，在享元类中要将内部状态和外部状态分开处理，通常将内部状态作为享元类的成员变量，而外部状态通过注入的方式添加到享元类中。典型的享元类代码如下所示：

```java
class Flyweight {
	//内部状态intrinsicState作为成员变量，同一个享元对象其内部状态是一致的
	private String intrinsicState;
	public Flyweight(String intrinsicState) {
		this.intrinsicState=intrinsicState;
	}
	//外部状态extrinsicState在使用时由外部设置，不保存在享元对象中，即使是同一个对象，在每一次调用时也可以传入不同的外部状态
	public void operation(String extrinsicState) {
	......
	}
}
```

#### 2、案例分析

​	Sunny软件公司开发人员通过对围棋棋子进行进一步分析，发现虽然黑色棋子和白色棋子可以共享，但是它们将显示在棋盘的不同位置，如何让相同的黑子或者白子能够多次重复显示且位于一个棋盘的不同地方？解决方法就是将棋子的位置定义为棋子的一个外部状态，在需要时再进行设置。因此，我们在图14-4中增加了一个新的类Coordinates（坐标类），用于存储每一个棋子的位置，修改之后的结构图如图14-5所示：

![13.享元模式2](C:\Users\Administrator\Desktop\笔记\java设计模式\13.享元模式2.png)

```java

/**
 * @author HouYC
 * @create 2020-11-17-23:07
 */
public class LgoChessmanFactory {

    private static LgoChessmanFactory instance = new LgoChessmanFactory();
    /**
     * 使用hashtable 充当存储享元对象，充当享元池
     */
    private static Hashtable hashtable;

    private LgoChessmanFactory() {
        hashtable = new Hashtable();
        LgoChessman black, white;
        black = new BlackLogChessman();
        white = new WhitelgoChessman();
        hashtable.put("b", black);
        hashtable.put("w", white);

    }

    /**
     * 创建唯一实例
     * @return
     */
    public static LgoChessmanFactory getInstance() {
        return instance;
    }

    public LgoChessman getIgoChessaman(String color) {
        return (LgoChessman) hashtable.get(color);
    }

}



/**
 * @author HouYC
 * @create 2020-11-17-23:03
 * 抽象享元类
 */
public abstract class LgoChessman {

    public abstract String getColor();

    /**
     *  引入有外部状态
     */
    public void display(Coordinates coordinates) {
        System.out.println("棋子颜色： " + this.getColor() + ", 棋子位置：" + coordinates.getX() + ", " + coordinates.getY());
    }
//    public void display() {
//        System.out.println("棋子颜色： " + this.getColor());
//    }
}

/**
 * @author HouYC
 * @create 2020-11-17-23:05
 */
public class BlackLogChessman extends LgoChessman {

    @Override
    public String getColor() {
        return "黑子";
    }
}

/**
 * @author HouYC
 * @create 2020-11-17-23:06
 */
public class WhitelgoChessman extends LgoChessman {
    @Override
    public String getColor() {
        return "白子";
    }
}

/**
 * @author HouYC
 * @create 2020-11-17-23:20
 */
public class Coordinates {

    private Integer x;

    private Integer y;

    public Coordinates() {
    }

    public Coordinates(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }
}

/**
 * @author HouYC
 * @create 2020-11-17-23:14
 */
public class Client {
    public static void main(String[] args) {

        LgoChessman black1, black2, black3, white1, white2;

        LgoChessmanFactory factory;

        factory = LgoChessmanFactory.getInstance();

        black1 = factory.getIgoChessaman("b");
        black2 = factory.getIgoChessaman("b");
        black3 = factory.getIgoChessaman("b");

        white1 = factory.getIgoChessaman("w");
        white2 = factory.getIgoChessaman("w");

        black1.display(new Coordinates(1,2));
        black2.display(new Coordinates(2,4));
        black3.display(new Coordinates(5,6));
        white1.display(new Coordinates(1,9));
        white2.display(new Coordinates(2,8));
    }
}

```

#### 3、优点

享元模式通常需要和其他模式一起连用，几种常见的连用方式如下：

​	1、在享元模式的享元工厂类中通常提供一个静态的工厂方法用于返回享元对象，使用简单工厂模式来生成享元对象。

​	2、在一个系统中，通常只有唯一一个享元工厂，因此可以使用单例进行享元工厂类的设计。

​	3、享元模式可以结合组合模式形式符合享元模式，统一对多个享元对象设置外部状态。

当系统中存在大量相同或者相似的对象时，享元模式是一种较好的解决方案，它通过共享技术实现相同或相似的细粒度对象的复用，从而节约了内存空间，提高了系统性能。相比其他结构型设计模式，享元模式的使用频率并不算太高，但是作为一种以 “节约内存，提高性能”为出发点的设计模式，它在软件设计开发中还是得到了一定程度的应用。

​	(1) 可以极大减少内存中对象的数量，使得相同或相似对象在内存中只保存一份，从而可以节约系统资源，提高系统性能。
​	(2) 享元模式的外部状态相对独立，而且不会影响其内部状态，从而使得享元对象可以在不同的环境中被共享。

#### 4、缺点

(1) 享元模式使得系统变得复杂，需要分离出内部状态和外部状态，这使得程序的逻辑复杂化。
(2) 为了使对象可以共享，享元模式需要将享元对象的部分状态外部化，而读取外部状态将使得运行时间变长。

#### 5、适用场景

(1) 一个系统有大量相同或者相似的对象，造成内存的大量耗费。
(2) 对象的大部分状态都可以外部化，可以将这些外部状态传入对象中。
(3) 在使用享元模式时需要维护一个存储享元对象的享元池，而这需要耗费一定的系统资源，因此，应当在需要多次重复使用享元对象时才值得使用享元模式。









































































​	











