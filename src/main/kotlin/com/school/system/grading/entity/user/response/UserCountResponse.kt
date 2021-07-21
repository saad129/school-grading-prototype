package com.school.system.grading.entity.user.response

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 7/1/21
 **/
data class UserCountResponse(
        val students: Long?,
        val teachers: Long?,
        val admins: Long?
)