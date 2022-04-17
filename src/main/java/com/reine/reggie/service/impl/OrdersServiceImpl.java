package com.reine.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reine.reggie.common.BaseContext;
import com.reine.reggie.common.CustomException;
import com.reine.reggie.dto.OrderDto;
import com.reine.reggie.entity.*;
import com.reine.reggie.mapper.OrdersMapper;
import com.reine.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 86158
 * @description 针对表【orders(订单表)】的数据库操作Service实现
 * @createDate 2022-04-16 08:04:36
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
        implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     *
     * @param orders 订单
     */
    @Transactional
    @Override
    public void submit(OrderDto orders) {
        // 获取当前用户id
        Long userId = BaseContext.getCurrentId();
        // 查询当前用户购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = Wrappers.lambdaQuery(ShoppingCart.class);
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        if (orders.getOrderDetails() != null && orders.getOrderDetails().size() != 0) {
            Long orderId = IdWorker.getId();
            orders.setNumber(String.valueOf(orderId));
            orders.setId(orderId);
            // 设置下单时间和结账时间
            orders.setOrderTime(LocalDateTime.now());
            orders.setCheckoutTime(LocalDateTime.now());
            // 待派送
            orders.setStatus(2);
            List<OrderDetail> orderDetails = orders.getOrderDetails().stream().peek(item -> {
                // 设置新订单id
                item.setOrderId(orders.getId());
                // 重置订单明细详情表的主键
                item.setId(IdWorker.getId());
            }).collect(Collectors.toList());
            orders.setOrderDetails(orderDetails);
            super.save(orders);
            orderDetailService.saveBatch(orders.getOrderDetails());
        } else {
            List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
            if (shoppingCarts == null || shoppingCarts.size() == 0) {
                throw new CustomException("购物车为空，不能下单");
            }
            // 查询用户数据
            User user = userService.getById(userId);
            // 查询地址数据
            Long addressBookId = orders.getAddressBookId();
            AddressBook addressBook = addressBookService.getById(addressBookId);
            if (addressBook == null) {
                throw new CustomException("用户地址信息有误，不能下单");
            }
            // 向订单表插入一条数据
            // 设置下单时间和结账时间
            orders.setOrderTime(LocalDateTime.now());
            orders.setCheckoutTime(LocalDateTime.now());
            // 待派送
            orders.setStatus(2);
            // 设置订单号
            Long orderId = IdWorker.getId();
            orders.setNumber(String.valueOf(orderId));
            orders.setId(orderId);
            // 设置总金额
            AtomicInteger amount = new AtomicInteger(0);
            List<OrderDetail> orderDetails = shoppingCarts.stream().map(item -> {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderId(orders.getId());
                orderDetail.setNumber(item.getNumber());
                orderDetail.setDishFlavor(item.getDishFlavor());
                orderDetail.setDishId(item.getDishId());
                orderDetail.setSetmealId(item.getSetmealId());
                orderDetail.setName(item.getName());
                orderDetail.setSetmealId(item.getSetmealId());
                orderDetail.setImage(item.getImage());
                orderDetail.setAmount(item.getAmount());
                // 获取当前菜品的总价并累加到订单金额
                amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
                return orderDetail;
            }).collect(Collectors.toList());
            orders.setAmount(new BigDecimal(amount.get()));
            orders.setUserId(user.getId());
            orders.setUserName(user.getName());
            orders.setConsignee(addressBook.getConsignee());
            orders.setPhone(addressBook.getPhone());
            orders.setAddress(
                    (addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName()) +
                            (addressBook.getCityName() == null ? "" : addressBook.getCityName()) +
                            (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName()) +
                            (addressBook.getDetail() == null ? "" : addressBook.getDetail())
            );
            super.save(orders);
            // 向订单明细表插入多条数据
            orderDetailService.saveBatch(orderDetails);
            // 清空购物车
            shoppingCartService.remove(queryWrapper);
        }

    }

}




