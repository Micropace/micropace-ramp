package com.micropace.ramp.wechat.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring HTTP请求过滤器注册
 *
 * @author Suffrajet
 */
@Component
public class FilterConfig {
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        //配置Restful请求过滤器
        RequestFilter filter = new RequestFilter();
        registrationBean.setFilter(filter);
        List<String> urlPatterns = new ArrayList<>();

        urlPatterns.add("/*");
        urlPatterns.add("/api/*");
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;
    }
}
