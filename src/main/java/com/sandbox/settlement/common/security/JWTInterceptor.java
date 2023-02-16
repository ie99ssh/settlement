package com.sandbox.settlement.common.security;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sandbox.settlement.admin.entity.AdminMenuAccessLog;
import com.sandbox.settlement.admin.service.AdminMenuAccessLogService;
import com.sandbox.settlement.common.annotation.AuthMethod;
import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.common.util.CommonUtil;
import com.sandbox.settlement.login.dto.Login;
import com.sandbox.settlement.menu.dto.AccessMenuDto;
import com.sandbox.settlement.menu.dto.AdminMenuDto;
import com.sandbox.settlement.menu.service.AdminMenuService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**--------------------------------------------------------------------
 * ■JWT 인터셉터 map의 Setter ■sangheon
 --------------------------------------------------------------------**/
@Component
@RequiredArgsConstructor
public class JWTInterceptor implements AsyncHandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(JWTInterceptor.class);

    private static final String LOGIN_URI = "/login";

    @Autowired
    private AdminMenuService adminMenuService;

    @Autowired
    private AdminMenuAccessLogService adminMenuAccessLogService;

    @Autowired
    private CommonUtil commonUtil;

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, Exception ex) throws Exception {
        // Nothing To Do
    }

    /**--------------------------------------------------------------------
     * ■PreHandle (주 처리 부) ■sangheon
     --------------------------------------------------------------------**/
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        Login login ;
        AccessMenuDto accessMenu;
        List<AdminMenuDto> menuList;

        String strRequestURI = httpServletRequest.getRequestURI();

        // 로그인 시도하는 경우, 로그를 남김
        if("/login/loginProc".equalsIgnoreCase(strRequestURI)) {
            insertLog(null, httpServletRequest, object, null);
            return true;
        }

        // 공통 프로세스 컨트롤러 호출 시 권한 확인 하지 않음
        if(strRequestURI.startsWith(String.format("/%s",GlobalConstants.PATH_COMMON))) {
            return true;
        }

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) httpServletRequest.getSession().getAttribute(GlobalConstants.SESSION_LOGIN_KEY));
            ObjectInputStream    ois  = new ObjectInputStream(bais);

            login = (Login) ois.readObject();
        } catch (Exception ex) {
            commonUtil.globalExceptionHandle(ex, logger);

            return redirectResponse(httpServletResponse);
        }

        if(ObjectUtils.isEmpty(login)) {
            return redirectResponse(httpServletResponse);
        }

        // 접속 메뉴 정보 획득
        accessMenu = getAccessMenu(login, strRequestURI);

        if (ObjectUtils.isEmpty(accessMenu)) {
            httpServletResponse.setStatus(401);
            return false;
        }

        // 메뉴 리스트 조회
        menuList = adminMenuService.getMenuList(login.getAdminId());

        // 접근 메뉴 권한 확인
        if(!checkAuth(object, accessMenu)) {
            httpServletResponse.setStatus(401);
            return false;
        }

        httpServletRequest.getSession().setAttribute(GlobalConstants.SESSION_CI_FIELD_FLAG, accessMenu.getCiReadFlag());

        // 관리자 메뉴 접근 로그 등록
        insertLog(accessMenu, httpServletRequest, object, login);

        // Request 정보 주입
        setRequestAttribute(httpServletRequest, accessMenu, login, menuList);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, ModelAndView objModalAndView) throws Exception {
        // Nothing To Do
    }

    /**--------------------------------------------------------------------
     * ■응답 경로 리다이렉션 모듈 ■sangheon
     --------------------------------------------------------------------**/
    private boolean redirectResponse(HttpServletResponse httpServletResponse) {
        try {
            httpServletResponse.sendRedirect(JWTInterceptor.LOGIN_URI);
        } catch (IOException ex) {
            commonUtil.globalExceptionHandle(ex, logger);
        }

        return false;
    }

    /**--------------------------------------------------------------------
     * ■유효 권한 확인 모듈 ■sangheon
     --------------------------------------------------------------------**/
    private boolean checkAuth(Object object,  AccessMenuDto accessMenu) {

        AuthMethod authMethod = null;

        if (object instanceof HandlerMethod) {
            authMethod = ((HandlerMethod) object).getMethodAnnotation(AuthMethod.class);
        }

        if (ObjectUtils.isEmpty(authMethod)) {
            return true;
        }

        int authVal = (authMethod.hasAuth()).getIntVal();
        int accessVal = accessMenu.getAuthCode();

        return accessVal <= authVal;
    }

    /**--------------------------------------------------------------------
     * ■접속 메뉴 정보 획득 모듈 ■sangheon
     --------------------------------------------------------------------**/
    private  AccessMenuDto getAccessMenu(Login login, String strRequestURI) {
         AccessMenuDto accessMenu = new  AccessMenuDto();

        accessMenu.setAdminId(login.getAdminId());
        accessMenu.setMenuDivSegment(getMenuSgmt(strRequestURI));

        return adminMenuService.getAccessMenu(accessMenu);
    }

    /**--------------------------------------------------------------------
     * ■호출 함수 정보 획득 모듈 ■sangheon
     --------------------------------------------------------------------**/
    private String getCallMethod(Object obj){

        String strMethodName = null;

        if (obj instanceof HandlerMethod) {
            strMethodName = ((HandlerMethod)obj).getMethod().getName();
        }
        return strMethodName;
    }

    /**--------------------------------------------------------------------
     * ■메뉴 Segment 정보 획득 모듈 ■sangheon
     --------------------------------------------------------------------**/
    private String getMenuSgmt(String strRequestURI){
        String[] arrMenuSgmt = strRequestURI.split("/");
        String strRet = null;

        if (arrMenuSgmt.length == 2) {
            strRet = String.format("/%s", arrMenuSgmt[1]);
        } else if (arrMenuSgmt.length > 2) {
            strRet = String.format("/%s/%s", arrMenuSgmt[1], arrMenuSgmt[2]);
        }
        return strRet;
    }

    /**--------------------------------------------------------------------
     * ■파라미터 정보 획득 모듈 ■sangheon
     --------------------------------------------------------------------**/
    private String getParameters(HttpServletRequest httpServletRequest) {
        String strParams  = "";
        String strRequest = "";

        StringBuilder strTemp = new StringBuilder();

        try {
            strRequest = IOUtils.toString(httpServletRequest.getInputStream(), "UTF-8");
        } catch (IOException ex) {
            commonUtil.globalExceptionHandle(ex, logger);

            return strParams;
        }

        if(StringUtils.isNotEmpty(strRequest) && !(httpServletRequest instanceof MultipartHttpServletRequest)) {
            try {
                Gson gson = new Gson();
                Map<String, Object> map = gson.fromJson(strRequest, new TypeToken<Map<String, Object>>(){}.getType());

                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String strKey   = entry.getKey();
                    String strValue = entry.getValue().toString();

                    if(strKey.contains("pwd")||strKey.contains("Password")||strKey.contains("Pwd")) {
                        strValue = "******";
                    } else if(strValue.length() > 100) {
                        strValue = strValue.substring(0, 100) + "...";
                    }

                    if(StringUtils.isNotEmpty(strTemp)) {
                        strTemp.append("|");
                    }

                    strTemp.append(String.format("%s=%s", strKey, strValue));
                }
            } catch (Exception ex) {
                commonUtil.globalExceptionHandle(ex, logger);

                strTemp.append(strRequest);
            }
        }

        return createParams(strTemp);
    }

    /**--------------------------------------------------------------------
     * ■파라메터 정보 생성 모듈 ■sangheon
     --------------------------------------------------------------------**/
    private String createParams(StringBuilder strTemp) {
        if(strTemp.length() > 0 && strTemp.length() <= 1000) {
            return strTemp.substring(0, strTemp.length());
        } else if(strTemp.length() > 1000) {
            return strTemp.substring(0, 1000);
        } else {
            return "";
        }
    }

    /**--------------------------------------------------------------------
     * ■관리자 메뉴 접근 로그 등록 ■sangheon
     --------------------------------------------------------------------**/
    private void insertLog(AccessMenuDto accessMenu, HttpServletRequest httpServletRequest, Object object, Login login) {
        String strMethodName = getCallMethod(object);

        if (strMethodName == null) {
            strMethodName = httpServletRequest.getRequestURI();
        }

        AdminMenuAccessLog adminMenuAccessLog = new AdminMenuAccessLog();

        if(strMethodName.contains("loginProc")) {
            adminMenuAccessLog.setMenuNo(0);
            adminMenuAccessLog.setMenuLink(httpServletRequest.getRequestURI());
            adminMenuAccessLog.setMethodName(strMethodName);
            adminMenuAccessLog.setLogDesc(getParameters(httpServletRequest));
            adminMenuAccessLog.setAdminNo(0);
        } else {
            adminMenuAccessLog.setMenuNo(accessMenu.getMenuNo());
            adminMenuAccessLog.setMenuLink(httpServletRequest.getRequestURI());
            adminMenuAccessLog.setMethodName(strMethodName);
            adminMenuAccessLog.setLogDesc(getParameters(httpServletRequest));
            adminMenuAccessLog.setAdminNo(login.getAdminNo());
            adminMenuAccessLog.setRegId(login.getAdminId());

        }
        adminMenuAccessLog.setIpAddr(commonUtil.getIpAddr(httpServletRequest));
        adminMenuAccessLogService.saveAdminMenuAccessLog(adminMenuAccessLog);
    }

    /**--------------------------------------------------------------------
     * ■Request 객체 Attribute 정보 주입 모듈 ■sangheon
     --------------------------------------------------------------------**/
    private void setRequestAttribute(HttpServletRequest httpServletRequest,  AccessMenuDto accessMenu
            , Login login, List<AdminMenuDto> menuList) {
        Date dtToYMD = new Date();

        Calendar calFromYMD       = Calendar.getInstance();
        Calendar calDefaultYM     = Calendar.getInstance();
        Calendar calDefaultFromYM = Calendar.getInstance();
        Calendar calDefaultToYM   = Calendar.getInstance();

        calFromYMD.setTime(dtToYMD);
        calFromYMD.add(Calendar.DATE, -7);

        //정산월의 경우 기본적으로 한달 전 세팅
        calDefaultYM.setTime(dtToYMD);
        calDefaultYM.add(Calendar.MONTH, -1);

        // 통계 데이터 조회 시 6개월 전 ~ 1개월 전 으로 세팅
        calDefaultFromYM.setTime(dtToYMD);
        calDefaultFromYM.add(Calendar.MONTH, -6);
        calDefaultToYM.setTime(dtToYMD);
        calDefaultToYM.add(Calendar.MONTH, -1);

        httpServletRequest.setAttribute("menuGroupNo",   accessMenu.getMenuGroupNo());
        httpServletRequest.setAttribute("menuGroupName", accessMenu.getMenuGroupName());
        httpServletRequest.setAttribute("menuNo",        accessMenu.getMenuNo());
        httpServletRequest.setAttribute("menuName",      accessMenu.getMenuName());
        httpServletRequest.setAttribute("authCode",      accessMenu.getAuthCode());
        httpServletRequest.setAttribute("ciReadFlag",    accessMenu.getCiReadFlag());
        httpServletRequest.setAttribute("dnAvailFlag",   accessMenu.getDnAvailFlag());

        httpServletRequest.setAttribute("adminNo",       login.getAdminNo());
        httpServletRequest.setAttribute("adminId",       login.getAdminId());
        httpServletRequest.setAttribute("strIPAddr",     login.getIpAddr());

        httpServletRequest.setAttribute("menuList",      menuList);

        httpServletRequest.setAttribute("strFromYMD",       new SimpleDateFormat("yyyy-MM-dd").format(calFromYMD.getTime()));
        httpServletRequest.setAttribute("strToYMD",         new SimpleDateFormat("yyyy-MM-dd").format(dtToYMD));
        httpServletRequest.setAttribute("strDefaultYM",     new SimpleDateFormat("yyyy.MM").format(calDefaultYM.getTime()));

        // 한달 전 세팅 (정산일자 검색조건에서 사용)
        httpServletRequest.setAttribute("strDefaultYMD",    new SimpleDateFormat("yyyy-MM-dd").format(calDefaultYM.getTime()));

        // 통계 조회 시 세팅
        httpServletRequest.setAttribute("strFromYM",        new SimpleDateFormat("yyyy.MM").format(calDefaultFromYM.getTime()));
        httpServletRequest.setAttribute("strToYM",          new SimpleDateFormat("yyyy.MM").format(calDefaultToYM.getTime()));
    }
}
