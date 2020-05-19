package com.cjq.config;

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
 * @author 被狗追过的夏天
 * date 2020-05-19
 */
@Configuration
@EnableSwagger2
public class Swagger2 {
    //swagger2 核心配置 docket
    //http://localhost:8088/swagger-ui.html
    //http://localhost:8088/doc.html

    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo()) //定义文档汇总信息
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.cjq.Controller")) //指定api包
                .paths(PathSelectors.any())  //所有controller
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("接口api")  //文档页标题
                .contact(new Contact("cjq","https://www.cjq.com","2333@qq.com")) //联系人信息
                .description("接口描述")  //详细信息
                .version("1.0.0") //文档版本号
                .termsOfServiceUrl("https://www.cjq.com") //网站地址
                .build();
    }
}
