package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetPost;

public interface PetPostMapper extends BaseMapper<PetPost> {
    @Select({
        "<script>",
        "select p.*, u.nick_name as authorName, pet.name as petName",
        "from pet_post p",
        "left join sys_user u on p.author_id = u.user_id",
        "left join pet_profile pet on p.pet_id = pet.id",
        "where 1 = 1",
        "<if test='auditStatus != null'>and p.audit_status = #{auditStatus}</if>",
        "<if test='auditStatus == null and statusScope != null and statusScope == \"history\"'>and p.audit_status in (1, 2)</if>",
        "<if test='title != null and title != \"\"'>and p.title like concat('%', #{title}, '%')</if>",
        "order by p.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "authorName", column = "authorName"),
        @Result(property = "petName", column = "petName")
    })
    List<PetPost> selectManagerPosts(@Param("auditStatus") Integer auditStatus,
                                     @Param("statusScope") String statusScope,
                                     @Param("title") String title);

    @Select({
        "<script>",
        "select p.*, u.nick_name as authorName, pet.name as petName, i.create_time as favoriteTime",
        "from pet_interaction i",
        "inner join pet_post p on i.target_id = p.id",
        "left join sys_user u on p.author_id = u.user_id",
        "left join pet_profile pet on p.pet_id = pet.id",
        "where i.user_id = #{userId} and i.target_type = 'post' and i.interaction_type = 'favorite'",
        "and p.audit_status = 1 and p.status = 0",
        "<if test='keyword != null and keyword != \"\"'>and (p.title like concat('%', #{keyword}, '%') or p.content like concat('%', #{keyword}, '%'))</if>",
        "order by i.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "authorName", column = "authorName"),
        @Result(property = "petName", column = "petName"),
        @Result(property = "favoriteTime", column = "favoriteTime")
    })
    List<PetPost> selectFavoritePosts(@Param("userId") Long userId, @Param("keyword") String keyword);
}
