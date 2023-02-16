package com.sandbox.settlement.admin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class AdminMenuAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accessNo;

    private Integer adminNo;
    private Integer menuNo;
    private String menuLink;
    private String methodName;
    private String logDesc;
    private String ipAddr;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @CreationTimestamp
    private LocalDateTime regDate;

    private String regId;
}
