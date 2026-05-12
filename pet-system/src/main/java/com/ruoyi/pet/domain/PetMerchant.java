package com.ruoyi.pet.domain;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pet_merchant")
public class PetMerchant extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String contactName;
    private String phone;
    private String city;
    private String district;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer qualificationStatus;
    private Integer status;
    private String description;
    private String logoUrl;
    private BigDecimal score;
    private Integer reviewCount;

    @TableField(exist = false)
    private String ownerName;
}
