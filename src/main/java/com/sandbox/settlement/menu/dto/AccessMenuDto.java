package com.sandbox.settlement.menu.dto;

import com.sandbox.settlement.common.domain.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**--------------------------------------------------------------------
 * ■접근 메뉴 ■sh_shin
 --------------------------------------------------------------------**/
@EqualsAndHashCode(callSuper = true)
@Data
public class AccessMenuDto extends BaseModel {
    // 요청
    private String  adminId;
    private String  menuDivSegment;

    // 응답
    private Integer menuGroupNo;
    private String  menuGroupName;
    private Boolean menuGroupUseFlag;
    private Integer menuNo;
    private String  menuName;

    private Boolean menuUseFlag;
    private Boolean menuRoleUseFlag;
    private Integer authCode;
    private Boolean ciReadFlag;
    private Boolean dnAvailFlag;
}
