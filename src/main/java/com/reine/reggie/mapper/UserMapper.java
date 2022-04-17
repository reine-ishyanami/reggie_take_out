package com.reine.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reine.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author reine
 * @since 2022/4/15 13:24
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
