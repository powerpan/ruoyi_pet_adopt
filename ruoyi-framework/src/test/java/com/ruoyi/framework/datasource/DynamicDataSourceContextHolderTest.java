package com.ruoyi.framework.datasource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DynamicDataSourceContextHolder 线程隔离")
class DynamicDataSourceContextHolderTest
{
    @AfterEach
    void tearDown()
    {
        DynamicDataSourceContextHolder.clearDataSourceType();
    }

    @Test
    @DisplayName("设置与读取当前线程数据源")
    void setAndGet()
    {
        DynamicDataSourceContextHolder.setDataSourceType("slave");
        assertEquals("slave", DynamicDataSourceContextHolder.getDataSourceType());
    }

    @Test
    @DisplayName("clear 后 ThreadLocal 为空")
    void clear_removes()
    {
        DynamicDataSourceContextHolder.setDataSourceType("master");
        DynamicDataSourceContextHolder.clearDataSourceType();
        assertNull(DynamicDataSourceContextHolder.getDataSourceType());
    }
}
