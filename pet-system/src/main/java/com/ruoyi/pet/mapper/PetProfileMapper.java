package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetProfile;

public interface PetProfileMapper extends BaseMapper<PetProfile> {
    @Select({
        "<script>",
        "select p.*, u.nick_name as ownerName",
        "from pet_profile p",
        "left join sys_user u on p.user_id = u.user_id",
        "where 1 = 1",
        "<if test='userId != null'>and p.user_id = #{userId}</if>",
        "<if test='name != null and name != \"\"'>and p.name like concat('%', #{name}, '%')</if>",
        "order by p.create_time desc",
        "</script>"
    })
    @Results(@Result(property = "ownerName", column = "ownerName"))
    List<PetProfile> selectManagerPets(@Param("userId") Long userId,
                                       @Param("name") String name);
}
