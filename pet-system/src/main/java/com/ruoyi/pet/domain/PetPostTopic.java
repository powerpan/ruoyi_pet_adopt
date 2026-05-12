package com.ruoyi.pet.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("pet_post_topic")
public class PetPostTopic {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;
    private Long topicId;
}
