package com.reine.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reine.reggie.entity.AddressBook;
import com.reine.reggie.mapper.AddressBookMapper;
import com.reine.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author reine
 * @description 针对表【address_book(地址管理)】的数据库操作Service实现
 * @createDate 2022-04-15 19:14:52
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
        implements AddressBookService {

}




