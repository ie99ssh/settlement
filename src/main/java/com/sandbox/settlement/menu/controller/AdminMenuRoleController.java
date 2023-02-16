package com.sandbox.settlement.menu.controller;

import com.sandbox.settlement.common.annotation.AuthMethod;
import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.common.domain.SessionInfo;
import com.sandbox.settlement.common.exception.GlobalException;
import com.sandbox.settlement.common.util.CommonUtil;
import com.sandbox.settlement.menu.dto.AdminMenuRoleDto;
import com.sandbox.settlement.menu.service.AdminMenuRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**--------------------------------------------------------------------
 * ■메뉴 역할 컨트롤러 ■sangheon
 --------------------------------------------------------------------**/
@Controller
@RequestMapping(value = GlobalConstants.PATH_MENU_ROLE)
@RequiredArgsConstructor
public class AdminMenuRoleController {

    private final AdminMenuRoleService adminMenuRoleService;
    private final CommonUtil commonUtil;

    /**--------------------------------------------------------------------
     * ■메뉴 역할 페이지 진입 ■sangheon
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/menuRole")
    public String initPage() {
        return GlobalConstants.PATH_MENU_ROLE;
    }

    /**--------------------------------------------------------------------
     * ■메뉴 역할 목록 조회 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/getAdminMenuRoleList")
    public ModelAndView getAdminMenuRoleList() throws GlobalException {
        return commonUtil.getResJsonView(adminMenuRoleService.getAdminMenuRoleList());
    }

    /**--------------------------------------------------------------------
     * ■메뉴 역할 상세 목록 조회 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/getAdminMenuRoleDetailList")
    public ModelAndView getAdminMenuRoleDetailList(@RequestBody AdminMenuRoleDto adminMenuRoleDto) throws GlobalException {

        return commonUtil.getResJsonView(adminMenuRoleService.getAdminMenuRoleDetailList(adminMenuRoleDto));
    }

    /**--------------------------------------------------------------------
     * ■메뉴 역할 등록/수정 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/saveAdminMenuRole")
    public ModelAndView saveAdminMenuRole(@RequestBody AdminMenuRoleDto adminMenuRoleDto, SessionInfo sessionInfo) throws GlobalException {

        adminMenuRoleDto.setAdminId(sessionInfo.getAdminId());
        adminMenuRoleService.saveAdminMenuRoleInfo(adminMenuRoleDto);
        return commonUtil.getResJsonView();
    }
}
