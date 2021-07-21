package com.school.system.grading.repository

import com.school.system.grading.entity.Response
import com.school.system.grading.entity.subject.response.UserSubjectResponse
import com.school.system.grading.entity.user.request.*
import com.school.system.grading.entity.user.response.SingleUserResponse
import com.school.system.grading.entity.user.response.StudentAverageResultResponse
import com.school.system.grading.entity.user.response.UserCountResponse
import com.school.system.grading.entity.user.response.UserResponse
import org.springframework.http.ResponseEntity

interface UserRepository {
    suspend fun fetchAllUsers(): ResponseEntity<Response<List<UserResponse>>>

    suspend fun fetchStudentsAverageGrade(subjects: List<UserSubjectResponse>?): ResponseEntity<Response<List<StudentAverageResultResponse>>>

    suspend fun getCountUsers(): ResponseEntity<Response<UserCountResponse>>

    suspend fun fetchAssignedUsers(): ResponseEntity<Response<List<UserResponse>>>

    suspend fun fetchDeassignedUsers(): ResponseEntity<Response<List<UserResponse>>>

    suspend fun findUserById(id: Int): ResponseEntity<Response<UserResponse>>

    fun findUserByUsername(username: String): ResponseEntity<Response<UserResponse>>

    suspend fun deleteUser(deleteRequest: UserDeleteRequest): ResponseEntity<Response<Unit>>

    suspend fun logoutUser(id: Int): ResponseEntity<Response<Unit>>

    suspend fun findUsersByRole(role: String): ResponseEntity<Response<List<UserResponse>>>

    suspend fun insertUser(usersRequest: UsersRequest): ResponseEntity<Response<UserResponse>>

    suspend fun loginUser(userLoginRequest: UserLoginRequest): ResponseEntity<Response<UserResponse>>

    suspend fun updateUser(userUpdateRequest: UserUpdateRequest): ResponseEntity<Response<UserResponse>>

    suspend fun contactUser(contactUserRequest: ContactUserRequest): ResponseEntity<Response<Unit>>

}