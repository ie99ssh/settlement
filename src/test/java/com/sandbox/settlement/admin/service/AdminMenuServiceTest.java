package com.sandbox.settlement.admin.service;

import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.menu.entity.AdminMenu;
import com.sandbox.settlement.menu.service.AdminMenuService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminMenuServiceTest {

    @Autowired private AdminMenuService service;

    @Test
    public void getAdminMenu() throws Exception {

        AdminMenu adminMenu = service.getAdminMenu(1);

        System.out.println("adminMenu : " + adminMenu);

    }

    @Test
    public void getAdminMenuList() throws Exception {

        List<AdminMenu> adminMenuList = service.getAdminMenuList(1);

        System.out.println("adminMenuList : " + adminMenuList);

    }

    @Test
    public void getHashValue() throws NoSuchAlgorithmException {

        BCryptPasswordEncoder passwordEncoder
                = new BCryptPasswordEncoder(10, SecureRandom.getInstance(GlobalConstants.BCRYPT_ALGORITHM));

        String hash = passwordEncoder.encode("qwer12#$");

        System.out.println(hash);
    }
}
