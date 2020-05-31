package com.cjq.Controller;

import com.cjq.enums.YesOrNo;
import com.cjq.pojo.Carousel;
import com.cjq.pojo.Category;
import com.cjq.pojo.vo.CategoryVO;
import com.cjq.pojo.vo.NewItemsVO;
import com.cjq.service.CarouselService;
import com.cjq.service.CategoryService;
import com.cjq.utils.JSONResult;
import com.cjq.utils.JsonUtils;
import com.cjq.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public JSONResult carousel() {
        String carousel = redisOperator.get("carousel");
        List<Carousel> list = new ArrayList<>();
        if(StringUtils.isNoneBlank(carousel)){
            list = JsonUtils.jsonToList(carousel,Carousel.class);
        }else{
            list = carouselService.queryAll(YesOrNo.YES.type);
            redisOperator.set("carousel", JsonUtils.objectToJson(list));
        }
        return JSONResult.ok(list);
    }

    /* *
     * 首页分类展示需求：
     * 1. 第一次刷新主页查询大分类，渲染展示到首页
     * 2. 如果鼠标上移到大分类，则加载其子分类的内容，如果已经存在子分类，则不需要加载（懒加载）
     */
    @ApiOperation(value = "获取商品分类(一级分类)", notes = "获取商品分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public JSONResult cats() {
        String cats = redisOperator.get("cats");
        List<Category> list = new ArrayList<>();
        if(StringUtils.isNotBlank(cats)){
            list = JsonUtils.jsonToList(cats,Category.class);
        }else{
            list = categoryService.queryAllRootLevelCat();
            redisOperator.set("cats", JsonUtils.objectToJson(list));
        }
        return JSONResult.ok(list);
    }

    @ApiOperation(value = "获取商品子分类", notes = "获取商品子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public JSONResult subCat(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return JSONResult.errorMsg("分类不存在");
        }

        String subCats = redisOperator.get("subCat:"+rootCatId);
        List<CategoryVO> list = new ArrayList<>();
        if(StringUtils.isNotBlank(subCats)){
            list = JsonUtils.jsonToList(subCats,CategoryVO.class);
        }else{
            list = categoryService.getSubCatList(rootCatId);
            redisOperator.set("subCat:"+rootCatId, JsonUtils.objectToJson(list));
        }
        return JSONResult.ok(list);
    }

    @ApiOperation(value = "查询每个一级分类下的最新6条商品数据", notes = "查询每个一级分类下的最新6条商品数据", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public JSONResult sixNewItems(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return JSONResult.errorMsg("分类不存在");
        }

        List<NewItemsVO> list = categoryService.getSixNewItemsLazy(rootCatId);
        return JSONResult.ok(list);
    }

}
