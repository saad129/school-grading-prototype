package com.school.system.grading.controller

import com.school.system.grading.controller.base.BaseController
import com.school.system.grading.datasource.UserSubjectDataSource
import com.school.system.grading.entity.*
import com.school.system.grading.entity.CLASS_ALREADY_EXIST
import com.school.system.grading.entity.subject.request.UserSubject
import com.school.system.grading.entity.userclass.request.UserClassCreate
import com.school.system.grading.service.UserSubjectService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/
@RestController
@RequestMapping("api/v1/subjects")
class UserSubjectController(
        private val userSubjectService: UserSubjectService,
        private val userSubjectDataSource: UserSubjectDataSource
): BaseController() {

    @GetMapping("/dummy")
    fun helloTest(): ResponseEntity<Response<List<String>>> = ResponseEntity.ok(Response(
            status = SUCCESS,
            message = "Dummy message",
            data = listOf("test","test2")
    ))

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getAllSubjects() = userSubjectService.getAllSubjects()

    @GetMapping("/{id}",produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    suspend fun getOneSubject(@PathVariable id: Int) = userSubjectService.getOneSubject(id)

    @PostMapping("/create",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    suspend fun createSubject(@RequestBody userSubject: UserSubject) = userSubjectService.createSubject(userSubject)

    @PostMapping("/dummy",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    suspend fun dummySubject(@RequestBody userSubject: UserSubject) = userSubjectDataSource.test(userSubject)
}