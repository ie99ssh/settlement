package com.sandbox.settlement.admin.controller;

import com.sandbox.settlement.admin.dto.AdministratorDto;
import com.sandbox.settlement.admin.service.AdminPwdResetService;
import com.sandbox.settlement.admin.service.AdministratorService;
import com.sandbox.settlement.common.annotation.AuthMethod;
import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.common.domain.BaseRes;
import com.sandbox.settlement.common.domain.SessionInfo;
import com.sandbox.settlement.common.exception.GlobalException;
import com.sandbox.settlement.common.util.CommonUtil;
import com.sandbox.settlement.menu.entity.AdminMenuRole;
import com.sandbox.settlement.menu.service.AdminMenuRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = GlobalConstants.PATH_ADMINISTRATOR)
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;
    private final AdminPwdResetService adminPwdResetService;
    private final AdminMenuRoleService adminMenuRoleService;
    private final CommonUtil commonUtil;

    /**--------------------------------------------------------------------
     * ■관리자 페이지 진입 함수 ■sangheon
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/administrator")
    public ModelAndView initPage() {

        ModelAndView mav = new ModelAndView(GlobalConstants.PATH_ADMINISTRATOR);

        List<AdminMenuRole> menuRoleList =  adminMenuRoleService.getAdminMenuRoleList();
        mav.addObject("menuRoleList", menuRoleList);

        return mav;
    }

    /**--------------------------------------------------------------------
     * ■관리자 목록 조회 함수 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/getAdministratorList")
    public ModelAndView getAdministratorList(@Valid @RequestBody AdministratorDto administratorDto)
            throws GlobalException {

        List<AdministratorDto> lstAdministrator
                =  administratorService.getAdministratorList(administratorDto);

        return commonUtil.getResJsonView(lstAdministrator, administratorDto);
    }

    /**--------------------------------------------------------------------
     * ■관리자 등록 함수 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/saveAdministrator")
    public ModelAndView saveAdministrator(@Valid @RequestBody AdministratorDto administratorDto, SessionInfo sessionInfo) throws Exception {
        administratorDto.setRegAdminNo(sessionInfo.getAdminNo());
        administratorService.saveAdministrator(administratorDto, sessionInfo);
        return commonUtil.getResJsonView();
    }

    /**--------------------------------------------------------------------
     * ■관리자 목록 엑셀 다운로드 ■sangheon
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @RequestMapping(value = "/excelDownload", method = RequestMethod.GET)
    public void excelDownload(HttpServletResponse response, AdministratorDto administratorDto) throws Exception {
        administratorService.excelDownload(response, administratorDto);
    }

    /**--------------------------------------------------------------------
     * ■관리자 비밀번호 초기화 메일 전송 함수 (관리자 계정 관리 메뉴) ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/resetAdministratorPwd")
    public ModelAndView resetAdministratorPwd(@Valid @RequestBody AdministratorDto administratorDto
            , SessionInfo sessionInfo) throws Exception {
        administratorDto.setAdminNo(sessionInfo.getAdminNo());
        administratorDto.setAdminId(sessionInfo.getAdminId());
        administratorService.resetAdministratorPwd(administratorDto);

        return commonUtil.getResJsonView();
    }

    /**--------------------------------------------------------------------
     * ■관리자 비밀번호 초기화 처리 함수 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @PostMapping(value = "/resetPwd")
    public ModelAndView resetPwd(@Valid @RequestBody AdministratorDto administratorDto, SessionInfo sessionInfo) {

        administratorDto.setAdminNo(sessionInfo.getAdminNo());
        administratorDto.setAdminId(sessionInfo.getAdminId());

        administratorService.resetPwd(administratorDto);

        return commonUtil.getResJsonView();
    }

    /**--------------------------------------------------------------------
     * ■비밀번호 변경 처리 함수 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @PostMapping(value = "/changePwd")
    public ModelAndView changePwd(@Valid @RequestBody AdministratorDto administratorDto, SessionInfo sessionInfo)
            throws GlobalException{

        administratorDto.setAdminNo(sessionInfo.getAdminNo());
        administratorDto.setAdminId(sessionInfo.getAdminId());
        administratorService.changePwd(administratorDto);

        return commonUtil.getResJsonView();
    }
}
