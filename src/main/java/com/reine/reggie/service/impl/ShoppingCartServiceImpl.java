package com.reine.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reine.reggie.entity.ShoppingCart;
import com.reine.reggie.mapper.ShoppingCartMapper;
import com.reine.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author reine
 * @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
 * @createDate 2022-04-15 21:27:28
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements ShoppingCartService {

}




