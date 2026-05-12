package com.ruoyi.system.service;

import java.util.List;

import com.ruoyi.system.domain.SysReadRangeApply;
import com.ruoyi.system.domain.SysReadRangeApplyItem;

/**
 * 【分层-Service 接口】文档阅读范围申请单：创建、列表、明细、审核通过/拒绝、叶子类型解析。
 * <p>实现 {@link com.ruoyi.system.service.impl.SysReadRangeApplyServiceImpl}；持久化
 * {@link com.ruoyi.system.mapper.SysReadRangeApplyMapper}、{@link com.ruoyi.system.mapper.SysReadRangeApplyItemMapper}；
 * 授权结果写入 {@link com.ruoyi.system.mapper.SysReadRangeMapper}（实体 {@link com.ruoyi.common.core.domain.entity.SysReadRange}）。</p>
 * <p>保留旧阅读范围服务接口，当前宠物领养平台本地版本不暴露对应控制器。</p>
 */
public interface ISysReadRangeApplyService
{
    /**
     * 创建一条权限申请
     *
     * @param userId 申请人
     * @param requestPreviewAll 是否申请全部预览（叶子类型全集）
     * @param requestDownloadAll 是否申请全部下载（叶子类型全集）
     * @param previewTypeIds 申请的预览类型（requestPreviewAll=false 时生效）
     * @param downloadTypeIds 申请的下载类型（requestDownloadAll=false 时生效）
     * @param applyUserNote 用户情况说明（选填）
     * @return 申请单ID
     */
    Long createApply(Long userId,
                      Boolean requestPreviewAll,
                      Boolean requestDownloadAll,
                      Long[] previewTypeIds,
                      Long[] downloadTypeIds,
                      String applyUserNote,
                      String scopeTypeKey);

    /**
     * 条件查询申请列表（分页由 PageHelper 控制）
     */
    List<SysReadRangeApply> selectReadRangeApplyList(SysReadRangeApply query);

    /**
     * 查询申请头信息
     */
    SysReadRangeApply selectApplyById(Long applyId);

    /**
     * 查询申请明细
     */
    List<SysReadRangeApplyItem> selectApplyItems(Long applyId);

    /**
     * 管理员审核通过（勾选生效）；仅待审核可处理
     */
    void reviewApply(Long applyId,
                      Long reviewerId,
                      String reviewerName,
                      Long[] approvedPreviewTypeIds,
                      Long[] approvedDownloadTypeIds);

    /**
     * 整单拒绝：不修改 sys_read_range，仅待审核可处理
     */
    void rejectApply(Long applyId, String reviewerName, String rejectReason);

    /**
     * 文档库 fileType 业务下可申请叶子类型 id（与 createApply 校验一致）
     */
    List<Long> selectLeafFileTypeIds();

    /**
     * 指定 type_key 下的叶子（证书/专利等分栏与客户端 type-list 一致）
     */
    List<Long> selectLeafFileTypeIdsByTypeKey(String typeKey);
}
