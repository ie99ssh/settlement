package com.sandbox.settlement.common.domain;

import lombok.Data;

/**--------------------------------------------------------------------
 * ■기본 DT 포함 모델 ■ymkang
 --------------------------------------------------------------------**/
@Data
public class BaseModel {
    // DataTable 관련 변수 정의
    private int     pageNo;
    private int     pageSize;
    private int     pageFechNo;
    private int     draw;
    private int     start;

    private int     length;
    private long    recordsFiltered;
    private long    recordsTotal;
    private Boolean partialSearchFlag;
    private String  strSortColumn;

    private String  strSortType;
    private String  langCode;

    private int     intProcRetCode; //프로시저 리턴 결과 (0: 정상, 그외 별도 처리)
    private String  strProcRetMsg;  //프로시저 리턴 메시지

    private int count;
}
