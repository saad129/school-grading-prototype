package com.school.system.grading.datasource.mapper

import com.school.system.grading.datasource.entities.UserClassEntity
import com.school.system.grading.entity.userclass.request.UserClassCreateRequest
import com.school.system.grading.entity.userclass.response.UserClassResponse
import javax.persistence.Tuple

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/7/21
 **/


fun UserClassCreateRequest.mapToUserClassEntity(): UserClassEntity {
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

fun List<Tuple>.mapTupleToUserClassResponse(): List<UserClassResponse> {
    val list = mutableListOf<UserClassResponse>()
    this.forEach {
        list.add(it.get(0,UserClassEntity::class.java).mapToUserClassResponse())
    }

    return list.toList()
}