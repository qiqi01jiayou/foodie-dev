package com.cjq.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 被狗追过的夏天
 * date 2020-05-17
 */
@RestController
public class HelloWord {

    @GetMapping("/hello")
    public String hello() {
        return "hello world";
    }
}
