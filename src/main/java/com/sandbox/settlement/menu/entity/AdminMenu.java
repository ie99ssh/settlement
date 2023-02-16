package com.sandbox.settlement.menu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class AdminMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer menuNo;
    private String menuName;
    private Integer menuGroupNo;
    private String menuDivSegment;
    private String menuLink;
    private String menuDesc;
    private Integer sortOrder;
    private Boolean useFlag = true;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @CreationTimestamp
    private LocalDateTime regDate;

    private String regId;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime updDate;
    private String updId;

    @ManyToOne
    @JoinColumn(name = "menuGroupNo", insertable = false, updatable = false)
    private AdminMenuGroup adminMenuGroup;

}
