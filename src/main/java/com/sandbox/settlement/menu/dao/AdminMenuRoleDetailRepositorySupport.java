package com.sandbox.settlement.menu.dao;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sandbox.settlement.menu.dto.AdminMenuRoleDto;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sandbox.settlement.menu.entity.QAdminMenu.adminMenu;
import static com.sandbox.settlement.menu.entity.QAdminMenuRoleDetail.adminMenuRoleDetail;

@Repository
public class AdminMenuRoleDetailRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    public AdminMenuRoleDetailRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(AdminMenuRoleDto.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<AdminMenuRoleDto> findAllMenuRole(AdminMenuRoleDto adminMenuRoleDto) {

        Integer menuRoleNo = adminMenuRoleDto.getMenuRoleNo();

        return jpaQueryFactory
                .select(Projections.fields(AdminMenuRoleDto.class,
                        adminMenu.menuGroupNo
                        ,adminMenu.menuNo
                        ,adminMenu.menuName
                        ,adminMenu.sortOrder
                        ,adminMenu.useFlag
                        ,adminMenuRoleDetail.authCode
                        ,adminMenuRoleDetail.ciReadFlag
                        ,adminMenuRoleDetail.dnAvailFlag
                ))
                .from(adminMenu)
                .leftJoin(adminMenuRoleDetail)
                .on(adminMenu.menuNo.eq(adminMenuRoleDetail.menuNo)
                        .and(adminMenuRoleDetail.menuRoleNo.eq(menuRoleNo == null ? 0 : menuRoleNo)))
                .where(adminMenu.useFlag.eq(true))
                .orderBy(adminMenu.sortOrder.asc())
                .fetch();
    }

}
