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
 * 控制层
 *
 * @author pwm
 * @date 2019-06-15
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    private ICategoryService categoryService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 获取分类
     *
     * @return ServerResponse
     */
    @GetMapping("/getCategory")
    public ServerResponse getCategory(String type) {
        // 数据库中读取
        if ("database".equals(type)) {
            List<Category> list = categoryService.list(new QueryWrapper<Category>().orderByDesc("rank"));
            return ServerResponse.success(list, "获取分类列表成功");
        }
        return ServerResponse.success(getCategory(), "获取分类列表成功");
    }

    /**
     * 添加分类
     *
     * @param name     name
     * @param parentId parentId
     * @return ServerResponse
     */
    @AuthorizeCheck(grade = "2")
    @PostMapping("/addCategory")
    @OpsRecord(msg = "添加了分类<<{0}>>")
    public ServerResponse addCategory(String name, String url, Long parentId) {
        Category category = new Category();
        category.setName(name);
        category.setUrl(url);
        category.setRank(1);
        category.setParentId(parentId);
        boolean result = categoryService.save(category);
        if (!result) {
            return ServerResponse.error("新增分类失败", 50002);
        }
        delRedisForCategory();
        return ServerResponse.success(null, "新增分类成功", name);
    }

    /**
     * 更新分类
     *
     * @param category category
     * @return ServerResponse
     */
    @AuthorizeCheck(grade = "2")
    @PostMapping("/updateCategory")
    @OpsRecord(msg = "更新了分类<<{0}>>")
    public ServerResponse updateCategory(Category category) {
        boolean tag = categoryService.updateById(category);
        if (!tag) {
            return ServerResponse.error("更新分类失败", 30001);
        }
        delRedisForCategory();
        return ServerResponse.success(null, "更新分类成功", category.getName());
    }

    /**
     * 删除分类
     *
     * @param id id
     * @return ServerResponse
     */
    @AuthorizeCheck(grade = "2")
    @OpsRecord(msg = "删除了分类<<{0}>>")
    @PostMapping("/delCategory")
    public ServerResponse delCategory(Long id) {
        ServerResponse response = categoryService.delCategory(id);
        if (response.isSuccess()) {
            delRedisForCategory();
        }
        return response;
    }


    /**
     * 通过链接获取分类id
     *
     * @param url url
     * @return ServerResponse
     */
    @GetMapping("/getCategoryIdByUrl")
    public ServerResponse getCategoryIdByUrl(String url) {
        for (Category c : getCategory()) {
            if (c.getUrl().equals(url)) {
                return ServerResponse.success(c.getId(), "获取分类id成功");
            }
        }
        return ServerResponse.error("获取分类id失败", 30001);
    }

    /**
     * 缓存有读缓存，没有读数据库再更新缓存
     */
    private List<Category> getCategory() {
        String json = (String) redisTemplate.opsForValue().get(Constant.CATEGORY_KEY);
        List<Category> list;
        if (StringUtil.isBlank(json)) {
            list = categoryService.list(new QueryWrapper<Category>().orderByDesc("rank"));
            String data = JSONObject.toJSONString(list);
            redisTemplate.opsForValue().set(Constant.CATEGORY_KEY, data);
        } else {
            list = JSONObject.parseArray(json, Category.class);
        }
        return list;
    }

    /**
     * 延时删除
     */
    private void delRedisForCategory(){
        try {
            Thread.sleep(500);
            redisTemplate.delete(Constant.CATEGORY_KEY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
