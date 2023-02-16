package com.sandbox.settlement.admin.dao;

import com.sandbox.settlement.admin.entity.AdminPwdReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminPwdResetRepository extends JpaRepository<AdminPwdReset, Integer> {
    AdminPwdReset findByAdminNo(Integer adminNo);
    AdminPwdReset findByToken(String token);
}
