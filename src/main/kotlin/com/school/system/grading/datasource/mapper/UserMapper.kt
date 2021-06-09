package com.school.system.grading.datasource.mapper

import com.school.system.grading.datasource.entities.UserEntity
import com.school.system.grading.entity.user.UserRoles
import com.school.system.grading.entity.user.request.Users
import com.school.system.grading.entity.user.response.UserResponse

fun List<UserEntity>.mapToUserSubjectResponse(): List<UserResponse> {
    val list = mutableListOf<UserResponse>()
    this.forEach {
        list.add(UserResponse(
                id = it.id,
                firstName = it.firstName,
                lastName = it.lastName,
                username = it.username,
                token = it.token,
                role = it.role.mapRoleToString()
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
            role = this.role.mapRoleToInt()
    )
}

fun UserEntity.mapToUserSubjectResponse() : UserResponse {
    return UserResponse(
            id = this.id,
            firstName = this.firstName,
            lastName = this.lastName,
            username = this.username,
            token = this.token,
            role = this.role.mapRoleToString()
    )
}


fun Int.mapRoleToString(): String = when(this) {
    1 -> UserRoles.ADMIN.name
    2 -> UserRoles.TEACHER.name
    3 -> UserRoles.STUDENT.name
    else -> ""
}

fun String?.mapRoleToInt(): Int = when(this?.toLowerCase()?.trim()) {
    "admin" -> UserRoles.ADMIN.role
    "teacher" -> UserRoles.TEACHER.role
    "student" -> UserRoles.STUDENT.role
    else -> 0
}
