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
@TableName("pet_health_record")
public class PetHealthRecord extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long petId;
    private String recordType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date recordDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date nextDueDate;
    private String title;
    private String description;
    private String attachmentUrls;
    private Long boardingRelationId;
    private Long sourceRecordId;

    @TableField(exist = false)
    private String ownerName;
    @TableField(exist = false)
    private String petName;
}
