package com.school.system.grading.controller

import com.school.system.grading.controller.base.BaseController
import com.school.system.grading.entity.CLASS_ALREADY_EXIST
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.userclass.request.UserClassCreate
import com.school.system.grading.service.UserClassService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/7/21
 **/

@RestController
@RequestMapping("api/v1/class")
class UserClassController(
        private val userClassService: UserClassService
): BaseController() {

    @ExceptionHandler(DataIntegrityViolationException::class)
    protected fun handleDataIntegrityException(ex: DataIntegrityViolationException, request: WebRequest) : ResponseEntity<Response<UserClassCreate>>{
        return ResponseEntity.badRequest().body(Response(
                status = CLASS_ALREADY_EXIST,
                message = "Class already exists"
        ))
    }
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    suspend fun getAllClasses() = userClassService.getAllClasses()

    @GetMapping( "/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getOneClass(@PathVariable id: Int) = userClassService.getOneClass(id)

    @PostMapping("/create",  consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    suspend fun createClass(@RequestBody userClassCreate: UserClassCreate) = userClassService.createClass(userClassCreate)
}