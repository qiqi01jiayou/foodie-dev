package com.cjq.Controller;

import com.cjq.pojo.bo.ShopcartBO;
import com.cjq.utils.JSONResult;
import com.cjq.utils.JsonUtils;
import com.cjq.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Api(value = "购物车接口controller", tags = {"购物车接口相关的api"})
@RequestMapping("shopcart")
@RestController
public class ShopcatController extends BaseController{
    @Autowired
    private RedisOperator redisOperator;


    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/add")
    public JSONResult add(
            @RequestParam String userId,
            @RequestBody ShopcartBO shopcartBO,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("");
        }

        System.out.println(shopcartBO);

        //前端用户在登录的情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存
        //需要判断当前购物车中包含已经存在的商品,如果存在则累加购买数量
        String shopCart = redisOperator.get("shop_cart:" + userId);
        List<ShopcartBO> shopcartBOList = null;
        if(StringUtils.isNotBlank(shopCart)){
            boolean isHaving = false;
            shopcartBOList = JsonUtils.jsonToList(shopCart, ShopcartBO.class);
            for (ShopcartBO bo : shopcartBOList) {
                String tempSpecId = bo.getSpecId();
                if(tempSpecId.equals(shopcartBO.getSpecId())){
                    bo.setBuyCounts(bo.getBuyCounts()+shopcartBO.getBuyCounts());
                    isHaving = true;
                }
            }
            if(!isHaving){
                shopcartBOList.add(shopcartBO);
            }
        }else{
            shopcartBOList = new ArrayList<>();
            shopcartBOList.add(shopcartBO);
        }
        redisOperator.set("shop_cart:"+userId,JsonUtils.objectToJson(shopcartBOList));
        return JSONResult.ok();
    }

    @ApiOperation(value = "从购物车中删除商品", notes = "从购物车中删除商品", httpMethod = "POST")
    @PostMapping("/del")
    public JSONResult del(
            @RequestParam String userId,
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return JSONResult.errorMsg("参数不能为空");
        }

        String shopCart = redisOperator.get("shop_cart:" + userId);
        if(StringUtils.isNotBlank(shopCart)){
            List<ShopcartBO> shopcartBOList = JsonUtils.jsonToList(shopCart, ShopcartBO.class);
            for (ShopcartBO bo : shopcartBOList) {
                String tempSpecId = bo.getSpecId();
                if(tempSpecId.equals(itemSpecId)){
                    shopcartBOList.remove(bo);
                    break;
                }
            }
            redisOperator.set("shop_cart:"+userId,JsonUtils.objectToJson(shopcartBOList));
        }
        return JSONResult.ok();
    }

}
