package com.school.system.grading.repository

import com.school.system.grading.entity.Response
import com.school.system.grading.entity.user.request.UserLogin
import com.school.system.grading.entity.user.request.UserUpdate
import com.school.system.grading.entity.user.request.Users
import com.school.system.grading.entity.user.response.UserResponse
import org.springframework.http.ResponseEntity

interface UserRepository {
    suspend fun fetchAllUsers(): ResponseEntity<Response<List<UserResponse>>>

    suspend fun findUserById(id: Int): ResponseEntity<Response<UserResponse>>

    suspend fun insertUser(users: Users): ResponseEntity<Response<UserResponse>>

    suspend fun loginUser(userLogin: UserLogin): ResponseEntity<Response<UserResponse>>

    suspend fun updateUser(userUpdate: UserUpdate): ResponseEntity<Response<UserResponse>>

}