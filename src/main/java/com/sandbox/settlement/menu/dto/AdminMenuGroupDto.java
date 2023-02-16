package com.sandbox.settlement.menu.dto;

import com.sandbox.settlement.common.domain.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**--------------------------------------------------------------------
 * ■메뉴 그룹 모델 ■sangheon
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class AdminMenuGroupDto extends BaseModel {
    //------------------------------------------------------------------
    // 엔티티 베이스 멤버 변수(엔티티 컬럼 정렬 순서 순)
    //------------------------------------------------------------------
    private Integer menuGroupNo;           // 메뉴 그룹 번호
    private String  menuGroupName;         // 메뉴 그룹 명
    private String  menuGroupIcon;         // 메뉴 그룹 아이콘
    private Integer sortOrder;             // 정렬 순서
    private Integer adminNo;               // 관리자 번호

    private Boolean useFlag;               // 사용 여부 (0:미사용, 1:사용)
    private String  regDate;               // 등록 일시
    private String  updDate;               // 수정 일시

    //------------------------------------------------------------------
    // 엔티티 베이스 가공 멤버 변수
    //------------------------------------------------------------------
    private String  adminId;               // 관리자 아이디
    private List<AdminMenuGroupDto> menuGroupList; // 메뉴 그룹 리스트

}
