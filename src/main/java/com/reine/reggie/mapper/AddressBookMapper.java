package com.reine.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reine.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author reine
 * @description 针对表【address_book(地址管理)】的数据库操作Mapper
 * @createDate 2022-04-15 19:14:52
 * @Entity generator.entity.AddressBook
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {

}




