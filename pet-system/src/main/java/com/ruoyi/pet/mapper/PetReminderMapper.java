package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetReminder;

public interface PetReminderMapper extends BaseMapper<PetReminder> {
    @Select({
        "<script>",
        "select r.*, u.nick_name as ownerName, p.name as petName",
        "from pet_reminder r",
        "left join sys_user u on r.user_id = u.user_id",
        "left join pet_profile p on r.pet_id = p.id",
        "where 1 = 1",
        "<if test='status != null'>and r.status = #{status}</if>",
        "<if test='petId != null'>and r.pet_id = #{petId}</if>",
        "order by r.due_time asc",
        "</script>"
    })
    @Results({
        @Result(property = "ownerName", column = "ownerName"),
        @Result(property = "petName", column = "petName")
    })
    List<PetReminder> selectManagerReminders(@Param("status") Integer status,
                                             @Param("petId") Long petId);
}
