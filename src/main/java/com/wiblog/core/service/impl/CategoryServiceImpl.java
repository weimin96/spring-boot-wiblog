package com.wiblog.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Category;
import com.wiblog.core.mapper.ArticleMapper;
import com.wiblog.core.mapper.CategoryMapper;
import com.wiblog.core.service.ICategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现类
 *
 * @author pwm
 * @since 2019-06-15
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    private final CategoryMapper categoryMapper;

    private final ArticleMapper articleMapper;

    public CategoryServiceImpl(ArticleMapper articleMapper, CategoryMapper categoryMapper) {
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<?> delCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        // 改变文章的分类
        articleMapper.updateByCategoryId(id);
        // 将该分类的子节点移到上一层
        categoryMapper.updateSubCategory(id);
        categoryMapper.deleteById(id);
        return ServerResponse.success(null, "删除分类成功", category.getName());
    }
}
