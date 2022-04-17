package com.reine.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reine.reggie.dto.DishDto;
import com.reine.reggie.entity.Dish;

import java.util.List;

/**
 * @author 86158
 * @description 针对表【dish(菜品管理)】的数据库操作Service
 * @createDate 2022-04-14 09:23:05
 */
public interface DishService extends IService<Dish> {

    /**
     * 新增菜品，同时插入菜品对应的口味数据
     *
     * @param dishDto 菜品属性
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id查询对应菜品信息和口味信息
     *
     * @param id 菜品id
     * @return 菜品和对应口味信息
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 更新菜品信息
     *
     * @param dishDto 菜品信息
     */
    void updateWithFlavor(DishDto dishDto);

    /**
     * 更新菜品删除信息
     *
     * @param ids 菜品id
     */
    void delete(List<Long> ids);

    /**
     * 更新菜品状态
     *
     * @param status 菜品状态
     * @param ids    菜品id
     */
    void updateStatus(int status, List<Long> ids);
}
