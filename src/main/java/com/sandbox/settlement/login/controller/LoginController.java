package com.sandbox.settlement.login.controller;

import com.sandbox.settlement.admin.service.AdministratorService;
import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.common.domain.BaseRes;
import com.sandbox.settlement.common.exception.GlobalException;
import com.sandbox.settlement.common.util.CommonUtil;
import com.sandbox.settlement.login.dto.Login;
import com.sandbox.settlement.login.service.LoginService;
import com.sandbox.settlement.menu.dto.AdminMenuDto;
import com.sandbox.settlement.menu.service.AdminMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**--------------------------------------------------------------------
 * ■로그인 컨트롤러 ■sangheon
 --------------------------------------------------------------------**/
@Controller
@RequestMapping(value = {"", "/login"})
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final AdministratorService administratorService;
    private final AdminMenuService adminMenuService;
    private final CommonUtil commonUtil;

    /**--------------------------------------------------------------------
     * ■로그인 페이지 진입 함수 ■sangheon
     --------------------------------------------------------------------**/
    @RequestMapping(value = {"", "/", "/login"})
    public ModelAndView initPage(HttpServletResponse httpServletResponse, HttpSession httpSession
            , @RequestParam(value="intAdminID", defaultValue = "") String adminId) throws Exception {
        String strInitMenuLink = GlobalConstants.PATH_LOGIN;

        // 로그인 세션이 남아 있을 때 초기 페이지로 리다이렉트
        if(null != httpSession && null != httpSession.getAttribute(GlobalConstants.SESSION_LOGIN_KEY)) {
            ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) httpSession.getAttribute(GlobalConstants.SESSION_LOGIN_KEY));
            ObjectInputStream    ois  = new ObjectInputStream(bais);

            // 세션 로그인 정보
            Login login = (Login) ois.readObject();

            // 세션에 남아있는 관리자와 비밀번호 변경 대상 관리자 비교
            String strSessionAdminID = login.getAdminId();
            if(!"".equals(adminId) && strSessionAdminID.equals(adminId)) {
                // 관리자 로그인 시 초기 페이지 셋팅 (접근 권한을 가지고 있는 메뉴 1개)
                AdminMenuDto adminMenuDto = adminMenuService.getMenuLinkByLoginAction(login.getAdminId());
                if(!ObjectUtils.isEmpty(adminMenuDto)) {
                    strInitMenuLink = adminMenuDto.getMenuLink();
                    httpServletResponse.sendRedirect(strInitMenuLink);
                }
            }
        }

        ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
        mav.setViewName(strInitMenuLink);
        mav.addObject("HIDDEN_CSRF_ID", GlobalConstants.HIDDEN_CSRF_ID);

        return mav;
    }

    /**--------------------------------------------------------------------
     * ■로그아웃 처리 함수 ■sangheon
     --------------------------------------------------------------------**/
    @RequestMapping(value = "/logout")
    public void logOut(HttpServletResponse httpServletResponse, HttpSession httpSession) throws Exception {
        httpSession.invalidate();

        httpServletResponse.sendRedirect("/");
    }

    /**--------------------------------------------------------------------
     * ■CSRF Token 체크 함수 ■sangheon
     --------------------------------------------------------------------**/
    @RequestMapping(value = "/getCSRFToken")
    public ModelAndView getCSRFToken(HttpSession httpSession) {
        ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());

        mav.addObject("CSRFToken", null != httpSession.getAttribute(GlobalConstants.SESSION_CSRF_KEY) ? httpSession.getAttribute(GlobalConstants.SESSION_CSRF_KEY) : "");

        return mav;
    }

    /**--------------------------------------------------------------------
     * ■로그인 처리 함수 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @PostMapping(value = "/loginProc")
    public ModelAndView loginProc(HttpServletRequest httpServletRequest, HttpSession httpSession,
                                  @Valid @RequestBody Login login) {
        ModelAndView objMV = new ModelAndView(new MappingJackson2JsonView());
        Login objRetLogin = null;

        int intResultCode     = GlobalConstants.COMMON_SUCCEED_CODE;
        String  strResultMessage  = "";

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream    oos  = new ObjectOutputStream(baos);

            login.setIpAddr(commonUtil.getIpAddr(httpServletRequest));

            // 로그인 처리
            objRetLogin = loginService.loginProc(login);

            oos.writeObject(objRetLogin);

            httpSession.setAttribute(GlobalConstants.SESSION_LOGIN_KEY, baos.toByteArray());
        } catch (Exception ex) {
            BaseRes baseModelRes = commonUtil.setCatchResult(ex);

            intResultCode = baseModelRes.getIntRetCode();
            strResultMessage  = baseModelRes.getStrRetMsg();
        } finally {
            objMV = commonUtil.getResJsonView(objRetLogin, intResultCode, strResultMessage);
        }

        return objMV;
    }

    /**--------------------------------------------------------------------
     * ■비밀번호 초기화 페이지 진입 함수 (메일에서의 진입) ■sangheon
     --------------------------------------------------------------------**/
    @RequestMapping(value = "/resetPwdRequest")
    public String resetPwdRequest(Model model, @RequestParam(value="token", defaultValue = "") String token) {
        String strReturnUrl = "";

        try {
            String adminId = administratorService.checkResetPwdRequest(token);

            model.addAttribute("token",   token);
            model.addAttribute("adminId", adminId);

            strReturnUrl = GlobalConstants.PATH_RESET_PWD;
        } catch(GlobalException ex) {
            model.addAttribute("strErrorMsg", ex.getMessage());

            strReturnUrl = GlobalConstants.PATH_LOGIN;
        }

        return strReturnUrl;
    }
}
