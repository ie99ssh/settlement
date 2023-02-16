package com.sandbox.settlement.menu.dao;

import com.sandbox.settlement.menu.entity.AdminMenuRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMenuRoleRepository extends JpaRepository<AdminMenuRole, Integer> {
    List<AdminMenuRole> findAll();
    AdminMenuRole findByMenuRoleNo(int menuRoleNo);
}
