# Swagger2构建restful接口测试

①可以生成文档形式的api 并提供给不同的团队

②便于自测，也便于领导查阅任务量

③无需过多冗余的word文档



​	Swagger是当前最好用的Restful API文档生成的开源项目，通过swagger-spring项目

实现了与SpingMVC框架的无缝集成功能，方便生成spring restful风格的接口文档，

同时swagger-ui还可以测试spring restful风格的接口功能。



中文网站：[http://www.sosoapi.com](http://www.sosoapi.com/)



API详细说明
注释汇总

作用范围				API				使用位置
对象属性		@ApiModelProperty		用在出入参数对象的字段上
协议集描述	@Api					用于controller类上
协议描述		@ApiOperation			用在controller的方法上
Response集	@ApiResponses			用在controller的方法上
Response	@ApiResponse			用在 @ApiResponses里边
非对象参数集	@ApiImplicitParams		用在controller的方法上
非对象参数描述	@ApiImplicitParam	用在@ApiImplicitParams的方法里边

@RequestMapping此注解的推荐配置 
value 
method 
produces

```Java 
    @ApiOperation("信息软删除")
    @ApiResponses({ @ApiResponse(code = CommonStatus.OK, message = "操作成功"),
            @ApiResponse(code = CommonStatus.EXCEPTION, message = "服务器内部异常"),
            @ApiResponse(code = CommonStatus.FORBIDDEN, message = "权限不足") })
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "query", dataType = "Long", name = "id", value = "信息id", required = true) })
    @RequestMapping(value = "/remove.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public RestfulProtocol remove(Long id) {

```

```java
   @ApiModelProperty(value = "标题")
    private String  title;
```



@ApiImplicitParam
属性			取值			作用
paramType						查询参数类型
​				path							以地址的形式提交数据
​				query							直接跟参数完成自动映射赋值
​				body							以流的形式提交 仅支持POST
​				header							参数在request headers 里边提交
​				form							以form表单的形式提交 仅支持POST
dataType						参数的数据类型 只作为标志说明，并没有实际验证
​				Long	
​				String	
name								接收参数名
value								接收参数的意义描述
required								参数是否必填
​				true					必填
​				false				非必填
defaultValue							默认值

paramType 示例详解
path

```java
@RequestMapping(value = "/findById1/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
 @PathVariable(name = "id") Long id
```


body

```java
@ApiImplicitParams({ @ApiImplicitParam(paramType = "body", dataType = "MessageParam", name = "param", value = "信息参数", required = true) })

  @RequestMapping(value = "/findById3", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)

  @RequestBody MessageParam param

  提交的参数是这个对象的一个json，然后会自动解析到对应的字段上去，也可以通过流的形式接收当前的请求数据，但是这个和上面的接收方式仅能使用一个（用@RequestBody之后流就会关闭了）

```


header

```java
@ApiImplicitParams({ @ApiImplicitParam(paramType = "header", dataType = "Long", name = "id", value = "信息id", required = true) }) 

   String idstr = request.getHeader("id");

if (StringUtils.isNumeric(idstr)) {
        id = Long.parseLong(idstr);
    }
```

Form

```java
@ApiImplicitParams({ @ApiImplicitParam(paramType = "form", dataType = "Long", name = "id", value = "信息id", required = true) })
```









Swagger是当前最好用的Restful API文档生成的开源项目，通过swagger-spring项目

实现了与SpingMVC框架的无缝集成功能，方便生成spring restful风格的接口文档，

同时swagger-ui还可以测试spring restful风格的接口功能。

 

官方网站为：<http://swagger.io/>

 

中文网站：[http://www.sosoapi.com](http://www.sosoapi.com/)

汇总

| 作用范围      | API                                      | 使用位置                      |
| --------- | ---------------------------------------- | ------------------------- |
| 对象属性      | @ApiModelProperty                        | 用在参数对象的字段上                |
| 协议集描述     | [@Api](https://my.oschina.net/u/2396174) | 用于controller类上            |
| 协议描述      | @ApiOperation                            | 用在controller的方法上          |
| Response集 | @ApiResponses                            | 用在controller的方法上          |
| Response  | @ApiResponse                             | 用在 @ApiResponses里边        |
| 非对象参数集    | @ApiImplicitParams                       | 用在controller的方法上          |
| 非对象参数描述   | @ApiImplicitParam                        | 用在@ApiImplicitParams的方法里边 |
| 描述返回对象的意义 | @ApiModel                                | 用在返回对象类上                  |

 

 

# @ApiModelProperty的用法   

value–字段说明 
name–重写属性名字 
dataType–重写属性类型 
required–是否必填 
example–举例说明 
hidden–隐藏

> //  我这个用在实体类的get()方法上了
> /**
> ​     * 获取城市编号
> ​     * @return 城市编号
> ​     */
> ​    @ApiModelProperty(value="城市编号",example="058",required=true)
> ​    public String getCode() {
> ​        return code;
> ​    }
>
> ​    /**
> ​     * 设置城市编号
> ​     * @param code  城市编号
> ​     */
> ​    public void setCode(String code) {
> ​        this.code = code;
> ​    }
>
> ​    /**
> ​     * 获取城市名称
> ​     * @return 城市名称
> ​     */
> ​    @ApiModelProperty(value="城市名称",example="guangZhou",required=true)
> ​    public String getName() {
> ​        return name;
> ​    }

 

# @Api

value - 字段说明

description - 注释说明这个类

 

# @ApiOperation

value - 字段说明

notes - 注释说明

httpMethod - 说明这个方法被请求的方式

response - 方法的返回值的类型

> ![img](https://www.liangzl.com/editorImages/cawler/20180604221621_136.jpg)

#  

# @ApiResponse

code - 响应的HTTP状态码

message - 响应的信息内容







模板请参考Swagger2类：



















































