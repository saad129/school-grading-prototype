package com.school.system.grading.datasource.mapper

import com.school.system.grading.datasource.entities.UserEntity
import com.school.system.grading.entity.user.UserRoles
import com.school.system.grading.entity.user.request.UsersRequest
import com.school.system.grading.entity.user.response.SingleUserResponse
import com.school.system.grading.entity.user.response.UserCountResponse
import com.school.system.grading.entity.user.response.UserResponse

fun List<UserEntity>.mapToUserResponse(): List<UserResponse> {
    val list = mutableListOf<UserResponse>()
    this.forEach {
        list.add(it.mapToUserResponse())
    }
    return list.toList()
}

fun UsersRequest.mapToUserEntity(encodedPassword: String): UserEntity {
    return UserEntity(
            firstName = this.firstName.toString(),
            lastName = this.lastName.toString(),
            username = this.username.toString().replace(" ",".").toLowerCase(),
            password = encodedPassword,
            email = this.email,
            role = this.role.mapRoleToInt()
    )
}

fun UserEntity.mapToUserResponse() : UserResponse {
    return UserResponse(
            id = this.id,
            firstName = this.firstName,
            lastName = this.lastName,
            username = this.username,
            password = this.password,
            role = this.role.mapRoleToString(),
            isAssigned = this.studentClass != null
    )
}

fun mapToUsersCount(admins: Long?, students: Long?, teachers: Long?): UserCountResponse {
    return UserCountResponse(
            admins = admins,
            students = students,
            teachers = teachers
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

fun String?.isValidRole(): Boolean {
    return when(this?.mapRoleToInt()) {
        0 -> false
        else -> true
    }
}
