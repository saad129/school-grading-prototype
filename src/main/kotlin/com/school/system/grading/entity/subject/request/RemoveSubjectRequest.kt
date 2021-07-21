package com.school.system.grading.entity.subject.request

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 7/3/21
 **/
data class RemoveSubjectRequest(
        val userId: Int?,
        val subjectIds: List<Int>?
)