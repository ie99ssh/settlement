package com.sandbox.settlement.menu.controller;

import com.sandbox.settlement.common.annotation.AuthMethod;
import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.common.domain.SessionInfo;
import com.sandbox.settlement.common.exception.GlobalException;
import com.sandbox.settlement.common.util.CommonUtil;
import com.sandbox.settlement.menu.dto.AdminMenuGroupDto;
import com.sandbox.settlement.menu.service.AdminMenuGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**--------------------------------------------------------------------
 * ■메뉴 그룹 컨트롤러 ■sangheon
 --------------------------------------------------------------------**/
@Controller
@RequiredArgsConstructor
@RequestMapping(value = GlobalConstants.PATH_MENU_GROUP)
public class AdminMenuGroupController {
    
    private final AdminMenuGroupService adminMenuGroupService;
    private final CommonUtil commonUtil;

    /**--------------------------------------------------------------------
     * ■메뉴 그룹 페이지 진입 함수 ■sangheon
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/menuGroup")
    public String initPage() {
        return GlobalConstants.PATH_MENU_GROUP;
    }

    /**--------------------------------------------------------------------
     * ■메뉴 그룹 조회 함수 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/getAdminMenuGroupList")
    public ModelAndView getAdminMenuGroupList() throws GlobalException {

        return commonUtil.getResJsonView(adminMenuGroupService.getAdminMenuGroupList());
    }

    /**--------------------------------------------------------------------
     * ■메뉴 그룹 목록 등록/수정 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/saveBulkAdminMenuGroup")
    public ModelAndView saveBulkAdminMenuGroup(SessionInfo sessionInfo
            , @RequestBody AdminMenuGroupDto adminMenuGroupDto) throws GlobalException {

        adminMenuGroupDto.setAdminId(sessionInfo.getAdminId());
        adminMenuGroupService.saveBulkAdminMenuGroup(adminMenuGroupDto);

        return commonUtil.getResJsonView();
    }

    /**--------------------------------------------------------------------
     * ■메뉴 그룹 삭제 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/deleteAdminMenuGroup")
    public ModelAndView deleteMenuGroup(@RequestBody AdminMenuGroupDto adminMenuGroupDto
            , SessionInfo sessionInfo) throws GlobalException {

        adminMenuGroupDto.setAdminId(sessionInfo.getAdminId());
        adminMenuGroupService.deleteAdminMenuGroupUpdOrderSort(adminMenuGroupDto);

        return commonUtil.getResJsonView();
    }
}
