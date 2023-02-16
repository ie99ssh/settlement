package com.sandbox.settlement.menu.service;

import com.sandbox.settlement.common.exception.GlobalException;
import com.sandbox.settlement.menu.dao.AdminMenuGroupRepository;
import com.sandbox.settlement.menu.dto.AdminMenuGroupDto;
import com.sandbox.settlement.menu.entity.AdminMenuGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminMenuGroupService {

    private final AdminMenuGroupRepository repository;

    public AdminMenuGroup getAdminMenuGroup(Integer menuGroupNo) {
        return repository.findByMenuGroupNo(menuGroupNo);
    }

    public List<AdminMenuGroup> getAdminMenuGroupList() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder"));
    }

    public void saveAdminMenuGroup(AdminMenuGroup adminMenuGroup) {
        repository.save(adminMenuGroup);
    }

    public void saveBulkAdminMenuGroup(AdminMenuGroupDto adminMenuGroupDto) throws GlobalException {

        for (AdminMenuGroupDto menuGroupDto : adminMenuGroupDto.getMenuGroupList()) {

            AdminMenuGroup adminMenuGroup = repository.findByMenuGroupNo(menuGroupDto.getMenuGroupNo());

            if (ObjectUtils.isEmpty(adminMenuGroup)) {

                adminMenuGroup = new AdminMenuGroup();
                BeanUtils.copyProperties(menuGroupDto, adminMenuGroup);
                adminMenuGroup.setRegDate(LocalDateTime.now());
                adminMenuGroup.setRegId(adminMenuGroupDto.getAdminId());
                this.saveAdminMenuGroup(adminMenuGroup);

            } else {
                BeanUtils.copyProperties(menuGroupDto, adminMenuGroup);
                adminMenuGroup.setUpdDate(LocalDateTime.now());
                adminMenuGroup.setUpdId(adminMenuGroupDto.getAdminId());
            }
        }
    }

    public void deleteAdminMenuGroup(AdminMenuGroupDto adminMenuGroupDto) {
        repository.deleteById(adminMenuGroupDto.getMenuGroupNo());
    }

    public void deleteAdminMenuGroupUpdOrderSort(AdminMenuGroupDto adminMenuGroupDto) {

        this.deleteAdminMenuGroup(adminMenuGroupDto);

        List<AdminMenuGroup> adminMenuGroupList
                = repository.findBySortOrderGreaterThan(adminMenuGroupDto.getSortOrder());

        for (AdminMenuGroup adminMenuGroup : adminMenuGroupList) {
            adminMenuGroup.setSortOrder(adminMenuGroup.getSortOrder() - 1);
            adminMenuGroup.setUpdId(adminMenuGroupDto.getAdminId());
            adminMenuGroup.setUpdDate(LocalDateTime.now());
        }
    }
}
