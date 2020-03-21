package com.wiblog.core.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiblog.core.aop.AuthorizeCheck;
import com.wiblog.core.aop.OpsRecord;
import com.wiblog.core.common.Constant;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Category;
import com.wiblog.core.service.ICategoryService;
import org.nlpcn.commons.lang.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 *  控制层
 *
 * @author pwm
 * @date 2019-06-15
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    private ICategoryService categoryService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    public CategoryController(ICategoryService categoryService){
        this.categoryService = categoryService;
    }

    /**
     * 获取分类
     * @return ServerResponse
     */
    @GetMapping("/getCategory")
    public ServerResponse getCategory(String type){
        // 数据库中读取
        if ("database".equals(type)){
            List<Category> list = categoryService.list(new QueryWrapper<Category>().orderByDesc("rank"));
            return ServerResponse.success(list,"获取分类列表成功");
        }
        String json = (String) redisTemplate.opsForValue().get(Constant.CATEGORY_KEY);
        if (StringUtil.isBlank(json)){
            return ServerResponse.error("获取分类列表失败",30001);
        }
        List<Category> list = JSONObject.parseArray(json,Category.class);
        return ServerResponse.success(list,"获取分类列表成功");
    }

    /**
     * 添加分类
     * @param name name
     * @param parentId parentId
     * @return ServerResponse
     */
    @AuthorizeCheck(grade = "2")
    @PostMapping("/addCategory")
    @OpsRecord(msg = "添加了分类<<{0}>>")
    public ServerResponse addCategory(String name,String url,Long parentId){
        Category category = new Category();
        category.setName(name);
        category.setUrl(url);
        category.setRank(1);
        /*Category parent = categoryService.getById(parentId);
        if (parent == null){
            return ServerResponse.error("不存在该父级分类",50001);
        }*/
        category.setParentId(parentId);
        boolean result = categoryService.save(category);
        if (!result){
            return ServerResponse.error("新增分类失败",50002);
        }
        return ServerResponse.success(null,"新增分类成功",name);
    }

    /**
     * 更新分类
     * @param category category
     * @return ServerResponse
     */
    @AuthorizeCheck(grade = "2")
    @PostMapping("/updateCategory")
    @OpsRecord(msg = "更新了分类<<{0}>>")
    public ServerResponse updateCategory(Category category){
        boolean tag = categoryService.updateById(category);
        if (!tag){
            return ServerResponse.error("更新分类失败",30001);
        }
        return ServerResponse.success(null,"更新分类成功",category.getName());
    }

    /**
     * 删除分类
     * @param id id
     * @return ServerResponse
     */
    @AuthorizeCheck(grade = "2")
    @OpsRecord(msg = "删除了分类<<{0}>>")
    @PostMapping("/delCategory")
    public ServerResponse delCategory(Long id){
        return categoryService.delCategory(id);
    }

    /**
     * 更新分类 缓存
     * @return ServerResponse
     */
    @AuthorizeCheck(grade = "2")
    @GetMapping("/updateCache")
    public ServerResponse updateCache(){
        List<Category> list = categoryService.list(new QueryWrapper<Category>().orderByDesc("rank"));
        String data = JSONObject.toJSONString(list);
        redisTemplate.opsForValue().set(Constant.CATEGORY_KEY,data);
        return ServerResponse.success(null,"更新数据成功");
    }
}
