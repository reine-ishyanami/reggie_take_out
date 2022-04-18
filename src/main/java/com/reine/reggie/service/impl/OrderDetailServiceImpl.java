package com.reine.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reine.reggie.entity.OrderDetail;
import com.reine.reggie.mapper.OrderDetailMapper;
import com.reine.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author reine
 * @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
 * @createDate 2022-04-16 08:04:36
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
        implements OrderDetailService {

}




