package com.school.system.grading.entity.tests.request

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/9/21
 **/
data class TestResultUpdateRequest(
        val studentId: Int?,
        val testId: Int?,
        val marks: String?
)