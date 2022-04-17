package com.reine.reggie.dto;

import com.reine.reggie.entity.Setmeal;
import com.reine.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    /**
     * 套餐内包含的菜品列表
     */
    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
