package com.school.system.grading.controller

import com.school.system.grading.controller.common.BaseController
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.user.request.*
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

    @PostMapping("/create",  consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    suspend fun createUser(@RequestBody usersRequest: UsersRequest): ResponseEntity<Response<UserResponse>> = userService.createUser(usersRequest)

    @PostMapping("/login",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun loginUser(@RequestBody userLogin: UserLoginRequest): ResponseEntity<Response<UserResponse>> = userService.loginUser(userLogin)

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getAllUsers(): ResponseEntity<Response<List<UserResponse>>> = userService.getAllUsers()

    @GetMapping("/count",produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getCountUsers() = userService.getCountUsers()

    @GetMapping( "/logout/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun logoutUser(@PathVariable id:Int) = userService.logoutUser(id)

    @DeleteMapping( "/delete", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun deleteUser(@RequestBody deleteRequest: UserDeleteRequest) = userService.deleteUser(deleteRequest)

    @GetMapping( "/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getUserById(@PathVariable id:Int) = userService.getUserById(id)

    @GetMapping( "/role/{role}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getUsersByRole(@PathVariable(value = "role") role:String?) = userService.getUsersByRole(role)

    @GetMapping( "/students/assigned", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getAllAssignedStudents() = userService.getAllAssignedStudents()

    @GetMapping( "/students/deassigned", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getAllDeassignedStudents() = userService.getAllDeassignedStudents()

    @GetMapping( "/students/results/{teacherId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getAllStudentsAverageResult(@PathVariable teacherId: Int) = userService.getAllStudentsAverageGrade(teacherId)

    @PutMapping("/update",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun updateUser(@RequestBody userUpdateRequest: UserUpdateRequest) = userService.updateUser(userUpdateRequest)


    @PostMapping("/contact",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun contactUser(@RequestBody contactUserRequest: ContactUserRequest) = userService.contactUser(contactUserRequest)

}