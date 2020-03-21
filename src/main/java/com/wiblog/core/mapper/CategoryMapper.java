package com.wiblog.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wiblog.core.entity.Category;

/**
 *  Mapper 接口
 *
 * @author pwm
 * @since 2019-06-15
 */
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 将该分类的子节点移到上一层
     * @param id
     * @return
     */
    int updateSubCategory(Long id);
}
