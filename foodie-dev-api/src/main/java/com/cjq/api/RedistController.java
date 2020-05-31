package com.cjq.api;

import com.cjq.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author 被狗追过的夏天
 * date 2020-05-31
 */
@RestController
@RequestMapping("/redis")
public class RedistController {

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/set")
    public Object set(String key, String value) {
        redisOperator.set(key,value);
        return "ok";
    }

    @GetMapping("/get")
    public Object get(String key) {
        return redisOperator.get(key);
    }

    @GetMapping("/delete")
    public Object delete(String key) {
        redisOperator.del(key);
        return "ok";
    }

}
