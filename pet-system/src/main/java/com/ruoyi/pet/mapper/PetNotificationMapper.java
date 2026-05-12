package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetNotification;

public interface PetNotificationMapper extends BaseMapper<PetNotification> {
    @Select({
        "<script>",
        "select n.*, u.nick_name as actorName",
        "from pet_notification n",
        "left join sys_user u on n.actor_user_id = u.user_id",
        "where n.receiver_user_id = #{receiverUserId}",
        "<if test='status != null'>and n.status = #{status}</if>",
        "<if test='noticeType != null and noticeType != \"\"'>and n.notice_type = #{noticeType}</if>",
        "order by n.create_time desc, n.id desc",
        "</script>"
    })
    @Results(@Result(property = "actorName", column = "actorName"))
    List<PetNotification> selectUserNotifications(@Param("receiverUserId") Long receiverUserId,
                                                   @Param("status") Integer status,
                                                   @Param("noticeType") String noticeType);

    @Select("select count(1) from pet_notification where receiver_user_id = #{receiverUserId} and status = 0")
    long countUnread(@Param("receiverUserId") Long receiverUserId);
}
