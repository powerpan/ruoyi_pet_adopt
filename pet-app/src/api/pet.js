import request from '@/utils/request'

export function getProfile(config = {}) {
  return request({ url: '/app/pet/profile', method: 'get', ...config })
}

export function updateProfile(data, config = {}) {
  return request({ url: '/app/pet/profile', method: 'put', data, ...config })
}

export function applyBlogger(config = {}) {
  return request({ url: '/app/pet/profile/blogger-apply', method: 'post', ...config })
}

export function listPets(params, config = {}) {
  return request({ url: '/app/pet/pets', method: 'get', params, ...config })
}

export function addPet(data, config = {}) {
  return request({ url: '/app/pet/pets', method: 'post', data, ...config })
}

export function updatePet(data, config = {}) {
  return request({ url: '/app/pet/pets', method: 'put', data, ...config })
}

export function deletePets(ids) {
  return request({ url: '/app/pet/pets/' + ids, method: 'delete' })
}

export function listTopics(params) {
  return request({ url: '/app/pet/topics', method: 'get', params })
}

export function listPosts(params) {
  return request({ url: '/app/pet/posts', method: 'get', params, headers: { isToken: false } })
}

export function getPost(id) {
  return request({ url: '/app/pet/posts/' + id, method: 'get' })
}

export function publishPost(data) {
  return request({ url: '/app/pet/posts', method: 'post', data })
}

export function updatePost(data) {
  return request({ url: '/app/pet/posts', method: 'put', data })
}

export function listMyPosts(params) {
  return request({ url: '/app/pet/posts/mine', method: 'get', params })
}

export function listFavoritePosts(params) {
  return request({ url: '/app/pet/posts/favorites', method: 'get', params })
}

export function deletePosts(ids) {
  return request({ url: '/app/pet/posts/' + ids, method: 'delete' })
}

export function listComments(postId, params) {
  return request({ url: `/app/pet/posts/${postId}/comments`, method: 'get', params, headers: { isToken: false } })
}

export function addComment(postId, data) {
  return request({ url: `/app/pet/posts/${postId}/comments`, method: 'post', data })
}

export function interactPost(postId, type) {
  return request({ url: `/app/pet/posts/${postId}/interactions`, method: 'post', params: { type } })
}

export function listMerchants(params) {
  return request({ url: '/app/pet/merchants', method: 'get', params, headers: { isToken: false } })
}

export function createMerchant(data) {
  return request({ url: '/app/pet/merchants', method: 'post', data })
}

export function updateMerchant(data) {
  return request({ url: '/app/pet/merchants', method: 'put', data })
}

export function createMerchantQualification(merchantId, data) {
  return request({ url: `/app/pet/merchants/${merchantId}/qualifications`, method: 'post', data })
}

export function listMyMerchants(params, config = {}) {
  return request({ url: '/app/pet/merchants/mine', method: 'get', params, ...config })
}

export function listMerchantQualifications(merchantId, params) {
  return request({ url: `/app/pet/merchants/${merchantId}/qualifications`, method: 'get', params })
}

export function listMyMerchantServices(merchantId, params) {
  return request({ url: `/app/pet/merchants/${merchantId}/services`, method: 'get', params })
}

export function createMerchantService(merchantId, data) {
  return request({ url: `/app/pet/merchants/${merchantId}/services`, method: 'post', data })
}

export function updateMerchantService(data) {
  return request({ url: '/app/pet/merchant-services', method: 'put', data })
}

export function deleteMerchantServices(ids) {
  return request({ url: '/app/pet/merchant-services/' + ids, method: 'delete' })
}

export function listServices(params) {
  return request({ url: '/app/pet/services', method: 'get', params, headers: { isToken: false } })
}

export function listAdoptions(params) {
  return request({ url: '/app/pet/adoptions', method: 'get', params, headers: { isToken: false } })
}

export function listMyAdoptions(params) {
  return request({ url: '/app/pet/adoptions/mine', method: 'get', params })
}

export function getAdoption(id) {
  return request({ url: '/app/pet/adoptions/' + id, method: 'get', headers: { isToken: false } })
}

export function createAdoption(data) {
  return request({ url: '/app/pet/adoptions', method: 'post', data })
}

export function updateAdoption(data) {
  return request({ url: '/app/pet/adoptions', method: 'put', data })
}

export function deleteAdoptions(ids) {
  return request({ url: '/app/pet/adoptions/' + ids, method: 'delete' })
}

export function applyAdoption(id, data) {
  return request({ url: `/app/pet/adoptions/${id}/applications`, method: 'post', data })
}

export function listMyAdoptionApplications(params) {
  return request({ url: '/app/pet/adoption-applications/mine', method: 'get', params })
}

export function listReceivedAdoptionApplications(params) {
  return request({ url: '/app/pet/adoption-applications/received', method: 'get', params })
}

export function updateAdoptionApplicationStatus(data) {
  return request({ url: '/app/pet/adoption-applications/status', method: 'put', data })
}

export function updateMyAdoptionApplication(data) {
  return request({ url: '/app/pet/adoption-applications', method: 'put', data })
}

export function withdrawAdoptionApplication(id) {
  return request({ url: `/app/pet/adoption-applications/${id}/withdraw`, method: 'post' })
}

export function confirmAdoptionApplication(id) {
  return request({ url: `/app/pet/adoption-applications/${id}/confirm`, method: 'post' })
}

export function listAdoptionFollowups(params) {
  return request({ url: '/app/pet/adoption-followups', method: 'get', params })
}

export function submitAdoptionFollowup(id, data) {
  return request({ url: `/app/pet/adoption-followups/${id}/submit`, method: 'post', data })
}

export function listServiceReviews(serviceId, params) {
  return request({ url: `/app/pet/services/${serviceId}/reviews`, method: 'get', params, headers: { isToken: false } })
}

export function createServiceRequest(data) {
  return request({ url: '/app/pet/service-requests', method: 'post', data })
}

export function listMyServiceRequests(params, config = {}) {
  return request({ url: '/app/pet/service-requests/mine', method: 'get', params, ...config })
}

export function listServiceRequestMessages(requestId) {
  return request({ url: `/app/pet/service-requests/${requestId}/messages`, method: 'get' })
}

export function sendServiceRequestMessage(requestId, data) {
  return request({ url: `/app/pet/service-requests/${requestId}/messages`, method: 'post', data })
}

export function listMerchantServiceRequests(params) {
  return request({ url: '/app/pet/merchant-service-requests', method: 'get', params })
}

export function updateMerchantServiceRequestStatus(data) {
  return request({ url: '/app/pet/merchant-service-requests/status', method: 'put', data })
}

export function createReview(data) {
  return request({ url: '/app/pet/service-reviews', method: 'post', data })
}

export function listMerchantReviews(params) {
  return request({ url: '/app/pet/merchant-reviews', method: 'get', params })
}

export function requestHideReview(reviewId, data) {
  return request({ url: `/app/pet/merchant-reviews/${reviewId}/hide-request`, method: 'post', data })
}

export function topMerchantReview(reviewId, data) {
  return request({ url: `/app/pet/merchant-reviews/${reviewId}/top`, method: 'post', data })
}

export function searchBoardingUsers(params) {
  return request({ url: '/app/pet/merchant-boarding/users', method: 'get', params })
}

export function createBoardingRequest(data) {
  return request({ url: '/app/pet/merchant-boarding/requests', method: 'post', data })
}

export function listMerchantBoardingRelations(params) {
  return request({ url: '/app/pet/merchant-boarding/relations', method: 'get', params })
}

export function listOwnerBoardingRelations(params) {
  return request({ url: '/app/pet/boarding/relations', method: 'get', params })
}

export function approveBoardingRelation(id) {
  return request({ url: `/app/pet/boarding/relations/${id}/approve`, method: 'post' })
}

export function rejectBoardingRelation(id) {
  return request({ url: `/app/pet/boarding/relations/${id}/reject`, method: 'post' })
}

export function cancelBoardingRelation(id) {
  return request({ url: `/app/pet/boarding/relations/${id}/cancel`, method: 'post' })
}

export function listNotifications(params) {
  return request({ url: '/app/pet/notifications', method: 'get', params })
}

export function getUnreadNotificationCount(config = {}) {
  return request({ url: '/app/pet/notifications/unread-count', method: 'get', ...config })
}

export function readNotification(id) {
  return request({ url: `/app/pet/notifications/${id}/read`, method: 'put' })
}

export function readAllNotifications() {
  return request({ url: '/app/pet/notifications/read-all', method: 'put' })
}

export function deleteNotifications(ids) {
  return request({ url: '/app/pet/notifications/' + ids, method: 'delete' })
}

export function listHealthRecords(params) {
  return request({ url: '/app/pet/health-records', method: 'get', params })
}

export function addHealthRecord(data) {
  return request({ url: '/app/pet/health-records', method: 'post', data })
}

export function updateHealthRecord(data) {
  return request({ url: '/app/pet/health-records', method: 'put', data })
}

export function deleteHealthRecords(ids) {
  return request({ url: '/app/pet/health-records/' + ids, method: 'delete' })
}

export function listReminders(params) {
  return request({ url: '/app/pet/reminders', method: 'get', params })
}

export function addReminder(data) {
  return request({ url: '/app/pet/reminders', method: 'post', data })
}

export function updateReminder(data) {
  return request({ url: '/app/pet/reminders', method: 'put', data })
}

export function deleteReminders(ids) {
  return request({ url: '/app/pet/reminders/' + ids, method: 'delete' })
}
