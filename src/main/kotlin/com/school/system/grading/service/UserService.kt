package com.school.system.grading.service

import com.school.system.grading.entity.FIELD_MISSING
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.user.request.UserLogin
import com.school.system.grading.entity.user.request.UserUpdate
import com.school.system.grading.entity.user.request.Users
import com.school.system.grading.entity.user.response.UserResponse
import com.school.system.grading.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(
        private val userRepository: UserRepository
) {

    fun getAllUsers(): ResponseEntity<Response<List<UserResponse>>> = userRepository.fetchAllUsers()

    fun createUser(users: Users): ResponseEntity<Response<UserResponse>> {
        return if (users.firstName.isNullOrEmpty() || users.lastName.isNullOrEmpty() || users.password.isNullOrEmpty()
                || users.role == null || users.username.isNullOrEmpty()) {
            ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        } else {
            userRepository.insertUser(users)
        }
    }

    fun loginUser(userLogin: UserLogin): ResponseEntity<Response<UserResponse>> = userRepository.loginUser(userLogin)

    fun updateUser(userUpdate: UserUpdate): ResponseEntity<Response<UserResponse>> {
        return if(userUpdate.firstName.isEmpty() || userUpdate.lastName.isEmpty() || userUpdate.password.isEmpty() ||
                userUpdate.username.isEmpty() || userUpdate.id == null) {
            ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        } else {
            userRepository.updateUser(userUpdate)
        }
    }
}