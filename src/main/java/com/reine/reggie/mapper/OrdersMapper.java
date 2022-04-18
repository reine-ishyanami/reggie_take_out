package com.reine.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reine.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author reine
 * @description 针对表【orders(订单表)】的数据库操作Mapper
 * @createDate 2022-04-16 08:04:36
 * @Entity generator.entity.Orders
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}




