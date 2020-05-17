package com.cjq.Controller;

import com.cjq.service.UserService;
import com.cjq.utils.JSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 被狗追过的夏天
 * date 2020-05-17
 */
@RestController
@RequestMapping("/passport")
public class PassportController {

    @Resource
    private UserService userService;

    @GetMapping("/usernameIsExists")
    public JSONResult usernameIsExists(@RequestParam String username) {
        if(StringUtils.isBlank(username)){
            return JSONResult.errorMsg("用户名不能为空");
        }
        boolean isExists = userService.queryUserNameIsExists(username);
        if(isExists){
            return JSONResult.errorMsg("用户名已经存在");
        }
        return JSONResult.ok();
    }


}
