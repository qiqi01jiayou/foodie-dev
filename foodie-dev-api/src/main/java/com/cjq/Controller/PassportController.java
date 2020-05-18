package com.cjq.Controller;

import com.cjq.pojo.Users;
import com.cjq.pojo.bo.UsersBO;
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

    @PostMapping("/register")
    public JSONResult createUsers(@RequestBody  UsersBO userBO){

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPwd = userBO.getConfirmPassword();

        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(confirmPwd)) {
            return JSONResult.errorMsg("用户名或密码不能为空");
        }
        // 1. 查询用户名是否存在
        boolean isExist = userService.queryUserNameIsExists(username);
        if (isExist) {
            return JSONResult.errorMsg("用户名已经存在");
        }
        // 2. 密码长度不能少于6位
        if (password.length() < 6) {
            return JSONResult.errorMsg("密码长度不能少于6");
        }
        // 3. 判断两次密码是否一致
        if (!password.equals(confirmPwd)) {
            return JSONResult.errorMsg("两次密码输入不一致");
        }
        Users user = userService.createUser(userBO);
        return JSONResult.ok(user);
    }


}
