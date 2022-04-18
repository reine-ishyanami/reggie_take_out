package com.reine.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reine.reggie.entity.Category;

/**
 * @author reine
 * @description 针对表【category(菜品及套餐分类)】的数据库操作Service
 * @createDate 2022-04-14 08:27:42
 */
public interface CategoryService extends IService<Category> {

    void remove(Long id);

}
