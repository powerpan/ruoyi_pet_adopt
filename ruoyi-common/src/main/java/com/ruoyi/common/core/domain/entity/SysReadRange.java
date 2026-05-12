package com.ruoyi.common.core.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 sys_read_range
 * 
 * @author ruoyi
 * @date 2026-01-15
 */
public class SysReadRange extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表 sys_read_range 为复合主键 (user_id, type_id)，无 id 列 */
    @TableField(exist = false)
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long userId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long typeId;

    /** 是否可预览 0否 1是 */
    private String allowPreview;

    /** 是否可下载 0否 1是 */
    private String allowDownload;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setTypeId(Long typeId) 
    {
        this.typeId = typeId;
    }

    public Long getTypeId() 
    {
        return typeId;
    }

    public void setAllowPreview(String allowPreview)
    {
        this.allowPreview = allowPreview;
    }

    public String getAllowPreview()
    {
        return allowPreview;
    }

    public void setAllowDownload(String allowDownload)
    {
        this.allowDownload = allowDownload;
    }

    public String getAllowDownload()
    {
        return allowDownload;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("typeId", getTypeId())
            .append("allowPreview", getAllowPreview())
            .append("allowDownload", getAllowDownload())
            .toString();
    }
}
