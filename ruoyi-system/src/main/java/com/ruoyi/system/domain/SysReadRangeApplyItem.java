package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 文档库权限审核申请明细表 sys_read_range_apply_item
 *
 * @author ruoyi
 */
public class SysReadRangeApplyItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 申请单ID */
    private Long applyId;

    /** 文档类型ID（对应 app_type.id / sys_read_range.type_id） */
    private Long typeId;

    /** 用户申请预览 0否 1是 */
    private String reqAllowPreview;

    /** 用户申请下载 0否 1是 */
    private String reqAllowDownload;

    /** 管理员审核预览 0否 1是 */
    private String auditAllowPreview;

    /** 管理员审核下载 0否 1是 */
    private String auditAllowDownload;

    public Long getApplyId()
    {
        return applyId;
    }

    public void setApplyId(Long applyId)
    {
        this.applyId = applyId;
    }

    public Long getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Long typeId)
    {
        this.typeId = typeId;
    }

    public String getReqAllowPreview()
    {
        return reqAllowPreview;
    }

    public void setReqAllowPreview(String reqAllowPreview)
    {
        this.reqAllowPreview = reqAllowPreview;
    }

    public String getReqAllowDownload()
    {
        return reqAllowDownload;
    }

    public void setReqAllowDownload(String reqAllowDownload)
    {
        this.reqAllowDownload = reqAllowDownload;
    }

    public String getAuditAllowPreview()
    {
        return auditAllowPreview;
    }

    public void setAuditAllowPreview(String auditAllowPreview)
    {
        this.auditAllowPreview = auditAllowPreview;
    }

    public String getAuditAllowDownload()
    {
        return auditAllowDownload;
    }

    public void setAuditAllowDownload(String auditAllowDownload)
    {
        this.auditAllowDownload = auditAllowDownload;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("applyId", getApplyId())
            .append("typeId", getTypeId())
            .append("reqAllowPreview", getReqAllowPreview())
            .append("reqAllowDownload", getReqAllowDownload())
            .append("auditAllowPreview", getAuditAllowPreview())
            .append("auditAllowDownload", getAuditAllowDownload())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

