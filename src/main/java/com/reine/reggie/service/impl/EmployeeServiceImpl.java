package com.reine.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reine.reggie.entity.Employee;
import com.reine.reggie.mapper.EmployeeMapper;
import com.reine.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author reine
 * @since 2022/4/13 12:08
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
