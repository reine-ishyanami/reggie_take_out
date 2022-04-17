package com.reine.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reine.reggie.common.Result;
import com.reine.reggie.entity.Employee;
import com.reine.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author reine
 * @since 2022/4/13 12:10
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request  前端请求
     * @param employee 登录用户数据
     * @return 登录成功或失败信息
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = Wrappers.lambdaQuery(Employee.class);
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        // 如果没有查询到返回登录失败结果
        if (emp == null) {
            return Result.error("用户名不存在");
        }
        // 密码比对
        if (!emp.getPassword().equals(password)) {
            return Result.error("密码错误");
        }
        // 查看状态
        if (emp.getStatus() == 0) {
            return Result.error("账号已禁用");
        }
        // 登录成功
        request.getSession().setAttribute("employee", emp.getId());
        return Result.success(emp);
    }

    /**
     * 员工退出
     *
     * @return 退出成功信息
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        // 清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    /**
     * 新增员工
     *
     * @param employee 员工属性
     * @return 新增员工成功信息
     */
    @PostMapping
    public Result<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());
        // 获得当前用户id
        // Long employeeId = (Long) request.getSession().getAttribute("employee");
        // employee.setCreateUser(employeeId);
        // employee.setUpdateUser(employeeId);
        employeeService.save(employee);
        return Result.success("新增员工成功");

    }

    /**
     * 员工信息分页查询
     *
     * @param page     页码
     * @param pageSize 分页条数
     * @param name     搜索的用户姓名
     * @return 查询的员工列表数据
     */
    @GetMapping("/page")
    public Result<Page<Employee>> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);
        // 构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = Wrappers.lambdaQuery(Employee.class);
        // 添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     *
     * @param employee 员工信息
     * @return 员工信息修改成功信息
     */
    @PutMapping
    public Result<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());
        long id = Thread.currentThread().getId();
        log.info("线程id为{}", id);
        employee.setUpdateTime(LocalDateTime.now());
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return Result.success("员工信息修改成功");
    }

    /**
     * 根据id获取员工信息
     *
     * @param id 员工id
     * @return 员工信息或查询失败信息
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return Result.success(employee);
        }
        return Result.error("没有查询到员工信息");
    }
}
