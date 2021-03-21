package com.example.javacodedemo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

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
