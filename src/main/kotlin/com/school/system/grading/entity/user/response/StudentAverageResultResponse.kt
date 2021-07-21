package com.school.system.grading.entity.user.response

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 7/4/21
 **/

data class StudentAverageResultResponse(
        val id: Int?,
        val firstName: String?,
        val lastName: String?,
        val className: String?,
        val averageGrade: String,
)