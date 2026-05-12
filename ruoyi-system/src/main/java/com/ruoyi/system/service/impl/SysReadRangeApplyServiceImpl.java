package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.core.domain.entity.SysReadRange;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.SysReadRangeApply;
import com.ruoyi.system.domain.SysReadRangeApplyItem;
import com.ruoyi.system.mapper.SysReadRangeApplyItemMapper;
import com.ruoyi.system.mapper.SysReadRangeApplyMapper;
import com.ruoyi.system.mapper.SysReadRangeMapper;
import com.ruoyi.system.service.ISysReadRangeApplyService;

/**
 * 【分层-Service 实现】申请频控、叶子集合校验、事务内写申请头/明细，并在审核通过时合并写入 {@code sys_read_range}。
 */
@Service
public class SysReadRangeApplyServiceImpl implements ISysReadRangeApplyService
{
    private static final int NOTE_MAX = 500;

    /** 同一用户两次提交权限申请的最小间隔（毫秒） */
    private static final long APPLY_SUBMIT_MIN_INTERVAL_MS = 10_000L;

    private static final String BIZ_FILE_TYPE = "fileType";

    @Autowired
    private SysReadRangeApplyMapper sysReadRangeApplyMapper;

    @Autowired
    private SysReadRangeApplyItemMapper sysReadRangeApplyItemMapper;

    @Autowired
    private SysReadRangeMapper sysReadRangeMapper;

    @Override
    public List<Long> selectLeafFileTypeIds()
    {
        List<Long> list = sysReadRangeApplyMapper.selectLeafTypeIdsByBusiness(BIZ_FILE_TYPE);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public List<Long> selectLeafFileTypeIdsByTypeKey(String typeKey)
    {
        if (StringUtils.isEmpty(typeKey))
        {
            return selectLeafFileTypeIds();
        }
        List<Long> list = sysReadRangeApplyMapper.selectLeafTypeIdsByBusinessAndTypeKey(BIZ_FILE_TYPE, typeKey.trim());
        return list != null ? list : Collections.emptyList();
    }

    @Override
    @Transactional
    public Long createApply(Long userId,
                             Boolean requestPreviewAll,
                             Boolean requestDownloadAll,
                             Long[] previewTypeIds,
                             Long[] downloadTypeIds,
                             String applyUserNote,
                             String scopeTypeKey)
    {
        if (userId == null)
        {
            throw new ServiceException("未登录或用户无效");
        }

        Date lastSubmit = sysReadRangeApplyMapper.selectLatestCreateTimeByUserId(userId);
        if (lastSubmit != null)
        {
            long elapsed = System.currentTimeMillis() - lastSubmit.getTime();
            if (elapsed >= 0 && elapsed < APPLY_SUBMIT_MIN_INTERVAL_MS)
            {
                long waitSec = (APPLY_SUBMIT_MIN_INTERVAL_MS - elapsed + 999) / 1000;
                throw new ServiceException("提交过于频繁，请 " + waitSec + " 秒后再试");
            }
        }

        List<Long> leafList = StringUtils.isNotEmpty(scopeTypeKey)
            ? selectLeafFileTypeIdsByTypeKey(scopeTypeKey)
            : selectLeafFileTypeIds();
        Set<Long> leafSet = new HashSet<>(leafList);

        String previewAllFlag = requestPreviewAll != null && requestPreviewAll ? "1" : "0";
        String downloadAllFlag = requestDownloadAll != null && requestDownloadAll ? "1" : "0";

        Set<Long> previewSet = new HashSet<>();
        Set<Long> downloadSet = new HashSet<>();

        if ("1".equals(previewAllFlag))
        {
            previewSet.addAll(leafList);
        }
        else if (previewTypeIds != null)
        {
            previewSet.addAll(Arrays.asList(previewTypeIds));
        }

        if ("1".equals(downloadAllFlag))
        {
            downloadSet.addAll(leafList);
        }
        else if (downloadTypeIds != null)
        {
            downloadSet.addAll(Arrays.asList(downloadTypeIds));
        }

        previewSet.remove(null);
        downloadSet.remove(null);

        validateSubsetOfLeaves(previewSet, leafSet);
        validateSubsetOfLeaves(downloadSet, leafSet);

        Set<Long> union = new HashSet<>();
        union.addAll(previewSet);
        union.addAll(downloadSet);

        if (union.isEmpty())
        {
            throw new ServiceException("请至少选择预览或下载权限");
        }

        String note = trimNote(applyUserNote);

        SysReadRangeApply apply = new SysReadRangeApply();
        apply.setUserId(userId);
        apply.setRequestPreviewAll(previewAllFlag);
        apply.setRequestDownloadAll(downloadAllFlag);
        apply.setStatus(0);
        apply.setReviewBy(null);
        apply.setReviewTime(null);
        if (note != null)
        {
            apply.setApplyUserNote(note);
        }

        sysReadRangeApplyMapper.insertSysReadRangeApply(apply);

        Long applyId = apply.getId();
        if (applyId == null)
        {
            throw new ServiceException("申请创建失败");
        }

        for (Long typeId : union)
        {
            SysReadRangeApplyItem item = new SysReadRangeApplyItem();
            item.setApplyId(applyId);
            item.setTypeId(typeId);
            item.setReqAllowPreview(previewSet.contains(typeId) ? "1" : "0");
            item.setReqAllowDownload(downloadSet.contains(typeId) ? "1" : "0");
            item.setAuditAllowPreview("0");
            item.setAuditAllowDownload("0");
            sysReadRangeApplyItemMapper.insertSysReadRangeApplyItem(item);
        }

        return applyId;
    }

    private static void validateSubsetOfLeaves(Set<Long> ids, Set<Long> leafSet)
    {
        for (Long id : ids)
        {
            if (id != null && !leafSet.contains(id))
            {
                throw new ServiceException("包含无效的文档类型，请刷新页面后重试");
            }
        }
    }

    private static String trimNote(String applyUserNote)
    {
        if (applyUserNote == null)
        {
            return null;
        }
        String t = applyUserNote.trim();
        if (t.isEmpty())
        {
            return null;
        }
        if (t.length() > NOTE_MAX)
        {
            return t.substring(0, NOTE_MAX);
        }
        return t;
    }

    @Override
    public List<SysReadRangeApply> selectReadRangeApplyList(SysReadRangeApply query)
    {
        return sysReadRangeApplyMapper.selectSysReadRangeApplyList(query);
    }

    @Override
    public SysReadRangeApply selectApplyById(Long applyId)
    {
        return sysReadRangeApplyMapper.selectSysReadRangeApplyById(applyId);
    }

    @Override
    public List<SysReadRangeApplyItem> selectApplyItems(Long applyId)
    {
        if (applyId == null)
        {
            return new ArrayList<>();
        }
        return sysReadRangeApplyItemMapper.selectItemsByApplyId(applyId);
    }

    @Override
    @Transactional
    public void reviewApply(Long applyId,
                             Long reviewerId,
                             String reviewerName,
                             Long[] approvedPreviewTypeIds,
                             Long[] approvedDownloadTypeIds)
    {
        SysReadRangeApply apply = requirePendingApply(applyId);

        List<SysReadRangeApplyItem> items = sysReadRangeApplyItemMapper.selectItemsByApplyId(applyId);
        if (items == null)
        {
            items = new ArrayList<>();
        }

        Set<Long> candidatePreviewTypeIds = items.stream()
            .filter(i -> "1".equals(i.getReqAllowPreview()))
            .map(SysReadRangeApplyItem::getTypeId)
            .collect(Collectors.toSet());

        Set<Long> candidateDownloadTypeIds = items.stream()
            .filter(i -> "1".equals(i.getReqAllowDownload()))
            .map(SysReadRangeApplyItem::getTypeId)
            .collect(Collectors.toSet());

        Set<Long> approvedPreview = approvedPreviewTypeIds == null
            ? new HashSet<>()
            : new HashSet<>(Arrays.asList(approvedPreviewTypeIds));
        Set<Long> approvedDownload = approvedDownloadTypeIds == null
            ? new HashSet<>()
            : new HashSet<>(Arrays.asList(approvedDownloadTypeIds));

        approvedPreview.retainAll(candidatePreviewTypeIds);
        approvedDownload.retainAll(candidateDownloadTypeIds);

        for (SysReadRangeApplyItem item : items)
        {
            if (item == null || item.getTypeId() == null)
            {
                continue;
            }
            item.setAuditAllowPreview(approvedPreview.contains(item.getTypeId()) ? "1" : "0");
            item.setAuditAllowDownload(approvedDownload.contains(item.getTypeId()) ? "1" : "0");
            item.setUpdateBy(reviewerName);
            item.setUpdateTime(new Date());
            sysReadRangeApplyItemMapper.updateAuditSysReadRangeApplyItem(item);
        }

        Set<Long> candidateUnion = new HashSet<>();
        candidateUnion.addAll(candidatePreviewTypeIds);
        candidateUnion.addAll(candidateDownloadTypeIds);

        SysReadRange q = new SysReadRange();
        q.setUserId(apply.getUserId());
        List<SysReadRange> existingList = sysReadRangeMapper.selectSysReadRangeList(q);
        Map<Long, SysReadRange> existingMap = new HashMap<>();
        if (existingList != null)
        {
            for (SysReadRange r : existingList)
            {
                if (r != null && r.getTypeId() != null)
                {
                    existingMap.put(r.getTypeId(), r);
                }
            }
        }

        for (Long typeId : candidateUnion)
        {
            if (typeId == null)
            {
                continue;
            }
            SysReadRange curr = existingMap.get(typeId);
            String existingPreview = curr != null && curr.getAllowPreview() != null ? curr.getAllowPreview() : "0";
            String existingDownload = curr != null && curr.getAllowDownload() != null ? curr.getAllowDownload() : "0";

            String finalPreview = approvedPreview.contains(typeId) ? "1" : existingPreview;
            String finalDownload = approvedDownload.contains(typeId) ? "1" : existingDownload;

            if (curr != null)
            {
                if (!finalPreview.equals(existingPreview) || !finalDownload.equals(existingDownload))
                {
                    curr.setAllowPreview(finalPreview);
                    curr.setAllowDownload(finalDownload);
                    sysReadRangeMapper.updateSysReadRange(curr);
                }
            }
            else
            {
                if ("1".equals(finalPreview) || "1".equals(finalDownload))
                {
                    SysReadRange up = new SysReadRange();
                    up.setUserId(apply.getUserId());
                    up.setTypeId(typeId);
                    up.setAllowPreview(finalPreview);
                    up.setAllowDownload(finalDownload);
                    sysReadRangeMapper.insertSysReadRange(up);
                }
            }
        }

        boolean anyApproved = !approvedPreview.isEmpty() || !approvedDownload.isEmpty();
        apply.setStatus(anyApproved ? 1 : 2);
        apply.setReviewBy(reviewerName);
        apply.setReviewTime(new Date());
        apply.setUpdateBy(reviewerName);
        apply.setUpdateTime(new Date());
        if (anyApproved)
        {
            apply.getParams().put("clearReviewRejectReason", true);
        }
        sysReadRangeApplyMapper.updateSysReadRangeApply(apply);
    }

    @Override
    @Transactional
    public void rejectApply(Long applyId, String reviewerName, String rejectReason)
    {
        SysReadRangeApply apply = requirePendingApply(applyId);
        String reason = trimRejectReason(rejectReason);
        if (reason == null || reason.isEmpty())
        {
            throw new ServiceException("请填写拒绝原因");
        }

        List<SysReadRangeApplyItem> items = sysReadRangeApplyItemMapper.selectItemsByApplyId(applyId);
        if (items != null)
        {
            for (SysReadRangeApplyItem item : items)
            {
                if (item == null || item.getTypeId() == null)
                {
                    continue;
                }
                item.setAuditAllowPreview("0");
                item.setAuditAllowDownload("0");
                item.setUpdateBy(reviewerName);
                item.setUpdateTime(new Date());
                sysReadRangeApplyItemMapper.updateAuditSysReadRangeApplyItem(item);
            }
        }

        apply.setStatus(2);
        apply.setReviewRejectReason(reason);
        apply.setReviewBy(reviewerName);
        apply.setReviewTime(new Date());
        apply.setUpdateBy(reviewerName);
        apply.setUpdateTime(new Date());
        sysReadRangeApplyMapper.updateSysReadRangeApply(apply);
    }

    private static String trimRejectReason(String rejectReason)
    {
        if (rejectReason == null)
        {
            return null;
        }
        String t = rejectReason.trim();
        if (t.length() > 500)
        {
            return t.substring(0, 500);
        }
        return t;
    }

    private SysReadRangeApply requirePendingApply(Long applyId)
    {
        if (applyId == null)
        {
            throw new ServiceException("申请单不存在");
        }
        SysReadRangeApply apply = sysReadRangeApplyMapper.selectSysReadRangeApplyById(applyId);
        if (apply == null)
        {
            throw new ServiceException("申请单不存在");
        }
        if (apply.getStatus() == null || apply.getStatus() != 0)
        {
            throw new ServiceException("申请已处理，无法重复审核");
        }
        return apply;
    }
}
