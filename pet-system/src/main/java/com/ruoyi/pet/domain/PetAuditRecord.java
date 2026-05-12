package com.ruoyi.pet.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pet_audit_record")
public class PetAuditRecord extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long auditorId;
    private String targetType;
    private Long targetId;
    private Integer auditStatus;
    private String auditReason;
}
