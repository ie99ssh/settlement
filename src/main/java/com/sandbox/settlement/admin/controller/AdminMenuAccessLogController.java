package com.sandbox.settlement.admin.controller;

import com.sandbox.settlement.admin.dto.AdminMenuAccessLogDto;
import com.sandbox.settlement.admin.entity.AdminMenuAccessLog;
import com.sandbox.settlement.admin.service.AdminMenuAccessLogService;
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
 * ■관리자 로그 컨트롤러 ■sangheon
 --------------------------------------------------------------------**/
@Controller
@RequestMapping(value = GlobalConstants.PATH_ADMIN_MENU_ACCESS_LOG)
@RequiredArgsConstructor
public class AdminMenuAccessLogController {

    private final AdminMenuAccessLogService adminMenuAccessLogService;
    private final CommonUtil commonUtil;

    /**--------------------------------------------------------------------
     * ■관리자 로그 페이지 진입 함수 ■sangheon
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/adminMenuAccessLog")
    public String initPage() {
        return GlobalConstants.PATH_ADMIN_MENU_ACCESS_LOG;
    }

    /**--------------------------------------------------------------------
     * ■관리자 로그 목록 조회 함수 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/selectAdminMenuAccessLogList")
    public ModelAndView selectAdminMenuAccessLogList(@Valid @RequestBody AdminMenuAccessLogDto adminMenuAccessLogDto)
            throws GlobalException {

        List<AdminMenuAccessLogDto> lstAdminMenuAccessLog
                = adminMenuAccessLogService.getAdminMenuAccessLogList(adminMenuAccessLogDto);

        return commonUtil.getResJsonView(lstAdminMenuAccessLog, adminMenuAccessLogDto);
    }

    /**--------------------------------------------------------------------
     * ■관리자 로그 목록 엑셀 다운로드 ■sangheon
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @RequestMapping(value = "/excelDownload", method = RequestMethod.GET)
    public void excelDownload(HttpServletResponse response, AdminMenuAccessLogDto adminMenuAccessLogDto) throws Exception {
        adminMenuAccessLogService.excelDownload(response, adminMenuAccessLogDto);
    }
}
