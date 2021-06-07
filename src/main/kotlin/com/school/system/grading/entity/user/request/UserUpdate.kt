package com.school.system.grading.entity.user.request

data class UserUpdate(
        val id: Int?,
        val firstName: String,
        val lastName: String,
        val username: String,
        val password: String
)