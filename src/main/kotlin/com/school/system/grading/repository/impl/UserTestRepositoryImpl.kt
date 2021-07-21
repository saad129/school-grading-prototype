package com.school.system.grading.repository.impl

import com.school.system.grading.datasource.UserTestDataSource
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.grade.response.TestGradeResponse
import com.school.system.grading.entity.tests.request.TestCreateRequest
import com.school.system.grading.entity.tests.request.TestResultUpdateRequest
import com.school.system.grading.entity.tests.request.TestUpdateRequest
import com.school.system.grading.entity.tests.response.StudentTestResponse
import com.school.system.grading.entity.tests.response.TestCreateResponse
import com.school.system.grading.entity.tests.response.TestResultUpdateResponse
import com.school.system.grading.entity.tests.response.TestsResponse
import com.school.system.grading.repository.UserTestRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/9/21
 **/
@Repository
class UserTestRepositoryImpl(
        private val testDataSource: UserTestDataSource
) : UserTestRepository {

    override suspend fun createTests(testCreateRequest: TestCreateRequest): ResponseEntity<Response<TestCreateResponse>> {
        return testDataSource.insert(testCreateRequest)
    }

    override suspend fun getAllTests(): ResponseEntity<Response<List<TestsResponse>>> {
        return testDataSource.findAll()
    }

    override suspend fun getAllTestsBySubjectId(id: Int): ResponseEntity<Response<List<TestsResponse>>> {
        return testDataSource.findBySubjectId(id)
    }

    override suspend fun getStudentTestsBySubjectId(userId: Int, subjectId: Int): ResponseEntity<Response<List<StudentTestResponse>>> {
        return testDataSource.findTestsBySubjectId(userId,subjectId)
    }

    override suspend fun getAllTestsByStudentId(id: Int): ResponseEntity<Response<List<TestsResponse>>> {
        return testDataSource.findByStudentId(id)
    }

    override suspend fun getAllTestResultByTestId(id: Int): ResponseEntity<Response<List<TestGradeResponse>>> {
        return testDataSource.findAllResultsById(id)
    }

    override suspend fun getAllTestsByTeacherId(id: Int): ResponseEntity<Response<List<TestsResponse>>> {
        return testDataSource.findByTeacherId(id)
    }

    override suspend fun getSingleTest(id: Int): ResponseEntity<Response<TestsResponse>> {
        return testDataSource.findById(id)
    }

    override suspend fun updateTest(testUpdateRequest: TestUpdateRequest): ResponseEntity<Response<TestsResponse>> {
        return testDataSource.update(testUpdateRequest)
    }

    override suspend fun updateTestResult(update: TestResultUpdateRequest): ResponseEntity<Response<TestResultUpdateResponse>> {
        return testDataSource.updateResult(update)
    }

    override suspend fun deleteTest(id: Int): ResponseEntity<Response<Unit>> {
        return testDataSource.deleteById(id)
    }

    override suspend fun notifyUserByIds(ids: List<Int>): ResponseEntity<Response<Unit>> {
        return testDataSource.notifyUsersById(ids)
    }

}