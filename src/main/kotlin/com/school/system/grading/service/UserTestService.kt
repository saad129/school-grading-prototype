package com.school.system.grading.service

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import com.school.system.grading.controller.UserTestController
import com.school.system.grading.entity.*
import com.school.system.grading.entity.tests.notify.NotifyTestRequest
import com.school.system.grading.entity.tests.request.TestCreateRequest
import com.school.system.grading.entity.tests.request.TestResultUpdateRequest
import com.school.system.grading.entity.tests.request.TestUpdateRequest
import com.school.system.grading.entity.tests.response.TestCreateResponse
import com.school.system.grading.entity.tests.response.TestResultUpdateResponse
import com.school.system.grading.entity.tests.response.TestsResponse
import com.school.system.grading.entity.tests.upload.ImportStudentTest
import com.school.system.grading.repository.UserRepository
import com.school.system.grading.repository.UserTestRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigDecimal


/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/9/21
 **/

@Service
class UserTestService(
        private val userTestRepository: UserTestRepository,
        @Autowired
        private val emailSend: JavaMailSender
) {



    suspend fun getAllTests() = userTestRepository.getAllTests()

    suspend fun getTestsBySubjectId(id: Int) = userTestRepository.getAllTestsBySubjectId(id)

    suspend fun getStudentTestsBySubjectId(userId: Int, subjectId: Int) = userTestRepository.getStudentTestsBySubjectId(userId, subjectId)

    suspend fun getTestsByStudentId(id: Int) = userTestRepository.getAllTestsByStudentId(id)

    suspend fun getTestsByTeacherId(id: Int) = userTestRepository.getAllTestsByTeacherId(id)

    suspend fun getTestById(id: Int) = userTestRepository.getSingleTest(id)

    suspend fun updateTestResult(update: TestResultUpdateRequest): ResponseEntity<Response<TestResultUpdateResponse>> {
        if(update.studentId == null || update.marks.isNullOrEmpty() || update.testId == null) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }

        if(!checkMarksValid(update.marks) || BigDecimal(update.marks) < BigDecimal.ZERO) {
            return ResponseEntity.ok(Response(
                    status = INVALID_MARKS,
                    message = "Please enter correct marks")
            )
        }
        println("initiate update result request ${update.studentId}")
        return userTestRepository.updateTestResult(update)
    }

    suspend fun updateTest(testUpdateRequest: TestUpdateRequest): ResponseEntity<Response<TestsResponse>> {
        if(testUpdateRequest.testName.isNullOrEmpty() || testUpdateRequest.testId == null
                || testUpdateRequest.testDate.isNullOrEmpty()) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }

        return userTestRepository.updateTest(testUpdateRequest)
    }

    suspend fun deleteTest(id: Int) = userTestRepository.deleteTest(id)

    suspend fun createTest(testCreateRequest: TestCreateRequest):ResponseEntity<Response<TestCreateResponse>> {
        if(testCreateRequest.subjectId == null || testCreateRequest.name.isNullOrEmpty()
                || testCreateRequest.teacherId == null || testCreateRequest.testDate.isNullOrEmpty()) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }

        return userTestRepository.createTests(testCreateRequest)
    }

    suspend fun notifyTest(notifyTestRequest: NotifyTestRequest): ResponseEntity<Response<Unit>> {
        if(notifyTestRequest.ids.isNullOrEmpty()) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }
        return userTestRepository.notifyUserByIds(notifyTestRequest.ids)
    }

    suspend fun getAllStudentsResultByTestId(id: Int) = userTestRepository.getAllTestResultByTestId(id)

    suspend fun uploadStudentTest(file: MultipartFile,testId: Int): ResponseEntity<Response<Unit>> {
            throwIfFileEmpty(file)
            var fileReader: BufferedReader? = null

            try {
                fileReader = BufferedReader(InputStreamReader(file.inputStream))
                val csvToBean = createCSVToBean(fileReader)

                csvToBean.parse().forEach {
                    println(it)
                    val result = updateTestResult(TestResultUpdateRequest(
                            testId = testId,
                            studentId = it.studentId,
                            marks = it.studentMarks
                    ))
                    println(result.body?.status)
                }
                return ResponseEntity.ok(Response(
                        status = SUCCESS,
                        message = "Result updated!")
                )
            } catch (ex: Exception) {
                return ResponseEntity.badRequest().body(Response(
                        status = ERROR_CSV_IMPORT,
                        message = "Error during csv import",
                        data = Unit
                ))
            } finally {
                closeFileReader(fileReader)
            }
    }

    private fun throwIfFileEmpty(file: MultipartFile) {
        if (file.isEmpty)
            throw UserTestController.BadRequestException("Empty file")
    }

    private fun closeFileReader(fileReader: BufferedReader?) {
        try {
            fileReader!!.close()
        } catch (ex: IOException) {
            throw UserTestController.CsvImportException("Error during csv import")
        }
    }

    private fun createCSVToBean(fileReader: BufferedReader?): CsvToBean<ImportStudentTest> =
            CsvToBeanBuilder<ImportStudentTest>(fileReader)
                    .withType(ImportStudentTest::class.java)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()


    private fun checkMarksValid(marks: String) = marks.matches(Regex("^[0-9]*\\.?[0-9]*$"))
}