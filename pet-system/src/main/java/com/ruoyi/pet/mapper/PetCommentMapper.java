package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetComment;

public interface PetCommentMapper extends BaseMapper<PetComment> {
    @Select({
        "select c.*, u.nick_name as userName, p.title as postTitle",
        "from pet_comment c",
        "left join sys_user u on c.user_id = u.user_id",
        "left join pet_post p on c.post_id = p.id",
        "where c.post_id = #{postId} and c.audit_status = 1 and c.status = 0",
        "order by c.create_time asc"
    })
    @Results({
        @Result(property = "userName", column = "userName"),
        @Result(property = "postTitle", column = "postTitle")
    })
    List<PetComment> selectVisibleByPost(@Param("postId") Long postId);

    @Select({
        "<script>",
        "select c.*, u.nick_name as userName, p.title as postTitle",
        "from pet_comment c",
        "left join sys_user u on c.user_id = u.user_id",
        "left join pet_post p on c.post_id = p.id",
        "where 1 = 1",
        "<if test='postId != null'>and c.post_id = #{postId}</if>",
        "<if test='auditStatus != null'>and c.audit_status = #{auditStatus}</if>",
        "<if test='auditStatus == null and statusScope != null and statusScope == \"history\"'>and c.audit_status in (1, 2)</if>",
        "order by c.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "userName", column = "userName"),
        @Result(property = "postTitle", column = "postTitle")
    })
    List<PetComment> selectManagerComments(@Param("postId") Long postId,
                                           @Param("auditStatus") Integer auditStatus,
                                           @Param("statusScope") String statusScope);
}
