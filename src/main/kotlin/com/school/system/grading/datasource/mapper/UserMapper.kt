package com.school.system.grading.datasource.mapper

import com.school.system.grading.datasource.entities.UserEntity
import com.school.system.grading.entity.user.request.UserUpdate
import com.school.system.grading.entity.user.request.Users
import com.school.system.grading.entity.user.response.UserResponse

fun List<UserEntity>.mapToUserResponse(): List<UserResponse> {
    val list = mutableListOf<UserResponse>()
    this.forEach {
        list.add(UserResponse(
                id = it.id,
                firstName = it.firstName,
                lastName = it.lastName,
                username = it.username,
                token = it.token,
                role = it.role
        ))
    }
    return list.toList()
}

fun Users.mapToUserEntity(encodedPassword: String): UserEntity {
    return UserEntity(
            firstName = this.firstName.toString(),
            lastName = this.lastName.toString(),
            username = this.username.toString().replace(" ",".").toLowerCase(),
            password = encodedPassword,
            role = this.role!!
    )
}

fun UserEntity.mapToUserResponse() : UserResponse {
    return UserResponse(
            id = this.id,
            firstName = this.firstName,
            lastName = this.lastName,
            username = this.username,
            token = this.token,
            role = this.role
    )
}
