package com.reine.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reine.reggie.common.BaseContext;
import com.reine.reggie.common.Result;
import com.reine.reggie.dto.OrderDto;
import com.reine.reggie.entity.OrderDetail;
import com.reine.reggie.entity.Orders;
import com.reine.reggie.entity.User;
import com.reine.reggie.service.OrderDetailService;
import com.reine.reggie.service.OrdersService;
import com.reine.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author reine
 * @since 2022/4/16 8:07
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    /**
     * 用户下单
     *
     * @param orders 订单
     * @return 成功信息
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody OrderDto orders) {
        log.info("订单数据:{}", orders);
        ordersService.submit(orders);
        return Result.success("下单成功");
    }

    /**
     * 用户查询历史订单信息
     *
     * @param page     页码
     * @param pageSize 每页条数
     * @return 订单列表信息
     */
    @GetMapping("/userPage")
    public Result<Page> userPage(int page, int pageSize) {
        // 构建分页构造器对象
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> orderDtoPage = new Page<>();
        // 查询条件
        LambdaQueryWrapper<Orders> queryWrapper = Wrappers.lambdaQuery(Orders.class);
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo, queryWrapper);
        // 复制属性
        BeanUtils.copyProperties(pageInfo, orderDtoPage, "records");
        List<Orders> records = pageInfo.getRecords();
        log.info("record:{}", records);
        List<OrderDto> list = records.stream().map(item -> {
            // 包含订单菜品列表的订单信息
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);
            Long orderId = item.getId();
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = Wrappers.lambdaQuery(OrderDetail.class);
            lambdaQueryWrapper.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> orderDetailList = orderDetailService.list(lambdaQueryWrapper);
            orderDto.setOrderDetails(orderDetailList);
            return orderDto;
        }).collect(Collectors.toList());
        orderDtoPage.setRecords(list);
        return Result.success(orderDtoPage);
    }

    /**
     * 再来一单
     *
     * @param order 订单编号
     * @return 下单成功信息
     */
    @PostMapping("/again")
    public Result<String> again(@RequestBody Orders order) {
        Long id = order.getId();
        Orders orders = ordersService.getById(id);
        log.info("orders:{}", orders);
        LambdaQueryWrapper<OrderDetail> queryWrapper = Wrappers.lambdaQuery(OrderDetail.class);
        queryWrapper.eq(OrderDetail::getOrderId, id);
        List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper);
        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(orders, orderDto);
        orderDto.setOrderDetails(orderDetails);
        log.info("orderDto:{}", orderDto);
        this.submit(orderDto);
        return Result.success("下单成功");
    }

    /**
     * 订单分页查询
     *
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> orderDtoPage = new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = Wrappers.lambdaQuery(Orders.class);
        queryWrapper.eq(number != null, Orders::getNumber, number);
        queryWrapper.between((beginTime != null && endTime != null), Orders::getOrderTime, beginTime, endTime);
        ordersService.page(pageInfo, queryWrapper);
        log.info("pageInfo:{}", pageInfo);
        // 拷贝属性
        BeanUtils.copyProperties(pageInfo, orderDtoPage);

        List<OrderDto> orderDtoList = pageInfo.getRecords().stream().map(item -> {
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);
            Long userId = orderDto.getUserId();
            User user = userService.getById(userId);
            orderDto.setUserName(user.getName());
            return orderDto;
        }).collect(Collectors.toList());
        orderDtoPage.setRecords(orderDtoList);

        return Result.success(orderDtoPage);
    }

    /**
     * 修改订单状态
     *
     * @param map 订单编号与订单状态位
     * @return 成功信息
     */
    @PutMapping
    public Result<String> delivery(@RequestBody Map map) {
        Long id = Long.parseLong(map.get("id").toString());
        Integer status = Integer.parseInt(map.get("status").toString());
        log.info("订单编号：{}，状态：{}", id, status);

        LambdaUpdateWrapper<Orders> updateWrapper = Wrappers.lambdaUpdate(Orders.class);
        updateWrapper.eq(Orders::getId, id);
        updateWrapper.set(Orders::getStatus, status);
        ordersService.update(updateWrapper);

        return Result.success("订单已派送");
    }
}
