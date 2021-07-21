package com.school.system.grading.entity.user.request

data class UserUpdateRequest(
        val id: Int?,
        val firstName: String?,
        val lastName: String?,
        val username: String?,
        val password: String?
)