package com.ruoyi.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Arith 精确运算")
class ArithTest
{
    @Test
    @DisplayName("浮点加法避免二进制误差")
    void add_avoidsBinaryDrift()
    {
        assertEquals(0.3, Arith.add(0.1, 0.2), 1e-12);
    }

    @Test
    @DisplayName("除法 scale 非法时抛 IllegalArgumentException")
    void div_negativeScale_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> Arith.div(1.0, 2.0, -1));
    }

    @Test
    @DisplayName("除零在 BigDecimal 语义下抛异常")
    void div_byZero_throws()
    {
        assertThrows(ArithmeticException.class, () -> Arith.div(1.0, 0.0, 4));
    }
}
