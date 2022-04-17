package com.reine.reggie.service;

import com.reine.reggie.dto.OrderDto;
import com.reine.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86158
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2022-04-16 08:04:36
*/
public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders 订单
     */
    void submit(OrderDto orders);

}
