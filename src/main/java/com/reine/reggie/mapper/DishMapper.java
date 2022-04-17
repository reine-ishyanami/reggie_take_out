package com.reine.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reine.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 86158
 * @description 针对表【dish(菜品管理)】的数据库操作Mapper
 * @createDate 2022-04-14 09:23:05
 * @Entity generator.entity.Dish
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}




