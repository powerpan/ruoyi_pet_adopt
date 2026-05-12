package com.ruoyi.pet.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pet_user_profile")
public class PetUserProfile extends PetBaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String nickname;
    private String avatar;
    private String bio;
    private String homepageCover;
    private Integer bloggerStatus;
    private Integer followerCount;
    private Integer followingCount;
    private Integer postCount;
}
