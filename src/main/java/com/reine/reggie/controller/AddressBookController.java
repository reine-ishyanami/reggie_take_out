package com.reine.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.reine.reggie.common.BaseContext;
import com.reine.reggie.common.Result;
import com.reine.reggie.entity.AddressBook;
import com.reine.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author reine
 * @since 2022/4/15 19:17
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 保存地址
     *
     * @param addressBook 地址信息
     * @return 地址信息
     */
    @PutMapping
    @PostMapping
    public Result<AddressBook> save(@RequestBody AddressBook addressBook) {
        if (addressBook.getId() != null) {
            addressBookService.updateById(addressBook);
        } else {
            addressBook.setUserId(BaseContext.getCurrentId());
            log.info("addressBook:{}", addressBook);
            addressBookService.save(addressBook);
        }
        return Result.success(addressBook);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook 地址信息
     * @return 地址信息
     */
    @PutMapping("/default")
    public Result<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        LambdaUpdateWrapper<AddressBook> updateWrapper = Wrappers.lambdaUpdate(AddressBook.class);
        updateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        updateWrapper.set(AddressBook::getIsDefault, 0);

        addressBookService.update(updateWrapper);

        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return Result.success(addressBook);
    }

    /**
     * 根据id查询地址信息
     *
     * @param id 地址id
     * @return 用户地址信息
     */
    @GetMapping("/{id}")
    public Result<AddressBook> get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return Result.success(addressBook);
        } else {
            return Result.error("没有找到该对象");
        }
    }

    /**
     * 删除地址
     *
     * @param ids 地址id
     * @return 删除成功信息
     */
    @DeleteMapping
    public Result<String> delete(Long ids) {
        addressBookService.removeById(ids);
        return Result.success("删除地址成功");
    }

    /**
     * 查询默认地址
     *
     * @return 默认地址
     */
    @GetMapping("/default")
    public Result<AddressBook> getDefault() {

        LambdaQueryWrapper<AddressBook> queryWrapper = Wrappers.lambdaQuery(AddressBook.class);
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (addressBook != null) {
            return Result.success(addressBook);
        } else {
            return Result.error("没有找到该对象");
        }
    }

    /**
     * 查询指定用户的全部地址
     *
     * @param addressBook 用户地址
     * @return 用户全部地址
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        LambdaQueryWrapper<AddressBook> queryWrapper = Wrappers.lambdaQuery(AddressBook.class);
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        return Result.success(addressBookService.list(queryWrapper));
    }

}
