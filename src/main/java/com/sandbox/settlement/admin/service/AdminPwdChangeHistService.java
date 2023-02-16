package com.sandbox.settlement.admin.service;

import com.sandbox.settlement.admin.dao.AdminPwdChangeHistRepository;
import com.sandbox.settlement.admin.entity.AdminPwdChangeHist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminPwdChangeHistService {

    private final AdminPwdChangeHistRepository repository;

    public AdminPwdChangeHist saveAdminPwdChangeHist(AdminPwdChangeHist adminPwdChangeHist) {
        return repository.save(adminPwdChangeHist);
    }
}
