package com.ruoyi.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysConfigService 缓存与键唯一性")
class SysConfigServiceImplTest
{
    @Mock
    private SysConfigMapper configMapper;

    @Mock
    private RedisCache redisCache;

    @InjectMocks
    private SysConfigServiceImpl configService;

    @Test
    @DisplayName("selectConfigByKey：缓存未命中则从库读取并回写 Redis")
    void selectConfigByKey_loadsFromDbWhenCacheMiss()
    {
        String key = "unit.test.key";
        String cacheKey = CacheConstants.SYS_CONFIG_KEY + key;
        when(redisCache.getCacheObject(cacheKey)).thenReturn(null);
        SysConfig fromDb = new SysConfig();
        fromDb.setConfigKey(key);
        fromDb.setConfigValue("db-value");
        when(configMapper.selectConfig(any(SysConfig.class))).thenReturn(fromDb);

        assertEquals("db-value", configService.selectConfigByKey(key));
        verify(redisCache).setCacheObject(eq(cacheKey), eq("db-value"));
    }

    @Test
    @DisplayName("selectConfigByKey：缓存命中不再访问 Mapper")
    void selectConfigByKey_usesCache()
    {
        String key = "cached.key";
        when(redisCache.getCacheObject(CacheConstants.SYS_CONFIG_KEY + key)).thenReturn("cached");
        assertEquals("cached", configService.selectConfigByKey(key));
        verify(configMapper, never()).selectConfig(any());
    }

    @Test
    @DisplayName("checkConfigKeyUnique：库中无记录为唯一")
    void checkConfigKeyUnique_whenNoRow()
    {
        SysConfig probe = new SysConfig();
        probe.setConfigKey("new.key");
        probe.setConfigId(null);
        when(configMapper.checkConfigKeyUnique("new.key")).thenReturn(null);
        assertTrue(configService.checkConfigKeyUnique(probe));
    }

    @Test
    @DisplayName("checkConfigKeyUnique：同 id 更新视为唯一")
    void checkConfigKeyUnique_sameId()
    {
        SysConfig probe = new SysConfig();
        probe.setConfigKey("exist");
        probe.setConfigId(5L);
        SysConfig existing = new SysConfig();
        existing.setConfigId(5L);
        when(configMapper.checkConfigKeyUnique("exist")).thenReturn(existing);
        assertTrue(configService.checkConfigKeyUnique(probe));
    }

    @Test
    @DisplayName("checkConfigKeyUnique：其他 id 占用为不唯一")
    void checkConfigKeyUnique_conflict()
    {
        SysConfig probe = new SysConfig();
        probe.setConfigKey("exist");
        probe.setConfigId(1L);
        SysConfig other = new SysConfig();
        other.setConfigId(2L);
        when(configMapper.checkConfigKeyUnique("exist")).thenReturn(other);
        assertFalse(configService.checkConfigKeyUnique(probe));
    }
}
