package com.sandbox.settlement.menu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class AdminMenuGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer menuGroupNo;

    private String menuGroupName;
    private String menuGroupIcon;
    private Integer sortOrder;
    private Boolean useFlag = true;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @CreationTimestamp
    private LocalDateTime regDate;

    private String regId;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime updDate;
    private String updId;

}
