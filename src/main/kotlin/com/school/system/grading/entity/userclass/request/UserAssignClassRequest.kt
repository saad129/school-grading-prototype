package com.school.system.grading.entity.userclass.request

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/10/21
 **/
data class UserAssignClassRequest(
        val adminId: Int?,
        val classId: Int?,
        val studentIds: List<Int>?
)