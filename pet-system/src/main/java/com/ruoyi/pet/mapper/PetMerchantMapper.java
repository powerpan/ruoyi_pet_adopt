package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetMerchant;

public interface PetMerchantMapper extends BaseMapper<PetMerchant> {
    @Select({
        "<script>",
        "select m.*, u.nick_name as ownerName",
        "from pet_merchant m",
        "left join sys_user u on m.user_id = u.user_id",
        "where 1 = 1",
        "<if test='name != null and name != \"\"'>and m.name like concat('%', #{name}, '%')</if>",
        "<if test='qualificationStatus != null'>and m.qualification_status = #{qualificationStatus}</if>",
        "<if test='qualificationStatus == null and statusScope != null and statusScope == \"history\"'>and m.qualification_status in (1, 2, 3)</if>",
        "order by m.create_time desc",
        "</script>"
    })
    @Results(@Result(property = "ownerName", column = "ownerName"))
    List<PetMerchant> selectManagerMerchants(@Param("name") String name,
                                             @Param("qualificationStatus") Integer qualificationStatus,
                                             @Param("statusScope") String statusScope);
}
