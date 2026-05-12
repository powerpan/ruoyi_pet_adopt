package com.ruoyi.common.utils;

/**
 * 首页/全文检索：规范化关键词，兼容无 {@code REGEXP_REPLACE} 的 MySQL 5.7 / MariaDB。
 */
public final class SearchNormalizeUtils
{
    private SearchNormalizeUtils()
    {
    }

    /**
     * 仅保留字母与数字并转小写（含中文等 Unicode 字母），去掉标点与空白。
     */
    public static String normalizeForSearch(String s)
    {
        if (s == null || s.isEmpty())
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        s.codePoints().forEach(cp -> {
            if (Character.isLetter(cp) || Character.isDigit(cp))
            {
                sb.appendCodePoint(Character.toLowerCase(cp));
            }
        });
        return sb.toString();
    }

    /**
     * 顺序模糊匹配：{@code ezone} → {@code %e%z%o%n%e%}，使 {@code E-Zone}、{@code E Zone} 等仍可命中。
     */
    public static String toSequentialLikePattern(String normalized)
    {
        if (normalized == null || normalized.isEmpty())
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < normalized.length(); )
        {
            int cp = normalized.codePointAt(i);
            i += Character.charCount(cp);
            sb.append('%');
            sb.appendCodePoint(cp);
        }
        sb.append('%');
        return sb.toString();
    }
}
