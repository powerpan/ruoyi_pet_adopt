/*
 * Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
 */
package com.ruoyi.pet.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.pet.domain.*;
import com.ruoyi.pet.mapper.*;
import com.ruoyi.pet.service.PetNotificationService;

@RestController
@RequestMapping("/manager/pet")
public class PetManagerController extends BaseController {
    @Autowired private PetUserProfileMapper userProfileMapper;
    @Autowired private PetProfileMapper petProfileMapper;
    @Autowired private PetPostMapper postMapper;
    @Autowired private PetPostMediaMapper postMediaMapper;
    @Autowired private PetPostTopicMapper postTopicMapper;
    @Autowired private PetTopicMapper topicMapper;
    @Autowired private PetCommentMapper commentMapper;
    @Autowired private PetInteractionMapper interactionMapper;
    @Autowired private PetMerchantMapper merchantMapper;
    @Autowired private PetMerchantQualificationMapper qualificationMapper;
    @Autowired private PetServiceItemMapper serviceItemMapper;
    @Autowired private PetServiceRequestMapper serviceRequestMapper;
    @Autowired private PetServiceReviewMapper serviceReviewMapper;
    @Autowired private PetHealthRecordMapper healthRecordMapper;
    @Autowired private PetReminderMapper reminderMapper;
    @Autowired private PetAuditRecordMapper auditRecordMapper;
    // 管理端审核通知入口，项目版权地址：https://github.com/powerpan/ruoyi_pet_adopt.git
    @Autowired private PetNotificationService notificationService;

    @PreAuthorize("@ss.hasPermi('manager:pet:profile:list')")
    @GetMapping("/profiles/list")
    public TableDataInfo profileList(PetUserProfile profile, @RequestParam(required = false) String statusScope) {
        startPage();
        QueryWrapper<PetUserProfile> wrapper = new QueryWrapper<>();
        if (profile.getUserId() != null) {
            wrapper.eq("user_id", profile.getUserId());
        }
        if (profile.getBloggerStatus() != null) {
            wrapper.eq("blogger_status", profile.getBloggerStatus());
        } else if ("history".equals(statusScope)) {
            wrapper.in("blogger_status", 1, 3);
        }
        if (StringUtils.isNotEmpty(profile.getNickname())) {
            wrapper.like("nickname", profile.getNickname());
        }
        wrapper.orderByDesc("create_time");
        return getDataTable(userProfileMapper.selectList(wrapper));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:profile:edit')")
    @PutMapping("/profiles")
    public AjaxResult updateProfile(@RequestBody PetUserProfile profile) {
        profile.setUpdateBy(getUsername());
        profile.setUpdateTime(new Date());
        return toAjax(userProfileMapper.updateById(profile));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:pet:list')")
    @GetMapping("/pets/list")
    public TableDataInfo petList(PetProfile pet) {
        startPage();
        return getDataTable(petProfileMapper.selectManagerPets(pet.getUserId(), pet.getName()));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:topic:list')")
    @GetMapping("/topics/list")
    public TableDataInfo topicList(PetTopic topic) {
        startPage();
        QueryWrapper<PetTopic> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(topic.getName())) {
            wrapper.like("name", topic.getName());
        }
        wrapper.orderByDesc("post_count").orderByDesc("create_time");
        return getDataTable(topicMapper.selectList(wrapper));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:topic:add')")
    @Log(title = "宠物话题", businessType = BusinessType.INSERT)
    @PostMapping("/topics")
    public AjaxResult addTopic(@RequestBody PetTopic topic) {
        topic.setPostCount(0);
        topic.setStatus(topic.getStatus() == null ? 0 : topic.getStatus());
        topic.setCreateBy(getUsername());
        topic.setCreateTime(new Date());
        return toAjax(topicMapper.insert(topic));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:topic:edit')")
    @Log(title = "宠物话题", businessType = BusinessType.UPDATE)
    @PutMapping("/topics")
    public AjaxResult updateTopic(@RequestBody PetTopic topic) {
        topic.setUpdateBy(getUsername());
        topic.setUpdateTime(new Date());
        return toAjax(topicMapper.updateById(topic));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:topic:remove')")
    @DeleteMapping("/topics/{ids}")
    public AjaxResult removeTopics(@PathVariable Long[] ids) {
        return toAjax(topicMapper.deleteBatchIds(Arrays.asList(ids)));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:post:list')")
    @GetMapping("/posts/list")
    public TableDataInfo postList(PetPost post, @RequestParam(required = false) String statusScope) {
        startPage();
        return getDataTable(postMapper.selectManagerPosts(post.getAuditStatus(), statusScope, post.getTitle()));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:post:review')")
    @Log(title = "帖子审核", businessType = BusinessType.UPDATE)
    @PostMapping("/posts/audit")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult auditPost(@RequestBody PetAuditRecord audit) {
        if (!isAllowedAuditStatus(audit.getAuditStatus(), 1, 2)) {
            return error("审核状态不合法");
        }
        PetPost post = postMapper.selectById(audit.getTargetId());
        int updated = postMapper.update(null, new UpdateWrapper<PetPost>()
                .eq("id", audit.getTargetId()).eq("audit_status", 0).set("audit_status", audit.getAuditStatus()));
        if (updated == 0) {
            return error("仅待审核帖子可处理，已处理记录请在审核历史中查看");
        }
        saveAudit("post", audit);
        if (post != null) {
            boolean passed = audit.getAuditStatus() == 1;
            notificationService.create(post.getAuthorId(), SecurityUtils.getUserId(), "post_audit", "post",
                    post.getId(), passed ? "动态审核通过" : "动态审核未通过",
                    passed ? "你的动态「" + post.getTitle() + "」已公开展示。" : defaultAuditReason(audit, "你的动态「" + post.getTitle() + "」未通过审核。"),
                    passed ? "/posts/" + post.getId() : "/publish");
            Set<Long> topicIds = postTopicMapper.selectList(new QueryWrapper<PetPostTopic>().eq("post_id", post.getId())).stream()
                    .map(PetPostTopic::getTopicId).collect(java.util.stream.Collectors.toSet());
            recalcTopicCounts(topicIds);
            recalcProfilePostCount(post.getAuthorId());
        }
        return success();
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:post:remove')")
    @Log(title = "帖子删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/posts/{ids}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult removePosts(@PathVariable Long[] ids) {
        List<PetPost> posts = postMapper.selectList(new QueryWrapper<PetPost>().in("id", Arrays.asList(ids)));
        for (PetPost post : posts) {
            notificationService.create(post.getAuthorId(), SecurityUtils.getUserId(), "post_delete", "post",
                    post.getId(), "动态已被管理员删除", "你的动态「" + post.getTitle() + "」已由管理员删除。", "/publish");
        }
        return toAjax(deletePostCascade(posts));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:comment:list')")
    @GetMapping("/comments/list")
    public TableDataInfo commentList(PetComment comment, @RequestParam(required = false) String statusScope) {
        startPage();
        return getDataTable(commentMapper.selectManagerComments(comment.getPostId(), comment.getAuditStatus(), statusScope));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:comment:review')")
    @PostMapping("/comments/audit")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult auditComment(@RequestBody PetAuditRecord audit) {
        if (!isAllowedAuditStatus(audit.getAuditStatus(), 1, 2)) {
            return error("审核状态不合法");
        }
        PetComment comment = commentMapper.selectById(audit.getTargetId());
        int updated = commentMapper.update(null, new UpdateWrapper<PetComment>()
                .eq("id", audit.getTargetId()).eq("audit_status", 0).set("audit_status", audit.getAuditStatus()));
        if (updated == 0) {
            return error("仅待审核评论可处理，已处理记录请在审核历史中查看");
        }
        saveAudit("comment", audit);
        if (comment != null) {
            boolean passed = audit.getAuditStatus() == 1;
            recalcPostCommentCount(comment.getPostId());
            notificationService.create(comment.getUserId(), SecurityUtils.getUserId(), "comment_audit", "comment",
                    comment.getId(), passed ? "评论审核通过" : "评论审核未通过",
                    passed ? "你的评论已展示。" : defaultAuditReason(audit, "你的评论未通过审核。"),
                    comment.getPostId() == null ? "/" : "/posts/" + comment.getPostId());
            if (passed) {
                PetPost post = postMapper.selectById(comment.getPostId());
                if (post != null && !post.getAuthorId().equals(comment.getUserId())) {
                    notificationService.create(post.getAuthorId(), comment.getUserId(), "post_comment", "post",
                            post.getId(), "动态收到新评论", "你的动态「" + post.getTitle() + "」有一条新评论。", "/posts/" + post.getId());
                }
            }
        }
        return success();
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:merchant:list')")
    @GetMapping("/merchants/list")
    public TableDataInfo merchantList(PetMerchant merchant, @RequestParam(required = false) String statusScope) {
        startPage();
        return getDataTable(merchantMapper.selectManagerMerchants(merchant.getName(), merchant.getQualificationStatus(), statusScope));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:merchant:review')")
    @PostMapping("/merchants/audit")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult auditMerchant(@RequestBody PetAuditRecord audit) {
        if (!isAllowedAuditStatus(audit.getAuditStatus(), 1, 2)) {
            return error("审核状态不合法");
        }
        PetMerchant merchant = merchantMapper.selectById(audit.getTargetId());
        Integer serviceStatus = audit.getAuditStatus() != null && audit.getAuditStatus() == 1 ? 0 : 1;
        int updated = merchantMapper.update(null, new UpdateWrapper<PetMerchant>()
                .eq("id", audit.getTargetId()).eq("qualification_status", 0)
                .set("qualification_status", audit.getAuditStatus())
                .set("status", serviceStatus));
        if (updated == 0) {
            return error("仅待审核商家可处理，已处理记录请在审核历史中查看");
        }
        qualificationMapper.update(null, new UpdateWrapper<PetMerchantQualification>()
                .eq("merchant_id", audit.getTargetId()).eq("audit_status", 0).set("audit_status", audit.getAuditStatus()));
        saveAudit("merchant", audit);
        if (merchant != null) {
            boolean passed = audit.getAuditStatus() == 1;
            notificationService.create(merchant.getUserId(), SecurityUtils.getUserId(), "merchant_audit", "merchant",
                    merchant.getId(), passed ? "商家入驻审核通过" : "商家入驻审核未通过",
                    passed ? "你的商家「" + merchant.getName() + "」已可发布服务。" : defaultAuditReason(audit, "你的商家「" + merchant.getName() + "」未通过审核。"),
                    "/merchant");
        }
        return success();
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:merchant:list')")
    @GetMapping("/merchants/{id}/qualifications")
    public TableDataInfo merchantQualifications(@PathVariable Long id) {
        startPage();
        return getDataTable(qualificationMapper.selectList(new QueryWrapper<PetMerchantQualification>()
                .eq("merchant_id", id).orderByDesc("create_time")));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:profile:review')")
    @PostMapping("/bloggers/audit")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult auditBlogger(@RequestBody PetAuditRecord audit) {
        if (!isAllowedAuditStatus(audit.getAuditStatus(), 1, 3)) {
            return error("审核状态不合法");
        }
        PetUserProfile profile = userProfileMapper.selectById(audit.getTargetId());
        int updated = userProfileMapper.update(null, new UpdateWrapper<PetUserProfile>()
                .eq("id", audit.getTargetId()).eq("blogger_status", 2).set("blogger_status", audit.getAuditStatus()));
        if (updated == 0) {
            return error("仅待审核博主申请可处理，已处理记录请在审核历史中查看");
        }
        saveAudit("blogger", audit);
        if (profile != null) {
            boolean passed = audit.getAuditStatus() == 1;
            notificationService.create(profile.getUserId(), SecurityUtils.getUserId(), "blogger_audit", "profile",
                    profile.getId(), passed ? "宠物博主认证通过" : "宠物博主认证未通过",
                    passed ? "你的主页已展示宠物博主标识。" : defaultAuditReason(audit, "你的宠物博主认证未通过。"),
                    "/me");
        }
        return success();
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:service:list')")
    @GetMapping("/services/list")
    public TableDataInfo serviceList(PetServiceItem service) {
        startPage();
        return getDataTable(serviceItemMapper.selectManagerServices(service.getServiceName(), service.getServiceType()));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:service:add')")
    @PostMapping("/services")
    public AjaxResult addService(@RequestBody PetServiceItem service) {
        if (service.getMerchantId() == null || approvedMerchant(service.getMerchantId()) == null) {
            return error("请选择已审核通过且启用的商家");
        }
        service.setStatus(service.getStatus() == null ? 0 : service.getStatus());
        service.setReviewScore(new BigDecimal("0.00"));
        service.setReviewCount(0);
        service.setCreateBy(getUsername());
        service.setCreateTime(new Date());
        return toAjax(serviceItemMapper.insert(service));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:service:edit')")
    @PutMapping("/services")
    public AjaxResult updateService(@RequestBody PetServiceItem service) {
        if (service.getId() == null) {
            return error("服务不存在");
        }
        PetServiceItem exists = serviceItemMapper.selectById(service.getId());
        if (exists == null) {
            return error("服务不存在");
        }
        Long merchantId = service.getMerchantId() == null ? exists.getMerchantId() : service.getMerchantId();
        if (approvedMerchant(merchantId) == null) {
            return error("请选择已审核通过且启用的商家");
        }
        service.setMerchantId(merchantId);
        service.setReviewScore(exists.getReviewScore());
        service.setReviewCount(exists.getReviewCount());
        service.setUpdateBy(getUsername());
        service.setUpdateTime(new Date());
        return toAjax(serviceItemMapper.updateById(service));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:service:remove')")
    @DeleteMapping("/services/{ids}")
    public AjaxResult removeServices(@PathVariable Long[] ids) {
        long requestCount = serviceRequestMapper.selectCount(new QueryWrapper<PetServiceRequest>().in("service_id", Arrays.asList(ids)));
        long reviewCount = serviceReviewMapper.selectCount(new QueryWrapper<PetServiceReview>().in("service_id", Arrays.asList(ids)));
        if (requestCount > 0 || reviewCount > 0) {
            return error("服务已有预约或评价记录，请改为下架以保留订单历史");
        }
        return toAjax(serviceItemMapper.deleteBatchIds(Arrays.asList(ids)));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:serviceRequest:list')")
    @GetMapping("/service-requests/list")
    public TableDataInfo serviceRequestList(PetServiceRequest request) {
        startPage();
        return getDataTable(serviceRequestMapper.selectManagerRequests(request.getStatus()));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:serviceRequest:list')")
    @PutMapping("/service-requests/status")
    public AjaxResult updateServiceRequestStatus(@RequestBody PetServiceRequest request) {
        if (request == null || request.getId() == null || !isAllowedAuditStatus(request.getStatus(), 1, 2, 3)) {
            return error("状态不合法");
        }
        PetServiceRequest exists = serviceRequestMapper.selectById(request.getId());
        if (exists == null) {
            return error("预约单不存在");
        }
        if (!canTransitionServiceRequest(exists.getStatus(), request.getStatus())) {
            return error("当前订单状态不能更新为「" + requestStatusText(request.getStatus()) + "」");
        }
        PetServiceRequest update = new PetServiceRequest();
        update.setId(request.getId());
        update.setStatus(request.getStatus());
        update.setRemark(request.getRemark());
        update.setUpdateBy(getUsername());
        update.setUpdateTime(new Date());
        int rows = serviceRequestMapper.updateById(update);
        if (rows > 0 && exists != null) {
            notificationService.create(exists.getUserId(), SecurityUtils.getUserId(), "service_request_status", "service_request",
                    exists.getId(), requestStatusTitle(request.getStatus()),
                    "你的预约单 #" + exists.getId() + " 已更新为「" + requestStatusText(request.getStatus()) + "」。", "/services");
        }
        return toAjax(rows);
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:review:list')")
    @GetMapping("/reviews/list")
    public TableDataInfo reviewList(PetServiceReview review, @RequestParam(required = false) String statusScope) {
        startPage();
        return getDataTable(serviceReviewMapper.selectManagerReviews(review.getStatus(), review.getHideStatus(), statusScope));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:review:list')")
    @PostMapping("/reviews/audit")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult auditReview(@RequestBody PetAuditRecord audit) {
        if (!isAllowedAuditStatus(audit.getAuditStatus(), 2, 3)) {
            return error("审核状态不合法");
        }
        PetServiceReview review = serviceReviewMapper.selectById(audit.getTargetId());
        if (review == null) {
            return error("评价不存在");
        }
        Integer visibleStatus = audit.getAuditStatus() != null && audit.getAuditStatus() == 2 ? 2 : 1;
        int updated = serviceReviewMapper.update(null, new UpdateWrapper<PetServiceReview>()
                .eq("id", audit.getTargetId()).eq("hide_status", 1)
                .set("hide_status", audit.getAuditStatus())
                .set("hide_audit_reason", audit.getAuditReason())
                .set("status", visibleStatus)
                .set("top_flag", visibleStatus == 2 ? 0 : review.getTopFlag())
                .set("top_time", visibleStatus == 2 ? null : review.getTopTime())
                .set("update_by", getUsername())
                .set("update_time", new Date()));
        if (updated == 0) {
            return error("仅待处理屏蔽申请可审核，已处理记录请在审核历史中查看");
        }
        saveAudit("review_hide", audit);
        recalcReviewStats(review.getServiceId(), review.getMerchantId());
        PetMerchant merchant = merchantMapper.selectById(review.getMerchantId());
        if (merchant != null) {
            boolean hidden = audit.getAuditStatus() == 2;
            notificationService.create(merchant.getUserId(), SecurityUtils.getUserId(), "review_hide_audit", "service_review",
                    review.getId(), hidden ? "评价屏蔽申请已通过" : "评价屏蔽申请已驳回",
                    hidden ? "评价已按申请屏蔽。" : defaultAuditReason(audit, "评价仍保持公开展示。"),
                    "/merchant");
        }
        return success();
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:health:list')")
    @GetMapping("/health-records/list")
    public TableDataInfo healthList(PetHealthRecord record) {
        startPage();
        return getDataTable(healthRecordMapper.selectManagerRecords(record.getPetId()));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:reminder:list')")
    @GetMapping("/reminders/list")
    public TableDataInfo reminderList(PetReminder reminder) {
        startPage();
        return getDataTable(reminderMapper.selectManagerReminders(reminder.getStatus(), reminder.getPetId()));
    }

    @PreAuthorize("@ss.hasPermi('manager:pet:statistics:view')")
    @GetMapping("/statistics/overview")
    public AjaxResult statisticsOverview() {
        Map<String, Object> data = new HashMap<>();
        data.put("profiles", userProfileMapper.selectCount(null));
        data.put("pets", petProfileMapper.selectCount(null));
        data.put("posts", postMapper.selectCount(null));
        data.put("pendingPosts", postMapper.selectCount(new QueryWrapper<PetPost>().eq("audit_status", 0)));
        data.put("comments", commentMapper.selectCount(null));
        data.put("merchants", merchantMapper.selectCount(null));
        data.put("pendingMerchants", merchantMapper.selectCount(new QueryWrapper<PetMerchant>().eq("qualification_status", 0)));
        data.put("services", serviceItemMapper.selectCount(null));
        data.put("reviews", serviceReviewMapper.selectCount(null));
        data.put("reminders", reminderMapper.selectCount(null));
        return success(data);
    }

    private void saveAudit(String targetType, PetAuditRecord audit) {
        audit.setTargetType(targetType);
        audit.setAuditorId(SecurityUtils.getUserId());
        audit.setCreateBy(getUsername());
        audit.setCreateTime(new Date());
        auditRecordMapper.insert(audit);
    }

    private String defaultAuditReason(PetAuditRecord audit, String fallback) {
        return StringUtils.isEmpty(audit.getAuditReason()) ? fallback : audit.getAuditReason();
    }

    private String requestStatusTitle(Integer status) {
        if (status != null && status == 1) {
            return "预约已被接受";
        }
        if (status != null && status == 2) {
            return "服务订单已完成";
        }
        if (status != null && status == 3) {
            return "预约已取消";
        }
        return "预约状态已更新";
    }

    private String requestStatusText(Integer status) {
        if (status != null && status == 1) {
            return "接受预约";
        }
        if (status != null && status == 2) {
            return "已完成";
        }
        if (status != null && status == 3) {
            return "已取消";
        }
        return "待处理";
    }

    private boolean isAllowedAuditStatus(Integer status, Integer... allowedStatuses) {
        if (status == null) {
            return false;
        }
        for (Integer allowedStatus : allowedStatuses) {
            if (status.equals(allowedStatus)) {
                return true;
            }
        }
        return false;
    }

    private int deletePostCascade(List<PetPost> posts) {
        if (posts == null || posts.isEmpty()) {
            return 0;
        }
        List<Long> ids = posts.stream().map(PetPost::getId).collect(java.util.stream.Collectors.toList());
        Set<Long> authorIds = posts.stream().map(PetPost::getAuthorId).collect(java.util.stream.Collectors.toSet());
        Set<Long> topicIds = postTopicMapper.selectList(new QueryWrapper<PetPostTopic>().in("post_id", ids)).stream()
                .map(PetPostTopic::getTopicId).collect(java.util.stream.Collectors.toSet());
        postMediaMapper.delete(new QueryWrapper<PetPostMedia>().in("post_id", ids));
        postTopicMapper.delete(new QueryWrapper<PetPostTopic>().in("post_id", ids));
        commentMapper.delete(new QueryWrapper<PetComment>().in("post_id", ids));
        interactionMapper.delete(new QueryWrapper<PetInteraction>()
                .eq("target_type", "post").in("target_id", ids));
        int rows = postMapper.deleteBatchIds(ids);
        recalcTopicCounts(topicIds);
        authorIds.forEach(this::recalcProfilePostCount);
        return rows;
    }

    private void recalcTopicCounts(Set<Long> topicIds) {
        if (topicIds == null || topicIds.isEmpty()) {
            return;
        }
        for (Long topicId : topicIds) {
            long count = postTopicMapper.selectCount(new QueryWrapper<PetPostTopic>()
                    .eq("topic_id", topicId)
                    .inSql("post_id", "select id from pet_post where audit_status = 1 and status = 0"));
            topicMapper.update(null, new UpdateWrapper<PetTopic>().eq("id", topicId).set("post_count", count));
        }
    }

    private void recalcPostCommentCount(Long postId) {
        if (postId == null) {
            return;
        }
        long count = commentMapper.selectCount(new QueryWrapper<PetComment>()
                .eq("post_id", postId).eq("audit_status", 1).eq("status", 0));
        postMapper.update(null, new UpdateWrapper<PetPost>().eq("id", postId).set("comment_count", count));
    }

    private void recalcProfilePostCount(Long userId) {
        if (userId == null) {
            return;
        }
        long count = postMapper.selectCount(new QueryWrapper<PetPost>()
                .eq("author_id", userId).eq("status", 0).eq("audit_status", 1));
        userProfileMapper.update(null, new UpdateWrapper<PetUserProfile>()
                .eq("user_id", userId).set("post_count", count));
    }

    private void recalcReviewStats(Long serviceId, Long merchantId) {
        if (serviceId != null) {
            List<PetServiceReview> serviceReviews = serviceReviewMapper.selectList(new QueryWrapper<PetServiceReview>()
                    .eq("service_id", serviceId).eq("status", 1));
            BigDecimal score = averageRating(serviceReviews);
            serviceItemMapper.update(null, new UpdateWrapper<PetServiceItem>().eq("id", serviceId)
                    .set("review_score", score).set("review_count", serviceReviews.size()));
        }
        if (merchantId != null) {
            List<PetServiceReview> merchantReviews = serviceReviewMapper.selectList(new QueryWrapper<PetServiceReview>()
                    .eq("merchant_id", merchantId).eq("status", 1));
            BigDecimal score = averageRating(merchantReviews);
            merchantMapper.update(null, new UpdateWrapper<PetMerchant>().eq("id", merchantId)
                    .set("score", score).set("review_count", merchantReviews.size()));
        }
    }

    private BigDecimal averageRating(List<PetServiceReview> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return new BigDecimal("0.00");
        }
        int sum = 0;
        for (PetServiceReview review : reviews) {
            sum += review.getRating() == null ? 0 : review.getRating();
        }
        return BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(reviews.size()), 2, RoundingMode.HALF_UP);
    }

    private PetMerchant approvedMerchant(Long merchantId) {
        if (merchantId == null) {
            return null;
        }
        return merchantMapper.selectOne(new QueryWrapper<PetMerchant>()
                .eq("id", merchantId).eq("qualification_status", 1).eq("status", 0));
    }

    private boolean canTransitionServiceRequest(Integer currentStatus, Integer nextStatus) {
        if (currentStatus == null || nextStatus == null || currentStatus.equals(nextStatus)) {
            return false;
        }
        if (Integer.valueOf(0).equals(currentStatus)) {
            return Integer.valueOf(1).equals(nextStatus) || Integer.valueOf(3).equals(nextStatus);
        }
        if (Integer.valueOf(1).equals(currentStatus)) {
            return Integer.valueOf(2).equals(nextStatus) || Integer.valueOf(3).equals(nextStatus);
        }
        return false;
    }
}
