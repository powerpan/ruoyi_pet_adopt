package com.ruoyi.system.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.core.domain.entity.SysReadRange;

/**
 * 【分层-Mapper / DAO】用户文档类型授权 {@code sys_read_range}（预览/下载按 {@code type_id}）。
 * <p>与申请审核联动：审核通过后写入/更新本表；一键清空等运维在 {@link com.ruoyi.system.service.ISysUserService} 侧亦有封装。</p>
 */
public interface SysReadRangeMapper extends BaseMapper<SysReadRange>
{
    /**
     * 按用户与文档类型查询一条阅读范围（复合主键 user_id + type_id）
     */
    public SysReadRange selectSysReadRangeById(SysReadRange sysReadRange);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param sysReadRange 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<SysReadRange> selectSysReadRangeList(SysReadRange sysReadRange);

    /**
     * 新增【请填写功能名称】
     * 
     * @param sysReadRange 【请填写功能名称】
     * @return 结果
     */
    public int insertSysReadRange(SysReadRange sysReadRange);

    /**
     * 修改【请填写功能名称】
     * 
     * @param sysReadRange 【请填写功能名称】
     * @return 结果
     */
    public int updateSysReadRange(SysReadRange sysReadRange);

    /**
     * 按复合主键删除一条阅读范围
     */
    public int deleteSysReadRangeById(SysReadRange sysReadRange);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysReadRangeByIds(Long[] ids);
}
