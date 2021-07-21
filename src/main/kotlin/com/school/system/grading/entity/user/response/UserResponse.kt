package com.school.system.grading.entity.user.response

data class UserResponse(
        val id: Int?,
        val firstName:String,
        val lastName:String,
        val password: String,
        val username: String,
        val role: String,
        val isAssigned: Boolean
)