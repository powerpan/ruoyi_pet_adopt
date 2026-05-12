package com.ruoyi.pet.domain;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pet_service_review")
public class PetServiceReview extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long serviceId;
    private Long merchantId;
    private Long requestId;
    private Integer rating;
    private String content;
    private String imageUrls;
    private Integer status;
    private Integer hideStatus;
    private String hideReason;
    private String hideAuditReason;
    private Integer topFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date topTime;

    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String serviceName;
    @TableField(exist = false)
    private String merchantName;
}
