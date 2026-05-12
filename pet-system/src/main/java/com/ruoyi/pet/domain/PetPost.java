package com.ruoyi.pet.domain;

import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pet_post")
public class PetPost extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long authorId;
    private Long petId;
    private String title;
    private String content;
    private String postType;
    private String coverUrl;
    private Integer auditStatus;
    private Integer visibility;
    private Integer status;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private Integer viewCount;

    @TableField(exist = false)
    private List<String> mediaUrls;

    @TableField(exist = false)
    private List<Long> topicIds;

    @TableField(exist = false)
    private Boolean liked;

    @TableField(exist = false)
    private Boolean favorited;

    @TableField(exist = false)
    private String authorName;

    @TableField(exist = false)
    private String petName;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date favoriteTime;
}
