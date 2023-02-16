package com.sandbox.settlement.menu.service;

import com.sandbox.settlement.common.exception.GlobalException;
import com.sandbox.settlement.menu.dao.AdminMenuRepository;
import com.sandbox.settlement.menu.dao.AdminMenuRepositorySupport;
import com.sandbox.settlement.menu.dto.AccessMenuDto;
import com.sandbox.settlement.menu.dto.AdminMenuDto;
import com.sandbox.settlement.menu.entity.AdminMenu;
import com.sandbox.settlement.menu.entity.AdminMenuGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminMenuService {

    private final AdminMenuRepository repository;
    private final AdminMenuRepositorySupport repositorySupport;
    private final AdminMenuGroupService adminMenuGroupService;

    public AdminMenu getAdminMenu(Integer menuNo) {
        return repository.findByMenuNo(menuNo);
    }

    public List<AdminMenu> getAdminMenuList(Integer menuGroupNo) {
        return repository.findByMenuGroupNoOrderBySortOrderAsc(menuGroupNo);
    }

    public List<AdminMenu> getAdminMenuList() {
        return repository.findAll();
    }

    public AdminMenuDto getMenuLinkByLoginAction(String adminId) {

        AdminMenuDto adminMenuDto = new AdminMenuDto();
        adminMenuDto.setAdminId(adminId);

        List<AdminMenuDto> adminMenuDtoList = repositorySupport.findMenuByAdminRoleNo(adminMenuDto);

        if (ObjectUtils.isEmpty(adminMenuDtoList)) {
            return null;
        } else {
            return adminMenuDtoList.get(0);
        }
    }

    public List<AdminMenuDto> getMenuList(String adminId) {

        List<AdminMenuDto> adminMenuDtoList = new ArrayList<>();

        List<AdminMenuGroup> adminMenuGroupList = adminMenuGroupService.getAdminMenuGroupList();
        adminMenuGroupList = adminMenuGroupList.stream()
                .filter(AdminMenuGroup::getUseFlag)
                .collect(Collectors.toList());

        AdminMenuDto adminMenuDto = new AdminMenuDto();
        adminMenuDto.setAdminId(adminId);

        List<AdminMenuDto> adminMenuList = repositorySupport.findMenuByAdminRoleNo(adminMenuDto);

        for (AdminMenuGroup adminMenuGroup : adminMenuGroupList) {

            AdminMenuDto menuGroupDto = new AdminMenuDto();
            BeanUtils.copyProperties(adminMenuGroup, menuGroupDto);
            adminMenuDtoList.add(menuGroupDto);
            int menuGroupNo = menuGroupDto.getMenuGroupNo();

            for(Iterator<AdminMenuDto> it = adminMenuList.iterator(); it.hasNext();) {
                AdminMenuDto adminMenuRole = it.next();
                if (menuGroupNo == adminMenuRole.getMenuGroupNo()) {
                    menuGroupDto.getMenuList().add(adminMenuRole);
                    it.remove();
                }
            }
        }

        adminMenuDtoList = adminMenuDtoList.stream()
                .filter(t -> !ObjectUtils.isEmpty(t.getMenuList()))
                .collect(Collectors.toList());

        return adminMenuDtoList;
    }

    public AccessMenuDto getAccessMenu(AccessMenuDto accessMenuDto) {

        AccessMenuDto accessMenuDtoRes = new AccessMenuDto();
        AdminMenuDto adminMenuDto = new AdminMenuDto();
        adminMenuDto.setAdminId(accessMenuDto.getAdminId());
        adminMenuDto.setMenuDivSegment(accessMenuDto.getMenuDivSegment());

        List<AdminMenuDto> adminMenuList = repositorySupport.findMenuByAdminRoleNo(adminMenuDto);

        if (!ObjectUtils.isEmpty(adminMenuList)) {
            AdminMenuDto adminMenuDtoRes = adminMenuList.get(0);
            BeanUtils.copyProperties(adminMenuDtoRes, accessMenuDtoRes);
        }

        return accessMenuDtoRes;
    }

    public void saveAdminMenu(AdminMenuDto adminMenuDto) throws GlobalException {

        Comparator<AdminMenu> comparatorBySortOrder = Comparator.comparingInt(AdminMenu::getSortOrder);

        if (ObjectUtils.isEmpty(adminMenuDto.getMenuNo())) {

            AdminMenu adminMenu = new AdminMenu();
            BeanUtils.copyProperties(adminMenuDto, adminMenu);
            adminMenu.setRegId(adminMenuDto.getAdminId());

            AdminMenu maxSortOrderMenu
                    = this.getAdminMenuList(adminMenu.getMenuGroupNo())
                      .stream().max(comparatorBySortOrder)
                      .orElseThrow(NoSuchElementException::new);

            int sortOrder = maxSortOrderMenu.getSortOrder() + 1;
            adminMenu.setSortOrder(sortOrder);

            repository.save(adminMenu);

        } else {

            AdminMenu adminMenu = repository.findByMenuNo(adminMenuDto.getMenuNo());
            int sortOrder = adminMenu.getSortOrder();
            BeanUtils.copyProperties(adminMenuDto, adminMenu);
            adminMenu.setUpdDate(LocalDateTime.now());
            adminMenu.setSortOrder(sortOrder);
            adminMenu.setUpdId(adminMenuDto.getAdminId());
        }
    }

    public void saveAdminMenuList(AdminMenuDto adminMenuDto) {

        for (AdminMenuDto obj : adminMenuDto.getMenuList()) {
            AdminMenu adminMenu = repository.findByMenuNo(obj.getMenuNo());
            adminMenu.setSortOrder(obj.getSortOrder());
            adminMenu.setUseFlag(obj.getUseFlag());
            adminMenu.setUpdDate(LocalDateTime.now());
            adminMenu.setUpdId(adminMenuDto.getAdminId());
        }
    }

    public void deleteAdminMenu(AdminMenuDto adminMenuDto) {
        repository.deleteById(adminMenuDto.getMenuNo());
    }

    public void deleteAdminMenuGroupUpdOrderSort(AdminMenuDto adminMenuDto) {

        this.deleteAdminMenu(adminMenuDto);

        List<AdminMenu> adminMenuList
                = repository.findByMenuGroupNoAndSortOrderGreaterThan(adminMenuDto.getMenuGroupNo(), adminMenuDto.getSortOrder());

        for (AdminMenu adminMenu : adminMenuList) {
            adminMenu.setSortOrder(adminMenu.getSortOrder() - 1);
            adminMenu.setUpdId(adminMenuDto.getAdminId());
            adminMenu.setUpdDate(LocalDateTime.now());
        }
    }

}
