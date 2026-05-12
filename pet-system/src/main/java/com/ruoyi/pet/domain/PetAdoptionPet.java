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
@TableName("pet_adoption_pet")
public class PetAdoptionPet extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long publisherUserId;
    private String publisherType;
    private Long merchantId;
    private String name;
    private String species;
    private String breed;
    private String gender;
    private Integer ageMonths;
    private String city;
    private String district;
    private String coverUrl;
    private String imageUrls;
    private String healthStatus;
    private String vaccineStatus;
    private Integer neutered;
    private String personality;
    private String sourceDesc;
    private String adoptionRequirements;
    private Integer status;
    private String auditReason;
    private Long adoptedUserId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date adoptedTime;

    @TableField(exist = false)
    private String publisherName;
    @TableField(exist = false)
    private String merchantName;
    @TableField(exist = false)
    private String adoptedUserName;
    @TableField(exist = false)
    private Integer applicationCount;
}
