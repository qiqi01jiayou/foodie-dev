package com.cjq.Controller;

import com.cjq.pojo.Users;
import com.cjq.pojo.bo.UsersBO;
import com.cjq.service.UserService;
import com.cjq.utils.JSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

/**
 * @author 被狗追过的夏天
 * date 2020-05-17
 */
@ApiIgnore
@RestController
public class testController {

    @GetMapping("/test")
    public Object test() {
        return "test";
    }


}
