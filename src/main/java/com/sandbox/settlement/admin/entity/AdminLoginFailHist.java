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
public class AdminLoginFailHist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer failNo;

    private String adminId;
    private Integer errCode;
    private String ipAddr;
    private String errMsg;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @CreationTimestamp
    private LocalDateTime regDate;

    private String regId;

}
