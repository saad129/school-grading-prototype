package com.school.system.grading.entity.userclass.request

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/27/21
 **/

data class UserClassUpdateRequest(
        val classId: Int? = null,
        val id: Int? = null,
        val className: String? = null
)