package com.cjq.Controller;

import com.cjq.pojo.Users;
import com.cjq.pojo.bo.UsersBO;
import com.cjq.pojo.vo.UsersVO;
import com.cjq.service.UserService;
import com.cjq.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author 被狗追过的夏天
 * date 2020-05-17
 */
@Api(value = "注册登录",tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("/passport")
public class PassportController extends BaseController{

    public final static Logger logger = LoggerFactory.getLogger(PassportController.class);

    @Resource
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public JSONResult login(@RequestBody UsersBO userBO,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        logger.info("/login");
        String username = userBO.getUsername();
        String password = userBO.getPassword();

        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            return JSONResult.errorMsg("用户名或密码不能为空");
        }

        // 1. 实现登录
        Users userResult = userService.queryUserForLogin(username,
                password);
        if (userResult == null) {
            return JSONResult.errorMsg("用户名或密码不正确");
        }


        //生成用户token，存入redis会话
        UsersVO usersVO = getUsersVO(userResult);
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);
        // TODO 同步购物车数据

        return JSONResult.ok(userResult);
    }


    @ApiOperation(value="用户名是否存在",notes = "用户名是否存在",httpMethod = "GET")
    @GetMapping("/usernameIsExist")
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

    @ApiOperation(value="用户注册",notes = "用户注册",httpMethod = "POST")
    @PostMapping("/regist")
    public JSONResult createUsers(@RequestBody  UsersBO userBO,
                                  HttpServletRequest request,
                                  HttpServletResponse response){

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

        //实现用户的redis会话
        UsersVO usersVO = getUsersVO(user);

        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);
        return JSONResult.ok();
    }

    private UsersVO getUsersVO(Users user) {
        String uniqueToken = UUID.randomUUID().toString();
        redisOperator.set(REDIS_USER_TOKEN+":"+user.getId(),uniqueToken);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user,usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        return usersVO;
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public JSONResult logout(@RequestParam String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        // 清除用户的相关信息的cookie
        CookieUtils.deleteCookie(request, response, "user");

        //用户退出登录
        redisOperator.del(REDIS_USER_TOKEN+":"+userId);
        // TODO 分布式会话中需要清除用户数据

        return JSONResult.ok();
    }



}
