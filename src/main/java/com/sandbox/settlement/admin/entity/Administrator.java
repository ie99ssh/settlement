package com.sandbox.settlement.admin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Administrator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adminNo;

    @Column(unique = true)
    private String adminId;

    private String adminPwd;
    private String adminName;
    private Integer menuRoleNo;
    private String phoneNo;
    private String email;
    private Boolean accessIPRestrictFlag;
    private String accessIP1;
    private String accessIP2;
    private String accessIP3;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime lastLoginDate;
    private String lastLoginIP;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime pwdUpdDate;
    private Integer regAdminNo;
    private Boolean useFlag = true;
    private Boolean authFlag;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime authExpDate;

    @CreationTimestamp
    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime regDate;

    private String regId;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime updDate;
    private String updId;

}
