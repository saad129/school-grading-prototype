package com.school.system.grading.entity.user.request


data class ContactUserRequest(
        val id: Int?,
        val to: String?,
        val subject: String?,
        val message: String?
)