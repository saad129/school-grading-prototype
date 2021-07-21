package com.school.system.grading.entity.tests.response

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/11/21
 **/
data class TestsResponse(
        val id: Int?,
        val testName: String,
        val marks: String?= null,
        val createdAt: String? = null,
        val subjectName: String? = null
)