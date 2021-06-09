package com.school.system.grading.datasource.mapper

import com.school.system.grading.datasource.entities.UserClassEntity
import com.school.system.grading.entity.userclass.request.UserClassCreate
import com.school.system.grading.entity.userclass.response.UserClassResponse

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/7/21
 **/


fun UserClassCreate.mapToUserClassEntity(): UserClassEntity {
    return UserClassEntity(
            name = this.className.toString().toLowerCase().trim()
    )
}

fun UserClassEntity.mapToUserClassResponse(): UserClassResponse {
    return UserClassResponse(
            id = this.id,
            className = this.name
    )
}

fun List<UserClassEntity>.mapToUserClassResponse(): List<UserClassResponse>{
    val list = mutableListOf<UserClassResponse>()
    this.forEach {
        list.add(it.mapToUserClassResponse())
    }
    return list
}