package com.umc.footprint.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class DecodingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(DecodingFilter.class);

    @Override public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Start Decoding"); Filter.super.init(filterConfig);
    }

    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        logger.info("Request URI: {}", req.getRequestURL());
        chain.doFilter(request, response);
        logger.info("Return URI: {}", req.getRequestURL());
    }

    @Override public void destroy() {
        logger.info("End Decoding");
        Filter.super.destroy();
    }

}
