package com.ruoyi.pet.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pet.domain.PetServiceItem;

public interface PetServiceItemMapper extends BaseMapper<PetServiceItem> {
    @Select({
        "<script>",
        "select s.*, m.name as merchantName, m.address as merchantAddress, m.logo_url as merchantLogoUrl, m.longitude, m.latitude",
        "from pet_service_item s",
        "inner join pet_merchant m on s.merchant_id = m.id",
        "where s.status = 0 and m.status = 0 and m.qualification_status = 1",
        "<if test='serviceType != null and serviceType != \"\"'>and s.service_type = #{serviceType}</if>",
        "<if test='keyword != null and keyword != \"\"'>and (s.service_name like concat('%', #{keyword}, '%') or m.name like concat('%', #{keyword}, '%'))</if>",
        "order by s.review_score desc, s.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "merchantName", column = "merchantName"),
        @Result(property = "merchantAddress", column = "merchantAddress"),
        @Result(property = "merchantLogoUrl", column = "merchantLogoUrl"),
        @Result(property = "longitude", column = "longitude"),
        @Result(property = "latitude", column = "latitude")
    })
    List<PetServiceItem> selectVisible(@Param("serviceType") String serviceType,
                                       @Param("keyword") String keyword);

    @Select({
        "<script>",
        "select s.*, m.name as merchantName, m.address as merchantAddress, m.logo_url as merchantLogoUrl, m.longitude, m.latitude,",
        "round(6371 * 2 * asin(sqrt(pow(sin((radians(#{latitude}) - radians(m.latitude)) / 2), 2) +",
        "cos(radians(#{latitude})) * cos(radians(m.latitude)) * pow(sin((radians(#{longitude}) - radians(m.longitude)) / 2), 2))), 2) as distanceKm",
        "from pet_service_item s",
        "inner join pet_merchant m on s.merchant_id = m.id",
        "where s.status = 0 and m.status = 0 and m.qualification_status = 1",
        "and m.latitude is not null and m.longitude is not null",
        "<if test='serviceType != null and serviceType != \"\"'>and s.service_type = #{serviceType}</if>",
        "<if test='keyword != null and keyword != \"\"'>and (s.service_name like concat('%', #{keyword}, '%') or m.name like concat('%', #{keyword}, '%'))</if>",
        "having distanceKm &lt;= #{radiusKm}",
        "order by distanceKm asc, s.review_score desc, s.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "merchantName", column = "merchantName"),
        @Result(property = "merchantAddress", column = "merchantAddress"),
        @Result(property = "merchantLogoUrl", column = "merchantLogoUrl"),
        @Result(property = "longitude", column = "longitude"),
        @Result(property = "latitude", column = "latitude"),
        @Result(property = "distanceKm", column = "distanceKm")
    })
    List<PetServiceItem> selectNearby(@Param("latitude") BigDecimal latitude,
                                      @Param("longitude") BigDecimal longitude,
                                      @Param("radiusKm") BigDecimal radiusKm,
                                      @Param("serviceType") String serviceType,
                                      @Param("keyword") String keyword);

    @Select({
        "<script>",
        "select s.*, m.name as merchantName, m.address as merchantAddress, m.logo_url as merchantLogoUrl, m.longitude, m.latitude",
        "from pet_service_item s",
        "left join pet_merchant m on s.merchant_id = m.id",
        "where 1 = 1",
        "<if test='serviceName != null and serviceName != \"\"'>and s.service_name like concat('%', #{serviceName}, '%')</if>",
        "<if test='serviceType != null and serviceType != \"\"'>and s.service_type = #{serviceType}</if>",
        "order by s.create_time desc",
        "</script>"
    })
    @Results({
        @Result(property = "merchantName", column = "merchantName"),
        @Result(property = "merchantAddress", column = "merchantAddress"),
        @Result(property = "merchantLogoUrl", column = "merchantLogoUrl"),
        @Result(property = "longitude", column = "longitude"),
        @Result(property = "latitude", column = "latitude")
    })
    List<PetServiceItem> selectManagerServices(@Param("serviceName") String serviceName,
                                               @Param("serviceType") String serviceType);
}
