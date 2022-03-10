package com.umc.footprint.filter;

import com.umc.footprint.config.EncryptProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FilterConfiguration implements WebMvcConfigurer {
    private final EncryptProperties encryptProperties;

    public FilterConfiguration(EncryptProperties encryptProperties){
        this.encryptProperties = encryptProperties;
    }

    @Bean
    public FilterRegistrationBean<DecodingFilter> decodingFilterRegistrationBean(){
        FilterRegistrationBean<DecodingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new DecodingFilter(encryptProperties));
        registrationBean.addUrlPatterns("/walks/decrypt");

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<EncodingFilter> encodingFilterRegistrationBean(){
        FilterRegistrationBean<EncodingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new EncodingFilter(encryptProperties));
        registrationBean.addUrlPatterns("/walks/check/encrypt");

        return registrationBean;
    }

}
