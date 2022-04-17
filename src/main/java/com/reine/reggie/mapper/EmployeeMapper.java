package com.reine.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reine.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author reine
 * @since 2022/4/13 12:06
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
