package com.reine.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.reine.reggie.common.BaseContext;
import com.reine.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登录
 *
 * @author reine
 * @since 2022/4/13 14:15
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    /**
     * 路径匹配器
     */
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 获取本次请求的URI
        String requestURI = req.getRequestURI();

        // 不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        // 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        // 不需要处理
        if (check) {
            log.info("不需要处理：{}", req.getRequestURI());
            chain.doFilter(request, response);
            return;
        }

        // 判断管理页面用户登录状态，已登录，放行
        if (req.getSession().getAttribute("employee") != null) {
            log.info("已登录，用户为{}，放行：{}", req.getSession().getAttribute("employee"), req.getRequestURI());
            Long empId = (Long) req.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            chain.doFilter(request, response);
            return;
        }

        // 判断移动端用户登录状态，已登录，放行
        if (req.getSession().getAttribute("user") != null) {
            log.info("已登录，用户为{}，放行：{}", req.getSession().getAttribute("user"), req.getRequestURI());
            Long userId = (Long) req.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            chain.doFilter(request, response);
            return;
        }

        // 未登录，通过输出流方式向客户端响应数据
        log.info("未登录：{}", req.getRequestURI());
        resp.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));

        return;

    }

    /**
     * 路径匹配，判断本次请求是否放行
     *
     * @param urls
     * @param requestURI
     * @return
     */
    private boolean check(String[] urls, String requestURI) {
        log.info("请求的request是:{}", requestURI);
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }

}
