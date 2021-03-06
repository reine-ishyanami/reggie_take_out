package com.reine.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reine.reggie.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author reine
 * @description 针对表【order_detail(订单明细表)】的数据库操作Mapper
 * @createDate 2022-04-16 08:04:36
 * @Entity generator.entity.OrderDetail
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

}




