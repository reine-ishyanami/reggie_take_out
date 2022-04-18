package com.reine.reggie.common;

import lombok.extern.slf4j.Slf4j;

/**
 * 基于ThreadLocal封装的工具类，用于保持和获取当前登录用户id
 *
 * @author reine
 * @since 2022/4/14 8:09
 */
@Slf4j
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }
}
