package com.school.system.grading.entity.tests.request

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/9/21
 **/

data class TestCreateRequest(
        val name: String?,
        val subjectId: Int?,
        val teacherId: Int?,
        val testDate: String?
)