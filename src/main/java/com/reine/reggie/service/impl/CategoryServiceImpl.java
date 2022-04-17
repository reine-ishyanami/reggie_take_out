package com.reine.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reine.reggie.common.CustomException;
import com.reine.reggie.entity.Category;
import com.reine.reggie.entity.Dish;
import com.reine.reggie.entity.Setmeal;
import com.reine.reggie.mapper.CategoryMapper;
import com.reine.reggie.service.CategoryService;
import com.reine.reggie.service.DishService;
import com.reine.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 86158
 * @description 针对表【category(菜品及套餐分类)】的数据库操作Service实现
 * @createDate 2022-04-14 08:27:42
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除前要进行判断
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = Wrappers.lambdaQuery(Dish.class);
        // 添加查询条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        // 查询当前分类是否关联菜品，如果关联，抛出异常
        int countDish = dishService.count(dishLambdaQueryWrapper);
        if (countDish > 0) {
            throw new CustomException("该分类关联了菜品，无法删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = Wrappers.lambdaQuery(Setmeal.class);
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        // 查询当前分类是否关联套餐，如果关联，抛出异常
        int countSetmeal = setmealService.count(setmealLambdaQueryWrapper);
        if (countSetmeal > 0) {
            throw new CustomException("该分类关联了套餐，无法删除");
        }
        // 正常删除
        super.removeById(id);

    }
}




