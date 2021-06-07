package com.school.system.grading.datasource

import com.school.system.grading.entity.Response
import com.school.system.grading.entity.user.request.UserLogin
import com.school.system.grading.entity.user.request.UserUpdate
import com.school.system.grading.entity.user.request.Users
import com.school.system.grading.entity.user.response.UserResponse
import org.springframework.http.ResponseEntity

interface UserDataSource {
    fun findAll(): ResponseEntity<Response<List<UserResponse>>>

    fun findById(id: Int): ResponseEntity<Response<UserResponse>>

    fun insert(users: Users): ResponseEntity<Response<UserResponse>>

    fun login(userLogin: UserLogin): ResponseEntity<Response<UserResponse>>

    fun update(userUpdate: UserUpdate): ResponseEntity<Response<UserResponse>>
}