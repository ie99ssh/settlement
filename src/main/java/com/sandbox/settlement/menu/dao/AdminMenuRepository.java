package com.sandbox.settlement.menu.dao;

import com.sandbox.settlement.menu.entity.AdminMenu;
import com.sandbox.settlement.menu.entity.AdminMenuGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMenuRepository extends JpaRepository<AdminMenu, Integer> {
    AdminMenu findByMenuNo(Integer menuNo);
    List<AdminMenu> findByMenuGroupNoOrderBySortOrderAsc(Integer menuGroupNo);
    List<AdminMenu> findByMenuGroupNoAndSortOrderGreaterThan(Integer menuGroupNo, Integer sortOrder);
}
