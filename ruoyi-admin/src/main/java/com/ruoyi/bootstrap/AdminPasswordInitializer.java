/*
 * Copyright https://github.com/powerpan/ruoyi_pet_adopt.git
 */
package com.ruoyi.bootstrap;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Initializes the local admin password from an environment variable when the
 * imported SQL keeps the admin account but omits authentication material.
 */
@Component
public class AdminPasswordInitializer implements ApplicationRunner
{
    private static final Logger log = LoggerFactory.getLogger(AdminPasswordInitializer.class);

    private static final String ADMIN_USER_NAME = "admin";

    private final JdbcTemplate jdbcTemplate;

    private final BCryptPasswordEncoder passwordEncoder;

    private final Environment environment;

    public AdminPasswordInitializer(JdbcTemplate jdbcTemplate, BCryptPasswordEncoder passwordEncoder, Environment environment)
    {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args)
    {
        String bootstrapPassword = environment.getProperty("ruoyi.pet.bootstrap.admin-password");
        if (!StringUtils.hasText(bootstrapPassword))
        {
            bootstrapPassword = environment.getProperty("RUOYI_PET_ADOPT_BOOTSTRAP_ADMIN_PASSWORD");
        }
        if (!StringUtils.hasText(bootstrapPassword))
        {
            return;
        }

        try
        {
            List<String> passwords = jdbcTemplate.queryForList(
                    "select password from sys_user where user_name = ? and del_flag = '0' limit 1",
                    String.class,
                    ADMIN_USER_NAME);
            if (passwords.isEmpty() || StringUtils.hasText(passwords.get(0)))
            {
                return;
            }

            jdbcTemplate.update(
                    "update sys_user set password = ?, update_by = 'bootstrap', update_time = now() where user_name = ? and del_flag = '0'",
                    passwordEncoder.encode(bootstrapPassword),
                    ADMIN_USER_NAME);
            log.info("Local admin password initialized from ruoyi.pet.bootstrap.admin-password");
        }
        catch (Exception e)
        {
            log.warn("Skip local admin password initialization: {}", e.getMessage());
        }
    }
}
