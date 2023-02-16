package com.sandbox.settlement.menu.dto;

import com.sandbox.settlement.common.domain.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminMenuDto extends BaseModel {

    private Integer menuNo;
    private String menuName;
    private Integer menuGroupNo;
    private String menuGroupName;
    private String menuDivSegment;
    private String menuLink;
    private String menuDesc;
    private String menuGroupIcon;
    private Integer sortOrder;
    private Integer menuGroupSortOrder;
    private String adminId;
    private Integer authCode;
    private Boolean useFlag;
    private Boolean ciReadFlag;
    private Boolean dnAvailFlag;

    private List<AdminMenuDto> menuList = new ArrayList<>();

}
