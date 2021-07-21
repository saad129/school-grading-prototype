package com.school.system.grading.entity.subject.request

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 7/3/21
 **/
data class UserSubjectUpdateRequest(
        val userId: Int?,
        val subjectId: Int?,
        val subjectName: String?,
)