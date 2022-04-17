package com.reine.reggie.dto;

import com.reine.reggie.entity.OrderDetail;
import com.reine.reggie.entity.Orders;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author reine
 * @since 2022/4/16 9:57
 */
@Data
public class OrderDto extends Orders {

    /**
     * 订单对应的商品数据
     */
    private List<OrderDetail> orderDetails = new ArrayList<>();

    private String userName;
}
