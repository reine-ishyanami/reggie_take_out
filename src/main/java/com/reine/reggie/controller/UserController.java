package com.reine.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.reine.reggie.common.Result;
import com.reine.reggie.entity.User;
import com.reine.reggie.service.UserService;
import com.reine.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author reine
 * @since 2022/4/15 13:27
 */
@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 模拟发送手机短信验证码
     *
     * @param user 用户手机号属性
     * @return 发送成功或失败
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user) {
        // 获取手机号
        String phone = user.getPhone();
        String code;
        if (StringUtils.isNotEmpty(phone)) {
            // 生成随机6位验证码
            code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("code={}", code);
            // 保存验证码到Session
            // session.setAttribute(phone, code);

            // 将生成的验证码缓存到redis中，设置过期时间为5min
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return Result.success(code);
        }
        return Result.error("验证码发送失败");
    }

    /**
     * 处理登录请求，校验验证码
     *
     * @param map     手机号，验证码
     * @param session session
     * @return 用户对象
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session) {
        log.info("map={}", map);

        // 获取手机号
        String phone = map.get("phone").toString();
        // 获取验证码
        String code = map.get("code").toString();
        // 从session中获取验证码
        // Object codeInSession = session.getAttribute(phone);

        // 从redis中获取缓存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        // 验证码校验
        if (codeInSession == null) {
            return Result.error("请先发送验证码");
        } else if (!codeInSession.equals(code)) {
            return Result.error("验证码错误");
        } else {
            // 登录成功
            // 判断当前手机号是否为新用户，是新用户自动注册
            LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class);
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                // 新用户自动注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                user.setSex("1");
                userService.save(user);
            }
            session.setAttribute("user", user.getId());

            // 用户登录成功，删除验证码
            redisTemplate.delete(phone);

            return Result.success(user);
        }
    }

    /**
     * 处理注销请求
     *
     * @param session session
     * @return 退出成功
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.removeAttribute("user");
        return Result.success("退出成功");
    }

    /**
     * 更新用户信息
     *
     * @param map     用户信息
     * @param session session
     * @return 用户信息
     */
    @PostMapping("/update")
    public Result<User> update(@RequestBody Map map, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("user").toString());

        log.info("用户id：{}", userId);

        String userSex = (String) map.get("userSex");
        log.info("要把性别修改为：{}", userSex);
        String userName = (String) map.get("userName");
        log.info("要把姓名修改为：{}", userName);

        LambdaUpdateWrapper<User> updateWrapper = Wrappers.lambdaUpdate(User.class);
        updateWrapper.eq(User::getId, userId);
        updateWrapper.set(userSex != null, User::getSex, userSex);
        updateWrapper.set(userName != null, User::getName, userName);

        userService.update(updateWrapper);
        User user = userService.getById(userId);

        return Result.success(user);
    }
}
