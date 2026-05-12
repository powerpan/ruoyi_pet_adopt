package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetHealthRecord;

public interface PetHealthRecordMapper extends BaseMapper<PetHealthRecord> {
    @Select({
        "<script>",
        "select h.*, u.nick_name as ownerName, p.name as petName",
        "from pet_health_record h",
        "left join sys_user u on h.user_id = u.user_id",
        "left join pet_profile p on h.pet_id = p.id",
        "where 1 = 1",
        "<if test='petId != null'>and h.pet_id = #{petId}</if>",
        "order by h.record_date desc, h.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "ownerName", column = "ownerName"),
        @Result(property = "petName", column = "petName")
    })
    List<PetHealthRecord> selectManagerRecords(@Param("petId") Long petId);
}
