package com.sandbox.settlement.menu.dto;

import com.sandbox.settlement.common.domain.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**--------------------------------------------------------------------
 * ■메뉴 역할 모델 ■sangheon
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class AdminMenuRoleDto extends BaseModel {
    //------------------------------------------------------------------
    // 엔티티 베이스 멤버 변수(엔티티 컬럼 정렬 순서 순)
    //------------------------------------------------------------------
    private Integer menuRoleNo;          // 메뉴 역할 번호
    private String  menuRoleName;        // 메뉴 역할 명
    private Integer adminNo;             // 관리자 번호
    private Boolean useFlag;              // 사용 여부 (0:미사용, 1:사용)
    private String  regDate;              // 등록 일시
    private String  updDate;              // 수정 일시

    //------------------------------------------------------------------
    // 메뉴 역할 상세
    //------------------------------------------------------------------
    private Integer menuGroupNo;         // 메뉴 그룹 번호
    private Integer menuNo;              // 메뉴 번호
    private Integer authCode;            // 권한 코드
    private Boolean ciReadFlag;           // 중요정보 열람 여부 (0:불가, 1:가능)
    private Boolean dnAvailFlag;          // 다운로드 가능 여부 (0:불가, 1:가능)

    //------------------------------------------------------------------
    // 엔티티 베이스 가공 멤버 변수
    //------------------------------------------------------------------
    private Integer menuGroupSortOrder;  // 메뉴 그룹 정렬 순서
    private String  menuName;            // 메뉴 명
    private Integer menuSortOrder;       // 메뉴 정렬 순서

    private Integer depth;               // 메뉴 Depth
    private String  adminId;             // 관리자 아이디
    private List<AdminMenuRoleDto> menuRoleList; // 메뉴 역할 리스트

    public List<AdminMenuRoleDto> getMenuRoleList() {

        if (menuRoleList == null) {
            menuRoleList = new ArrayList();
        }
        return menuRoleList;
    }
}
