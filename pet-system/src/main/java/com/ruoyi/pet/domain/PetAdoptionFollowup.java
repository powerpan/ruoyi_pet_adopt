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
@TableName("pet_adoption_followup")
public class PetAdoptionFollowup extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private Long adoptionPetId;
    private Long adopterUserId;
    private Long publisherUserId;
    private Integer followupRound;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date planTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualTime;
    private String healthStatus;
    private String livingStatus;
    private String imageUrls;
    private Integer status;
    private String abnormalReason;
    private String handleResult;

    @TableField(exist = false)
    private String petName;
    @TableField(exist = false)
    private String petCoverUrl;
    @TableField(exist = false)
    private String adopterName;
    @TableField(exist = false)
    private String publisherName;
}
