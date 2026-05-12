package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetAdoptionFollowup;

public interface PetAdoptionFollowupMapper extends BaseMapper<PetAdoptionFollowup> {
    @Select({
        "<script>",
        "select f.*, p.name as petName, p.cover_url as petCoverUrl, au.nick_name as adopterName, pu.nick_name as publisherName",
        "from pet_adoption_followup f",
        "left join pet_adoption_pet p on f.adoption_pet_id = p.id",
        "left join sys_user au on f.adopter_user_id = au.user_id",
        "left join sys_user pu on f.publisher_user_id = pu.user_id",
        "where (f.adopter_user_id = #{userId} or f.publisher_user_id = #{userId})",
        "<if test='status != null'>and f.status = #{status}</if>",
        "order by f.plan_time asc, f.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "petName", column = "petName"),
        @Result(property = "petCoverUrl", column = "petCoverUrl"),
        @Result(property = "adopterName", column = "adopterName"),
        @Result(property = "publisherName", column = "publisherName")
    })
    List<PetAdoptionFollowup> selectUserFollowups(@Param("userId") Long userId,
                                                  @Param("status") Integer status);

    @Select({
        "<script>",
        "select f.*, p.name as petName, p.cover_url as petCoverUrl, au.nick_name as adopterName, pu.nick_name as publisherName",
        "from pet_adoption_followup f",
        "left join pet_adoption_pet p on f.adoption_pet_id = p.id",
        "left join sys_user au on f.adopter_user_id = au.user_id",
        "left join sys_user pu on f.publisher_user_id = pu.user_id",
        "where 1 = 1",
        "<if test='status != null'>and f.status = #{status}</if>",
        "<if test='keyword != null and keyword != \"\"'>and (p.name like concat('%', #{keyword}, '%') or au.nick_name like concat('%', #{keyword}, '%') or pu.nick_name like concat('%', #{keyword}, '%'))</if>",
        "order by f.plan_time asc, f.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "petName", column = "petName"),
        @Result(property = "petCoverUrl", column = "petCoverUrl"),
        @Result(property = "adopterName", column = "adopterName"),
        @Result(property = "publisherName", column = "publisherName")
    })
    List<PetAdoptionFollowup> selectManagerFollowups(@Param("status") Integer status,
                                                     @Param("keyword") String keyword);
}
