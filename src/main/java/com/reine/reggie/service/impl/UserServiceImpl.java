package com.reine.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reine.reggie.entity.User;
import com.reine.reggie.mapper.UserMapper;
import com.reine.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author reine
 * @since 2022/4/15 13:25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
