package com.reine.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reine.reggie.common.CustomException;
import com.reine.reggie.dto.SetmealDto;
import com.reine.reggie.entity.Setmeal;
import com.reine.reggie.entity.SetmealDish;
import com.reine.reggie.mapper.SetmealMapper;
import com.reine.reggie.service.SetmealDishService;
import com.reine.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author reine
 * @description 针对表【setmeal(套餐)】的数据库操作Service实现
 * @createDate 2022-04-14 09:25:42
 */
@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     *
     * @param setmealDto 套餐属性
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto.toString());
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal);
        super.save(setmeal);
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().peek(item -> item.setSetmealId(setmealId)).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐，同时删除套餐和菜品的关联数据
     *
     * @param ids 被删除的套餐id
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态，停售则可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = Wrappers.lambdaQuery(Setmeal.class);
        // in查询
        queryWrapper.in(Setmeal::getId, ids);
        // 查询起售状态的套餐
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = super.count(queryWrapper);
        if (count > 0) {
            // 套餐没有停售，不能删除
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        // 可以删除
        super.removeByIds(ids);

        // 删除关系表数据
        setmealDishService.removeByIds(ids);
    }

    /**
     * 更新套餐状态
     *
     * @param status 菜品状态
     * @param ids    套餐id
     */
    @Override
    public void updateStatus(int status, List<Long> ids) {
        LambdaUpdateWrapper<Setmeal> updateWrapper = Wrappers.lambdaUpdate(Setmeal.class);
        updateWrapper.set(Setmeal::getStatus, status);
        updateWrapper.in(Setmeal::getId, ids);
        super.update(updateWrapper);
    }

    /**
     * 根据id查询对应的套餐信息和菜品信息
     *
     * @param id 套餐id
     * @return 套餐和对应菜品信息
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        // 查询套餐基本信息
        Setmeal setmeal = super.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        // 查询当前套餐对应的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = Wrappers.lambdaQuery(SetmealDish.class);
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(dishes);
        return setmealDto;
    }

    /**
     * 更新套餐信息
     *
     * @param setmealDto 套餐信息
     */
    @Transactional
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        // 更新setmeal表信息
        super.updateById(setmealDto);
        // 清除菜品数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = Wrappers.lambdaQuery(SetmealDish.class);
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        // 添加菜品数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().peek(item -> item.setSetmealId(setmealDto.getId())).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);

    }
}