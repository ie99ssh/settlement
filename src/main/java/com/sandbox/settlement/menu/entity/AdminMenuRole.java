package com.sandbox.settlement.menu.entity;

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
public class AdminMenuRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer menuRoleNo;

    private String menuRoleName;
    private Boolean useFlag = true;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @CreationTimestamp
    private LocalDateTime regDate;

    private String regId;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime updDate;
    private String updId;

}
