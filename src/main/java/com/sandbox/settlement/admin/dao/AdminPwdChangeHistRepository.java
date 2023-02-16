package com.sandbox.settlement.admin.dao;

import com.sandbox.settlement.admin.entity.AdminPwdChangeHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminPwdChangeHistRepository extends JpaRepository<AdminPwdChangeHist, Integer> {

}
