package com.sandbox.settlement.admin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class AdminPwdReset {

    @Id
    private Integer adminNo;

    private String token;
    private Boolean authFlag;
    private LocalDateTime authExpDate;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @CreationTimestamp
    private LocalDateTime regDate;

    private String regId;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime updDate;
    private String updId;


}
