package com.ruoyi.pet.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pet_merchant_qualification")
public class PetMerchantQualification extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long merchantId;
    private String materialType;
    private String materialUrl;
    private Integer auditStatus;
    private String auditRemark;
}
