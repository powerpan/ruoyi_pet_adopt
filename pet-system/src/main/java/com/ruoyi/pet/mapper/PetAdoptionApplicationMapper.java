package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetAdoptionApplication;

public interface PetAdoptionApplicationMapper extends BaseMapper<PetAdoptionApplication> {
    @Select({
        "<script>",
        "select a.*, p.name as petName, p.cover_url as petCoverUrl, p.status as petStatus,",
        "au.nick_name as applicantName, pu.nick_name as publisherName",
        "from pet_adoption_application a",
        "left join pet_adoption_pet p on a.adoption_pet_id = p.id",
        "left join sys_user au on a.applicant_user_id = au.user_id",
        "left join sys_user pu on a.publisher_user_id = pu.user_id",
        "where a.applicant_user_id = #{userId}",
        "<if test='status != null'>and a.status = #{status}</if>",
        "order by a.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "petName", column = "petName"),
        @Result(property = "petCoverUrl", column = "petCoverUrl"),
        @Result(property = "petStatus", column = "petStatus"),
        @Result(property = "applicantName", column = "applicantName"),
        @Result(property = "publisherName", column = "publisherName")
    })
    List<PetAdoptionApplication> selectUserApplications(@Param("userId") Long userId,
                                                        @Param("status") Integer status);

    @Select({
        "<script>",
        "select a.*, p.name as petName, p.cover_url as petCoverUrl, p.status as petStatus,",
        "au.nick_name as applicantName, pu.nick_name as publisherName",
        "from pet_adoption_application a",
        "left join pet_adoption_pet p on a.adoption_pet_id = p.id",
        "left join sys_user au on a.applicant_user_id = au.user_id",
        "left join sys_user pu on a.publisher_user_id = pu.user_id",
        "where a.publisher_user_id = #{publisherUserId}",
        "<if test='status != null'>and a.status = #{status}</if>",
        "<if test='adoptionPetId != null'>and a.adoption_pet_id = #{adoptionPetId}</if>",
        "order by a.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "petName", column = "petName"),
        @Result(property = "petCoverUrl", column = "petCoverUrl"),
        @Result(property = "petStatus", column = "petStatus"),
        @Result(property = "applicantName", column = "applicantName"),
        @Result(property = "publisherName", column = "publisherName")
    })
    List<PetAdoptionApplication> selectReceivedApplications(@Param("publisherUserId") Long publisherUserId,
                                                            @Param("status") Integer status,
                                                            @Param("adoptionPetId") Long adoptionPetId);

    @Select({
        "<script>",
        "select a.*, p.name as petName, p.cover_url as petCoverUrl, p.status as petStatus,",
        "au.nick_name as applicantName, pu.nick_name as publisherName",
        "from pet_adoption_application a",
        "left join pet_adoption_pet p on a.adoption_pet_id = p.id",
        "left join sys_user au on a.applicant_user_id = au.user_id",
        "left join sys_user pu on a.publisher_user_id = pu.user_id",
        "where 1 = 1",
        "<if test='status != null'>and a.status = #{status}</if>",
        "<if test='keyword != null and keyword != \"\"'>and (p.name like concat('%', #{keyword}, '%') or au.nick_name like concat('%', #{keyword}, '%') or a.real_name like concat('%', #{keyword}, '%'))</if>",
        "order by a.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "petName", column = "petName"),
        @Result(property = "petCoverUrl", column = "petCoverUrl"),
        @Result(property = "petStatus", column = "petStatus"),
        @Result(property = "applicantName", column = "applicantName"),
        @Result(property = "publisherName", column = "publisherName")
    })
    List<PetAdoptionApplication> selectManagerApplications(@Param("status") Integer status,
                                                           @Param("keyword") String keyword);
}
