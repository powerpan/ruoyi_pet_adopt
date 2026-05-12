package com.ruoyi.pet.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pet_comment")
public class PetComment extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;
    private Long parentId;
    private Long userId;
    private String content;
    private Integer auditStatus;
    private Integer status;
    private Integer likeCount;

    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String postTitle;
}
