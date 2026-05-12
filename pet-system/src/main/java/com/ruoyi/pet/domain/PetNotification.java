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
@TableName("pet_notification")
public class PetNotification extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long receiverUserId;
    private Long actorUserId;
    private String noticeType;
    private String targetType;
    private Long targetId;
    private String title;
    private String content;
    private String actionUrl;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    @TableField(exist = false)
    private String actorName;
}
