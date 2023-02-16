package com.sandbox.settlement.menu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sandbox.settlement.menu.entity.key.AdminMenuRoleDetailKey;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.LocalDateTime;

@Data
@Entity
@IdClass(AdminMenuRoleDetailKey.class)
public class AdminMenuRoleDetail {

    @Id
    private Integer menuRoleNo;

    @Id
    private Integer menuNo;

    private Integer authCode;
    private Boolean ciReadFlag;
    private Boolean dnAvailFlag;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @CreationTimestamp
    private LocalDateTime regDate;

    private String regId;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime updDate;
    private String updId;


}
