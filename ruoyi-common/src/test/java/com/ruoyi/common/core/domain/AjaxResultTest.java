package com.ruoyi.common.core.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ruoyi.common.constant.HttpStatus;

@DisplayName("AjaxResult 统一响应")
class AjaxResultTest
{
    @Test
    @DisplayName("success 携带数据与状态")
    void success_withData()
    {
        AjaxResult r = AjaxResult.success("ok", Integer.valueOf(42));
        assertTrue(r.isSuccess());
        assertEquals(HttpStatus.SUCCESS, r.get(AjaxResult.CODE_TAG));
        assertEquals(42, r.get(AjaxResult.DATA_TAG));
    }

    @Test
    @DisplayName("error 默认失败文案")
    void error_defaultMessage()
    {
        AjaxResult r = AjaxResult.error();
        assertTrue(r.isError());
        assertEquals("操作失败", r.get(AjaxResult.MSG_TAG));
    }

    @Test
    @DisplayName("warn 为警告状态")
    void warn_flag()
    {
        AjaxResult r = AjaxResult.warn("注意");
        assertTrue(r.isWarn());
        assertFalse(r.isSuccess());
    }

    @Test
    @DisplayName("success(null data) 不放入 data 键")
    void success_nullData_omitsDataKey()
    {
        AjaxResult r = AjaxResult.success("仅消息", null);
        assertNull(r.get(AjaxResult.DATA_TAG));
    }
}
