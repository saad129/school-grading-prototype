package com.school.system.grading.datasource.mapper

import com.school.system.grading.datasource.entities.UserSubjectEntity
import com.school.system.grading.entity.subject.request.UserSubject
import com.school.system.grading.entity.subject.response.UserSubjectResponse
import com.school.system.grading.extensions.replaceSpaceWithDash

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/

fun UserSubject.mapToUserSubjectEntity(className: String): UserSubjectEntity {
    return UserSubjectEntity(
            name = makeUniqueSubjectName(className,this.subjectName)
    )
}

fun UserSubjectEntity.mapToUserSubjectResponse(): UserSubjectResponse {
    return UserSubjectResponse(
            subjectName = this.name,
            subjectId = this.id ?: -1,
    )
}

fun List<UserSubjectEntity>.mapToUserSubjectResponse(): List<UserSubjectResponse> {
    val list = mutableListOf<UserSubjectResponse>()
    this.forEach {
        list.add(it.mapToUserSubjectResponse())
    }
    return list.toList()
}

fun makeUniqueSubjectName(className: String, subjectName: String): String {
    return "${className.replaceSpaceWithDash()}_${subjectName.replaceSpaceWithDash()}".toLowerCase().trim()
}