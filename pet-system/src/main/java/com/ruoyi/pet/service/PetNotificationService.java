package com.ruoyi.pet.service;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.pet.domain.PetNotification;
import com.ruoyi.pet.domain.PetReminder;
import com.ruoyi.pet.mapper.PetNotificationMapper;
import com.ruoyi.pet.mapper.PetReminderMapper;

@Service
public class PetNotificationService {
    @Autowired private PetNotificationMapper notificationMapper;
    @Autowired private PetReminderMapper reminderMapper;

    public void create(Long receiverUserId, Long actorUserId, String noticeType, String targetType,
                       Long targetId, String title, String content, String actionUrl) {
        if (receiverUserId == null || StringUtils.isEmpty(title)) {
            return;
        }
        PetNotification notification = new PetNotification();
        notification.setReceiverUserId(receiverUserId);
        notification.setActorUserId(actorUserId);
        notification.setNoticeType(defaultText(noticeType, "system"));
        notification.setTargetType(defaultText(targetType, "general"));
        notification.setTargetId(targetId);
        notification.setTitle(title);
        notification.setContent(content == null ? "" : content);
        notification.setActionUrl(actionUrl == null ? "" : actionUrl);
        notification.setStatus(0);
        notification.setCreateBy(actorUserId == null ? "system" : String.valueOf(actorUserId));
        notification.setCreateTime(new Date());
        notificationMapper.insert(notification);
    }

    public long countUnread(Long receiverUserId) {
        syncDueReminderNotifications(receiverUserId);
        return notificationMapper.countUnread(receiverUserId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncDueReminderNotifications(Long userId) {
        if (userId == null) {
            return;
        }
        List<PetReminder> reminders = reminderMapper.selectList(new QueryWrapper<PetReminder>()
                .eq("user_id", userId)
                .eq("status", 0)
                .eq("notice_sent", 0)
                .le("due_time", new Date()));
        for (PetReminder reminder : reminders) {
            int claimed = reminderMapper.update(null, new UpdateWrapper<PetReminder>()
                    .eq("id", reminder.getId())
                    .eq("user_id", userId)
                    .eq("status", 0)
                    .eq("notice_sent", 0)
                    .set("status", 2)
                    .set("notice_sent", 1));
            if (claimed > 0) {
                create(userId, null, "reminder_due", "reminder", reminder.getId(),
                        "健康提醒已到期", reminder.getTitle(), "/health");
            }
        }
    }

    private String defaultText(String value, String fallback) {
        return StringUtils.isEmpty(value) ? fallback : value;
    }
}
