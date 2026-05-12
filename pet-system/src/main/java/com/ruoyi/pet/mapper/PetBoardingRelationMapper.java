package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetBoardingRelation;
import com.ruoyi.pet.domain.PetBoardingUserCandidate;

public interface PetBoardingRelationMapper extends BaseMapper<PetBoardingRelation> {
    @Select({
        "<script>",
        "select b.*, m.name as merchantName, m.logo_url as merchantLogoUrl,",
        "owner.user_name as ownerUserName, owner.nick_name as ownerNickName,",
        "merchantUser.user_name as merchantUserName, merchantUser.nick_name as merchantNickName",
        "from pet_boarding_relation b",
        "left join pet_merchant m on b.merchant_id = m.id",
        "left join sys_user owner on b.owner_user_id = owner.user_id",
        "left join sys_user merchantUser on b.merchant_user_id = merchantUser.user_id",
        "where b.owner_user_id = #{ownerUserId}",
        "<if test='status != null'>and b.status = #{status}</if>",
        "order by b.update_time desc, b.create_time desc, b.id desc",
        "</script>"
    })
    @Results({
        @Result(property = "merchantName", column = "merchantName"),
        @Result(property = "merchantLogoUrl", column = "merchantLogoUrl"),
        @Result(property = "ownerUserName", column = "ownerUserName"),
        @Result(property = "ownerNickName", column = "ownerNickName"),
        @Result(property = "merchantUserName", column = "merchantUserName"),
        @Result(property = "merchantNickName", column = "merchantNickName")
    })
    List<PetBoardingRelation> selectOwnerRelations(@Param("ownerUserId") Long ownerUserId,
                                                   @Param("status") Integer status);

    @Select({
        "<script>",
        "select b.*, m.name as merchantName, m.logo_url as merchantLogoUrl,",
        "owner.user_name as ownerUserName, owner.nick_name as ownerNickName,",
        "merchantUser.user_name as merchantUserName, merchantUser.nick_name as merchantNickName",
        "from pet_boarding_relation b",
        "inner join pet_merchant m on b.merchant_id = m.id",
        "left join sys_user owner on b.owner_user_id = owner.user_id",
        "left join sys_user merchantUser on b.merchant_user_id = merchantUser.user_id",
        "where m.user_id = #{merchantUserId}",
        "<if test='merchantId != null'>and b.merchant_id = #{merchantId}</if>",
        "<if test='status != null'>and b.status = #{status}</if>",
        "order by b.update_time desc, b.create_time desc, b.id desc",
        "</script>"
    })
    @Results({
        @Result(property = "merchantName", column = "merchantName"),
        @Result(property = "merchantLogoUrl", column = "merchantLogoUrl"),
        @Result(property = "ownerUserName", column = "ownerUserName"),
        @Result(property = "ownerNickName", column = "ownerNickName"),
        @Result(property = "merchantUserName", column = "merchantUserName"),
        @Result(property = "merchantNickName", column = "merchantNickName")
    })
    List<PetBoardingRelation> selectMerchantRelations(@Param("merchantUserId") Long merchantUserId,
                                                      @Param("merchantId") Long merchantId,
                                                      @Param("status") Integer status);

    @Select({
        "<script>",
        "select user_id as userId, user_name as userName, nick_name as nickName",
        "from sys_user",
        "where del_flag = '0' and status = '0'",
        "<if test='keyword != null and keyword != \"\"'>",
        "and (cast(user_id as char) = #{keyword} or user_name like concat('%', #{keyword}, '%'))",
        "</if>",
        "order by case when cast(user_id as char) = #{keyword} or user_name = #{keyword} then 0 else 1 end, user_id desc",
        "limit 10",
        "</script>"
    })
    List<PetBoardingUserCandidate> searchUserCandidates(@Param("keyword") String keyword);
}
