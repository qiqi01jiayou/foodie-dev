package com.cjq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author 被狗追过的夏天
 * date 2020-05-17
 */

@SpringBootApplication
@MapperScan(basePackages = "com.cjq.mapper")//扫描mytbatis mapper包
@ComponentScan(basePackages = {"com.cjq","org.n3r.idworker"})//默认com.cjq当前包
@EnableScheduling //开启定时器
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
