package com.school.system.grading.entity.subject.response

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/
data class UserSubjectResponse(
        val id: Int?,
        val classId: Int?= null,
        val teacherId: Int?= null,
        val subjectName: String?,
        val className: String?= null,
        val teacherName: String?= null,
        val hasTest: Boolean?
)