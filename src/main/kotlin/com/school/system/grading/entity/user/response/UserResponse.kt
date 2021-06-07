package com.school.system.grading.entity.user.response

class UserResponse(
        val id: Int?,
        val firstName:String,
        val lastName:String,
        val token: String?,
        val username: String,
        val role: String
)