package com.reine.reggie.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果，服务端响应的数据都会封装成此对象
 *
 * @author reine
 * @since 2022/4/13 12:13
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 返回码
     */
    private Integer code;
    /**
     * 返回错误信息
     */
    private String msg;
    /**
     * 返回数据
     */
    private T data;

    /**
     * 动态数据
     */
    private Map map = new HashMap();

    public static <T> Result<T> success(T object) {
        Result<T> r = new Result<T>();
        r.data = object;
        r.code = 200;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<T>();
        r.msg = msg;
        r.code = 404;
        return r;
    }

    public Result<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
