package com.reine.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reine.reggie.common.Result;
import com.reine.reggie.entity.Category;
import com.reine.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author reine
 * @since 2022/4/14 8:32
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param category 分类属性
     * @return 成功信息
     */
    @PostMapping
    public Result<String> save(@RequestBody Category category) {
        log.info("传入的菜品是:{}", category.toString());
        categoryService.save(category);
        return Result.success("新增分类成功");
    }

    /**
     * 分页查询
     *
     * @param page     页码
     * @param pageSize 分页条数
     * @return 查询的分类列表数据
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize) {
        Page<Category> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = Wrappers.lambdaQuery(Category.class);
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 根据id删除分类
     *
     * @param ids 分类id
     * @return 成功或失败信息
     */
    @DeleteMapping
    public Result<String> delete(Long ids) {
        log.info("删除分类，id为{}", ids);
        categoryService.remove(ids);
        return Result.success("分类信息删除成功");
    }

    /**
     * 修改分类信息
     *
     * @param category 分类信息
     * @return 修改分类信息成功
     */
    @PutMapping
    public Result<String> update(@RequestBody Category category) {
        log.info("修改分类信息：{}", category);
        categoryService.updateById(category);
        return Result.success("修改分类信息成功");
    }

    /**
     * 根据条件查询分类数据
     *
     * @param category 查询条件
     * @return 分类列表
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = Wrappers.lambdaQuery(Category.class);
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return Result.success(list);
    }
}
