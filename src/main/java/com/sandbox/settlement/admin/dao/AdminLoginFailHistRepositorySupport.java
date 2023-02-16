package com.sandbox.settlement.admin.dao;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sandbox.settlement.admin.dto.AdminLoginFailHistDto;
import com.sandbox.settlement.admin.dto.AdminMenuAccessLogDto;
import com.sandbox.settlement.admin.entity.AdminLoginFailHist;
import com.sandbox.settlement.common.util.CommonUtil;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.sandbox.settlement.admin.entity.QAdminLoginFailHist.adminLoginFailHist;

@Repository
public class AdminLoginFailHistRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;
    private final CommonUtil commonUtil;

    public AdminLoginFailHistRepositorySupport(JPAQueryFactory jpaQueryFactory, CommonUtil commonUtil) {
        super(AdminLoginFailHist.class);
        this.jpaQueryFactory = jpaQueryFactory;
        this.commonUtil = commonUtil;
    }

    public List<AdminLoginFailHistDto> findAll(AdminLoginFailHistDto adminLoginFailHistDto) {

        QueryResults<AdminLoginFailHistDto> resultList = jpaQueryFactory
                .select(Projections.fields(AdminLoginFailHistDto.class,
                        adminLoginFailHist.adminId,
                        adminLoginFailHist.failNo,
                        adminLoginFailHist.ipAddr,
                        adminLoginFailHist.regDate,
                        adminLoginFailHist.regId,
                        adminLoginFailHist.errCode,
                        adminLoginFailHist.errMsg))
                .from(adminLoginFailHist)
                .where(adminIdEq(adminLoginFailHistDto.getAdminId()),
                        ipAddrLike(adminLoginFailHistDto.getIpAddr()),
                        regDateBetween(adminLoginFailHistDto.getStrFromYMD(), adminLoginFailHistDto.getStrToYMD())
                )
                .orderBy(commonUtil.getOrderSpecifier(AdminMenuAccessLogDto.class, "failNo"
                        , adminLoginFailHistDto.getStrSortType()
                        , adminLoginFailHistDto.getStrSortColumn()))
                .limit(adminLoginFailHistDto.getPageSize())
                .offset(adminLoginFailHistDto.getPageFechNo())
                .fetchResults();

        long totalCount = resultList.getTotal();

        adminLoginFailHistDto.setRecordsFiltered(totalCount);
        adminLoginFailHistDto.setRecordsTotal(totalCount);

        return resultList.getResults();
    }

    private BooleanExpression adminIdEq(String adminId) {
        return !ObjectUtils.isEmpty(adminId) ? adminLoginFailHist.adminId.eq(adminId) : null;
    }

    private BooleanExpression ipAddrLike(String ipAddr) {
        return !ObjectUtils.isEmpty(ipAddr) ? adminLoginFailHist.ipAddr.like("%"+ipAddr+"%") : null;
    }

    private BooleanExpression regDateBetween(String fromYmd, String toYmd) {

        if (!ObjectUtils.isEmpty(fromYmd) && !ObjectUtils.isEmpty(toYmd)) {

            LocalDateTime fromDate = commonUtil.getFromDate(fromYmd);
            LocalDateTime toDate = commonUtil.getToDate(toYmd);

            return adminLoginFailHist.regDate.between(fromDate, toDate);
        }
        return null;
    }

}
