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
@TableName("pet_adoption_application")
public class PetAdoptionApplication extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long adoptionPetId;
    private Long applicantUserId;
    private Long publisherUserId;
    private String realName;
    private String phone;
    private String city;
    private String housingType;
    private String petExperience;
    private String applyReason;
    private String commitment;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date visitTime;
    private String visitAddress;
    private Integer status;
    private String reviewReason;

    @TableField(exist = false)
    private String petName;
    @TableField(exist = false)
    private String petCoverUrl;
    @TableField(exist = false)
    private String applicantName;
    @TableField(exist = false)
    private String publisherName;
    @TableField(exist = false)
    private Integer petStatus;
}
