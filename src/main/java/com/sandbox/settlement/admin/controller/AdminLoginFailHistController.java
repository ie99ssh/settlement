package com.sandbox.settlement.admin.controller;

import com.sandbox.settlement.admin.dto.AdminLoginFailHistDto;
import com.sandbox.settlement.admin.entity.AdminLoginFailHist;
import com.sandbox.settlement.admin.service.AdminLoginFailHistService;
import com.sandbox.settlement.common.annotation.AuthMethod;
import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.common.exception.GlobalException;
import com.sandbox.settlement.common.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;


/**--------------------------------------------------------------------
 * ■관리자 로그인 실패 이력 컨트롤러 ■sangheon
 --------------------------------------------------------------------**/
@Controller
@RequestMapping(value = GlobalConstants.PATH_ADMIN_LOGIN_FAIL_HIST)
@RequiredArgsConstructor
public class AdminLoginFailHistController {

    private final AdminLoginFailHistService adminLoginFailHistService;
    private final CommonUtil commonUtil;

    /**--------------------------------------------------------------------
     * ■로그인 실패 이력 페이지 진입 함수 ■sangheon
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/adminLoginFailHist")
    public String initPage() {
        return GlobalConstants.PATH_ADMIN_LOGIN_FAIL_HIST;
    }

    /**--------------------------------------------------------------------
     * ■로그인 실패 이력 목록 조회 함수 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/selectAdminLoginFailHistList")
    public ModelAndView selectAdminLoginFailHistList(@Valid @RequestBody AdminLoginFailHistDto adminLoginFailHistDto) throws GlobalException {

        List<AdminLoginFailHistDto> lstAdminLoginFailHist
                = adminLoginFailHistService.getAdminLoginFailHistList(adminLoginFailHistDto);

        return commonUtil.getResJsonView(lstAdminLoginFailHist, adminLoginFailHistDto);
    }

    /**--------------------------------------------------------------------
     * ■관리자 로그인 실패 이력 목록 엑셀 다운로드 ■sangheon
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @RequestMapping(value = "/excelDownload", method = RequestMethod.GET)
    public void excelDownload(HttpServletResponse response, AdminLoginFailHistDto adminLoginFailHistDto) throws Exception {
        adminLoginFailHistService.excelDownload(response, adminLoginFailHistDto);
    }
}
