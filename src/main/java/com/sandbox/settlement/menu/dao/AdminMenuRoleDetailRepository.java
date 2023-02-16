package com.sandbox.settlement.menu.dao;

import com.sandbox.settlement.menu.entity.AdminMenuRoleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMenuRoleDetailRepository extends JpaRepository<AdminMenuRoleDetail, Integer> {
    List<AdminMenuRoleDetail> findAll();

    List<AdminMenuRoleDetail> findByMenuRoleNo(int menuRoleNo);
}
