package com.reine.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reine.reggie.common.Result;
import com.reine.reggie.dto.DishDto;
import com.reine.reggie.entity.Category;
import com.reine.reggie.entity.Dish;
import com.reine.reggie.entity.DishFlavor;
import com.reine.reggie.service.CategoryService;
import com.reine.reggie.service.DishFlavorService;
import com.reine.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author reine
 * @since 2022/4/14 15:17
 */
@Slf4j
@RequestMapping("/dish")
@RestController
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品
     *
     * @param dishDto 菜品属性
     * @return 成功或失败信息
     */
    @PostMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    public Result<String> save(@RequestBody DishDto dishDto) {
        log.info("菜品信息为：{}", dishDto);
        dishService.saveWithFlavor(dishDto);
        return Result.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     *
     * @param page     页码
     * @param pageSize 分页条数
     * @param name     搜索的菜品名称
     * @return 查询的菜品列表数据
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        // 构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = Wrappers.lambdaQuery(Dish.class);
        // 添加过滤条件
        queryWrapper.eq(Dish::getIsDeleted, 0);
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);
        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            // 分类id
            Long categoryId = item.getCategoryId();
            // 根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return Result.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应口味信息
     *
     * @param id 菜品id
     * @return 查询的菜品数据
     */
    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return Result.success(dishDto);
    }

    /**
     * 修改菜品
     *
     * @param dishDto 菜品属性
     * @return 更新菜品成功
     */
    @PutMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    public Result<String> update(@RequestBody DishDto dishDto) {
        log.info("菜品信息为：{}", dishDto);
        dishService.updateWithFlavor(dishDto);
        return Result.success("更新菜品成功");
    }

    /**
     * 根据菜品id进行菜品删除
     *
     * @param ids 菜品id
     * @return 删除成功信息
     */
    @DeleteMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    public Result<String> delete(@RequestParam List<Long> ids) {
        dishService.delete(ids);
        return Result.success("删除成功");
    }

    /**
     * 修改菜品状态：起售和停售
     *
     * @param status 1起售，0停售
     * @param ids    套餐id
     * @return 停售成功结果
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = "dishCache", allEntries = true)
    public Result<String> updateStatus(@PathVariable int status, @RequestParam List<Long> ids) {
        dishService.updateStatus(status, ids);
        return Result.success("停售成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     *
     * @param dish 菜品属性
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "dishCache", key = "#dish.categoryId + '_' + #dish.status")
    public Result<List<DishDto>> list(Dish dish) {
        // 构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = Wrappers.lambdaQuery(Dish.class);
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        // 查询状态为1（起售状态）
        queryWrapper.eq(Dish::getStatus, 1);
        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        List<DishDto> dishDtoList = list.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            // 分类id
            Long categoryId = item.getCategoryId();
            // 根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            // 当前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = Wrappers.lambdaQuery(DishFlavor.class);
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            // 根据菜品id查询菜品口味
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return Result.success(dishDtoList);
    }

}
