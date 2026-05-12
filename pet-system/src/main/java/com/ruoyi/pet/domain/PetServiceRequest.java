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
@TableName("pet_service_request")
public class PetServiceRequest extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long serviceId;
    private Long merchantId;
    private Long petId;
    private String contactPhone;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date serviceTime;
    private Integer status;
    private String message;

    @TableField(exist = false)
    private String serviceName;
    @TableField(exist = false)
    private String serviceType;
    @TableField(exist = false)
    private String merchantName;
    @TableField(exist = false)
    private String petName;
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private Integer reviewed;
}
