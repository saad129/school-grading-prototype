package com.school.system.grading.controller

import com.school.system.grading.controller.base.BaseController
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.SUCCESS
import com.school.system.grading.entity.user.request.UserLogin
import com.school.system.grading.entity.user.request.UserUpdate
import com.school.system.grading.entity.user.request.Users
import com.school.system.grading.entity.user.response.UserResponse
import com.school.system.grading.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/users")
class UserController(
        private val userService: UserService
):BaseController() {

    @GetMapping("/dummy")
    fun helloTest(): ResponseEntity<Response<List<String>>> = ResponseEntity.ok(Response(
            status = SUCCESS,
            message = "Dummy message",
            data = listOf("test","test2")
    ))

    @PostMapping("/create",  consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    fun createUser(@RequestBody users: Users): ResponseEntity<Response<UserResponse>> = userService.createUser(users)


    @PostMapping("/login",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun loginUser(@RequestBody userLogin: UserLogin): ResponseEntity<Response<UserResponse>> = userService.loginUser(userLogin)


    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getAllUsers(): ResponseEntity<Response<List<UserResponse>>> = userService.getAllUsers()


    @GetMapping( "/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getUserById(@PathVariable id:Int) = userService.getUserById(id)


    @PutMapping("/update",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun updateUser(@RequestBody userUpdate: UserUpdate): ResponseEntity<Response<UserResponse>> = userService.updateUser(userUpdate)



}