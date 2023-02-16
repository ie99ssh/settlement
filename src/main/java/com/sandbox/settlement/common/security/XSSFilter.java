package com.sandbox.settlement.common.security;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**--------------------------------------------------------------------
 * ■XSS Filter (기본 한글) ■sangheon
 --------------------------------------------------------------------**/
public class XSSFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest objHttpServletRequest = (HttpServletRequest) servletRequest;
        XSSRequestWrapper  httpServletRequestWrapper = new XSSRequestWrapper(objHttpServletRequest);

        String strRequestURI = objHttpServletRequest.getRequestURI();

        // 예외 처리
        if("///".equalsIgnoreCase(strRequestURI)){
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            filterChain.doFilter(httpServletRequestWrapper, servletResponse);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing To Do
    }

    @Override
    public void destroy() {
        // Nothing To Do
    }
}
