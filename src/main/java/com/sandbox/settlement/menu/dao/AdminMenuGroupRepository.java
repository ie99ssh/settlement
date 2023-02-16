package com.sandbox.settlement.menu.dao;

import com.sandbox.settlement.menu.entity.AdminMenuGroup;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMenuGroupRepository extends JpaRepository<AdminMenuGroup, Integer> {
    AdminMenuGroup findByMenuGroupNo(Integer menuGroupNo);
    List<AdminMenuGroup> findAll(Sort sort);
    List<AdminMenuGroup> findBySortOrderGreaterThan(Integer sortOrder);
}
