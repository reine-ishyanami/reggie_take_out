package com.reine.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.reine.reggie.common.BaseContext;
import com.reine.reggie.common.Result;
import com.reine.reggie.entity.ShoppingCart;
import com.reine.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author reine
 * @since 2022/4/15 21:30
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加或减少菜品或套餐到购物车
     *
     * @param shoppingCart 菜品或套餐信息
     * @return 添加的单条数据
     */
    @PostMapping("/add/{count}")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, @PathVariable int count) {
        log.info("shoppingcart:{}", shoppingCart);
        // 设置用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        // 查询当前菜品或套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = Wrappers.lambdaQuery(ShoppingCart.class);
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        if (dishId != null) {
            // 添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            // 添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if (cartServiceOne == null) {
            // 如果不存在，添加到购物车，数量默认为1
            shoppingCart.setNumber(count);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        } else {
            // 如果已经存在，就在原来数量基础上进行操作
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + count);
            if (cartServiceOne.getNumber() == 0) {
                shoppingCartService.removeById(cartServiceOne.getId());
            } else {
                shoppingCartService.updateById(cartServiceOne);
            }
        }

        return Result.success(cartServiceOne);
    }

    /**
     * 查看购物车
     *
     * @return 购物车信息
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        log.info("查看购物车");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = Wrappers.lambdaQuery(ShoppingCart.class);
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return Result.success(list);
    }

    /**
     * 清空购物车
     *
     * @return 成功信息
     */
    @DeleteMapping("/clean")
    public Result<String> clean() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = Wrappers.lambdaQuery(ShoppingCart.class);
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return Result.success("清空购物车成功");

    }
}
