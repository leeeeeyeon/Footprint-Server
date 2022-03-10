package com.umc.footprint.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.footprint.config.EncryptProperties;
import com.umc.footprint.src.users.model.PatchUserGoalReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class DecodingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(DecodingFilter.class);
    private final EncryptProperties encryptProperties;

    public DecodingFilter(EncryptProperties encryptProperties){
        this.encryptProperties = encryptProperties;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Start Decoding");
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        try{
            logger.info("Request URI: {}", req.getRequestURL());

            RequestBodyDecryptWrapper requestWrapper = new RequestBodyDecryptWrapper(req, encryptProperties);

            chain.doFilter(requestWrapper, response);

            logger.info("Return URI: {}", req.getRequestURL());
        } catch (Exception exception){
            logger.error("디코딩이 불가합니다.");
        }

    }

    @Override
    public void destroy() {
        logger.info("End Decoding");
        Filter.super.destroy();
    }

}
