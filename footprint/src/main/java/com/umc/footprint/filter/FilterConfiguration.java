package com.umc.footprint.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FilterConfiguration implements WebMvcConfigurer {

    public FilterRegistrationBean<DecodingFilter> decodingFilterRegistrationBean(){
        FilterRegistrationBean<DecodingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new DecodingFilter());
        registrationBean.addUrlPatterns("/walk/");
    }
}
