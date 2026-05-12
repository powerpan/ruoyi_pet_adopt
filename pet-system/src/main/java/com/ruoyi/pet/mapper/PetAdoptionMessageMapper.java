package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetAdoptionMessage;

public interface PetAdoptionMessageMapper extends BaseMapper<PetAdoptionMessage> {
    @Select({
        "select msg.*, u.nick_name as senderName",
        "from pet_adoption_message msg",
        "left join sys_user u on msg.sender_user_id = u.user_id",
        "where msg.application_id = #{applicationId}",
        "order by msg.create_time asc, msg.id asc"
    })
    @Results(@Result(property = "senderName", column = "senderName"))
    List<PetAdoptionMessage> selectByApplicationId(@Param("applicationId") Long applicationId);
}
