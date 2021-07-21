package com.school.system.grading.entity.user.response

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/13/21
 **/
data class SingleUserResponse (
        val id: Int?,
        val firstName:String,
        val lastName:String,
        val username: String,
        val role: String,
        val password: String,
)