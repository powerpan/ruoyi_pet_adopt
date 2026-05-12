package com.ruoyi.pet.domain;

import java.math.BigDecimal;
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
@TableName("pet_profile")
public class PetProfile extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String species;
    private String breed;
    private String gender;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    private BigDecimal weightKg;
    private String avatar;
    private String healthStatus;
    private Integer neutered;
    private Integer status;

    @TableField(exist = false)
    private String ownerName;
    @TableField(exist = false)
    private Boolean boardingFlag;
    @TableField(exist = false)
    private String boardingRole;
    @TableField(exist = false)
    private Long boardingRelationId;
    @TableField(exist = false)
    private String boardingMerchantName;
    @TableField(exist = false)
    private String boardingOwnerName;
}
