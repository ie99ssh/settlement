package com.sandbox.settlement.common.domain;

import lombok.Data;

import java.util.List;

@Data
public class TotalInfoModel<T> {
    private List<T> listData;
    private T totalInfo;
}
