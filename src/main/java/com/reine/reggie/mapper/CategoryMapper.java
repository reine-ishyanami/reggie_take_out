package com.reine.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reine.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author reine
 * @description 针对表【category(菜品及套餐分类)】的数据库操作Mapper
 * @createDate 2022-04-14 08:27:42
 * @Entity generator.entity.Category
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}




