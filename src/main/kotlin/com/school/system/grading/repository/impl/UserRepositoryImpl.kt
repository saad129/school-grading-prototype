package com.school.system.grading.repository.impl

import com.school.system.grading.datasource.UserDataSource
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.user.request.UserLogin
import com.school.system.grading.entity.user.request.UserUpdate
import com.school.system.grading.entity.user.request.Users
import com.school.system.grading.entity.user.response.UserResponse
import com.school.system.grading.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
        private val userDataSource: UserDataSource
): UserRepository {

    override suspend fun fetchAllUsers(): ResponseEntity<Response<List<UserResponse>>> = userDataSource.findAll()

    override suspend fun findUserById(id: Int): ResponseEntity<Response<UserResponse>> {
        return userDataSource.findById(id)
    }

    override suspend fun insertUser(users: Users): ResponseEntity<Response<UserResponse>> = userDataSource.insert(users)

    override suspend fun loginUser(userLogin: UserLogin): ResponseEntity<Response<UserResponse>> = userDataSource.login(userLogin)

    override suspend fun updateUser(userUpdate: UserUpdate): ResponseEntity<Response<UserResponse>> {
        return userDataSource.update(userUpdate)
    }

}