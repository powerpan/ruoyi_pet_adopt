package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetAdoptionPet;

public interface PetAdoptionPetMapper extends BaseMapper<PetAdoptionPet> {
    @Select({
        "<script>",
        "select p.*, pu.nick_name as publisherName, au.nick_name as adoptedUserName, m.name as merchantName,",
        "(select count(1) from pet_adoption_application a where a.adoption_pet_id = p.id) as applicationCount",
        "from pet_adoption_pet p",
        "left join sys_user pu on p.publisher_user_id = pu.user_id",
        "left join sys_user au on p.adopted_user_id = au.user_id",
        "left join pet_merchant m on p.merchant_id = m.id",
        "where p.status in (2, 3, 4)",
        "<if test='status != null'>and p.status = #{status}</if>",
        "<if test='species != null and species != \"\"'>and p.species = #{species}</if>",
        "<if test='city != null and city != \"\"'>and p.city like concat('%', #{city}, '%')</if>",
        "<if test='keyword != null and keyword != \"\"'>and (p.name like concat('%', #{keyword}, '%') or p.breed like concat('%', #{keyword}, '%') or p.adoption_requirements like concat('%', #{keyword}, '%'))</if>",
        "order by p.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "publisherName", column = "publisherName"),
        @Result(property = "adoptedUserName", column = "adoptedUserName"),
        @Result(property = "merchantName", column = "merchantName"),
        @Result(property = "applicationCount", column = "applicationCount")
    })
    List<PetAdoptionPet> selectPublicAdoptions(@Param("keyword") String keyword,
                                               @Param("species") String species,
                                               @Param("city") String city,
                                               @Param("status") Integer status);

    @Select({
        "<script>",
        "select p.*, pu.nick_name as publisherName, au.nick_name as adoptedUserName, m.name as merchantName,",
        "(select count(1) from pet_adoption_application a where a.adoption_pet_id = p.id) as applicationCount",
        "from pet_adoption_pet p",
        "left join sys_user pu on p.publisher_user_id = pu.user_id",
        "left join sys_user au on p.adopted_user_id = au.user_id",
        "left join pet_merchant m on p.merchant_id = m.id",
        "where p.publisher_user_id = #{publisherUserId}",
        "<if test='status != null'>and p.status = #{status}</if>",
        "<if test='keyword != null and keyword != \"\"'>and (p.name like concat('%', #{keyword}, '%') or p.breed like concat('%', #{keyword}, '%'))</if>",
        "order by p.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "publisherName", column = "publisherName"),
        @Result(property = "adoptedUserName", column = "adoptedUserName"),
        @Result(property = "merchantName", column = "merchantName"),
        @Result(property = "applicationCount", column = "applicationCount")
    })
    List<PetAdoptionPet> selectUserPublished(@Param("publisherUserId") Long publisherUserId,
                                             @Param("status") Integer status,
                                             @Param("keyword") String keyword);

    @Select({
        "<script>",
        "select p.*, pu.nick_name as publisherName, au.nick_name as adoptedUserName, m.name as merchantName,",
        "(select count(1) from pet_adoption_application a where a.adoption_pet_id = p.id) as applicationCount",
        "from pet_adoption_pet p",
        "left join sys_user pu on p.publisher_user_id = pu.user_id",
        "left join sys_user au on p.adopted_user_id = au.user_id",
        "left join pet_merchant m on p.merchant_id = m.id",
        "where 1 = 1",
        "<if test='status != null'>and p.status = #{status}</if>",
        "<if test='status == null and statusScope != null and statusScope == \"history\"'>and p.status in (2, 3, 4, 5, 6)</if>",
        "<if test='keyword != null and keyword != \"\"'>and (p.name like concat('%', #{keyword}, '%') or p.breed like concat('%', #{keyword}, '%') or pu.nick_name like concat('%', #{keyword}, '%'))</if>",
        "order by p.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "publisherName", column = "publisherName"),
        @Result(property = "adoptedUserName", column = "adoptedUserName"),
        @Result(property = "merchantName", column = "merchantName"),
        @Result(property = "applicationCount", column = "applicationCount")
    })
    List<PetAdoptionPet> selectManagerAdoptions(@Param("status") Integer status,
                                                @Param("statusScope") String statusScope,
                                                @Param("keyword") String keyword);
}
