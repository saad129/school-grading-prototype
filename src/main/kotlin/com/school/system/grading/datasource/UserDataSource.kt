package com.school.system.grading.datasource

import com.school.system.grading.entity.Response
import com.school.system.grading.entity.subject.response.UserSubjectResponse
import com.school.system.grading.entity.user.request.UserDeleteRequest
import com.school.system.grading.entity.user.request.UserLoginRequest
import com.school.system.grading.entity.user.request.UserUpdateRequest
import com.school.system.grading.entity.user.request.UsersRequest
import com.school.system.grading.entity.user.response.SingleUserResponse
import com.school.system.grading.entity.user.response.StudentAverageResultResponse
import com.school.system.grading.entity.user.response.UserCountResponse
import com.school.system.grading.entity.user.response.UserResponse
import org.springframework.http.ResponseEntity

interface UserDataSource {
    suspend fun count(): ResponseEntity<Response<UserCountResponse>>

    suspend fun findAll(): ResponseEntity<Response<List<UserResponse>>>

    suspend fun fetchAllAverageGrades(subjects: List<UserSubjectResponse>?): ResponseEntity<Response<List<StudentAverageResultResponse>>>

    suspend fun findAllAssignedStudents(): ResponseEntity<Response<List<UserResponse>>>

    suspend fun findAllDessignedStudents(): ResponseEntity<Response<List<UserResponse>>>

    suspend fun findById(id: Int): ResponseEntity<Response<UserResponse>>

    fun findByUsername(username: String): ResponseEntity<Response<UserResponse>>

    suspend fun findByRole(role: String): ResponseEntity<Response<List<UserResponse>>>

    suspend fun insert(usersRequest: UsersRequest): ResponseEntity<Response<UserResponse>>

    suspend fun login(userLoginRequest: UserLoginRequest): ResponseEntity<Response<UserResponse>>

    suspend fun update(userUpdateRequest: UserUpdateRequest): ResponseEntity<Response<UserResponse>>

    suspend fun delete(deleteRequest: UserDeleteRequest): ResponseEntity<Response<Unit>>

    suspend fun logout(id: Int): ResponseEntity<Response<Unit>>
}