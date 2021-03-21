## 一、SpringBoot集成Swagger2

### 1、简介

- API 接口众多，细节复杂，需要考虑不同的HTTP请求类型、HTTP头部信息、HTTP请求内容等，想要高质量的完成这份文档需要耗费大量的精力；
- 难以维护。随着需求的变更和项目的优化、推进，接口的细节在不断地演变，接口描述文档也需要同步修订，可是文档和代码处于两个不同的媒介，除非有严格的管理机制，否则很容易出现文档、接口不一致的情况

**Swagger2** 的出现就是为了从根本上解决上述问题。它作为一个规范和完整的框架，可以用于生成、描述、调用和可视化 RESTful 风格的 Web 服务：

1. 接口文档在线自动生成，文档随接口变动实时更新，节省维护成本
2. 支持在线接口测试，不依赖第三方工具

### 2、pom 依赖

```java
 <!-- Swagger2-->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
```

### 3、 swagger2 配置

```java

/**
 * @author HouYC
 * @create 2020-06-25-20:32
 *
 * Swagger 配置类
 */
@Slf4j
@EnableSwagger2
@Configuration
public class Swagger2Config {

    /**
     * Swagger2 信息
     * @return
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                //API 基本信息
                .apiInfo(apiInfo())
                //设置允许暴露的接口
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.javacodedemo.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * Api 基本信息
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("HYC-Demo 项目")
                .description("综合知识项目")
                .contact(new Contact("侯亚超", "", "814428354@qq.com"))
                .version("1.0.0")
                .build();
    }
}

```

需要在web配置添加swagger2静态映射：

```java

/**
 * @author HouYC
 * @create 2020-06-24-22:39
 *
 * Web 配置类
 */
@Slf4j
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    /**
     * 全局限流拦截器
     */
    @Resource
    private RateLimitInterceptor rateLimitInterceptor;

    /**
     * 向Web中添加拦截器
     * @param registration
     */
    @Override
    public void addInterceptors(InterceptorRegistry registration) {
        //配置拦截器，拦截所有以/api 开头的请求
        registration.addInterceptor(rateLimitInterceptor).addPathPatterns("/api/**");
    }

    /**
     * 静态资源配置
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //配置本地文件夹目录映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("C:\\Users\\Administrator\\Desktop\\JavaCodeDemo\\uploads\\");

        //Swagger2 做的映射
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}

```

### 4、 具体使用

```java

/**
 * @author HouYC
 * @create 2020-06-21-21:54
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
@Validated
@Api(
  		tags="说明该类的作用"
        value = "用户管理Controller",
        protocols = "http,https",
        hidden = false
)
public class UserController {

    @Autowired
    private ExcelExportService excelExportService;

    /**
     * 用户导出
     * @param query
     * @param fileName
     * @return
     */
    public ResponseResult<Boolean> export(@Validated UserQueryDTO query, @NotEmpty String fileName) {

        excelExportService.export(query, fileName);
        return ResponseResult.success(Boolean.TRUE);
    }

    /**
     * 新增用户
     * @param userDTO
     * @return
     */
    @PostMapping("/save")
    public ResponseResult save(@Validated(InsertValidationGroup.class) @RequestBody UserDTO userDTO) {

        return ResponseResult.success("新增成功！");
    }

    /**
     * 更新用户信息
     * @param id
     * @param userDTO
     * @return
     */
    @ApiOperation(
            value = "更新用户信息",
            notes = "备注说明信息",
            response = ResponseResult.class,
            httpMethod = "PUT"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "id",
                    value = "参数说明，用户主键",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "1234"
            ),
            @ApiImplicitParam(
                    name = "userDTO",
                    value = "用户信息",
                    required = true,
                    paramType = "body",
                    dataType = "UserDTO",
                    dataTypeClass = UserDTO.class
            )
    })
    @ApiResponses({
            @ApiResponse(code = 0000, message = "执行成功"),
            @ApiResponse(code = 3004, message = "执行失败")
    })
    @PutMapping("/update")
    @CacheEvict(cacheNames = "users-cache", allEntries = true)
    public ResponseResult update(@NotBlank @PathVariable Long id,
                                 @Validated(InsertValidationGroup.class) @RequestBody UserDTO userDTO) {

        return ResponseResult.success("更新成功!");
    }

    /**
     * 删除用户信息
     * @param id
     * @return
     */
    @DeleteMapping("delete")
    public ResponseResult delete(@NotBlank @PathVariable Long id) {

        return ResponseResult.success("删除成功！");
    }

    /**
     *  查询用户信息
     * @param pageNo
     * @param pageSize
     * @param query
     * @return
     */
    @GetMapping("get")
    @Cacheable(cacheNames = "users-cache")
    public ResponseResult<PageResult> query(@NotBlank @RequestParam("pageNo") Integer pageNo,
                                            @NotBlank @RequestParam("pageSize") Integer pageSize,
                                            @Validated @RequestBody UserQueryDTO query) {

        log.info("未使用缓存");
        return ResponseResult.success(new PageResult());
    }

}

```

```java

/**
 * @author HouYC
 * @create 2020-06-21-21:55
 *
 * 类描述：通用返回结果模型
 */
@Data
@ApiModel(
        value = "统一封装结果实体",
        description = "封装统一返回结果实体"
)
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = 9212121343853534L;

    /**
     * 是否成功
     */
    @ApiModelProperty(
            name = "success",
            value = "执行成功",
            required = true,
            dataType = "Boolean"
    )
    private Boolean success;

    /**
     * 编码
     */
    @ApiModelProperty(
            value = "编码"
    )
    private String code;

    /**
     * 描述信息
     */
    @ApiModelProperty(
            value = "描述信息"
    )
    private String message;

    /**
     * 结果
     */
    @ApiModelProperty(
            value = "泛型结果T"
    )
    private T result;

    public static <T> ResponseResult<T> success(T result) {
        ResponseResult<T> responseResult = new ResponseResult<>();

        responseResult.setSuccess(Boolean.TRUE);
        responseResult.setResult(result);
        return responseResult;
    }

    public static <T> ResponseResult<T> failure(String code, String message) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setMessage(message);
        responseResult.setCode(code);
        responseResult.setSuccess(Boolean.FALSE);

        return responseResult;
    }

    public static <T> ResponseResult<T> failure(ErrorCodeEnum errorCodeEnum) {
        return failure(errorCodeEnum.getCode(), errorCodeEnum.getMessage());
    }


}

```



如果有shiro框架使用，需要在shiro配置类中放行swagger2相关资源：

```java
//swagger2免拦截
filterChainDefinitionMap.put("/swagger-ui.html**", "anon");
filterChainDefinitionMap.put("/v2/api-docs", "anon");
filterChainDefinitionMap.put("/swagger-resources/**", "anon");
filterChainDefinitionMap.put("/webjars/**", "anon");
```



### 5、常用注解详解

#### 5.1、@Api ：请求类的说明

```java
@Api：放在请求的类上，与 @Controller 并列，说明类的作用，如用户模块，订单类等。
    tags="说明该类的作用"
    value="该参数没什么意义，所以不需要配置"
```

##### 举例：

```java
@Api(tags = "账户相关模块")
@RestController
@RequestMapping("/api/account")
public class AccountController {
    //TODO
}
```

#### 5.2、@ApiOperation：方法的说明

```java
@ApiOperation："用在请求的方法上，说明方法的作用"
    value="说明方法的作用"
    notes="方法的备注说明"
```

##### 举例：

```java
@ApiOperation(value = "修改密码", notes = "方法的备注说明，如果有可以写在这里")
@PostMapping("/changepass")
public AjaxResult changePassword(@AutosetParam SessionInfo sessionInfo,
        @RequestBody @Valid PasswordModel passwordModel) {
    //TODO
}
```

#### 5.3、@ApiImplicitParams、@ApiImplicitParam：方法参数的说明

```java
@ApiImplicitParams：用在请求的方法上，包含一组参数说明
    @ApiImplicitParam：对单个参数的说明      
        name：参数名
        value：参数的汉字说明、解释
        required：参数是否必须传
        paramType：参数放在哪个地方
            · header --> 请求参数的获取：@RequestHeader
            · query --> 请求参数的获取：@RequestParam
            · path（用于restful接口）--> 请求参数的获取：@PathVariable
            · body（请求体）-->  @RequestBody User user
            · form（普通表单提交）     
        dataType：参数类型，默认String，其它值dataType="int"       
        defaultValue：参数的默认值
```

##### 举例：

```java
@ApiOperation(value="用户登录",notes="随边说点啥")
@ApiImplicitParams({
        @ApiImplicitParam(name="mobile",value="手机号",required=true,paramType="form"),
        @ApiImplicitParam(name="password",value="密码",required=true,paramType="form"),
        @ApiImplicitParam(name="age",value="年龄",required=true,paramType="form",dataType="Integer")
})
@PostMapping("/login")
public AjaxResult login(@RequestParam String mobile, @RequestParam String password,
                        @RequestParam Integer age){
    //TODO
    return AjaxResult.OK();
}
```

##### 单个参数举例

```java
@ApiOperation("根据部门Id删除")
@ApiImplicitParam(name="depId",value="部门id",required=true,paramType="query")
@GetMapping("/delete")
public AjaxResult delete(String depId) {
    //TODO
}
```

#### 5.4、@ApiResponses、@ApiResponse：方法返回值的说明

```java
@ApiResponses：方法返回对象的说明
    @ApiResponse：每个参数的说明
        code：数字，例如400
        message：信息，例如"请求参数没填好"
        response：抛出异常的类
```

##### 举例：

```java
@ApiOperation(value = "修改密码", notes = "方法的备注说明，如果有可以写在这里")
@ApiResponses({
        @ApiResponse(code = 400, message = "请求参数没填好"),
        @ApiResponse(code = 404, message = "请求路径找不到")
})
@PostMapping("/changepass")
public AjaxResult changePassword(@AutosetParam SessionInfo sessionInfo,
        @RequestBody @Valid PasswordModel passwordModel) {
    //TODO
}
```

#### 5.5、@ApiModel：用于JavaBean上面，表示一个JavaBean

```java
@ApiModel：用于JavaBean的类上面，表示此 JavaBean 整体的信息
    （这种一般用在post创建的时候，使用 @RequestBody 这样的场景，请求参数无法使用 @ApiImplicitParam 注解进行描述的时候 ）
```

#### 5.6. @ApiModelProperty：用在JavaBean的属性上面，说明属性的含义

##### @ApiModel和 @ApiModelProperty举例：

```java
@ApiModel("修改密码所需参数封装类")
public class PasswordModel
{
    @ApiModelProperty("账户Id")
    private String accountId;
//TODO
}
```

## 总结：API文档浏览地址

> 配置好Swagger2并适当添加注解后，启动SpringBoot应用，
> 访问http://localhost:8080/swagger-ui.html 即可浏览API文档。
> 另外，我们需要为了API文档的可读性，适当的使用以上几种注解就可以。





