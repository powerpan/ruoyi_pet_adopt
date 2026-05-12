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
@TableName("pet_boarding_relation")
public class PetBoardingRelation extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long merchantId;
    private Long merchantUserId;
    private Long ownerUserId;
    private Long ownerPetId;
    private Long merchantPetId;
    private String petName;
    private String requestNote;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date requestTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    private Long cancelBy;

    @TableField(exist = false)
    private String merchantName;

    @TableField(exist = false)
    private String merchantLogoUrl;

    @TableField(exist = false)
    private String ownerUserName;

    @TableField(exist = false)
    private String ownerNickName;

    @TableField(exist = false)
    private String merchantUserName;

    @TableField(exist = false)
    private String merchantNickName;
}
