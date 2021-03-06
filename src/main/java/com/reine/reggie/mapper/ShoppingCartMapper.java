package com.reine.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reine.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author reine
 * @description 针对表【shopping_cart(购物车)】的数据库操作Mapper
 * @createDate 2022-04-15 21:27:28
 * @Entity generator.entity.ShoppingCart
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

}




