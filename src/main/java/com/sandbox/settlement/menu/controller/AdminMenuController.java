package com.sandbox.settlement.menu.controller;

import com.sandbox.settlement.common.annotation.AuthMethod;
import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.common.domain.SessionInfo;
import com.sandbox.settlement.common.exception.GlobalException;
import com.sandbox.settlement.common.util.CommonUtil;
import com.sandbox.settlement.menu.dto.AdminMenuDto;
import com.sandbox.settlement.menu.entity.AdminMenuGroup;
import com.sandbox.settlement.menu.service.AdminMenuGroupService;
import com.sandbox.settlement.menu.service.AdminMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**--------------------------------------------------------------------
 * ■메뉴 컨트롤러 ■sangheon
 --------------------------------------------------------------------**/
@RestController
@RequiredArgsConstructor
@RequestMapping(value = GlobalConstants.PATH_MENU)
public class AdminMenuController {

    private final AdminMenuService adminMenuService;
    private final AdminMenuGroupService adminMenuGroupService;
    private final CommonUtil commonUtil;

    /**--------------------------------------------------------------------
     * ■메뉴 페이지 진입 함수 ■sangheon
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/menu")
    public ModelAndView initPage() {

        ModelAndView mav = new ModelAndView(GlobalConstants.PATH_MENU);

        List<AdminMenuGroup> menuGroupList = adminMenuGroupService.getAdminMenuGroupList();

        mav.addObject("menuGroupList", menuGroupList);

        return mav;
    }

    /**--------------------------------------------------------------------
     * ■메뉴 목록 조회 함수 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/getAdminMenuList")
    public ModelAndView getAdminMenuList(@RequestBody AdminMenuDto adminMenuDto) throws GlobalException {

        return commonUtil.getResJsonView(adminMenuService.getAdminMenuList(adminMenuDto.getMenuGroupNo()));
    }

    /**--------------------------------------------------------------------
     * ■메뉴 목록 조회 함수 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/getAdminMenu")
    public ModelAndView getAdminMenu(@RequestBody AdminMenuDto adminMenuDto) throws GlobalException {
        return commonUtil.getResJsonView(adminMenuService.getAdminMenu(adminMenuDto.getMenuNo()));
    }

    /**--------------------------------------------------------------------
     * ■메뉴 등록/수정 함수 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/saveAdminMenu")
    public ModelAndView saveAdminMenu(@RequestBody AdminMenuDto adminMenuDto, SessionInfo sessionInfo) throws GlobalException {

        adminMenuDto.setAdminId(sessionInfo.getAdminId());
        adminMenuService.saveAdminMenu(adminMenuDto);

        return commonUtil.getResJsonView();
    }

    /**--------------------------------------------------------------------
     * ■메뉴 목록 수정 함수 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/saveAdminMenuList")
    public ModelAndView saveAdminMenuList(@RequestBody AdminMenuDto adminMenuDto, SessionInfo sessionInfo) throws GlobalException {

        adminMenuDto.setAdminId(sessionInfo.getAdminId());
        adminMenuService.saveAdminMenuList(adminMenuDto);
        return commonUtil.getResJsonView();
    }

    /**--------------------------------------------------------------------
     * ■메뉴 삭제 함수 ■sangheon
     --------------------------------------------------------------------**/
    @ResponseBody
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/deleteAdminMenu")
    public ModelAndView deleteAdminMenu(@RequestBody AdminMenuDto adminMenuDto, SessionInfo sessionInfo) throws GlobalException {
        adminMenuDto.setAdminId(sessionInfo.getAdminId());
        adminMenuService.deleteAdminMenuGroupUpdOrderSort(adminMenuDto);
        return commonUtil.getResJsonView();
    }
}
