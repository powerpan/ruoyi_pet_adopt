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
@TableName("pet_service_item")
public class PetServiceItem extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long merchantId;
    private String serviceName;
    private String serviceType;
    private String description;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private String coverUrl;
    private Integer status;
    private BigDecimal reviewScore;
    private Integer reviewCount;

    @TableField(exist = false)
    private String merchantName;
    @TableField(exist = false)
    private String merchantAddress;
    @TableField(exist = false)
    private String merchantLogoUrl;
    @TableField(exist = false)
    private BigDecimal longitude;
    @TableField(exist = false)
    private BigDecimal latitude;
    @TableField(exist = false)
    private BigDecimal distanceKm;
}
