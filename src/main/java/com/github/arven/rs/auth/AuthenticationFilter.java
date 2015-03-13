/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.arven.rs.auth;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author brian.becker
 */
@WebFilter(filterName = "AuthenticationFilter")
public class AuthenticationFilter implements Filter {
    
    @Override
    public void init(FilterConfig cfg) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            if(req.getHeader("Authorization") != null) {
                String s[] = new String(Base64.decodeBase64((req.getHeader("Authorization").split(" ")[1]))).split(":");
                req.login(s[0], s[1]);
            } else {
                req.login("anonymous", "anonymous");
            }
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
    
}
