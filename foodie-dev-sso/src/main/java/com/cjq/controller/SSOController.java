package com.cjq.controller;

import com.cjq.pojo.Users;
import com.cjq.pojo.vo.UsersVO;
import com.cjq.service.UserService;
import com.cjq.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author 被狗追过的夏天
 * date 2020-05-17
 */
@Controller
public class SSOController {

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_TICKET = "redis_user_ticket";
    public static final String REDIS_TEMP_TICKET = "redis_temp_ticket";


    public static final String COOKIE_USER_TICKET = "cookie_user_ticket";

    @Resource
    private UserService userService;
    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/login")
    public String login(String returnUrl, Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("returnUrl",returnUrl);
        // 1 获取userTicket全局门票 如果cookie中能够获取到 此时签发一个临时票据
        String userTicket = getCookie(COOKIE_USER_TICKET, request);
        boolean isVerified = verifyUserTicket(userTicket);
        if(!isVerified){
            //从未登录
            return "login";
        }
        String tempTicket = createTempTicket();
        return "login";
//        return "redirect:"+returnUrl+"?tempTicket="+tempTicket;
    }

    @GetMapping("/logout")
    public JSONResult logout(String userId, HttpServletRequest request, HttpServletResponse response) {
        // 获取CAS 用户门票
        String userTicket = getCookie(COOKIE_USER_TICKET, request);
        //清除cookie/redis
        deleteCookie(COOKIE_USER_TICKET,response);
        redisOperator.del(REDIS_USER_TICKET+":"+userTicket);
        //清除 用户全局会话/分布式会话
        redisOperator.del(REDIS_USER_TOKEN+":"+userId);
        return JSONResult.ok();
//        return "redirect:"+returnUrl+"?tempTicket="+tempTicket;
    }


    @PostMapping("/doLogin")
    public String doLogin(String username,String password,String returnUrl,
                          Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("returnUrl",returnUrl);

        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            model.addAttribute("errmsg","用户不能为空");
            return "login";
        }

        // 1. 实现登录
        Users userResult = userService.queryUserForLogin(username,
                password);
        if (userResult == null) {
            model.addAttribute("errmsg","用户名或密码错误");
            return "login";
        }

        // 2.实现用户redis会话
        String uniqueToken = UUID.randomUUID().toString();
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult,usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN+":"+userResult.getId(),JsonUtils.objectToJson(usersVO));

        // 3.生成全局门票 代表用户在CAS端登录过
        String userTicket = UUID.randomUUID().toString().trim();
        //放入CAS cookie
        setCookie(COOKIE_USER_TICKET,userTicket,response);
        //4. ticket关联用户 并且放入redis
        redisOperator.set(REDIS_USER_TICKET+":"+userTicket,userResult.getId());
        //5 生成临时票据 回调调用端 是由CAS端签发的一次性临时ticket
        String tempTicket = createTempTicket();
        /**
         *userTicket 用户颁发用户在CAS端的一个登陆状态 已经登录
         *tempTicket 用于颁发给用户进行一次性验证的票据 有时效性
         */

        return "login";
//        return "redirect:"+returnUrl+"?tempTicket="+tempTicket;
    }

    @PostMapping("/verifyTempTicket")
    @ResponseBody
    public JSONResult verifyTempTicket(String tempTicket, HttpServletRequest request, HttpServletResponse response) {
        String tempTicketValue = redisOperator.get(REDIS_TEMP_TICKET + ":" + tempTicket);
        if(StringUtils.isBlank(tempTicketValue)){
            return JSONResult.errorMsg("门票异常");
        }
        //如果临时票据ok 则需要销毁 并且需要拿到CAS全局userTicket 以此获取用户会话
        try {
            if(!tempTicketValue.equals(MD5Utils.getMD5Str(tempTicket))){
                return JSONResult.errorMsg("门票异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        redisOperator.del(REDIS_TEMP_TICKET + ":" + tempTicket);
        String userTicket = getCookie(COOKIE_USER_TICKET, request);
        String userId = redisOperator.get(REDIS_USER_TICKET+":"+userTicket);//????????
        if(StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("门票异常");
        }
        //验证门票对应的会话
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if(StringUtils.isBlank(userRedis)){
            return JSONResult.errorMsg("门票异常");
        }

        return JSONResult.ok(JsonUtils.jsonToPojo(userRedis,UsersVO.class));
    }



    private boolean verifyUserTicket(String userTicket){
        if(StringUtils.isBlank(userTicket)){
            return false;
        }
        // 1 验证CAS门票 是否有效
        String userId = redisOperator.get(REDIS_USER_TICKET+":"+userTicket);
        if(StringUtils.isBlank(userId)){
            return false;
        }
        //2 验证门票对应的会话
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if(StringUtils.isBlank(userRedis)){
            return false;
        }
        return true;
    }

    private String createTempTicket(){
        String tempTicket = UUID.randomUUID().toString();
        try {
            redisOperator.set(REDIS_TEMP_TICKET+":"+tempTicket, MD5Utils.getMD5Str(tempTicket),600);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempTicket;
    }

    private void setCookie(String key,String val,HttpServletResponse response){
        Cookie cookie = new Cookie(key,val);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    private String getCookie(String key,HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies==null || StringUtils.isBlank(key)){
            return null;
        }
        String value = Arrays.stream(cookies).filter(v -> v.getName().equals(key)).findFirst().get().getValue();
        return value;
    }

    private void  deleteCookie(String key,HttpServletResponse response){
        Cookie cookie = new Cookie(key,null);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }
}
