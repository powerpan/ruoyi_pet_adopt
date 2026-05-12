package com.ruoyi.framework.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

@DisplayName("KaptchaTextCreator 算术验证码格式")
class KaptchaTextCreatorTest
{
    private static final Pattern EXPRESSION = Pattern.compile(
            "^[0-9]{1,2}[\\*\\+\\-/][0-9]{1,2}=\\?@[0-9]+$");

    @RepeatedTest(30)
    @DisplayName("多次生成均符合「操作数运算符操作数=?@结果」形态")
    void getText_matchesPattern()
    {
        String text = new KaptchaTextCreator().getText();
        assertTrue(EXPRESSION.matcher(text).matches(), () -> "unexpected: " + text);
    }
}
