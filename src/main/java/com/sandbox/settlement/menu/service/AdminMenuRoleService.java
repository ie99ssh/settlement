package com.sandbox.settlement.menu.service;

import com.sandbox.settlement.common.exception.GlobalException;
import com.sandbox.settlement.menu.dao.AdminMenuRoleDetailRepository;
import com.sandbox.settlement.menu.dao.AdminMenuRoleDetailRepositorySupport;
import com.sandbox.settlement.menu.dao.AdminMenuRoleRepository;
import com.sandbox.settlement.menu.dto.AdminMenuRoleDto;
import com.sandbox.settlement.menu.entity.AdminMenuGroup;
import com.sandbox.settlement.menu.entity.AdminMenuRole;
import com.sandbox.settlement.menu.entity.AdminMenuRoleDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminMenuRoleService {

    private final AdminMenuRoleRepository roleRepository;
    private final AdminMenuRoleDetailRepository roleDetailRepository;
    private final AdminMenuRoleDetailRepositorySupport roleDetailRepositorySupport;
    private final AdminMenuGroupService adminMenuGroupService;

    public List<AdminMenuRole> getAdminMenuRoleList() {
        return roleRepository.findAll();
    }

    public List<AdminMenuRoleDto> getAdminMenuRoleDetailList(AdminMenuRoleDto adminMenuRoleDto) throws GlobalException {

        List<AdminMenuRoleDto> menuRoleDetailList = new ArrayList<>();

        List<AdminMenuGroup> adminMenuGroupList = adminMenuGroupService.getAdminMenuGroupList();
        List<AdminMenuRoleDto> adminMenuRoleDtoList = roleDetailRepositorySupport.findAllMenuRole(adminMenuRoleDto);

        for (AdminMenuGroup adminMenuGroup : adminMenuGroupList) {

            AdminMenuRoleDto menuGroupRole = new AdminMenuRoleDto();
            menuGroupRole.setMenuGroupNo(adminMenuGroup.getMenuGroupNo());
            menuGroupRole.setAuthCode(0);
            menuGroupRole.setMenuNo(0);
            menuGroupRole.setDepth(1);
            menuGroupRole.setMenuName(adminMenuGroup.getMenuGroupName());
            menuRoleDetailList.add(menuGroupRole);
            int menuGroupNo = menuGroupRole.getMenuGroupNo();

            for(Iterator<AdminMenuRoleDto> it = adminMenuRoleDtoList.iterator(); it.hasNext();) {
                AdminMenuRoleDto adminMenuRole = it.next();
                if (menuGroupNo == adminMenuRole.getMenuGroupNo()) {
                    adminMenuRole.setDepth(2);
                    menuRoleDetailList.add(adminMenuRole);
                    it.remove();
                }
            }
        }

        return menuRoleDetailList;
    }

    public void deleteAdminMenuRoleDetail(AdminMenuRoleDetail adminMenuRoleDetail) {
        roleDetailRepository.delete(adminMenuRoleDetail);
    }

    public void deleteAdminMenuRoleDetail(AdminMenuRoleDto adminMenuRoleDto) throws GlobalException {
        List<AdminMenuRoleDetail> adminMenuRoleDetailList
                = roleDetailRepository.findByMenuRoleNo(adminMenuRoleDto.getMenuRoleNo());

        for (AdminMenuRoleDetail adminMenuRoleDetail : adminMenuRoleDetailList) {
            this.deleteAdminMenuRoleDetail(adminMenuRoleDetail);
        }
    }

    public AdminMenuRole saveAdminMenuRole(AdminMenuRoleDto adminMenuRoleDto) throws GlobalException {

        Integer menuRoleNo = adminMenuRoleDto.getMenuRoleNo();
        AdminMenuRole adminMenuRole;

        if (ObjectUtils.isEmpty(menuRoleNo)) {
            adminMenuRole = new AdminMenuRole();
            BeanUtils.copyProperties(adminMenuRoleDto, adminMenuRole);
            adminMenuRole.setRegId(adminMenuRoleDto.getAdminId());
            roleRepository.save(adminMenuRole);
        } else {
            adminMenuRole = roleRepository.findByMenuRoleNo(menuRoleNo);
            BeanUtils.copyProperties(adminMenuRoleDto, adminMenuRole);
            adminMenuRole.setUpdDate(LocalDateTime.now());
            adminMenuRole.setUpdId(adminMenuRoleDto.getAdminId());
        }
        return adminMenuRole;
    }

    public void saveAdminMenuRoleDetail(AdminMenuRoleDto adminMenuRoleDto) throws GlobalException {

        AdminMenuRoleDetail adminMenuRoleDetail = new AdminMenuRoleDetail();
        BeanUtils.copyProperties(adminMenuRoleDto, adminMenuRoleDetail);
        adminMenuRoleDetail.setRegId(adminMenuRoleDto.getAdminId());
        roleDetailRepository.save(adminMenuRoleDetail);
    }

    public void saveAdminMenuRoleInfo(AdminMenuRoleDto adminMenuRoleDto) throws GlobalException {

        Integer menuRoleNo = adminMenuRoleDto.getMenuRoleNo();

        if (!ObjectUtils.isEmpty(menuRoleNo)) {
            this.deleteAdminMenuRoleDetail(adminMenuRoleDto);
        }

        AdminMenuRole resAdminMenuRole = this.saveAdminMenuRole(adminMenuRoleDto);

        for (AdminMenuRoleDto obj : adminMenuRoleDto.getMenuRoleList()) {
            obj.setAdminId(adminMenuRoleDto.getAdminId());
            obj.setMenuRoleNo(resAdminMenuRole.getMenuRoleNo());
            this.saveAdminMenuRoleDetail(obj);
        }
    }
}
