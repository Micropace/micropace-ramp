package com.micropace.ramp.wechat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Restful 请求Filter，设置请求数据格式、跨域支持
 */
public class RequestFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(RequestFilter.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        res.setCharacterEncoding("utf8");
        res.setContentType("application/json;charset=utf8");
        //res.setContentType("");
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Authorization, Content-Type, Accept");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE, PUT");
        res.setHeader("Access-Control-Max-Age", "3600");

        // 打印请求信息
        logRequest(req);

        // 拦截请求
        if ("/".equals(req.getServletPath())) {
            res.getWriter().write("RAMP API Server");
        }
        // 错误请求处理
        else if ("/error".equals(req.getServletPath())) {
            res.getWriter().write("Oops! Page not found!");
        } else {
            chain.doFilter(request, response);
        }
    }

    private void logRequest(HttpServletRequest request) {
        String req = request.getMethod() + " from " + request.getRemoteHost() + " path " + request.getServletPath();
        try {
            req += " param " + mapper.writeValueAsString(request.getParameterMap());
            logger.info(req);
        } catch (IOException ignored) {
        }
    }
}