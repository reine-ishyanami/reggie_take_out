package com.reine.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reine.reggie.common.CustomException;
import com.reine.reggie.dto.DishDto;
import com.reine.reggie.entity.Dish;
import com.reine.reggie.entity.DishFlavor;
import com.reine.reggie.entity.SetmealDish;
import com.reine.reggie.mapper.DishMapper;
import com.reine.reggie.service.DishFlavorService;
import com.reine.reggie.service.DishService;
import com.reine.reggie.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author reine
 * @description 针对表【dish(菜品管理)】的数据库操作Service实现
 * @createDate 2022-04-14 09:23:05
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
        implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增菜品，同时插入菜品对应的口味数据
     *
     * @param dishDto 菜品属性
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品基本信息到菜品表
        this.save(dishDto);
        // 菜品id
        Long dishId = dishDto.getId();
        // 保存菜品口味信息到菜品口味表
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().peek(item -> item.setDishId(dishId)).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询对应菜品信息和口味信息
     *
     * @param id 菜品id
     * @return 菜品和对应口味信息
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息
        Dish dish = super.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        // 查询当前菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = Wrappers.lambdaQuery(DishFlavor.class);
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 更新菜品信息
     *
     * @param dishDto 菜品信息
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表信息
        super.updateById(dishDto);
        // 清除口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = Wrappers.lambdaQuery(DishFlavor.class);
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        // 添加口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().peek(item -> item.setDishId(dishDto.getId())).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 更新菜品删除信息
     *
     * @param ids 菜品id
     */
    @Override
    public void delete(List<Long> ids) {
        // 检查菜品是否起售，若起售则不能删除
        LambdaQueryWrapper<Dish> queryWrapper = Wrappers.lambdaQuery(Dish.class);
        // in查询
        queryWrapper.in(Dish::getId, ids);
        // 查询起售状态的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        int count = super.count(queryWrapper);
        if (count > 0) {
            // 套餐没有停售，不能删除
            throw new CustomException("菜品正在售卖中，不能删除");
        }
        // 检查菜品是否在套餐中，若在套餐则不能删除
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = Wrappers.lambdaQuery(SetmealDish.class);
        // in查询
        queryWrapper1.in(SetmealDish::getDishId, ids);
        // 查询起售状态的菜品
        int count1 = setmealDishService.count(queryWrapper1);
        if (count1 > 0) {
            // 菜品在套餐中，不能删除
            throw new CustomException("菜品在套餐中，不能删除");
        }
        // 更改菜品删除字段为1
        super.removeByIds(ids);
    }

    /**
     * 更新菜品状态
     *
     * @param status 菜品状态
     * @param ids    菜品id
     */
    @Override
    public void updateStatus(int status, List<Long> ids) {
        LambdaUpdateWrapper<Dish> updateWrapper = Wrappers.lambdaUpdate(Dish.class);
        updateWrapper.set(Dish::getStatus, status);
        updateWrapper.in(Dish::getId, ids);
        super.update(updateWrapper);
    }
}