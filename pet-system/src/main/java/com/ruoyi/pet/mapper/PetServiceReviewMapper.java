package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetServiceReview;

public interface PetServiceReviewMapper extends BaseMapper<PetServiceReview> {
    @Select({
        "<script>",
        "select r.*, u.nick_name as userName, s.service_name as serviceName, m.name as merchantName",
        "from pet_service_review r",
        "left join sys_user u on r.user_id = u.user_id",
        "left join pet_service_item s on r.service_id = s.id",
        "left join pet_merchant m on r.merchant_id = m.id",
        "where r.service_id = #{serviceId} and r.status = 1",
        "<if test='ratingType != null and ratingType == \"good\"'>and r.rating &gt;= 4</if>",
        "<if test='ratingType != null and ratingType == \"middle\"'>and r.rating = 3</if>",
        "<if test='ratingType != null and ratingType == \"bad\"'>and r.rating &lt;= 2</if>",
        "<choose>",
        "<when test='ratingType == null or ratingType == \"\"'>order by ifnull(r.top_flag, 0) desc, r.top_time desc, r.create_time desc</when>",
        "<otherwise>order by r.create_time desc</otherwise>",
        "</choose>",
        "</script>"
    })
    @Results({
        @Result(property = "topFlag", column = "top_flag"),
        @Result(property = "topTime", column = "top_time"),
        @Result(property = "userName", column = "userName"),
        @Result(property = "serviceName", column = "serviceName"),
        @Result(property = "merchantName", column = "merchantName")
    })
    List<PetServiceReview> selectPublicReviews(@Param("serviceId") Long serviceId,
                                               @Param("ratingType") String ratingType);

    @Select({
        "<script>",
        "select r.*, u.nick_name as userName, s.service_name as serviceName, m.name as merchantName",
        "from pet_service_review r",
        "inner join pet_merchant m on r.merchant_id = m.id",
        "left join sys_user u on r.user_id = u.user_id",
        "left join pet_service_item s on r.service_id = s.id",
        "where m.user_id = #{ownerUserId}",
        "<if test='merchantId != null'>and r.merchant_id = #{merchantId}</if>",
        "<if test='hideStatus != null'>and r.hide_status = #{hideStatus}</if>",
        "<if test='ratingType != null and ratingType == \"good\"'>and r.rating &gt;= 4</if>",
        "<if test='ratingType != null and ratingType == \"middle\"'>and r.rating = 3</if>",
        "<if test='ratingType != null and ratingType == \"bad\"'>and r.rating &lt;= 2</if>",
        "<choose>",
        "<when test='ratingType == null or ratingType == \"\"'>order by ifnull(r.top_flag, 0) desc, r.top_time desc, r.create_time desc</when>",
        "<otherwise>order by r.create_time desc</otherwise>",
        "</choose>",
        "</script>"
    })
    @Results({
        @Result(property = "topFlag", column = "top_flag"),
        @Result(property = "topTime", column = "top_time"),
        @Result(property = "userName", column = "userName"),
        @Result(property = "serviceName", column = "serviceName"),
        @Result(property = "merchantName", column = "merchantName")
    })
    List<PetServiceReview> selectMerchantReviews(@Param("ownerUserId") Long ownerUserId,
                                                 @Param("merchantId") Long merchantId,
                                                 @Param("hideStatus") Integer hideStatus,
                                                 @Param("ratingType") String ratingType);

    @Select({
        "<script>",
        "select r.*, u.nick_name as userName, s.service_name as serviceName, m.name as merchantName",
        "from pet_service_review r",
        "left join sys_user u on r.user_id = u.user_id",
        "left join pet_service_item s on r.service_id = s.id",
        "left join pet_merchant m on r.merchant_id = m.id",
        "where 1 = 1",
        "<if test='status != null'>and r.status = #{status}</if>",
        "<if test='hideStatus != null'>and r.hide_status = #{hideStatus}</if>",
        "<if test='hideStatus == null and statusScope != null and statusScope == \"history\"'>and r.hide_status in (2, 3)</if>",
        "order by r.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "topFlag", column = "top_flag"),
        @Result(property = "topTime", column = "top_time"),
        @Result(property = "userName", column = "userName"),
        @Result(property = "serviceName", column = "serviceName"),
        @Result(property = "merchantName", column = "merchantName")
    })
    List<PetServiceReview> selectManagerReviews(@Param("status") Integer status,
                                                @Param("hideStatus") Integer hideStatus,
                                                @Param("statusScope") String statusScope);
}
