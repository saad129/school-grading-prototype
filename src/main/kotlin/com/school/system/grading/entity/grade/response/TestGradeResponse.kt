package com.school.system.grading.entity.grade.response

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 7/4/21
 **/

data class TestGradeResponse(
        val id: Int?,
        val studentId: Int?,
        val testId: Int?,
        val studentName: String?,
        val marks: String?
)