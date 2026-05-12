package com.ruoyi.system.domain;

import java.util.Date;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 文档库权限审核申请表 sys_read_range_apply
 *
 * @author ruoyi
 */
public class SysReadRangeApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 申请单ID */
    private Long id;

    /** 申请用户ID */
    @NotNull
    private Long userId;

    /** 申请用户昵称 */
    private String nickName;

    /** 申请全部预览 0否 1是 */
    private String requestPreviewAll;

    /** 申请全部下载 0否 1是 */
    private String requestDownloadAll;

    /** 用户申请情况说明（选填） */
    private String applyUserNote;

    /** 0待审核 1已通过 2已拒绝 */
    private Integer status;

    /** 审核人 */
    private String reviewBy;

    /** 审核时间 */
    private Date reviewTime;

    /** 审核拒绝原因（整单拒绝时） */
    private String reviewRejectReason;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getRequestPreviewAll()
    {
        return requestPreviewAll;
    }

    public void setRequestPreviewAll(String requestPreviewAll)
    {
        this.requestPreviewAll = requestPreviewAll;
    }

    public String getRequestDownloadAll()
    {
        return requestDownloadAll;
    }

    public void setRequestDownloadAll(String requestDownloadAll)
    {
        this.requestDownloadAll = requestDownloadAll;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public String getReviewBy()
    {
        return reviewBy;
    }

    public void setReviewBy(String reviewBy)
    {
        this.reviewBy = reviewBy;
    }

    public Date getReviewTime()
    {
        return reviewTime;
    }

    public void setReviewTime(Date reviewTime)
    {
        this.reviewTime = reviewTime;
    }

    public String getApplyUserNote()
    {
        return applyUserNote;
    }

    public void setApplyUserNote(String applyUserNote)
    {
        this.applyUserNote = applyUserNote;
    }

    public String getReviewRejectReason()
    {
        return reviewRejectReason;
    }

    public void setReviewRejectReason(String reviewRejectReason)
    {
        this.reviewRejectReason = reviewRejectReason;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("nickName", getNickName())
            .append("requestPreviewAll", getRequestPreviewAll())
            .append("requestDownloadAll", getRequestDownloadAll())
            .append("applyUserNote", getApplyUserNote())
            .append("status", getStatus())
            .append("reviewBy", getReviewBy())
            .append("reviewTime", getReviewTime())
            .append("reviewRejectReason", getReviewRejectReason())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

