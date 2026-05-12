package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetServiceMessage;

public interface PetServiceMessageMapper extends BaseMapper<PetServiceMessage> {
    @Select({
        "select msg.*, u.nick_name as senderName",
        "from pet_service_message msg",
        "left join sys_user u on msg.sender_user_id = u.user_id",
        "where msg.request_id = #{requestId}",
        "order by msg.create_time asc, msg.id asc"
    })
    @Results(@Result(property = "senderName", column = "senderName"))
    List<PetServiceMessage> selectByRequestId(@Param("requestId") Long requestId);
}
