package com.sandbox.settlement.menu.dao;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sandbox.settlement.menu.dto.AdminMenuDto;
import com.sandbox.settlement.menu.dto.AdminMenuRoleDto;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.sandbox.settlement.admin.entity.QAdministrator.administrator;
import static com.sandbox.settlement.menu.entity.QAdminMenu.adminMenu;
import static com.sandbox.settlement.menu.entity.QAdminMenuGroup.adminMenuGroup;
import static com.sandbox.settlement.menu.entity.QAdminMenuRole.adminMenuRole;
import static com.sandbox.settlement.menu.entity.QAdminMenuRoleDetail.adminMenuRoleDetail;

@Repository
public class AdminMenuRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    public AdminMenuRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(AdminMenuDto.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<AdminMenuDto> findMenuByAdminRoleNo(AdminMenuDto adminMenuDto) {

       return jpaQueryFactory
                .select(Projections.fields(AdminMenuDto.class,
                        adminMenu.menuGroupNo
                        ,adminMenu.menuNo
                        ,adminMenu.menuName
                        ,adminMenu.sortOrder
                        ,adminMenu.useFlag
                        ,adminMenu.menuLink
                        ,adminMenu.menuDivSegment
                        ,adminMenuRoleDetail.authCode
                        ,adminMenuRoleDetail.ciReadFlag
                        ,adminMenuRoleDetail.dnAvailFlag
                ))
                .from(administrator)
                .join(adminMenuRole).on(administrator.menuRoleNo.eq(adminMenuRole.menuRoleNo))
                .join(adminMenuRoleDetail).on(adminMenuRole.menuRoleNo.eq(adminMenuRoleDetail.menuRoleNo))
                .join(adminMenu).on(adminMenuRoleDetail.menuNo.eq(adminMenu.menuNo))
                .join(adminMenuGroup).on(adminMenu.menuGroupNo.eq(adminMenuGroup.menuGroupNo))
                .where(administrator.adminId.eq(adminMenuDto.getAdminId()),
                        adminMenu.useFlag.eq(true),
                        adminMenuRole.useFlag.eq(true),
                        adminMenu.useFlag.eq(true),
                        adminMenuGroup.useFlag.eq(true),
                        menuDivSegmentEq(adminMenuDto.getMenuDivSegment())
                )
                .orderBy(adminMenuGroup.sortOrder.asc(), adminMenu.sortOrder.asc())
                .fetch();
    }

    private BooleanExpression menuDivSegmentEq(String menuDivSegment) {
        return !ObjectUtils.isEmpty(menuDivSegment) ? adminMenu.menuDivSegment.eq(menuDivSegment) : null;
    }

}
