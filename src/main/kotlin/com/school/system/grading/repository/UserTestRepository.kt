package com.school.system.grading.repository

import com.school.system.grading.entity.Response
import com.school.system.grading.entity.grade.response.TestGradeResponse
import com.school.system.grading.entity.tests.request.TestCreateRequest
import com.school.system.grading.entity.tests.request.TestResultUpdateRequest
import com.school.system.grading.entity.tests.request.TestUpdateRequest
import com.school.system.grading.entity.tests.response.StudentTestResponse
import com.school.system.grading.entity.tests.response.TestCreateResponse
import com.school.system.grading.entity.tests.response.TestResultUpdateResponse
import com.school.system.grading.entity.tests.response.TestsResponse
import org.springframework.http.ResponseEntity

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/9/21
 **/

interface UserTestRepository {
    suspend fun createTests(testCreateRequest: TestCreateRequest): ResponseEntity<Response<TestCreateResponse>>

    suspend fun getAllTests(): ResponseEntity<Response<List<TestsResponse>>>

    suspend fun getAllTestsBySubjectId(id: Int): ResponseEntity<Response<List<TestsResponse>>>

    suspend fun getStudentTestsBySubjectId(userId: Int, subjectId: Int): ResponseEntity<Response<List<StudentTestResponse>>>

    suspend fun getAllTestsByStudentId(id: Int): ResponseEntity<Response<List<TestsResponse>>>

    suspend fun getAllTestResultByTestId(id: Int): ResponseEntity<Response<List<TestGradeResponse>>>

    suspend fun getAllTestsByTeacherId(id: Int): ResponseEntity<Response<List<TestsResponse>>>

    suspend fun getSingleTest(id: Int): ResponseEntity<Response<TestsResponse>>

    suspend fun updateTest(testUpdateRequest: TestUpdateRequest): ResponseEntity<Response<TestsResponse>>

    suspend fun deleteTest(id: Int) : ResponseEntity<Response<Unit>>

    suspend fun updateTestResult(update: TestResultUpdateRequest): ResponseEntity<Response<TestResultUpdateResponse>>

    suspend fun notifyUserByIds(ids: List<Int>): ResponseEntity<Response<Unit>>

}