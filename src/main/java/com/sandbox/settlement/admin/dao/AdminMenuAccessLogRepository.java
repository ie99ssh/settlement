package com.sandbox.settlement.admin.dao;

import com.sandbox.settlement.admin.entity.AdminMenuAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMenuAccessLogRepository extends JpaRepository<AdminMenuAccessLog, Integer> {
    List<AdminMenuAccessLog> findByAdminNo(Integer adminNo);
}
