package com.sandbox.settlement.admin.service;

import com.sandbox.settlement.admin.dao.AdminPwdResetRepository;
import com.sandbox.settlement.admin.entity.AdminPwdReset;
import com.sandbox.settlement.common.service.SendMailService;
import com.sandbox.settlement.common.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminPwdResetService {

    private final AdminPwdResetRepository repository;

    public AdminPwdReset getAdminPwdResetByToken(String token) {
        return repository.findByToken(token);
    }

    public AdminPwdReset saveAdminPwdReset(AdminPwdReset adminPwdReset) {
        return repository.save(adminPwdReset);
    }

}
