package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetServiceRequest;

public interface PetServiceRequestMapper extends BaseMapper<PetServiceRequest> {
    @Select({
        "<script>",
        "select r.*, u.nick_name as userName, s.service_name as serviceName, s.service_type as serviceType, m.name as merchantName, p.name as petName,",
        "case when exists(select 1 from pet_service_review rv where rv.request_id = r.id) then 1 else 0 end as reviewed",
        "from pet_service_request r",
        "inner join pet_merchant m on r.merchant_id = m.id",
        "left join sys_user u on r.user_id = u.user_id",
        "left join pet_service_item s on r.service_id = s.id",
        "left join pet_profile p on r.pet_id = p.id",
        "where m.user_id = #{ownerUserId}",
        "<if test='merchantId != null'>and r.merchant_id = #{merchantId}</if>",
        "<if test='status != null'>and r.status = #{status}</if>",
        "order by r.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "userName", column = "userName"),
        @Result(property = "serviceName", column = "serviceName"),
        @Result(property = "serviceType", column = "serviceType"),
        @Result(property = "merchantName", column = "merchantName"),
        @Result(property = "petName", column = "petName")
    })
    List<PetServiceRequest> selectMerchantRequests(@Param("ownerUserId") Long ownerUserId,
                                                   @Param("merchantId") Long merchantId,
                                                   @Param("status") Integer status);

    @Select({
        "<script>",
        "select r.*, u.nick_name as userName, s.service_name as serviceName, s.service_type as serviceType, m.name as merchantName, p.name as petName,",
        "case when exists(select 1 from pet_service_review rv where rv.request_id = r.id) then 1 else 0 end as reviewed",
        "from pet_service_request r",
        "inner join pet_merchant m on r.merchant_id = m.id",
        "left join sys_user u on r.user_id = u.user_id",
        "left join pet_service_item s on r.service_id = s.id",
        "left join pet_profile p on r.pet_id = p.id",
        "where r.user_id = #{userId}",
        "<if test='status != null'>and r.status = #{status}</if>",
        "order by r.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "userName", column = "userName"),
        @Result(property = "serviceName", column = "serviceName"),
        @Result(property = "serviceType", column = "serviceType"),
        @Result(property = "merchantName", column = "merchantName"),
        @Result(property = "petName", column = "petName")
    })
    List<PetServiceRequest> selectUserRequests(@Param("userId") Long userId,
                                               @Param("status") Integer status);

    @Select({
        "<script>",
        "select r.*, u.nick_name as userName, s.service_name as serviceName, s.service_type as serviceType, m.name as merchantName, p.name as petName,",
        "case when exists(select 1 from pet_service_review rv where rv.request_id = r.id) then 1 else 0 end as reviewed",
        "from pet_service_request r",
        "inner join pet_merchant m on r.merchant_id = m.id",
        "left join sys_user u on r.user_id = u.user_id",
        "left join pet_service_item s on r.service_id = s.id",
        "left join pet_profile p on r.pet_id = p.id",
        "where 1 = 1",
        "<if test='status != null'>and r.status = #{status}</if>",
        "order by r.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "userName", column = "userName"),
        @Result(property = "serviceName", column = "serviceName"),
        @Result(property = "serviceType", column = "serviceType"),
        @Result(property = "merchantName", column = "merchantName"),
        @Result(property = "petName", column = "petName")
    })
    List<PetServiceRequest> selectManagerRequests(@Param("status") Integer status);
}
