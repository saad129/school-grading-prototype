package com.school.system.grading.datasource

import com.school.system.grading.entity.Response
import com.school.system.grading.entity.user.request.UserLogin
import com.school.system.grading.entity.user.request.UserUpdate
import com.school.system.grading.entity.user.request.Users
import com.school.system.grading.entity.user.response.UserResponse
import org.springframework.http.ResponseEntity

interface UserDataSource {
    suspend fun findAll(): ResponseEntity<Response<List<UserResponse>>>

    suspend fun findById(id: Int): ResponseEntity<Response<UserResponse>>

    suspend fun insert(users: Users): ResponseEntity<Response<UserResponse>>

    suspend fun login(userLogin: UserLogin): ResponseEntity<Response<UserResponse>>

    suspend fun update(userUpdate: UserUpdate): ResponseEntity<Response<UserResponse>>
}