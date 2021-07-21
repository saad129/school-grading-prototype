package com.school.system.grading.entity.tests.request

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 7/4/21
 **/
data class TestUpdateRequest(
        val testId: Int?,
        val testName: String?,
        val testDate: String?
)