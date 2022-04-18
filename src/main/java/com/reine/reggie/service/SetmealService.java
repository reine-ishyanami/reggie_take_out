package com.reine.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reine.reggie.dto.SetmealDto;
import com.reine.reggie.entity.Setmeal;

import java.util.List;

/**
 * @author reine
 * @description 针对表【setmeal(套餐)】的数据库操作Service
 * @createDate 2022-04-14 09:25:42
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐
     *
     * @param setmealDto 套餐属性
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除套餐和菜品的关联数据
     *
     * @param ids 被删除的套餐id
     */
    void removeWithDish(List<Long> ids);

    /**
     * 更新套餐状态
     *
     * @param status 菜品状态
     * @param ids    套餐id
     */
    void updateStatus(int status, List<Long> ids);

    /**
     * 根据id查询对应的套餐信息和菜品信息
     *
     * @param id 套餐id
     * @return 套餐和对应菜品信息
     */
    SetmealDto getByIdWithDish(Long id);

    /**
     * 更新套餐信息
     *
     * @param setmealDto 套餐信息
     */
    void updateWithDish(SetmealDto setmealDto);
}
