/*
 * Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
 */
package com.ruoyi.pet.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.pet.domain.*;
import com.ruoyi.pet.mapper.*;
import com.ruoyi.pet.service.PetNotificationService;

@RestController
@RequestMapping("/app/pet")
public class PetAppController extends BaseController {
    @Autowired private PetUserProfileMapper userProfileMapper;
    @Autowired private PetProfileMapper petProfileMapper;
    @Autowired private PetPostMapper postMapper;
    @Autowired private PetPostMediaMapper postMediaMapper;
    @Autowired private PetTopicMapper topicMapper;
    @Autowired private PetPostTopicMapper postTopicMapper;
    @Autowired private PetCommentMapper commentMapper;
    @Autowired private PetInteractionMapper interactionMapper;
    @Autowired private PetMerchantMapper merchantMapper;
    @Autowired private PetMerchantQualificationMapper qualificationMapper;
    @Autowired private PetServiceItemMapper serviceItemMapper;
    @Autowired private PetServiceRequestMapper serviceRequestMapper;
    @Autowired private PetServiceMessageMapper serviceMessageMapper;
    @Autowired private PetServiceReviewMapper serviceReviewMapper;
    @Autowired private PetHealthRecordMapper healthRecordMapper;
    @Autowired private PetReminderMapper reminderMapper;
    @Autowired private PetNotificationMapper notificationMapper;
    @Autowired private PetBoardingRelationMapper boardingRelationMapper;
    @Autowired private PetAdoptionPetMapper adoptionPetMapper;
    @Autowired private PetAdoptionApplicationMapper adoptionApplicationMapper;
    @Autowired private PetAdoptionMessageMapper adoptionMessageMapper;
    @Autowired private PetAdoptionFollowupMapper adoptionFollowupMapper;
    // 客户端通知管线入口，项目版权地址：https://github.com/powerpan/ruoyi_pet_adopt.git
    @Autowired private PetNotificationService notificationService;

    @GetMapping("/profile")
    public R<PetUserProfile> profile() {
        Long userId = SecurityUtils.getUserId();
        PetUserProfile profile = userProfileMapper.selectOne(new QueryWrapper<PetUserProfile>().eq("user_id", userId));
        if (profile == null) {
            profile = new PetUserProfile();
            profile.setUserId(userId);
            profile.setNickname(SecurityUtils.getUsername());
            profile.setBloggerStatus(0);
            profile.setFollowerCount(0);
            profile.setFollowingCount(0);
            profile.setPostCount(0);
            profile.setCreateBy(SecurityUtils.getUsername());
            profile.setCreateTime(new Date());
            userProfileMapper.insert(profile);
        }
        return R.ok(profile);
    }

    @PutMapping("/profile")
    public R<Integer> updateProfile(@RequestBody PetUserProfile profile) {
        Long userId = SecurityUtils.getUserId();
        PetUserProfile exists = userProfileMapper.selectOne(new QueryWrapper<PetUserProfile>().eq("user_id", userId));
        PetUserProfile update = new PetUserProfile();
        update.setUserId(userId);
        update.setNickname(profile.getNickname());
        update.setAvatar(profile.getAvatar());
        update.setBio(profile.getBio());
        update.setHomepageCover(profile.getHomepageCover());
        update.setUpdateBy(SecurityUtils.getUsername());
        update.setUpdateTime(new Date());
        if (exists == null) {
            update.setBloggerStatus(0);
            update.setFollowerCount(0);
            update.setFollowingCount(0);
            update.setPostCount(0);
            update.setCreateBy(SecurityUtils.getUsername());
            update.setCreateTime(new Date());
            return R.ok(userProfileMapper.insert(update));
        }
        update.setId(exists.getId());
        return R.ok(userProfileMapper.updateById(update));
    }

    @GetMapping("/notifications")
    public TableDataInfo notifications(@RequestParam(required = false) Integer status,
                                       @RequestParam(required = false) String noticeType) {
        Long userId = SecurityUtils.getUserId();
        notificationService.syncDueReminderNotifications(userId);
        startPage();
        return getDataTable(notificationMapper.selectUserNotifications(userId, status, noticeType));
    }

    @GetMapping("/notifications/unread-count")
    public R<Long> unreadNotificationCount() {
        return R.ok(notificationService.countUnread(SecurityUtils.getUserId()));
    }

    @PutMapping("/notifications/{id:[0-9]+}/read")
    public R<Integer> readNotification(@PathVariable Long id) {
        return R.ok(notificationMapper.update(null, new UpdateWrapper<PetNotification>()
                .eq("id", id)
                .eq("receiver_user_id", SecurityUtils.getUserId())
                .set("status", 1)
                .set("read_time", new Date())
                .set("update_by", SecurityUtils.getUsername())
                .set("update_time", new Date())));
    }

    @PutMapping("/notifications/read-all")
    public R<Integer> readAllNotifications() {
        return R.ok(notificationMapper.update(null, new UpdateWrapper<PetNotification>()
                .eq("receiver_user_id", SecurityUtils.getUserId())
                .eq("status", 0)
                .set("status", 1)
                .set("read_time", new Date())
                .set("update_by", SecurityUtils.getUsername())
                .set("update_time", new Date())));
    }

    @DeleteMapping("/notifications/{ids:[0-9,]+}")
    public R<Integer> deleteNotifications(@PathVariable Long[] ids) {
        return R.ok(notificationMapper.delete(new QueryWrapper<PetNotification>()
                .in("id", Arrays.asList(ids))
                .eq("receiver_user_id", SecurityUtils.getUserId())));
    }

    @PostMapping("/profile/blogger-apply")
    public R<Integer> applyBlogger() {
        Long userId = SecurityUtils.getUserId();
        PetUserProfile profile = userProfileMapper.selectOne(new QueryWrapper<PetUserProfile>().eq("user_id", userId));
        if (profile == null) {
            profile();
            profile = userProfileMapper.selectOne(new QueryWrapper<PetUserProfile>().eq("user_id", userId));
        }
        if (profile.getBloggerStatus() != null && profile.getBloggerStatus() == 1) {
            return R.fail("宠物博主认证已通过，无需重复申请");
        }
        if (profile.getBloggerStatus() != null && profile.getBloggerStatus() == 2) {
            return R.fail("宠物博主认证正在审核中，请勿重复提交");
        }
        int rows = userProfileMapper.update(null, new UpdateWrapper<PetUserProfile>()
                .eq("id", profile.getId())
                .in("blogger_status", Arrays.asList(0, 3))
                .set("blogger_status", 2)
                .set("update_by", SecurityUtils.getUsername())
                .set("update_time", new Date()));
        if (rows == 0) {
            return R.fail("认证状态已变化，请刷新后重试");
        }
        return R.ok(rows);
    }

    @GetMapping("/pets")
    public TableDataInfo pets(PetProfile query) {
        startPage();
        QueryWrapper<PetProfile> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", SecurityUtils.getUserId()).orderByDesc("create_time");
        List<PetProfile> pets = petProfileMapper.selectList(wrapper);
        decorateBoardingPets(pets, SecurityUtils.getUserId());
        return getDataTable(pets);
    }

    @PostMapping("/pets")
    public R<Integer> addPet(@RequestBody PetProfile pet) {
        if (pet == null || StringUtils.isEmpty(pet.getName()) || StringUtils.isEmpty(pet.getSpecies())) {
            return R.fail("请填写宠物名称和物种");
        }
        pet.setUserId(SecurityUtils.getUserId());
        pet.setStatus(defaultInt(pet.getStatus(), 0));
        pet.setCreateBy(SecurityUtils.getUsername());
        pet.setCreateTime(new Date());
        return R.ok(petProfileMapper.insert(pet));
    }

    @PutMapping("/pets")
    public R<Integer> updatePet(@RequestBody PetProfile pet) {
        if (pet == null || pet.getId() == null) {
            return R.fail("宠物档案不存在");
        }
        if (StringUtils.isEmpty(pet.getName()) || StringUtils.isEmpty(pet.getSpecies())) {
            return R.fail("请填写宠物名称和物种");
        }
        pet.setUserId(SecurityUtils.getUserId());
        pet.setUpdateBy(SecurityUtils.getUsername());
        pet.setUpdateTime(new Date());
        return R.ok(petProfileMapper.update(pet, new UpdateWrapper<PetProfile>().eq("id", pet.getId()).eq("user_id", pet.getUserId())));
    }

    @Anonymous
    @GetMapping("/topics")
    public TableDataInfo topics(PetTopic topic) {
        startPage();
        QueryWrapper<PetTopic> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(topic.getName())) {
            wrapper.like("name", topic.getName());
        }
        wrapper.eq("status", 0).orderByDesc("post_count").orderByDesc("create_time");
        return getDataTable(topicMapper.selectList(wrapper));
    }

    @Anonymous
    @GetMapping("/posts")
    public TableDataInfo posts(@RequestParam(required = false) Long topicId,
                               @RequestParam(required = false) String keyword) {
        startPage();
        QueryWrapper<PetPost> wrapper = new QueryWrapper<>();
        wrapper.eq("audit_status", 1).eq("status", 0);
        if (StringUtils.isNotEmpty(keyword)) {
            wrapper.and(w -> w.like("title", keyword).or().like("content", keyword));
        }
        if (topicId != null) {
            List<Object> postIds = postTopicMapper.selectObjs(new QueryWrapper<PetPostTopic>()
                    .select("post_id").eq("topic_id", topicId));
            if (postIds.isEmpty()) {
                wrapper.eq("id", -1);
            } else {
                wrapper.in("id", postIds);
            }
        }
        wrapper.orderByDesc("create_time");
        return getDataTable(postMapper.selectList(wrapper));
    }

    @Anonymous
    @GetMapping("/posts/{id:[0-9]+}")
    public R<PetPost> postDetail(@PathVariable Long id) {
        PetPost post = publicPost(id);
        if (post == null) {
            return R.fail("动态不存在或暂未公开");
        }
        postMapper.update(null, new UpdateWrapper<PetPost>()
                .eq("id", id).eq("audit_status", 1).eq("status", 0)
                .setSql("view_count = ifnull(view_count, 0) + 1"));
        post = publicPost(id);
        if (post != null) {
            hydratePost(post);
            Long userId = currentUserIdOrNull();
            if (userId != null) {
                post.setLiked(hasInteraction(userId, id, "like"));
                post.setFavorited(hasInteraction(userId, id, "favorite"));
            }
        }
        return R.ok(post);
    }

    @GetMapping("/posts/mine")
    public TableDataInfo myPosts(@RequestParam(required = false) Integer auditStatus) {
        startPage();
        QueryWrapper<PetPost> wrapper = new QueryWrapper<PetPost>()
                .eq("author_id", SecurityUtils.getUserId());
        if (auditStatus != null) {
            wrapper.eq("audit_status", auditStatus);
        }
        wrapper.orderByDesc("create_time");
        List<PetPost> posts = postMapper.selectList(wrapper);
        posts.forEach(this::hydratePost);
        return getDataTable(posts);
    }

    @GetMapping("/posts/favorites")
    public TableDataInfo favoritePosts(@RequestParam(required = false) String keyword) {
        Long userId = SecurityUtils.getUserId();
        startPage();
        List<PetPost> posts = postMapper.selectFavoritePosts(userId, keyword);
        posts.forEach(post -> {
            hydratePost(post);
            post.setFavorited(true);
            post.setLiked(hasInteraction(userId, post.getId(), "like"));
        });
        return getDataTable(posts);
    }

    @PostMapping("/posts")
    @Transactional(rollbackFor = Exception.class)
    public R<Long> addPost(@RequestBody PetPost post) {
        post.setAuthorId(SecurityUtils.getUserId());
        post.setAuditStatus(0);
        post.setStatus(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setFavoriteCount(0);
        post.setViewCount(0);
        post.setCreateBy(SecurityUtils.getUsername());
        post.setCreateTime(new Date());
        postMapper.insert(post);
        savePostMedia(post);
        savePostTopics(post);
        recalcProfilePostCount(SecurityUtils.getUserId());
        return R.ok(post.getId());
    }

    @PutMapping("/posts")
    @Transactional(rollbackFor = Exception.class)
    public R<Integer> updatePost(@RequestBody PetPost post) {
        Long userId = SecurityUtils.getUserId();
        PetPost exists = postMapper.selectOne(new QueryWrapper<PetPost>()
                .eq("id", post.getId()).eq("author_id", userId));
        if (exists == null) {
            return R.fail("帖子不存在或无权编辑");
        }
        if (exists.getAuditStatus() != null && exists.getAuditStatus() == 1) {
            return R.fail("已通过审核的帖子暂不支持编辑");
        }
        Set<Long> affectedTopicIds = new HashSet<>();
        postTopicMapper.selectObjs(new QueryWrapper<PetPostTopic>().select("topic_id").eq("post_id", post.getId()))
                .forEach(item -> affectedTopicIds.add(Long.valueOf(String.valueOf(item))));
        if (!CollectionUtils.isEmpty(post.getTopicIds())) {
            affectedTopicIds.addAll(post.getTopicIds());
        }
        post.setAuthorId(userId);
        post.setAuditStatus(0);
        post.setStatus(defaultInt(post.getStatus(), 0));
        post.setUpdateBy(SecurityUtils.getUsername());
        post.setUpdateTime(new Date());
        int rows = postMapper.update(post, new UpdateWrapper<PetPost>()
                .eq("id", post.getId()).eq("author_id", userId).ne("audit_status", 1));
        if (rows == 0) {
            return R.fail("帖子状态已变化，请刷新后重试");
        }
        postMediaMapper.delete(new QueryWrapper<PetPostMedia>().eq("post_id", post.getId()));
        postTopicMapper.delete(new QueryWrapper<PetPostTopic>().eq("post_id", post.getId()));
        savePostMedia(post);
        savePostTopics(post);
        recalcTopicCounts(affectedTopicIds);
        return R.ok(rows);
    }

    @DeleteMapping("/posts/{ids:[0-9,]+}")
    @Transactional(rollbackFor = Exception.class)
    public R<Integer> deletePosts(@PathVariable Long[] ids) {
        List<PetPost> posts = postMapper.selectList(new QueryWrapper<PetPost>()
                .in("id", Arrays.asList(ids)).eq("author_id", SecurityUtils.getUserId()));
        return R.ok(deletePostCascade(posts));
    }

    @Anonymous
    @GetMapping("/posts/{postId:[0-9]+}/comments")
    public TableDataInfo comments(@PathVariable Long postId) {
        if (publicPost(postId) == null) {
            return getDataTable(java.util.Collections.emptyList());
        }
        startPage();
        return getDataTable(commentMapper.selectVisibleByPost(postId));
    }

    @PostMapping("/posts/{postId:[0-9]+}/comments")
    public R<Integer> addComment(@PathVariable Long postId, @RequestBody PetComment comment) {
        if (publicPost(postId) == null) {
            return R.fail("动态不存在或暂未公开");
        }
        comment.setPostId(postId);
        comment.setUserId(SecurityUtils.getUserId());
        comment.setAuditStatus(0);
        comment.setStatus(0);
        comment.setLikeCount(0);
        comment.setCreateBy(SecurityUtils.getUsername());
        comment.setCreateTime(new Date());
        int rows = commentMapper.insert(comment);
        return R.ok(rows);
    }

    @PostMapping("/posts/{postId:[0-9]+}/interactions")
    public R<Map<String, Object>> interact(@PathVariable Long postId, @RequestParam String type) {
        if (!"like".equals(type) && !"favorite".equals(type)) {
            return R.fail("不支持的互动类型");
        }
        if (publicPost(postId) == null) {
            return R.fail("动态不存在或暂未公开");
        }
        Long userId = SecurityUtils.getUserId();
        QueryWrapper<PetInteraction> exists = new QueryWrapper<PetInteraction>()
                .eq("user_id", userId).eq("target_type", "post").eq("target_id", postId).eq("interaction_type", type);
        Map<String, Object> data = new HashMap<>();
        PetInteraction old = interactionMapper.selectOne(exists);
        if (old != null) {
            interactionMapper.deleteById(old.getId());
            if ("like".equals(type)) {
                postMapper.update(null, new UpdateWrapper<PetPost>().eq("id", postId).setSql("like_count = greatest(ifnull(like_count, 0) - 1, 0)"));
            } else {
                postMapper.update(null, new UpdateWrapper<PetPost>().eq("id", postId).setSql("favorite_count = greatest(ifnull(favorite_count, 0) - 1, 0)"));
            }
            data.put("active", false);
            return R.ok(data, "已取消");
        }
        PetInteraction interaction = new PetInteraction();
        interaction.setUserId(userId);
        interaction.setTargetType("post");
        interaction.setTargetId(postId);
        interaction.setInteractionType(type);
        interaction.setCreateTime(new Date());
        int rows = interactionMapper.insert(interaction);
        if ("like".equals(type)) {
            postMapper.update(null, new UpdateWrapper<PetPost>().eq("id", postId).setSql("like_count = ifnull(like_count, 0) + 1"));
        } else if ("favorite".equals(type)) {
            postMapper.update(null, new UpdateWrapper<PetPost>().eq("id", postId).setSql("favorite_count = ifnull(favorite_count, 0) + 1"));
        }
        data.put("active", true);
        data.put("rows", rows);
        return R.ok(data);
    }

    @PostMapping("/merchants")
    public R<Long> addMerchant(@RequestBody PetMerchant merchant) {
        if (currentMerchantForUser(SecurityUtils.getUserId()) != null) {
            return R.fail("一个账号仅能绑定一个商家，请在商家工作台维护当前商家资料");
        }
        if (merchant == null || StringUtils.isEmpty(merchant.getName()) || StringUtils.isEmpty(merchant.getPhone())) {
            return R.fail("请填写商家名称和联系电话");
        }
        merchant.setUserId(SecurityUtils.getUserId());
        merchant.setQualificationStatus(0);
        merchant.setStatus(1);
        merchant.setScore(new BigDecimal("0.00"));
        merchant.setReviewCount(0);
        merchant.setCreateBy(SecurityUtils.getUsername());
        merchant.setCreateTime(new Date());
        merchantMapper.insert(merchant);
        return R.ok(merchant.getId());
    }

    @PutMapping("/merchants")
    public R<Integer> updateMerchant(@RequestBody PetMerchant merchant) {
        if (merchant == null || merchant.getId() == null) {
            return R.fail("商家不存在");
        }
        PetMerchant exists = ownedMerchant(merchant.getId());
        if (exists == null) {
            return R.fail("商家不存在或无权编辑");
        }
        if (StringUtils.isEmpty(merchant.getName()) || StringUtils.isEmpty(merchant.getPhone())) {
            return R.fail("请填写商家名称和联系电话");
        }
        PetMerchant update = new PetMerchant();
        update.setId(exists.getId());
        update.setUserId(SecurityUtils.getUserId());
        update.setName(merchant.getName());
        update.setContactName(merchant.getContactName());
        update.setPhone(merchant.getPhone());
        update.setCity(merchant.getCity());
        update.setDistrict(merchant.getDistrict());
        update.setAddress(merchant.getAddress());
        update.setLongitude(merchant.getLongitude());
        update.setLatitude(merchant.getLatitude());
        update.setDescription(merchant.getDescription());
        update.setLogoUrl(merchant.getLogoUrl());
        update.setScore(exists.getScore());
        update.setReviewCount(exists.getReviewCount());
        if (exists.getQualificationStatus() != null && exists.getQualificationStatus() == 1) {
            update.setQualificationStatus(exists.getQualificationStatus());
            update.setStatus(exists.getStatus());
        } else {
            update.setQualificationStatus(0);
            update.setStatus(1);
        }
        update.setUpdateBy(SecurityUtils.getUsername());
        update.setUpdateTime(new Date());
        return R.ok(merchantMapper.update(update, new UpdateWrapper<PetMerchant>()
                .eq("id", exists.getId()).eq("user_id", SecurityUtils.getUserId())));
    }

    @PostMapping("/merchants/{merchantId:[0-9]+}/qualifications")
    public R<Integer> addQualification(@PathVariable Long merchantId, @RequestBody PetMerchantQualification qualification) {
        if (ownedMerchant(merchantId) == null) {
            return R.fail("商家不存在或无权提交资质");
        }
        qualification.setMerchantId(merchantId);
        qualification.setAuditStatus(0);
        qualification.setCreateBy(SecurityUtils.getUsername());
        qualification.setCreateTime(new Date());
        return R.ok(qualificationMapper.insert(qualification));
    }

    @GetMapping("/merchants/mine")
    public TableDataInfo myMerchants() {
        startPage();
        return getDataTable(merchantMapper.selectList(new QueryWrapper<PetMerchant>()
                .eq("user_id", SecurityUtils.getUserId()).orderByDesc("create_time")));
    }

    @GetMapping("/merchants/{merchantId:[0-9]+}/qualifications")
    public TableDataInfo merchantQualifications(@PathVariable Long merchantId) {
        PetMerchant merchant = merchantMapper.selectOne(new QueryWrapper<PetMerchant>()
                .eq("id", merchantId).eq("user_id", SecurityUtils.getUserId()));
        if (merchant == null) {
            return getDataTable(java.util.Collections.emptyList());
        }
        startPage();
        return getDataTable(qualificationMapper.selectList(new QueryWrapper<PetMerchantQualification>()
                .eq("merchant_id", merchantId).orderByDesc("create_time")));
    }

    @Anonymous
    @GetMapping("/merchants")
    public TableDataInfo merchants(PetMerchant merchant) {
        startPage();
        QueryWrapper<PetMerchant> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0).eq("qualification_status", 1);
        if (StringUtils.isNotEmpty(merchant.getName())) {
            wrapper.like("name", merchant.getName());
        }
        if (StringUtils.isNotEmpty(merchant.getCity())) {
            wrapper.eq("city", merchant.getCity());
        }
        wrapper.orderByDesc("score").orderByDesc("create_time");
        return getDataTable(merchantMapper.selectList(wrapper));
    }

    @Anonymous
    @GetMapping("/services")
    public TableDataInfo services(@RequestParam(required = false) BigDecimal latitude,
                                  @RequestParam(required = false) BigDecimal longitude,
                                  @RequestParam(defaultValue = "5") BigDecimal radiusKm,
                                  @RequestParam(required = false) String serviceType,
                                  @RequestParam(required = false) String keyword) {
        startPage();
        if (latitude != null && longitude != null) {
            return getDataTable(serviceItemMapper.selectNearby(latitude, longitude, radiusKm, serviceType, keyword));
        }
        return getDataTable(serviceItemMapper.selectVisible(serviceType, keyword));
    }

    @GetMapping("/merchants/{merchantId:[0-9]+}/services")
    public TableDataInfo myMerchantServices(@PathVariable Long merchantId,
                                            @RequestParam(required = false) Integer status) {
        PetMerchant merchant = ownedMerchant(merchantId);
        if (merchant == null) {
            return getDataTable(java.util.Collections.emptyList());
        }
        startPage();
        QueryWrapper<PetServiceItem> wrapper = new QueryWrapper<PetServiceItem>().eq("merchant_id", merchantId);
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        return getDataTable(serviceItemMapper.selectList(wrapper));
    }

    @PostMapping("/merchants/{merchantId:[0-9]+}/services")
    public R<Long> addMerchantService(@PathVariable Long merchantId, @RequestBody PetServiceItem service) {
        PetMerchant merchant = ownedApprovedMerchant(merchantId);
        if (merchant == null) {
            return R.fail("商家审核通过后才能发布服务");
        }
        if (service == null || !isAllowedStatus(defaultInt(service.getStatus(), 0), 0, 1)) {
            return R.fail("服务状态不合法");
        }
        service.setMerchantId(merchant.getId());
        service.setStatus(defaultInt(service.getStatus(), 0));
        service.setReviewScore(new BigDecimal("0.00"));
        service.setReviewCount(0);
        service.setCreateBy(SecurityUtils.getUsername());
        service.setCreateTime(new Date());
        serviceItemMapper.insert(service);
        return R.ok(service.getId());
    }

    @PutMapping("/merchant-services")
    public R<Integer> updateMerchantService(@RequestBody PetServiceItem service) {
        if (service == null || service.getId() == null) {
            return R.fail("服务不存在");
        }
        if (service.getStatus() != null && !isAllowedStatus(service.getStatus(), 0, 1)) {
            return R.fail("服务状态不合法");
        }
        PetServiceItem exists = serviceItemMapper.selectById(service.getId());
        if (exists == null || ownedMerchant(exists.getMerchantId()) == null) {
            return R.fail("服务不存在或无权编辑");
        }
        if (ownedApprovedMerchant(exists.getMerchantId()) == null) {
            return R.fail("商家审核通过后才能维护服务");
        }
        service.setMerchantId(exists.getMerchantId());
        service.setReviewScore(exists.getReviewScore());
        service.setReviewCount(exists.getReviewCount());
        service.setUpdateBy(SecurityUtils.getUsername());
        service.setUpdateTime(new Date());
        return R.ok(serviceItemMapper.updateById(service));
    }

    @DeleteMapping("/merchant-services/{ids:[0-9,]+}")
    public R<Integer> deleteMerchantServices(@PathVariable Long[] ids) {
        List<Long> merchantIds = merchantMapper.selectObjs(new QueryWrapper<PetMerchant>()
                .select("id").eq("user_id", SecurityUtils.getUserId()))
                .stream().map(item -> Long.valueOf(String.valueOf(item))).collect(java.util.stream.Collectors.toList());
        if (merchantIds.isEmpty()) {
            return R.ok(0);
        }
        long requestCount = serviceRequestMapper.selectCount(new QueryWrapper<PetServiceRequest>()
                .in("service_id", Arrays.asList(ids)).in("merchant_id", merchantIds));
        long reviewCount = serviceReviewMapper.selectCount(new QueryWrapper<PetServiceReview>()
                .in("service_id", Arrays.asList(ids)).in("merchant_id", merchantIds));
        if (requestCount > 0 || reviewCount > 0) {
            return R.fail("服务已有预约或评价记录，请改为下架以保留订单历史");
        }
        return R.ok(serviceItemMapper.delete(new QueryWrapper<PetServiceItem>()
                .in("id", Arrays.asList(ids)).in("merchant_id", merchantIds)));
    }

    @PostMapping("/service-requests")
    public R<Long> addServiceRequest(@RequestBody PetServiceRequest request) {
        if (request == null || request.getServiceId() == null) {
            return R.fail("请选择要预约的服务");
        }
        PetServiceItem service = visibleService(request.getServiceId());
        if (service == null) {
            return R.fail("服务已下架或商家未通过审核");
        }
        if (request.getPetId() != null && !isOwnedPet(request.getPetId())) {
            return R.fail("宠物档案不存在或无权用于预约");
        }
        request.setUserId(SecurityUtils.getUserId());
        request.setMerchantId(service.getMerchantId());
        request.setStatus(0);
        request.setCreateBy(SecurityUtils.getUsername());
        request.setCreateTime(new Date());
        serviceRequestMapper.insert(request);
        PetMerchant merchant = merchantMapper.selectById(service.getMerchantId());
        if (merchant != null) {
            notificationService.create(merchant.getUserId(), SecurityUtils.getUserId(), "service_request", "service_request",
                    request.getId(), "收到新的预约咨询",
                    service.getServiceName() + " 有新的预约，请在商家工作台处理。", "/merchant");
        }
        return R.ok(request.getId());
    }

    @GetMapping("/service-requests/mine")
    public TableDataInfo myServiceRequests(@RequestParam(required = false) Integer status) {
        startPage();
        return getDataTable(serviceRequestMapper.selectUserRequests(SecurityUtils.getUserId(), status));
    }

    @GetMapping("/service-requests/{requestId:[0-9]+}/messages")
    public R<List<PetServiceMessage>> serviceRequestMessages(@PathVariable Long requestId) {
        PetServiceRequest request = authorizedServiceRequest(requestId);
        if (request == null) {
            return R.fail("订单不存在或无权查看");
        }
        return R.ok(serviceMessageMapper.selectByRequestId(requestId));
    }

    @PostMapping("/service-requests/{requestId:[0-9]+}/messages")
    public R<Integer> addServiceRequestMessage(@PathVariable Long requestId, @RequestBody PetServiceMessage message) {
        PetServiceRequest request = authorizedServiceRequest(requestId);
        if (request == null) {
            return R.fail("订单不存在或无权发送消息");
        }
        if (message == null || StringUtils.isEmpty(message.getContent())) {
            return R.fail("消息内容不能为空");
        }
        String actorRole = serviceRequestActorRole(request);
        if (actorRole == null) {
            return R.fail("订单不存在或无权发送消息");
        }
        PetServiceMessage insert = new PetServiceMessage();
        insert.setRequestId(requestId);
        insert.setSenderUserId(SecurityUtils.getUserId());
        insert.setSenderRole(actorRole);
        insert.setContent(message.getContent());
        insert.setCreateBy(SecurityUtils.getUsername());
        insert.setCreateTime(new Date());
        int rows = serviceMessageMapper.insert(insert);
        notifyServiceMessage(request, actorRole, message.getContent());
        return R.ok(rows);
    }

    @GetMapping("/merchant-service-requests")
    public TableDataInfo myMerchantServiceRequests(@RequestParam(required = false) Long merchantId,
                                                   @RequestParam(required = false) Integer status) {
        startPage();
        return getDataTable(serviceRequestMapper.selectMerchantRequests(SecurityUtils.getUserId(), merchantId, status));
    }

    @PutMapping("/merchant-service-requests/status")
    public R<Integer> updateMerchantServiceRequestStatus(@RequestBody PetServiceRequest request) {
        if (request == null || request.getId() == null || !isAllowedStatus(request.getStatus(), 1, 2, 3)) {
            return R.fail("状态不合法");
        }
        PetServiceRequest exists = serviceRequestMapper.selectById(request.getId());
        if (exists == null || ownedMerchant(exists.getMerchantId()) == null) {
            return R.fail("咨询不存在或无权处理");
        }
        if (!canTransitionServiceRequest(exists.getStatus(), request.getStatus())) {
            return R.fail("当前订单状态不能更新为「" + requestStatusText(request.getStatus()) + "」");
        }
        PetServiceRequest update = new PetServiceRequest();
        update.setId(request.getId());
        update.setStatus(request.getStatus());
        update.setRemark(request.getRemark());
        update.setUpdateBy(SecurityUtils.getUsername());
        update.setUpdateTime(new Date());
        int rows = serviceRequestMapper.update(update, new UpdateWrapper<PetServiceRequest>()
                .eq("id", exists.getId())
                .eq("merchant_id", exists.getMerchantId())
                .eq("status", exists.getStatus()));
        if (rows == 0) {
            return R.fail("订单状态已变化，请刷新后重试");
        }
        if (rows > 0) {
            notificationService.create(exists.getUserId(), SecurityUtils.getUserId(), "service_request_status", "service_request",
                    exists.getId(), requestStatusTitle(request.getStatus()),
                    "你的预约单 #" + exists.getId() + " 已更新为「" + requestStatusText(request.getStatus()) + "」。", "/services");
        }
        return R.ok(rows);
    }

    @PostMapping("/service-reviews")
    public R<Integer> addServiceReview(@RequestBody PetServiceReview review) {
        if (review.getRequestId() == null) {
            return R.fail("请从已完成预约单提交评价");
        }
        PetServiceRequest request = serviceRequestMapper.selectById(review.getRequestId());
        if (request == null || !SecurityUtils.getUserId().equals(request.getUserId())) {
            return R.fail("预约单不存在或无权评价");
        }
        if (request.getStatus() == null || request.getStatus() != 2) {
            return R.fail("商家完成订单后才能评价");
        }
        if (serviceReviewMapper.selectCount(new QueryWrapper<PetServiceReview>().eq("request_id", request.getId())) > 0) {
            return R.fail("该预约单已评价");
        }
        PetServiceItem service = serviceItemMapper.selectById(request.getServiceId());
        if (service == null) {
            return R.fail("服务记录不存在，无法评价");
        }
        if (review.getServiceId() != null && !request.getServiceId().equals(review.getServiceId())) {
            return R.fail("评价服务与预约单不匹配");
        }
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            return R.fail("评分范围应为1到5");
        }
        review.setUserId(SecurityUtils.getUserId());
        review.setServiceId(request.getServiceId());
        review.setMerchantId(service.getMerchantId());
        review.setStatus(1);
        review.setHideStatus(0);
        review.setTopFlag(0);
        review.setCreateBy(SecurityUtils.getUsername());
        review.setCreateTime(new Date());
        int rows = serviceReviewMapper.insert(review);
        recalcReviewStats(review.getServiceId(), review.getMerchantId());
        PetMerchant merchant = merchantMapper.selectById(review.getMerchantId());
        if (merchant != null) {
            notificationService.create(merchant.getUserId(), SecurityUtils.getUserId(), "service_review", "service_review",
                    review.getId(), "收到新的服务评价",
                    "用户对「" + service.getServiceName() + "」提交了 " + review.getRating() + " 星评价。", "/merchant");
        }
        return R.ok(rows);
    }

    @Anonymous
    @GetMapping("/services/{serviceId:[0-9]+}/reviews")
    public TableDataInfo serviceReviews(@PathVariable Long serviceId,
                                        @RequestParam(required = false) String ratingType) {
        startPage();
        return getDataTable(serviceReviewMapper.selectPublicReviews(serviceId, normalizeRatingType(ratingType)));
    }

    @GetMapping("/merchant-reviews")
    public TableDataInfo merchantReviews(@RequestParam(required = false) Long merchantId,
                                         @RequestParam(required = false) Integer hideStatus,
                                         @RequestParam(required = false) String ratingType) {
        startPage();
        return getDataTable(serviceReviewMapper.selectMerchantReviews(SecurityUtils.getUserId(), merchantId, hideStatus, normalizeRatingType(ratingType)));
    }

    @PostMapping("/merchant-reviews/{reviewId:[0-9]+}/top")
    public R<Integer> topMerchantReview(@PathVariable Long reviewId, @RequestBody PetServiceReview request) {
        PetServiceReview review = serviceReviewMapper.selectById(reviewId);
        if (review == null || ownedMerchant(review.getMerchantId()) == null) {
            return R.fail("评价不存在或无权处理");
        }
        boolean top = request != null && request.getTopFlag() != null && request.getTopFlag() == 1;
        if (top) {
            if (review.getStatus() == null || review.getStatus() != 1) {
                return R.fail("仅公开评价可置顶");
            }
            if (review.getRating() == null || review.getRating() < 4) {
                return R.fail("仅好评支持置顶");
            }
            if (review.getHideStatus() != null && review.getHideStatus() == 2) {
                return R.fail("已屏蔽评价不能置顶");
            }
            if (review.getHideStatus() != null && review.getHideStatus() == 1) {
                return R.fail("屏蔽审核中的评价不能置顶");
            }
        }
        UpdateWrapper<PetServiceReview> update = new UpdateWrapper<PetServiceReview>()
                .eq("id", reviewId)
                .set("top_flag", top ? 1 : 0)
                .set("top_time", top ? new Date() : null)
                .set("update_by", SecurityUtils.getUsername())
                .set("update_time", new Date());
        return R.ok(serviceReviewMapper.update(null, update));
    }

    @PostMapping("/merchant-reviews/{reviewId:[0-9]+}/hide-request")
    public R<Integer> requestHideReview(@PathVariable Long reviewId, @RequestBody PetServiceReview request) {
        PetServiceReview review = serviceReviewMapper.selectById(reviewId);
        if (review == null || ownedMerchant(review.getMerchantId()) == null) {
            return R.fail("评价不存在或无权处理");
        }
        if (review.getStatus() == null || review.getStatus() != 1) {
            return R.fail("仅公开评价可申请屏蔽");
        }
        if (review.getHideStatus() != null && (review.getHideStatus() == 1 || review.getHideStatus() == 2)) {
            return R.fail("该评价已提交屏蔽申请或已屏蔽");
        }
        if (request == null || StringUtils.isEmpty(request.getHideReason())) {
            return R.fail("请填写申请屏蔽理由");
        }
        return R.ok(serviceReviewMapper.update(null, new UpdateWrapper<PetServiceReview>()
                .eq("id", reviewId)
                .set("hide_status", 1)
                .set("hide_reason", request.getHideReason())
                .set("hide_audit_reason", "")
                .set("update_by", SecurityUtils.getUsername())
                .set("update_time", new Date())));
    }

    @GetMapping("/merchant-boarding/users")
    public R<List<PetBoardingUserCandidate>> searchBoardingUsers(@RequestParam String keyword) {
        PetMerchant merchant = currentApprovedMerchantForUser(SecurityUtils.getUserId());
        if (merchant == null) {
            return R.fail("商家审核通过后才能搜索客户并发起寄养档案请求");
        }
        if (StringUtils.isEmpty(keyword) || keyword.trim().length() < 2) {
            return R.fail("请输入至少2个字符的用户名或完整用户ID");
        }
        return R.ok(boardingRelationMapper.searchUserCandidates(keyword.trim()));
    }

    @PostMapping("/merchant-boarding/requests")
    @Transactional(rollbackFor = Exception.class)
    public R<Long> createBoardingRequest(@RequestBody PetBoardingRelation request) {
        PetMerchant merchant = currentApprovedMerchantForUser(SecurityUtils.getUserId());
        if (merchant == null) {
            return R.fail("商家审核通过后才能发起寄养档案请求");
        }
        if (request == null || request.getOwnerUserId() == null || StringUtils.isEmpty(request.getPetName())) {
            return R.fail("请选择客户并填写准确宠物名");
        }
        if (SecurityUtils.getUserId().equals(request.getOwnerUserId())) {
            return R.fail("不能向当前商家账号自己发起寄养档案请求");
        }
        String petName = request.getPetName().trim();
        List<PetProfile> matchedPets = petProfileMapper.selectList(new QueryWrapper<PetProfile>()
                .eq("user_id", request.getOwnerUserId()).eq("name", petName));
        if (matchedPets.size() != 1) {
            return R.fail("用户或宠物信息不匹配，请确认用户名/用户ID和准确宠物名");
        }
        PetProfile ownerPet = matchedPets.get(0);
        long openCount = boardingRelationMapper.selectCount(new QueryWrapper<PetBoardingRelation>()
                .eq("merchant_id", merchant.getId())
                .eq("owner_pet_id", ownerPet.getId())
                .in("status", Arrays.asList(0, 1)));
        if (openCount > 0) {
            return R.fail("该宠物已有待确认或进行中的寄养关系");
        }
        PetBoardingRelation relation = new PetBoardingRelation();
        relation.setMerchantId(merchant.getId());
        relation.setMerchantUserId(merchant.getUserId());
        relation.setOwnerUserId(request.getOwnerUserId());
        relation.setOwnerPetId(ownerPet.getId());
        relation.setPetName(ownerPet.getName());
        relation.setRequestNote(request.getRequestNote());
        relation.setStatus(0);
        relation.setRequestTime(new Date());
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(new Date());
        boardingRelationMapper.insert(relation);
        notificationService.create(request.getOwnerUserId(), SecurityUtils.getUserId(), "boarding_request", "boarding_relation",
                relation.getId(), "商家请求寄养档案授权",
                merchant.getName() + " 请求复制宠物「" + ownerPet.getName() + "」的档案用于临时寄养，请到宠物档案页确认。", "/pets");
        return R.ok(relation.getId());
    }

    @GetMapping("/merchant-boarding/relations")
    public TableDataInfo merchantBoardingRelations(@RequestParam(required = false) Long merchantId,
                                                   @RequestParam(required = false) Integer status) {
        if (currentMerchantForUser(SecurityUtils.getUserId()) == null) {
            return getDataTable(java.util.Collections.emptyList());
        }
        startPage();
        return getDataTable(boardingRelationMapper.selectMerchantRelations(SecurityUtils.getUserId(), merchantId, status));
    }

    @GetMapping("/boarding/relations")
    public TableDataInfo ownerBoardingRelations(@RequestParam(required = false) Integer status) {
        startPage();
        return getDataTable(boardingRelationMapper.selectOwnerRelations(SecurityUtils.getUserId(), status));
    }

    @PostMapping("/boarding/relations/{id:[0-9]+}/approve")
    @Transactional(rollbackFor = Exception.class)
    public R<Integer> approveBoardingRelation(@PathVariable Long id) {
        Long userId = SecurityUtils.getUserId();
        PetBoardingRelation relation = boardingRelationMapper.selectById(id);
        if (relation == null || !userId.equals(relation.getOwnerUserId())) {
            return R.fail("寄养请求不存在或无权处理");
        }
        if (relation.getStatus() == null || relation.getStatus() != 0) {
            return R.fail("该寄养请求已处理");
        }
        PetMerchant merchant = merchantMapper.selectById(relation.getMerchantId());
        PetProfile ownerPet = petProfileMapper.selectOne(new QueryWrapper<PetProfile>()
                .eq("id", relation.getOwnerPetId()).eq("user_id", relation.getOwnerUserId()));
        if (merchant == null || ownerPet == null) {
            return R.fail("寄养请求关联的商家或宠物不存在");
        }
        Date now = new Date();
        int claimed = boardingRelationMapper.update(null, new UpdateWrapper<PetBoardingRelation>()
                .eq("id", relation.getId())
                .eq("owner_user_id", userId)
                .eq("status", 0)
                .set("status", 1)
                .set("confirm_time", now)
                .set("update_by", SecurityUtils.getUsername())
                .set("update_time", now));
        if (claimed == 0) {
            return R.fail("该寄养请求已处理，请刷新后查看");
        }
        PetProfile merchantPet = copyPetForBoarding(ownerPet, relation, merchant);
        copyHealthRecordsForBoarding(ownerPet, merchantPet, relation);
        int rows = boardingRelationMapper.update(null, new UpdateWrapper<PetBoardingRelation>()
                .eq("id", relation.getId())
                .eq("status", 1)
                .isNull("merchant_pet_id")
                .set("merchant_pet_id", merchantPet.getId())
                .set("update_by", SecurityUtils.getUsername())
                .set("update_time", new Date()));
        if (rows == 0) {
            throw new ServiceException("寄养请求状态已变化，请刷新后重试");
        }
        notificationService.create(relation.getMerchantUserId(), SecurityUtils.getUserId(), "boarding_approved", "boarding_relation",
                relation.getId(), "客户已同意寄养档案授权",
                "宠物「" + relation.getPetName() + "」的档案已复制到商家寄养档案。", "/pets");
        return R.ok(rows);
    }

    @PostMapping("/boarding/relations/{id:[0-9]+}/reject")
    @Transactional(rollbackFor = Exception.class)
    public R<Integer> rejectBoardingRelation(@PathVariable Long id) {
        PetBoardingRelation relation = boardingRelationMapper.selectById(id);
        if (relation == null || !SecurityUtils.getUserId().equals(relation.getOwnerUserId())) {
            return R.fail("寄养请求不存在或无权处理");
        }
        if (relation.getStatus() == null || relation.getStatus() != 0) {
            return R.fail("该寄养请求已处理");
        }
        int rows = boardingRelationMapper.update(null, new UpdateWrapper<PetBoardingRelation>()
                .eq("id", id)
                .eq("owner_user_id", SecurityUtils.getUserId())
                .eq("status", 0)
                .set("status", 2)
                .set("update_by", SecurityUtils.getUsername())
                .set("update_time", new Date()));
        if (rows == 0) {
            return R.fail("该寄养请求已处理，请刷新后查看");
        }
        notificationService.create(relation.getMerchantUserId(), SecurityUtils.getUserId(), "boarding_rejected", "boarding_relation",
                relation.getId(), "客户拒绝了寄养档案授权",
                "宠物「" + relation.getPetName() + "」的档案授权请求已被客户拒绝。", "/pets");
        return R.ok(rows);
    }

    @PostMapping("/boarding/relations/{id:[0-9]+}/cancel")
    @Transactional(rollbackFor = Exception.class)
    public R<Integer> cancelBoardingRelation(@PathVariable Long id) {
        PetBoardingRelation relation = boardingRelationMapper.selectById(id);
        if (relation == null) {
            return R.fail("寄养关系不存在");
        }
        Long userId = SecurityUtils.getUserId();
        boolean ownerCancel = userId.equals(relation.getOwnerUserId());
        boolean merchantCancel = userId.equals(relation.getMerchantUserId());
        if (!ownerCancel && !merchantCancel) {
            return R.fail("无权取消该寄养关系");
        }
        if (relation.getStatus() == null || relation.getStatus() != 1) {
            return R.fail("只有进行中的寄养关系可以取消");
        }
        int status = ownerCancel ? 3 : 4;
        int rows = boardingRelationMapper.update(null, new UpdateWrapper<PetBoardingRelation>()
                .eq("id", id)
                .eq("status", 1)
                .set("status", status)
                .set("cancel_by", userId)
                .set("cancel_time", new Date())
                .set("update_by", SecurityUtils.getUsername())
                .set("update_time", new Date()));
        if (rows == 0) {
            return R.fail("寄养关系状态已变化，请刷新后重试");
        }
        syncBoardingBackToOwner(relation);
        Long receiver = ownerCancel ? relation.getMerchantUserId() : relation.getOwnerUserId();
        String actionUrl = "/pets";
        notificationService.create(receiver, userId, "boarding_cancelled", "boarding_relation",
                relation.getId(), "寄养关系已取消",
                "宠物「" + relation.getPetName() + "」的最新档案已同步回客户账户，商家侧临时档案已收回。", actionUrl);
        return R.ok(rows);
    }

    @Anonymous
    @GetMapping("/adoptions")
    public TableDataInfo adoptionList(@RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) String species,
                                      @RequestParam(required = false) String city,
                                      @RequestParam(required = false) Integer status) {
        startPage();
        return getDataTable(adoptionPetMapper.selectPublicAdoptions(keyword, species, city, status));
    }

    @GetMapping("/adoptions/mine")
    public TableDataInfo myAdoptions(@RequestParam(required = false) Integer status,
                                     @RequestParam(required = false) String keyword) {
        startPage();
        return getDataTable(adoptionPetMapper.selectUserPublished(SecurityUtils.getUserId(), status, keyword));
    }

    @Anonymous
    @GetMapping("/adoptions/{id:[0-9]+}")
    public R<PetAdoptionPet> adoptionDetail(@PathVariable Long id) {
        PetAdoptionPet pet = adoptionPetMapper.selectById(id);
        if (pet == null) {
            return R.fail("待领养宠物不存在");
        }
        Long userId = currentUserIdOrNull();
        boolean visible = isPublicAdoptionStatus(pet.getStatus())
                || (userId != null && (userId.equals(pet.getPublisherUserId()) || userId.equals(pet.getAdoptedUserId())));
        if (!visible) {
            return R.fail("待领养宠物不存在或暂未公开");
        }
        return R.ok(pet);
    }

    @PostMapping("/adoptions")
    public R<Long> addAdoption(@RequestBody PetAdoptionPet pet) {
        if (pet == null || StringUtils.isEmpty(pet.getName()) || StringUtils.isEmpty(pet.getSpecies())) {
            return R.fail("请填写宠物名称和物种");
        }
        Long userId = SecurityUtils.getUserId();
        pet.setPublisherUserId(userId);
        pet.setPublisherType(StringUtils.isEmpty(pet.getPublisherType()) ? "personal" : pet.getPublisherType());
        if (pet.getMerchantId() != null && ownedApprovedMerchant(pet.getMerchantId()) == null) {
            return R.fail("只能使用本人已审核通过的商家身份发布");
        }
        pet.setStatus(1);
        pet.setAuditReason("");
        pet.setAdoptedUserId(null);
        pet.setAdoptedTime(null);
        pet.setCreateBy(SecurityUtils.getUsername());
        pet.setCreateTime(new Date());
        adoptionPetMapper.insert(pet);
        return R.ok(pet.getId());
    }

    @PutMapping("/adoptions")
    public R<Integer> updateAdoption(@RequestBody PetAdoptionPet pet) {
        if (pet == null || pet.getId() == null) {
            return R.fail("待领养宠物不存在");
        }
        PetAdoptionPet exists = adoptionPetMapper.selectOne(new QueryWrapper<PetAdoptionPet>()
                .eq("id", pet.getId()).eq("publisher_user_id", SecurityUtils.getUserId()));
        if (exists == null) {
            return R.fail("待领养宠物不存在或无权编辑");
        }
        if (exists.getStatus() != null && (exists.getStatus() == 2 || exists.getStatus() == 3 || exists.getStatus() == 4)) {
            return R.fail("已公开、已预约或已领养的记录不能直接编辑");
        }
        if (StringUtils.isEmpty(pet.getName()) || StringUtils.isEmpty(pet.getSpecies())) {
            return R.fail("请填写宠物名称和物种");
        }
        pet.setPublisherUserId(SecurityUtils.getUserId());
        pet.setPublisherType(StringUtils.isEmpty(pet.getPublisherType()) ? exists.getPublisherType() : pet.getPublisherType());
        if (pet.getMerchantId() != null && ownedApprovedMerchant(pet.getMerchantId()) == null) {
            return R.fail("只能使用本人已审核通过的商家身份发布");
        }
        pet.setStatus(1);
        pet.setAuditReason("");
        pet.setAdoptedUserId(null);
        pet.setAdoptedTime(null);
        pet.setUpdateBy(SecurityUtils.getUsername());
        pet.setUpdateTime(new Date());
        return R.ok(adoptionPetMapper.update(pet, new UpdateWrapper<PetAdoptionPet>()
                .eq("id", pet.getId()).eq("publisher_user_id", SecurityUtils.getUserId())
                .notIn("status", Arrays.asList(2, 3, 4))));
    }

    @DeleteMapping("/adoptions/{ids:[0-9,]+}")
    public R<Integer> deleteAdoptions(@PathVariable Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        List<Long> deletableIds = adoptionPetMapper.selectObjs(new QueryWrapper<PetAdoptionPet>()
                .select("id")
                .in("id", idList)
                .eq("publisher_user_id", SecurityUtils.getUserId())
                .in("status", Arrays.asList(0, 5, 6)))
                .stream().map(item -> Long.valueOf(String.valueOf(item))).collect(java.util.stream.Collectors.toList());
        if (deletableIds.isEmpty()) {
            return R.ok(0);
        }
        long applicationCount = adoptionApplicationMapper.selectCount(new QueryWrapper<PetAdoptionApplication>()
                .in("adoption_pet_id", deletableIds));
        long followupCount = adoptionFollowupMapper.selectCount(new QueryWrapper<PetAdoptionFollowup>()
                .in("adoption_pet_id", deletableIds));
        if (applicationCount > 0 || followupCount > 0) {
            return R.fail("待领养宠物已有申请或回访记录，请改为下架以保留业务历史");
        }
        return R.ok(adoptionPetMapper.delete(new QueryWrapper<PetAdoptionPet>()
                .in("id", deletableIds)));
    }

    @PostMapping("/adoptions/{id:[0-9]+}/applications")
    public R<Long> applyAdoption(@PathVariable Long id, @RequestBody PetAdoptionApplication application) {
        PetAdoptionPet pet = adoptionPetMapper.selectById(id);
        if (pet == null || pet.getStatus() == null || pet.getStatus() != 2) {
            return R.fail("当前宠物暂不可申请领养");
        }
        Long userId = SecurityUtils.getUserId();
        if (userId.equals(pet.getPublisherUserId())) {
            return R.fail("发布方不能申请自己发布的宠物");
        }
        long exists = adoptionApplicationMapper.selectCount(new QueryWrapper<PetAdoptionApplication>()
                .eq("adoption_pet_id", id)
                .eq("applicant_user_id", userId)
                .notIn("status", Arrays.asList(3, 4, 6, 7)));
        if (exists > 0) {
            return R.fail("你已提交过该宠物的领养申请，请勿重复提交");
        }
        if (application == null || StringUtils.isEmpty(application.getRealName()) || StringUtils.isEmpty(application.getPhone())) {
            return R.fail("请填写真实姓名和联系电话");
        }
        application.setAdoptionPetId(id);
        application.setApplicantUserId(userId);
        application.setPublisherUserId(pet.getPublisherUserId());
        application.setStatus(0);
        application.setReviewReason("");
        application.setCreateBy(SecurityUtils.getUsername());
        application.setCreateTime(new Date());
        adoptionApplicationMapper.insert(application);
        notificationService.create(pet.getPublisherUserId(), userId, "adoption_application", "adoption_application",
                application.getId(), "收到新的领养申请", "宠物「" + pet.getName() + "」收到新的领养申请，请及时处理。", "/adoption-manage");
        return R.ok(application.getId());
    }

    @GetMapping("/adoption-applications/mine")
    public TableDataInfo myAdoptionApplications(@RequestParam(required = false) Integer status) {
        startPage();
        return getDataTable(adoptionApplicationMapper.selectUserApplications(SecurityUtils.getUserId(), status));
    }

    @GetMapping("/adoption-applications/received")
    public TableDataInfo receivedAdoptionApplications(@RequestParam(required = false) Integer status,
                                                      @RequestParam(required = false) Long adoptionPetId) {
        startPage();
        return getDataTable(adoptionApplicationMapper.selectReceivedApplications(SecurityUtils.getUserId(), status, adoptionPetId));
    }

    @GetMapping("/adoption-applications/{applicationId}/messages")
    public R<List<PetAdoptionMessage>> adoptionApplicationMessages(@PathVariable String applicationId) {
        Long parsedApplicationId = parseApplicationId(applicationId);
        if (parsedApplicationId == null) {
            return R.fail("申请记录缺少ID，请刷新后重试");
        }
        PetAdoptionApplication application = authorizedAdoptionApplication(parsedApplicationId);
        if (application == null) {
            return R.fail("领养申请不存在或无权查看对话");
        }
        return R.ok(adoptionMessageMapper.selectByApplicationId(parsedApplicationId));
    }

    @PostMapping("/adoption-applications/{applicationId}/messages")
    public R<Integer> addAdoptionApplicationMessage(@PathVariable String applicationId, @RequestBody PetAdoptionMessage message) {
        Long parsedApplicationId = parseApplicationId(applicationId);
        if (parsedApplicationId == null) {
            return R.fail("申请记录缺少ID，请刷新后重试");
        }
        PetAdoptionApplication application = authorizedAdoptionApplication(parsedApplicationId);
        if (application == null) {
            return R.fail("领养申请不存在或无权发送消息");
        }
        if (isTerminalAdoptionApplication(application.getStatus())) {
            return R.fail("已结束的申请不能继续沟通");
        }
        String content = message == null || message.getContent() == null ? "" : message.getContent().trim();
        if (StringUtils.isEmpty(content)) {
            return R.fail("消息内容不能为空");
        }
        String actorRole = adoptionApplicationActorRole(application);
        if (actorRole == null) {
            return R.fail("领养申请不存在或无权发送消息");
        }
        PetAdoptionMessage insert = new PetAdoptionMessage();
        insert.setApplicationId(parsedApplicationId);
        insert.setAdoptionPetId(application.getAdoptionPetId());
        insert.setSenderUserId(SecurityUtils.getUserId());
        insert.setSenderRole(actorRole);
        insert.setMessageType("text");
        insert.setContent(content);
        insert.setCreateBy(SecurityUtils.getUsername());
        insert.setCreateTime(new Date());
        int rows = adoptionMessageMapper.insert(insert);
        notifyAdoptionMessage(application, actorRole, content);
        return R.ok(rows);
    }

    @PutMapping("/adoption-applications/status")
    @Transactional(rollbackFor = Exception.class)
    public R<Integer> updateAdoptionApplicationStatus(@RequestBody PetAdoptionApplication application) {
        if (application == null || application.getId() == null || !isAllowedAdoptionApplicationStatus(application.getStatus())) {
            return R.fail("申请状态不合法");
        }
        PetAdoptionApplication exists = adoptionApplicationMapper.selectById(application.getId());
        if (exists == null || !SecurityUtils.getUserId().equals(exists.getPublisherUserId())) {
            return R.fail("领养申请不存在或无权处理");
        }
        if (isTerminalAdoptionApplication(exists.getStatus())) {
            return R.fail("已结束的申请不能重复处理");
        }
        PetAdoptionPet pet = adoptionPetMapper.selectById(exists.getAdoptionPetId());
        if (pet == null || pet.getStatus() == null || pet.getStatus() == 4) {
            return R.fail("待领养宠物不存在或已完成领养");
        }
        if (!canTransitionAdoptionApplication(exists.getStatus(), application.getStatus())) {
            return R.fail("当前申请状态不能更新为「" + adoptionApplicationStatusText(application.getStatus()) + "」");
        }
        if (application.getStatus() != null && application.getStatus() == 5) {
            if (exists.getStatus() == null || (exists.getStatus() != 1 && exists.getStatus() != 5)) {
                return R.fail("请先进入沟通，再安排看宠");
            }
            if (application.getVisitTime() == null || application.getVisitAddress() == null || application.getVisitAddress().trim().length() == 0) {
                return R.fail("安排看宠必须填写看宠时间和地点");
            }
        }
        PetAdoptionApplication update = new PetAdoptionApplication();
        update.setId(exists.getId());
        update.setStatus(application.getStatus());
        update.setReviewReason(application.getReviewReason());
        update.setVisitTime(application.getVisitTime());
        update.setVisitAddress(application.getVisitAddress());
        update.setUpdateBy(SecurityUtils.getUsername());
        update.setUpdateTime(new Date());
        if (application.getStatus() != null && application.getStatus() == 5) {
            long scheduledCount = adoptionApplicationMapper.selectCount(new QueryWrapper<PetAdoptionApplication>()
                    .eq("adoption_pet_id", pet.getId())
                    .ne("id", exists.getId())
                    .eq("status", 5));
            if (scheduledCount > 0) {
                return R.fail("该宠物已有约看中的申请，请先处理原约看安排");
            }
            if (pet.getStatus() != null && pet.getStatus() != 2 && pet.getStatus() != 3) {
                return R.fail("当前宠物状态不能安排看宠预约");
            }
        }
        int rows = adoptionApplicationMapper.update(update, new UpdateWrapper<PetAdoptionApplication>()
                .eq("id", exists.getId())
                .eq("publisher_user_id", SecurityUtils.getUserId())
                .eq("status", exists.getStatus()));
        if (rows == 0) {
            return R.fail("申请状态已变化，请刷新后重试");
        }
        if (application.getStatus() != null && application.getStatus() == 5) {
            int petRows = adoptionPetMapper.update(null, new UpdateWrapper<PetAdoptionPet>()
                    .eq("id", pet.getId())
                    .in("status", Arrays.asList(2, 3))
                    .set("status", 3)
                    .set("update_by", SecurityUtils.getUsername())
                    .set("update_time", new Date()));
            if (petRows == 0) {
                throw new ServiceException("宠物状态已变化，请刷新后重试");
            }
        } else if (exists.getStatus() != null && exists.getStatus() == 5) {
            adoptionApplicationMapper.update(null, new UpdateWrapper<PetAdoptionApplication>()
                    .eq("id", exists.getId())
                    .eq("status", application.getStatus())
                    .set("visit_time", null)
                    .set("visit_address", ""));
            refreshAdoptionReservationState(pet.getId());
        }
        notificationService.create(exists.getApplicantUserId(), SecurityUtils.getUserId(), "adoption_application_status", "adoption_application",
                exists.getId(), adoptionApplicationStatusTitle(application.getStatus()),
                defaultReviewReason(application.getReviewReason(), "宠物「" + pet.getName() + "」的领养申请状态已更新。"), "/adoption-manage");
        return R.ok(rows);
    }

    @PutMapping("/adoption-applications")
    public R<Integer> updateMyAdoptionApplication(@RequestBody PetAdoptionApplication application) {
        if (application == null || application.getId() == null) {
            return R.fail("领养申请不存在");
        }
        PetAdoptionApplication exists = adoptionApplicationMapper.selectById(application.getId());
        Long userId = SecurityUtils.getUserId();
        if (exists == null || !userId.equals(exists.getApplicantUserId())) {
            return R.fail("领养申请不存在或无权修改");
        }
        if (exists.getStatus() == null || (exists.getStatus() != 0 && exists.getStatus() != 2)) {
            return R.fail("当前申请状态不能补充资料");
        }
        if (StringUtils.isEmpty(application.getRealName()) || StringUtils.isEmpty(application.getPhone())) {
            return R.fail("请填写真实姓名和联系电话");
        }
        PetAdoptionPet pet = adoptionPetMapper.selectById(exists.getAdoptionPetId());
        if (pet == null || pet.getStatus() == null || pet.getStatus() == 4) {
            return R.fail("待领养宠物不存在或已完成领养");
        }
        PetAdoptionApplication update = new PetAdoptionApplication();
        update.setId(exists.getId());
        update.setRealName(application.getRealName());
        update.setPhone(application.getPhone());
        update.setCity(application.getCity());
        update.setHousingType(application.getHousingType());
        update.setPetExperience(application.getPetExperience());
        update.setApplyReason(application.getApplyReason());
        update.setCommitment(application.getCommitment());
        update.setStatus(0);
        update.setReviewReason("");
        update.setUpdateBy(SecurityUtils.getUsername());
        update.setUpdateTime(new Date());
        int rows = adoptionApplicationMapper.update(update, new UpdateWrapper<PetAdoptionApplication>()
                .eq("id", exists.getId())
                .eq("applicant_user_id", userId)
                .in("status", Arrays.asList(0, 2)));
        if (rows > 0) {
            notificationService.create(exists.getPublisherUserId(), userId, "adoption_application_update", "adoption_application",
                    exists.getId(), "领养申请已补充资料", "宠物「" + pet.getName() + "」的领养申请已补充资料，请重新处理。", "/adoption-manage");
        }
        return R.ok(rows);
    }

    @PostMapping("/adoption-applications/{id:[0-9]+}/withdraw")
    @Transactional(rollbackFor = Exception.class)
    public R<Integer> withdrawAdoptionApplication(@PathVariable Long id) {
        PetAdoptionApplication exists = adoptionApplicationMapper.selectById(id);
        Long userId = SecurityUtils.getUserId();
        if (exists == null || !userId.equals(exists.getApplicantUserId())) {
            return R.fail("领养申请不存在或无权撤回");
        }
        if (isTerminalAdoptionApplication(exists.getStatus())) {
            return R.fail("已结束的申请不能撤回");
        }
        Date now = new Date();
        int rows = adoptionApplicationMapper.update(null, new UpdateWrapper<PetAdoptionApplication>()
                .eq("id", exists.getId())
                .eq("applicant_user_id", userId)
                .eq("status", exists.getStatus())
                .set("status", 4)
                .set("visit_time", null)
                .set("visit_address", "")
                .set("review_reason", "申请人主动撤回")
                .set("update_by", SecurityUtils.getUsername())
                .set("update_time", now));
        if (rows == 0) {
            return R.fail("申请状态已变化，请刷新后重试");
        }
        if (exists.getStatus() != null && exists.getStatus() == 5) {
            refreshAdoptionReservationState(exists.getAdoptionPetId());
        }
        PetAdoptionPet pet = adoptionPetMapper.selectById(exists.getAdoptionPetId());
        notificationService.create(exists.getPublisherUserId(), userId, "adoption_application_withdraw", "adoption_application",
                exists.getId(), "领养申请已撤回",
                pet == null ? "有一条领养申请已由申请人撤回。" : "宠物「" + pet.getName() + "」的一条领养申请已由申请人撤回。",
                "/adoption-manage");
        return R.ok(rows);
    }

    @PostMapping("/adoption-applications/{id:[0-9]+}/confirm")
    @Transactional(rollbackFor = Exception.class)
    public R<Integer> confirmAdoption(@PathVariable Long id) {
        PetAdoptionApplication application = adoptionApplicationMapper.selectById(id);
        if (application == null || !SecurityUtils.getUserId().equals(application.getPublisherUserId())) {
            return R.fail("领养申请不存在或无权确认");
        }
        if (application.getStatus() == null || application.getStatus() != 5) {
            return R.fail("只有已约看宠的申请可以确认移交");
        }
        PetAdoptionPet pet = adoptionPetMapper.selectById(application.getAdoptionPetId());
        if (pet == null || pet.getStatus() == null || pet.getStatus() == 4) {
            return R.fail("待领养宠物不存在或已完成领养");
        }
        Date now = new Date();
        int rows = adoptionApplicationMapper.update(null, new UpdateWrapper<PetAdoptionApplication>()
                .eq("id", id)
                .eq("status", 5)
                .set("status", 6)
                .set("update_by", SecurityUtils.getUsername())
                .set("update_time", now));
        if (rows == 0) {
            return R.fail("申请状态已变化，请刷新后重试");
        }
        int petRows = adoptionPetMapper.update(null, new UpdateWrapper<PetAdoptionPet>()
                .eq("id", pet.getId())
                .in("status", Arrays.asList(2, 3))
                .set("status", 4)
                .set("adopted_user_id", application.getApplicantUserId())
                .set("adopted_time", now)
                .set("update_by", SecurityUtils.getUsername())
                .set("update_time", now));
        if (petRows == 0) {
            throw new ServiceException("宠物状态已变化，请刷新后重试");
        }
        List<PetAdoptionApplication> autoClosedApplications = adoptionApplicationMapper.selectList(new QueryWrapper<PetAdoptionApplication>()
                .eq("adoption_pet_id", pet.getId())
                .ne("id", id)
                .notIn("status", Arrays.asList(3, 4, 6, 7)));
        adoptionApplicationMapper.update(null, new UpdateWrapper<PetAdoptionApplication>()
                .eq("adoption_pet_id", pet.getId())
                .ne("id", id)
                .notIn("status", Arrays.asList(3, 4, 6, 7))
                .set("status", 7)
                .set("review_reason", "该宠物已完成领养，其他申请自动关闭")
                .set("update_by", SecurityUtils.getUsername())
                .set("update_time", now));
        for (PetAdoptionApplication closed : autoClosedApplications) {
            notificationService.create(closed.getApplicantUserId(), SecurityUtils.getUserId(), "adoption_application_status", "adoption_application",
                    closed.getId(), "领养申请已关闭",
                    "宠物「" + pet.getName() + "」已完成领养，你的申请已自动关闭。", "/adoption-manage");
        }
        createProfileFromAdoption(pet, application.getApplicantUserId());
        createAdoptionFollowupPlans(application, pet, now);
        notificationService.create(application.getApplicantUserId(), SecurityUtils.getUserId(), "adoption_confirmed", "adoption_application",
                application.getId(), "领养移交已确认", "你已成功领养宠物「" + pet.getName() + "」，后续请按回访计划提交照护情况。", "/adoption-manage");
        return R.ok(rows);
    }

    @GetMapping("/adoption-followups")
    public TableDataInfo adoptionFollowups(@RequestParam(required = false) Integer status) {
        startPage();
        return getDataTable(adoptionFollowupMapper.selectUserFollowups(SecurityUtils.getUserId(), status));
    }

    @PostMapping("/adoption-followups/{id:[0-9]+}/submit")
    public R<Integer> submitAdoptionFollowup(@PathVariable Long id, @RequestBody PetAdoptionFollowup followup) {
        PetAdoptionFollowup exists = adoptionFollowupMapper.selectById(id);
        Long userId = SecurityUtils.getUserId();
        if (exists == null || (!userId.equals(exists.getAdopterUserId()) && !userId.equals(exists.getPublisherUserId()))) {
            return R.fail("回访记录不存在或无权提交");
        }
        if (exists.getStatus() != null && (exists.getStatus() == 2 || exists.getStatus() == 4)) {
            return R.fail("已归档或已关闭的回访不能重复提交");
        }
        PetAdoptionFollowup update = new PetAdoptionFollowup();
        update.setId(id);
        update.setActualTime(new Date());
        update.setHealthStatus(followup.getHealthStatus());
        update.setLivingStatus(followup.getLivingStatus());
        update.setImageUrls(followup.getImageUrls());
        update.setAbnormalReason(followup.getAbnormalReason());
        update.setStatus(StringUtils.isEmpty(followup.getAbnormalReason()) ? 2 : 3);
        update.setUpdateBy(SecurityUtils.getUsername());
        update.setUpdateTime(new Date());
        int rows = adoptionFollowupMapper.updateById(update);
        if (update.getStatus() == 3) {
            Long receiver = userId.equals(exists.getAdopterUserId()) ? exists.getPublisherUserId() : exists.getAdopterUserId();
            notificationService.create(receiver, userId, "adoption_followup_abnormal", "adoption_followup",
                    id, "领养回访存在异常", "宠物回访记录已标记异常，请等待平台处理。", "/adoption-manage");
        }
        return R.ok(rows);
    }

    @GetMapping("/health-records")
    public TableDataInfo healthRecords(@RequestParam(required = false) Long petId) {
        startPage();
        QueryWrapper<PetHealthRecord> wrapper = new QueryWrapper<PetHealthRecord>().eq("user_id", SecurityUtils.getUserId());
        if (petId != null) {
            wrapper.eq("pet_id", petId);
        }
        wrapper.orderByDesc("record_date").orderByDesc("create_time");
        return getDataTable(healthRecordMapper.selectList(wrapper));
    }

    @PostMapping("/health-records")
    public R<Integer> addHealthRecord(@RequestBody PetHealthRecord record) {
        if (record == null || StringUtils.isEmpty(record.getTitle()) || StringUtils.isEmpty(record.getRecordType()) || record.getRecordDate() == null) {
            return R.fail("请填写健康记录标题、类型和记录日期");
        }
        if (!isOwnedPet(record.getPetId())) {
            return R.fail("宠物档案不存在或无权维护");
        }
        record.setUserId(SecurityUtils.getUserId());
        attachBoardingRelationForHealthRecord(record);
        record.setCreateBy(SecurityUtils.getUsername());
        record.setCreateTime(new Date());
        return R.ok(healthRecordMapper.insert(record));
    }

    @PutMapping("/health-records")
    public R<Integer> updateHealthRecord(@RequestBody PetHealthRecord record) {
        if (record == null || record.getId() == null) {
            return R.fail("健康记录不存在");
        }
        if (StringUtils.isEmpty(record.getTitle()) || StringUtils.isEmpty(record.getRecordType()) || record.getRecordDate() == null) {
            return R.fail("请填写健康记录标题、类型和记录日期");
        }
        if (!isOwnedPet(record.getPetId())) {
            return R.fail("宠物档案不存在或无权维护");
        }
        record.setUserId(SecurityUtils.getUserId());
        attachBoardingRelationForHealthRecord(record);
        record.setUpdateBy(SecurityUtils.getUsername());
        record.setUpdateTime(new Date());
        return R.ok(healthRecordMapper.update(record, new UpdateWrapper<PetHealthRecord>()
                .eq("id", record.getId()).eq("user_id", record.getUserId())));
    }

    @DeleteMapping("/health-records/{ids:[0-9,]+}")
    public R<Integer> deleteHealthRecords(@PathVariable Long[] ids) {
        return R.ok(healthRecordMapper.delete(new QueryWrapper<PetHealthRecord>()
                .in("id", Arrays.asList(ids)).eq("user_id", SecurityUtils.getUserId())));
    }

    @GetMapping("/reminders")
    public TableDataInfo reminders(@RequestParam(required = false) Long petId) {
        startPage();
        QueryWrapper<PetReminder> wrapper = new QueryWrapper<PetReminder>().eq("user_id", SecurityUtils.getUserId());
        if (petId != null) {
            wrapper.eq("pet_id", petId);
        }
        wrapper.orderByAsc("due_time");
        return getDataTable(reminderMapper.selectList(wrapper));
    }

    @PostMapping("/reminders")
    public R<Integer> addReminder(@RequestBody PetReminder reminder) {
        if (reminder == null || StringUtils.isEmpty(reminder.getTitle()) || StringUtils.isEmpty(reminder.getReminderType()) || reminder.getDueTime() == null) {
            return R.fail("请填写提醒标题、类型和提醒时间");
        }
        if (!isOwnedPet(reminder.getPetId())) {
            return R.fail("宠物档案不存在或无权维护");
        }
        if (!isAllowedStatus(defaultInt(reminder.getStatus(), 0), 0, 1, 2)) {
            return R.fail("提醒状态不合法");
        }
        reminder.setUserId(SecurityUtils.getUserId());
        reminder.setStatus(defaultInt(reminder.getStatus(), 0));
        reminder.setNoticeSent(0);
        reminder.setCreateBy(SecurityUtils.getUsername());
        reminder.setCreateTime(new Date());
        return R.ok(reminderMapper.insert(reminder));
    }

    @PutMapping("/reminders")
    public R<Integer> updateReminder(@RequestBody PetReminder reminder) {
        if (reminder == null || reminder.getId() == null) {
            return R.fail("提醒不存在");
        }
        if (StringUtils.isEmpty(reminder.getTitle()) || StringUtils.isEmpty(reminder.getReminderType()) || reminder.getDueTime() == null) {
            return R.fail("请填写提醒标题、类型和提醒时间");
        }
        if (!isOwnedPet(reminder.getPetId())) {
            return R.fail("宠物档案不存在或无权维护");
        }
        if (reminder.getStatus() != null && !isAllowedStatus(reminder.getStatus(), 0, 1, 2)) {
            return R.fail("提醒状态不合法");
        }
        reminder.setUserId(SecurityUtils.getUserId());
        if (reminder.getStatus() != null && reminder.getStatus() == 0
                && reminder.getDueTime() != null && reminder.getDueTime().after(new Date())) {
            reminder.setNoticeSent(0);
        }
        reminder.setUpdateBy(SecurityUtils.getUsername());
        reminder.setUpdateTime(new Date());
        return R.ok(reminderMapper.update(reminder, new UpdateWrapper<PetReminder>()
                .eq("id", reminder.getId()).eq("user_id", reminder.getUserId())));
    }

    @DeleteMapping("/reminders/{ids:[0-9,]+}")
    public R<Integer> deleteReminders(@PathVariable Long[] ids) {
        return R.ok(reminderMapper.delete(new QueryWrapper<PetReminder>()
                .in("id", Arrays.asList(ids)).eq("user_id", SecurityUtils.getUserId())));
    }

    @DeleteMapping("/pets/{ids:[0-9,]+}")
    public R<Integer> deletePets(@PathVariable Long[] ids) {
        if (hasActiveBoardingRelation(Arrays.asList(ids), SecurityUtils.getUserId())) {
            return R.fail("存在待确认或寄养中的宠物档案，请先处理寄养关系后再删除");
        }
        healthRecordMapper.delete(new QueryWrapper<PetHealthRecord>()
                .in("pet_id", Arrays.asList(ids)).eq("user_id", SecurityUtils.getUserId()));
        reminderMapper.delete(new QueryWrapper<PetReminder>()
                .in("pet_id", Arrays.asList(ids)).eq("user_id", SecurityUtils.getUserId()));
        return R.ok(petProfileMapper.delete(new QueryWrapper<PetProfile>()
                .in("id", Arrays.asList(ids)).eq("user_id", SecurityUtils.getUserId())));
    }

    private int deletePostCascade(List<PetPost> posts) {
        if (CollectionUtils.isEmpty(posts)) {
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

    private void hydratePost(PetPost post) {
        if (post == null || post.getId() == null) {
            return;
        }
        List<Object> mediaUrls = postMediaMapper.selectObjs(new QueryWrapper<PetPostMedia>()
                .select("url").eq("post_id", post.getId()).orderByAsc("sort_order"));
        List<Object> topicIds = postTopicMapper.selectObjs(new QueryWrapper<PetPostTopic>()
                .select("topic_id").eq("post_id", post.getId()));
        post.setMediaUrls(mediaUrls.stream().map(String::valueOf).collect(java.util.stream.Collectors.toList()));
        post.setTopicIds(topicIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(java.util.stream.Collectors.toList()));
    }

    private boolean hasInteraction(Long userId, Long postId, String type) {
        return interactionMapper.selectCount(new QueryWrapper<PetInteraction>()
                .eq("user_id", userId).eq("target_type", "post").eq("target_id", postId).eq("interaction_type", type)) > 0;
    }

    private PetPost publicPost(Long postId) {
        if (postId == null) {
            return null;
        }
        return postMapper.selectOne(new QueryWrapper<PetPost>()
                .eq("id", postId).eq("audit_status", 1).eq("status", 0));
    }

    private Long currentUserIdOrNull() {
        try {
            return SecurityUtils.getUserId();
        } catch (Exception e) {
            return null;
        }
    }

    private void recalcTopicCounts(Set<Long> topicIds) {
        if (CollectionUtils.isEmpty(topicIds)) {
            return;
        }
        for (Long topicId : topicIds) {
            long count = postTopicMapper.selectCount(new QueryWrapper<PetPostTopic>()
                    .eq("topic_id", topicId)
                    .inSql("post_id", "select id from pet_post where audit_status = 1 and status = 0"));
            topicMapper.update(null, new UpdateWrapper<PetTopic>().eq("id", topicId).set("post_count", count));
        }
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

    private void savePostMedia(PetPost post) {
        if (CollectionUtils.isEmpty(post.getMediaUrls())) {
            return;
        }
        int index = 0;
        for (String url : post.getMediaUrls()) {
            if (StringUtils.isEmpty(url)) {
                continue;
            }
            PetPostMedia media = new PetPostMedia();
            media.setPostId(post.getId());
            media.setUrl(url);
            media.setMediaType(url.toLowerCase().matches(".*\\.(mp4|mov|m4v|webm)$") ? "video" : "image");
            media.setSortOrder(index++);
            postMediaMapper.insert(media);
        }
    }

    private void savePostTopics(PetPost post) {
        if (CollectionUtils.isEmpty(post.getTopicIds())) {
            return;
        }
        for (Long topicId : post.getTopicIds()) {
            PetPostTopic relation = new PetPostTopic();
            relation.setPostId(post.getId());
            relation.setTopicId(topicId);
            postTopicMapper.insert(relation);
        }
    }

    private void decorateBoardingPets(List<PetProfile> pets, Long userId) {
        if (CollectionUtils.isEmpty(pets) || userId == null) {
            return;
        }
        Map<Long, PetBoardingRelation> ownerRelations = new HashMap<>();
        for (PetBoardingRelation relation : boardingRelationMapper.selectOwnerRelations(userId, 1)) {
            if (relation.getOwnerPetId() != null) {
                ownerRelations.put(relation.getOwnerPetId(), relation);
            }
        }
        Map<Long, PetBoardingRelation> merchantRelations = new HashMap<>();
        for (PetBoardingRelation relation : boardingRelationMapper.selectMerchantRelations(userId, null, 1)) {
            if (relation.getMerchantPetId() != null) {
                merchantRelations.put(relation.getMerchantPetId(), relation);
            }
        }
        for (PetProfile pet : pets) {
            PetBoardingRelation ownerRelation = ownerRelations.get(pet.getId());
            if (ownerRelation != null) {
                pet.setBoardingFlag(true);
                pet.setBoardingRole("owner");
                pet.setBoardingRelationId(ownerRelation.getId());
                pet.setBoardingMerchantName(ownerRelation.getMerchantName());
                continue;
            }
            PetBoardingRelation merchantRelation = merchantRelations.get(pet.getId());
            if (merchantRelation != null) {
                pet.setBoardingFlag(true);
                pet.setBoardingRole("merchant");
                pet.setBoardingRelationId(merchantRelation.getId());
                pet.setBoardingMerchantName(merchantRelation.getMerchantName());
                pet.setBoardingOwnerName(displayName(merchantRelation.getOwnerNickName(), merchantRelation.getOwnerUserName(), merchantRelation.getOwnerUserId()));
            }
        }
    }

    private PetProfile copyPetForBoarding(PetProfile ownerPet, PetBoardingRelation relation, PetMerchant merchant) {
        PetProfile merchantPet = new PetProfile();
        copyPetFields(ownerPet, merchantPet);
        merchantPet.setUserId(merchant.getUserId());
        merchantPet.setStatus(defaultInt(ownerPet.getStatus(), 0));
        merchantPet.setRemark("寄养临时档案，来源用户ID：" + relation.getOwnerUserId() + "，来源宠物ID：" + ownerPet.getId());
        merchantPet.setCreateBy(SecurityUtils.getUsername());
        merchantPet.setCreateTime(new Date());
        petProfileMapper.insert(merchantPet);
        return merchantPet;
    }

    private void copyPetFields(PetProfile source, PetProfile target) {
        target.setName(source.getName());
        target.setSpecies(source.getSpecies());
        target.setBreed(source.getBreed());
        target.setGender(source.getGender());
        target.setBirthday(source.getBirthday());
        target.setWeightKg(source.getWeightKg());
        target.setAvatar(source.getAvatar());
        target.setHealthStatus(source.getHealthStatus());
        target.setNeutered(source.getNeutered());
        target.setStatus(source.getStatus());
    }

    private void copyHealthRecordsForBoarding(PetProfile ownerPet, PetProfile merchantPet, PetBoardingRelation relation) {
        List<PetHealthRecord> ownerRecords = healthRecordMapper.selectList(new QueryWrapper<PetHealthRecord>()
                .eq("user_id", relation.getOwnerUserId()).eq("pet_id", ownerPet.getId()));
        for (PetHealthRecord source : ownerRecords) {
            PetHealthRecord copy = new PetHealthRecord();
            copyHealthRecordFields(source, copy);
            copy.setUserId(relation.getMerchantUserId());
            copy.setPetId(merchantPet.getId());
            copy.setBoardingRelationId(relation.getId());
            copy.setSourceRecordId(source.getId());
            copy.setCreateBy(SecurityUtils.getUsername());
            copy.setCreateTime(new Date());
            healthRecordMapper.insert(copy);
        }
    }

    private void syncBoardingBackToOwner(PetBoardingRelation relation) {
        if (relation.getMerchantPetId() == null) {
            return;
        }
        PetProfile merchantPet = petProfileMapper.selectOne(new QueryWrapper<PetProfile>()
                .eq("id", relation.getMerchantPetId()).eq("user_id", relation.getMerchantUserId()));
        PetProfile ownerPet = petProfileMapper.selectOne(new QueryWrapper<PetProfile>()
                .eq("id", relation.getOwnerPetId()).eq("user_id", relation.getOwnerUserId()));
        if (merchantPet == null || ownerPet == null) {
            return;
        }
        PetProfile ownerUpdate = new PetProfile();
        ownerUpdate.setId(ownerPet.getId());
        ownerUpdate.setUserId(ownerPet.getUserId());
        copyPetFields(merchantPet, ownerUpdate);
        ownerUpdate.setUpdateBy(SecurityUtils.getUsername());
        ownerUpdate.setUpdateTime(new Date());
        petProfileMapper.update(ownerUpdate, new UpdateWrapper<PetProfile>()
                .eq("id", ownerPet.getId()).eq("user_id", ownerPet.getUserId()));

        List<PetHealthRecord> merchantRecords = healthRecordMapper.selectList(new QueryWrapper<PetHealthRecord>()
                .eq("user_id", relation.getMerchantUserId()).eq("pet_id", merchantPet.getId()));
        for (PetHealthRecord merchantRecord : merchantRecords) {
            PetHealthRecord ownerRecord = null;
            if (merchantRecord.getSourceRecordId() != null) {
                ownerRecord = healthRecordMapper.selectOne(new QueryWrapper<PetHealthRecord>()
                        .eq("id", merchantRecord.getSourceRecordId())
                        .eq("user_id", relation.getOwnerUserId())
                        .eq("pet_id", relation.getOwnerPetId()));
            }
            if (ownerRecord == null) {
                ownerRecord = new PetHealthRecord();
                copyHealthRecordFields(merchantRecord, ownerRecord);
                ownerRecord.setUserId(relation.getOwnerUserId());
                ownerRecord.setPetId(relation.getOwnerPetId());
                ownerRecord.setCreateBy(SecurityUtils.getUsername());
                ownerRecord.setCreateTime(new Date());
                healthRecordMapper.insert(ownerRecord);
            } else {
                PetHealthRecord update = new PetHealthRecord();
                update.setId(ownerRecord.getId());
                update.setUserId(ownerRecord.getUserId());
                update.setPetId(ownerRecord.getPetId());
                copyHealthRecordFields(merchantRecord, update);
                update.setUpdateBy(SecurityUtils.getUsername());
                update.setUpdateTime(new Date());
                healthRecordMapper.update(update, new UpdateWrapper<PetHealthRecord>()
                        .eq("id", ownerRecord.getId()).eq("user_id", ownerRecord.getUserId()));
            }
        }
        healthRecordMapper.delete(new QueryWrapper<PetHealthRecord>()
                .eq("user_id", relation.getMerchantUserId()).eq("pet_id", merchantPet.getId()));
        petProfileMapper.delete(new QueryWrapper<PetProfile>()
                .eq("id", merchantPet.getId()).eq("user_id", relation.getMerchantUserId()));
    }

    private void copyHealthRecordFields(PetHealthRecord source, PetHealthRecord target) {
        target.setRecordType(source.getRecordType());
        target.setRecordDate(source.getRecordDate());
        target.setNextDueDate(source.getNextDueDate());
        target.setTitle(source.getTitle());
        target.setDescription(source.getDescription());
        target.setAttachmentUrls(source.getAttachmentUrls());
    }

    private void attachBoardingRelationForHealthRecord(PetHealthRecord record) {
        if (record == null || record.getPetId() == null) {
            return;
        }
        PetBoardingRelation relation = boardingRelationMapper.selectOne(new QueryWrapper<PetBoardingRelation>()
                .eq("merchant_user_id", SecurityUtils.getUserId())
                .eq("merchant_pet_id", record.getPetId())
                .eq("status", 1)
                .last("limit 1"));
        if (relation != null) {
            record.setBoardingRelationId(relation.getId());
        }
    }

    private boolean isOwnedPet(Long petId) {
        if (petId == null) {
            return true;
        }
        return petProfileMapper.selectCount(new QueryWrapper<PetProfile>()
                .eq("id", petId).eq("user_id", SecurityUtils.getUserId())) > 0;
    }

    private boolean hasActiveBoardingRelation(List<Long> petIds, Long userId) {
        if (CollectionUtils.isEmpty(petIds) || userId == null) {
            return false;
        }
        long ownerCount = boardingRelationMapper.selectCount(new QueryWrapper<PetBoardingRelation>()
                .eq("owner_user_id", userId).in("owner_pet_id", petIds).in("status", Arrays.asList(0, 1)));
        if (ownerCount > 0) {
            return true;
        }
        return boardingRelationMapper.selectCount(new QueryWrapper<PetBoardingRelation>()
                .eq("merchant_user_id", userId).in("merchant_pet_id", petIds).eq("status", 1)) > 0;
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

    private String displayName(String nickName, String userName, Long userId) {
        if (StringUtils.isNotEmpty(nickName)) {
            return nickName;
        }
        if (StringUtils.isNotEmpty(userName)) {
            return userName;
        }
        return userId == null ? "" : String.valueOf(userId);
    }

    private boolean isPublicAdoptionStatus(Integer status) {
        return status != null && (status == 2 || status == 3 || status == 4);
    }

    private void refreshAdoptionReservationState(Long adoptionPetId) {
        if (adoptionPetId == null) {
            return;
        }
        long scheduledCount = adoptionApplicationMapper.selectCount(new QueryWrapper<PetAdoptionApplication>()
                .eq("adoption_pet_id", adoptionPetId)
                .eq("status", 5));
        if (scheduledCount == 0) {
            adoptionPetMapper.update(null, new UpdateWrapper<PetAdoptionPet>()
                    .eq("id", adoptionPetId)
                    .eq("status", 3)
                    .set("status", 2)
                    .set("update_by", SecurityUtils.getUsername())
                    .set("update_time", new Date()));
        }
    }

    private boolean isAllowedAdoptionApplicationStatus(Integer status) {
        return status != null && (status == 1 || status == 2 || status == 3 || status == 5 || status == 7);
    }

    private boolean canTransitionAdoptionApplication(Integer currentStatus, Integer nextStatus) {
        if (currentStatus == null || nextStatus == null || isTerminalAdoptionApplication(currentStatus)) {
            return false;
        }
        if (nextStatus == 1) {
            return currentStatus == 0 || currentStatus == 5;
        }
        if (nextStatus == 2) {
            return currentStatus == 0 || currentStatus == 1;
        }
        if (nextStatus == 3) {
            return currentStatus == 0 || currentStatus == 1 || currentStatus == 2 || currentStatus == 5;
        }
        if (nextStatus == 5) {
            return currentStatus == 1 || currentStatus == 5;
        }
        if (nextStatus == 7) {
            return currentStatus == 1 || currentStatus == 2 || currentStatus == 5;
        }
        return false;
    }

    private boolean isTerminalAdoptionApplication(Integer status) {
        return status != null && (status == 3 || status == 4 || status == 6 || status == 7);
    }

    private String adoptionApplicationStatusTitle(Integer status) {
        if (status != null && status == 1) {
            return "领养申请已进入沟通";
        }
        if (status != null && status == 2) {
            return "领养申请需要补充资料";
        }
        if (status != null && status == 3) {
            return "领养申请未通过";
        }
        if (status != null && status == 5) {
            return "已安排看宠";
        }
        if (status != null && status == 7) {
            return "领养申请已关闭";
        }
        return "领养申请状态已更新";
    }

    private String adoptionApplicationStatusText(Integer status) {
        if (status != null && status == 1) {
            return "沟通中";
        }
        if (status != null && status == 2) {
            return "待补充";
        }
        if (status != null && status == 3) {
            return "拒绝";
        }
        if (status != null && status == 5) {
            return "已约看宠";
        }
        if (status != null && status == 7) {
            return "已关闭";
        }
        return "未知";
    }

    private String defaultReviewReason(String reason, String fallback) {
        return StringUtils.isEmpty(reason) ? fallback : reason;
    }

    private void createProfileFromAdoption(PetAdoptionPet adoptionPet, Long adopterUserId) {
        PetProfile pet = new PetProfile();
        pet.setUserId(adopterUserId);
        pet.setName(adoptionPet.getName());
        pet.setSpecies(adoptionPet.getSpecies());
        pet.setBreed(adoptionPet.getBreed());
        pet.setGender(adoptionPet.getGender());
        pet.setAvatar(adoptionPet.getCoverUrl());
        pet.setHealthStatus(adoptionPet.getHealthStatus());
        pet.setNeutered(defaultInt(adoptionPet.getNeutered(), 0));
        pet.setStatus(0);
        pet.setCreateBy("adoption");
        pet.setCreateTime(new Date());
        petProfileMapper.insert(pet);
    }

    private void createAdoptionFollowupPlans(PetAdoptionApplication application, PetAdoptionPet pet, Date baseTime) {
        int[] days = new int[] {7, 30, 90};
        for (int i = 0; i < days.length; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(baseTime);
            calendar.add(Calendar.DAY_OF_MONTH, days[i]);
            PetAdoptionFollowup followup = new PetAdoptionFollowup();
            followup.setApplicationId(application.getId());
            followup.setAdoptionPetId(pet.getId());
            followup.setAdopterUserId(application.getApplicantUserId());
            followup.setPublisherUserId(application.getPublisherUserId());
            followup.setFollowupRound(i + 1);
            followup.setPlanTime(calendar.getTime());
            followup.setStatus(0);
            followup.setCreateBy("adoption");
            followup.setCreateTime(new Date());
            adoptionFollowupMapper.insert(followup);
        }
    }

    private static Integer defaultInt(Integer value, Integer fallback) {
        return value == null ? fallback : value;
    }

    private PetMerchant ownedMerchant(Long merchantId) {
        if (merchantId == null) {
            return null;
        }
        return merchantMapper.selectOne(new QueryWrapper<PetMerchant>()
                .eq("id", merchantId).eq("user_id", SecurityUtils.getUserId()));
    }

    private PetMerchant currentMerchantForUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return merchantMapper.selectOne(new QueryWrapper<PetMerchant>()
                .eq("user_id", userId).orderByDesc("create_time").last("limit 1"));
    }

    private PetMerchant currentApprovedMerchantForUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return merchantMapper.selectOne(new QueryWrapper<PetMerchant>()
                .eq("user_id", userId).eq("qualification_status", 1).eq("status", 0)
                .orderByDesc("create_time").last("limit 1"));
    }

    private PetMerchant ownedApprovedMerchant(Long merchantId) {
        if (merchantId == null) {
            return null;
        }
        return merchantMapper.selectOne(new QueryWrapper<PetMerchant>()
                .eq("id", merchantId).eq("user_id", SecurityUtils.getUserId())
                .eq("qualification_status", 1).eq("status", 0));
    }

    private PetServiceRequest authorizedServiceRequest(Long requestId) {
        if (requestId == null) {
            return null;
        }
        PetServiceRequest request = serviceRequestMapper.selectById(requestId);
        if (request == null) {
            return null;
        }
        Long userId = SecurityUtils.getUserId();
        if (userId.equals(request.getUserId())) {
            return request;
        }
        return ownedMerchant(request.getMerchantId()) == null ? null : request;
    }

    private String serviceRequestActorRole(PetServiceRequest request) {
        Long userId = SecurityUtils.getUserId();
        if (userId.equals(request.getUserId())) {
            return "user";
        }
        if (ownedMerchant(request.getMerchantId()) != null) {
            return "merchant";
        }
        return null;
    }

    private PetAdoptionApplication authorizedAdoptionApplication(Long applicationId) {
        if (applicationId == null) {
            return null;
        }
        PetAdoptionApplication application = adoptionApplicationMapper.selectById(applicationId);
        if (application == null) {
            return null;
        }
        Long userId = SecurityUtils.getUserId();
        if (userId.equals(application.getApplicantUserId()) || userId.equals(application.getPublisherUserId())) {
            return application;
        }
        return null;
    }

    private Long parseApplicationId(String applicationId) {
        if (StringUtils.isEmpty(applicationId) || !applicationId.matches("\\d+")) {
            return null;
        }
        try {
            return Long.valueOf(applicationId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String adoptionApplicationActorRole(PetAdoptionApplication application) {
        Long userId = SecurityUtils.getUserId();
        if (userId.equals(application.getApplicantUserId())) {
            return "applicant";
        }
        if (userId.equals(application.getPublisherUserId())) {
            return "publisher";
        }
        return null;
    }

    private PetServiceItem visibleService(Long serviceId) {
        if (serviceId == null) {
            return null;
        }
        return serviceItemMapper.selectOne(new QueryWrapper<PetServiceItem>()
                .eq("id", serviceId).eq("status", 0)
                .inSql("merchant_id", "select id from pet_merchant where status = 0 and qualification_status = 1"));
    }

    private void recalcReviewStats(Long serviceId, Long merchantId) {
        if (serviceId != null) {
            List<PetServiceReview> serviceReviews = serviceReviewMapper.selectList(new QueryWrapper<PetServiceReview>()
                    .eq("service_id", serviceId).eq("status", 1));
            serviceItemMapper.update(null, new UpdateWrapper<PetServiceItem>().eq("id", serviceId)
                    .set("review_score", averageRating(serviceReviews)).set("review_count", serviceReviews.size()));
        }
        if (merchantId != null) {
            List<PetServiceReview> merchantReviews = serviceReviewMapper.selectList(new QueryWrapper<PetServiceReview>()
                    .eq("merchant_id", merchantId).eq("status", 1));
            merchantMapper.update(null, new UpdateWrapper<PetMerchant>().eq("id", merchantId)
                    .set("score", averageRating(merchantReviews)).set("review_count", merchantReviews.size()));
        }
    }

    private void notifyServiceMessage(PetServiceRequest request, String actorRole, String content) {
        Long receiverUserId = null;
        String actionUrl = "/services";
        if ("user".equals(actorRole)) {
            PetMerchant merchant = merchantMapper.selectById(request.getMerchantId());
            if (merchant != null) {
                receiverUserId = merchant.getUserId();
                actionUrl = "/merchant";
            }
        } else if ("merchant".equals(actorRole)) {
            receiverUserId = request.getUserId();
        }
        if (receiverUserId == null || receiverUserId.equals(SecurityUtils.getUserId())) {
            return;
        }
        String snippet = StringUtils.substring(content == null ? "" : content, 0, 80);
        notificationService.create(receiverUserId, SecurityUtils.getUserId(), "service_message", "service_request",
                request.getId(), "订单对话有新消息", snippet, actionUrl);
    }

    private void notifyAdoptionMessage(PetAdoptionApplication application, String actorRole, String content) {
        Long receiverUserId = null;
        if ("applicant".equals(actorRole)) {
            receiverUserId = application.getPublisherUserId();
        } else if ("publisher".equals(actorRole)) {
            receiverUserId = application.getApplicantUserId();
        }
        if (receiverUserId == null || receiverUserId.equals(SecurityUtils.getUserId())) {
            return;
        }
        String snippet = StringUtils.substring(content == null ? "" : content, 0, 80);
        notificationService.create(receiverUserId, SecurityUtils.getUserId(), "adoption_message", "adoption_application",
                application.getId(), "领养沟通有新消息", snippet, "/adoption-manage");
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
            return "已接受";
        }
        if (status != null && status == 2) {
            return "已完成";
        }
        if (status != null && status == 3) {
            return "已取消";
        }
        return "待处理";
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

    private String normalizeRatingType(String ratingType) {
        if ("good".equals(ratingType) || "middle".equals(ratingType) || "bad".equals(ratingType)) {
            return ratingType;
        }
        return null;
    }

    private boolean isAllowedStatus(Integer status, Integer... allowedStatuses) {
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
}
