package com.ruoyi.pet.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pet_adoption_message")
public class PetAdoptionMessage extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private Long adoptionPetId;
    private Long senderUserId;
    private String senderRole;
    private String messageType;
    private String content;

    @TableField(exist = false)
    private String senderName;
}
