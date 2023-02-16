package com.sandbox.settlement.common.security;

import com.sandbox.settlement.common.constants.GlobalConstants;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginCheckInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{

        HttpSession session = request.getSession();

        Object loginSession = session.getAttribute(GlobalConstants.SESSION_LOGIN_KEY);

        if(ObjectUtils.isEmpty(loginSession)){

            String strContentType = StringUtils.isEmpty(request.getHeader(GlobalConstants.CONTENT_TYPE)) ?
                    request.getHeader(GlobalConstants.ACCEPT).replaceAll(" ", "")
                    : request.getHeader(GlobalConstants.CONTENT_TYPE).replaceAll(" ", "");

            if(!StringUtils.isEmpty(strContentType) && (strContentType.startsWith(MediaType.APPLICATION_JSON_VALUE))) {
                response.setStatus(GlobalConstants.SESSION_EXPIRE_CODE);
            } else {
                response.sendRedirect("/");
                return false;
            }
        }
        return true;
    }
}
