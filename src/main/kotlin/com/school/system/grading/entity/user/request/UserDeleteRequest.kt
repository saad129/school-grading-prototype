package com.school.system.grading.entity.user.request

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/29/21
 **/
data class UserDeleteRequest(
        val userId: Int?,
        val deleteId: Int?
)