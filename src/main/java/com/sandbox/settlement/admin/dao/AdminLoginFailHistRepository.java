package com.sandbox.settlement.admin.dao;

import com.sandbox.settlement.admin.entity.AdminLoginFailHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminLoginFailHistRepository extends JpaRepository<AdminLoginFailHist, Integer> {
    AdminLoginFailHist findByFailNo(Integer failNo);
}
