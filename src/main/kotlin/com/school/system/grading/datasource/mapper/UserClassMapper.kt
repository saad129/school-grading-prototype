package com.school.system.grading.datasource.mapper

import com.school.system.grading.datasource.entities.UserClassEntity
import com.school.system.grading.datasource.entities.UserEntity
import com.school.system.grading.entity.userclass.request.UserClassCreate
import com.school.system.grading.entity.userclass.response.UserClassCreateResponse

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

fun UserEntity.mapToUserClassResponse(): UserClassCreateResponse {
    return UserClassCreateResponse(
            userId = this.id,
            classId = this.userClass?.id,
            className = this.userClass?.name
    )
}