package com.sandbox.settlement.admin.dao;

import com.sandbox.settlement.admin.entity.Administrator;
import com.sandbox.settlement.menu.entity.AdminMenuGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Integer> {
    Administrator findByAdminNo(Integer adminNo);
    Administrator findByAdminId(String adminId);
}
