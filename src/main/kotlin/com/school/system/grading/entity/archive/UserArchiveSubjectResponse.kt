package com.school.system.grading.entity.archive

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/27/21
 **/
data class UserArchiveSubjectResponse(
        val studentId: Int? = null,
        val teacherId: Int? = null,
        val subjectId: Int? = null,
        val studentName: String? = null,
        val teacherName: String? = null,
        val subjectName: String? = null,
        val tests: List<UserArchiveTestResponse>? = null
)