package com.sandbox.settlement.common.resolver;

import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.common.domain.SessionInfo;
import com.sandbox.settlement.login.dto.Login;
import org.springframework.core.MethodParameter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Map;

/**--------------------------------------------------------------------
 * ■파라미터를 정의된 타입으로 변환하여 Controller에 전달하는 클래스 ■sangheon
 --------------------------------------------------------------------**/
public class ArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
            NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
        Class<?> parameterType = methodParameter.getParameterType();

        if (parameterType.equals(RequestParameterMap.class)) {
            RequestAdapter reqAdapter = new RequestAdapter();

            RequestParameterMap<String, Object> paramMap = new RequestParameterMap<>();

            paramMap.setMap((Map<String, Object>)reqAdapter.convert((HttpServletRequest) nativeWebRequest.getNativeRequest()));

            return paramMap;
        }

        SessionInfo sessionInfo = new SessionInfo();
        HttpSession httpSession = request.getSession();
        ByteArrayInputStream byteArrayInputStream
                = new ByteArrayInputStream((byte[]) httpSession.getAttribute(GlobalConstants.SESSION_LOGIN_KEY));
        ObjectInputStream objectInputStream  = new ObjectInputStream(byteArrayInputStream);
        Login login = (Login) objectInputStream.readObject();

        if (!ObjectUtils.isEmpty(login)) {
            String adminId = login.getAdminId();
            Integer adminNo = login.getAdminNo();
            sessionInfo.setAdminId(adminId);
            sessionInfo.setAdminNo(adminNo);
        }

        return sessionInfo;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        if (methodParameter.getParameterType().equals(RequestParameterMap.class)) {
            return RequestParameterMap.class.isAssignableFrom(methodParameter.getParameterType());
        }

        return SessionInfo.class.isAssignableFrom(methodParameter.getParameterType());
    }
}
