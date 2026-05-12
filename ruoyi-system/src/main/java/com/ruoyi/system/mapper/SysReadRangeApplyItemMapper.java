package com.ruoyi.system.mapper;

import java.util.List;

import com.ruoyi.system.domain.SysReadRangeApplyItem;

/**
 * 【分层-Mapper / DAO】申请明细 {@code sys_read_range_apply_item}（每类型预览/下载申请与审核结果）。
 */
public interface SysReadRangeApplyItemMapper
{
    List<SysReadRangeApplyItem> selectItemsByApplyId(Long applyId);

    int insertSysReadRangeApplyItem(SysReadRangeApplyItem item);

    int updateAuditSysReadRangeApplyItem(SysReadRangeApplyItem item);
}

