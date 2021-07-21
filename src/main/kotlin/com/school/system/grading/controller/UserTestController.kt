package com.school.system.grading.controller

import com.school.system.grading.controller.common.BaseController
import com.school.system.grading.entity.tests.notify.NotifyTestRequest
import com.school.system.grading.entity.tests.request.TestCreateRequest
import com.school.system.grading.entity.tests.request.TestResultUpdateRequest
import com.school.system.grading.entity.tests.request.TestUpdateRequest
import com.school.system.grading.service.UserTestService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/9/21
 **/
@RestController
@RequestMapping("api/v1/tests")
class UserTestController(
        private val userTestService: UserTestService
): BaseController() {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    class BadRequestException(msg: String) : RuntimeException(msg)

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    class CsvImportException(msg: String) : RuntimeException(msg)

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getAllTests() = userTestService.getAllTests()

    @GetMapping("/subject/{subjectId}",produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getTestsBySubjectId(@PathVariable subjectId: Int) = userTestService.getTestsBySubjectId(subjectId)

    @GetMapping("/subject/{userId}/{subjectId}",produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getStudentTestsBySubjectId(@PathVariable userId: Int, @PathVariable subjectId: Int) = userTestService.getStudentTestsBySubjectId(userId,subjectId)

    @GetMapping("/student/{studentId}",produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getTestsByStudentId(@PathVariable studentId: Int) = userTestService.getTestsByStudentId(studentId)

    @GetMapping("/teacher/{teacherId}",produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getTestsByTeacherId(@PathVariable teacherId: Int) = userTestService.getTestsByTeacherId(teacherId)

    @GetMapping("/{testId}",produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getSingleTest(@PathVariable testId: Int) = userTestService.getTestById(testId)

    @GetMapping("/result/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getAllStudentsResultByTestId(@PathVariable id: Int)
            = userTestService.getAllStudentsResultByTestId(id)

    @PostMapping("/create",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    suspend fun createTest(@RequestBody testCreateRequest: TestCreateRequest)
        = userTestService.createTest(testCreateRequest)

    @PutMapping("/update/result",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun updateTestResult(@RequestBody testResultUpdateRequest: TestResultUpdateRequest)
            = userTestService.updateTestResult(testResultUpdateRequest)

    @PutMapping("/update",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun updateTest(@RequestBody testUpdateRequest: TestUpdateRequest)
            = userTestService.updateTest(testUpdateRequest)

    @DeleteMapping("/delete/{id}",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun deleteTest(@PathVariable id: Int)
            = userTestService.deleteTest(id)

    @PostMapping("/import/{testId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun importTest(@RequestPart("file") file: MultipartFile,@PathVariable testId: Int)
            = userTestService.uploadStudentTest(file,testId)

    @PostMapping("/notify",consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun notifyUser(@RequestBody notifyTestRequest: NotifyTestRequest) = userTestService.notifyTest(notifyTestRequest)
}