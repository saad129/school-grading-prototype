package com.school.system.grading.entity.subject.request

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/
data class UserSubject(
        val classId: Int,
        val teacherId: Int,
        val subjectName: String
)