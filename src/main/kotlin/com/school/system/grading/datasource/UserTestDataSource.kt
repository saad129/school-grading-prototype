package com.school.system.grading.datasource

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
import org.springframework.web.multipart.MultipartFile

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/9/21
 **/
interface UserTestDataSource {
    suspend fun findAll(): ResponseEntity<Response<List<TestsResponse>>>

    suspend fun findBySubjectId(id: Int): ResponseEntity<Response<List<TestsResponse>>>

    suspend fun findByStudentId(id: Int): ResponseEntity<Response<List<TestsResponse>>>

    suspend fun findTestsBySubjectId(userId: Int, subjectId: Int): ResponseEntity<Response<List<StudentTestResponse>>>

    suspend fun findByTeacherId(id: Int): ResponseEntity<Response<List<TestsResponse>>>

    suspend fun findAllResultsById(id: Int): ResponseEntity<Response<List<TestGradeResponse>>>

    suspend fun findById(id: Int): ResponseEntity<Response<TestsResponse>>

    suspend fun update(testUpdateRequest: TestUpdateRequest): ResponseEntity<Response<TestsResponse>>

    suspend fun insert(testCreateRequest: TestCreateRequest): ResponseEntity<Response<TestCreateResponse>>

    suspend fun deleteById(id: Int): ResponseEntity<Response<Unit>>

    suspend fun updateResult(testResultUpdateRequest: TestResultUpdateRequest): ResponseEntity<Response<TestResultUpdateResponse>>

    suspend fun notifyUsersById(ids: List<Int>): ResponseEntity<Response<Unit>>
}