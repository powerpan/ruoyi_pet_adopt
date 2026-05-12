package com.ruoyi.common.core.domain.model;

import lombok.Data;

/**
 * 登录用户身份权限
 *
 * @author ruoyi
 */
@Data
public class TempUser {
    private static final long serialVersionUID = 1L;

    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickName;
}
