package com.cjq.Controller.intercepter;

import com.cjq.utils.JSONResult;
import com.cjq.utils.JsonUtils;
import com.cjq.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author 被狗追过的夏天
 * date 2020-05-31
 */
public class UserTokenIntercepter implements HandlerInterceptor {
    public static final String REDIS_USER_TOKEN = "redis_user_token";

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 请求之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("进入到拦截器,被拦截..............");
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");
        String uniqueToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(userToken) || StringUtils.isBlank(uniqueToken)) {
            returnErrorResponse(response,JSONResult.errorMsg("请登录......"));
            return false;
        }
        if (!uniqueToken.equals(userToken)) {
            returnErrorResponse(response,JSONResult.errorMsg("账号异地登录......"));
        }
        //false  请求被拦截 验证 true放行
        return true;
    }

    private void returnErrorResponse(HttpServletResponse response, JSONResult result) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/json");
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 请求之后 渲染之前
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 渲染之后
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
