package com.ruoyi.system.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.system.domain.SysReadRangeApply;

/**
 * 【分层-Mapper / DAO】申请单主表 {@code sys_read_range_apply}；XML 在 {@code ruoyi-system} 模块 {@code resources/mapper/system/} 下同名文件。
 */
public interface SysReadRangeApplyMapper
{
    SysReadRangeApply selectSysReadRangeApplyById(Long id);

    List<SysReadRangeApply> selectSysReadRangeApplyList(SysReadRangeApply apply);

    int insertSysReadRangeApply(SysReadRangeApply apply);

    int updateSysReadRangeApply(SysReadRangeApply apply);

    /**
     * 该用户最近一次权限申请单的创建时间（用于提交频率限制）
     */
    Date selectLatestCreateTimeByUserId(@Param("userId") Long userId);

    /**
     * 查询文件类型 id 列表（app_type.business 对应 fileType）
     */
    List<Long> selectAppTypeIdsByBusiness(String business);

    /**
     * 查询叶子类型 id：business 匹配且不存在子节点 parent_id 指向本 id
     */
    List<Long> selectLeafTypeIdsByBusiness(String business);

    /**
     * 某业务 + type_key 下的叶子类型（与客户端 type-list 按 typeKey 划分范围一致）
     */
    List<Long> selectLeafTypeIdsByBusinessAndTypeKey(@Param("business") String business, @Param("typeKey") String typeKey);
}

