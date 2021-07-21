package com.school.system.grading.datasource.mapper

import com.school.system.grading.datasource.entities.UserClassEntity
import com.school.system.grading.datasource.entities.UserEntity
import com.school.system.grading.datasource.entities.UserSubjectEntity
import com.school.system.grading.entity.subject.SubjectState
import com.school.system.grading.entity.subject.request.UserSubjectRequest
import com.school.system.grading.entity.subject.response.ArchivedSubjectResponse
import com.school.system.grading.entity.subject.response.UserSubjectResponse
import com.school.system.grading.extensions.replaceSpaceWithDash
import javax.persistence.Tuple

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/

fun UserSubjectRequest.mapToUserSubjectEntity(): UserSubjectEntity {
    return UserSubjectEntity(
            name = this.subjectName ?: "",
            archived = SubjectState.UNARCHIVED.value
    )
}

fun UserSubjectEntity.mapToUserResponse(classEntity: UserClassEntity?, userEntity: UserEntity): UserSubjectResponse {
    return UserSubjectResponse(
            subjectName = this.name,
            id = this.id ?: -1,
            teacherId = userEntity.id,
            teacherName = "${userEntity.firstName} ${userEntity.lastName}",
            classId = classEntity?.id,
            className = classEntity?.name,
            hasTest = this.userTest?.isNotEmpty()
    )
}

fun UserSubjectEntity.mapToUserResponse(): UserSubjectResponse {
    return UserSubjectResponse(
            subjectName = this.name,
            id = this.id,
            teacherId = this.users?.id,
            teacherName = "${this.users?.firstName} ${this.users?.lastName}",
            classId = this.classes?.id,
            className = this.classes?.name,
            hasTest = this.userTest?.isNotEmpty()
    )
}

fun UserSubjectEntity.mapToSubjectArchivedResponse(): ArchivedSubjectResponse {
    return ArchivedSubjectResponse(
            subjectName = this.name,
            id = this.id,
            totalTests = this.userTest?.count()
    )
}

fun List<UserSubjectEntity>.mapToSubjectArchivedResponse(): List<ArchivedSubjectResponse> {
    val list = mutableListOf<ArchivedSubjectResponse>()
    this.forEachIndexed { index, userSubjectEntity ->
        list.add(userSubjectEntity.mapToSubjectArchivedResponse())
    }
    return list.toList()
}

fun List<UserSubjectEntity>.mapToUserResponse(): List<UserSubjectResponse> {
    val list = mutableListOf<UserSubjectResponse>()
    this.forEachIndexed { index, userSubjectEntity ->
        list.add(userSubjectEntity.mapToUserResponse())
    }
    return list.toList()
}

fun List<Tuple>.mapToUserSubjectResponse(): List<UserSubjectResponse> {
    val list = mutableListOf<UserSubjectResponse>()
    this.forEach {
        if(it.get(1,UserSubjectEntity::class.java) != null) {
            list.add(it.get(1,UserSubjectEntity::class.java).mapToUserResponse())
        }
    }

    return list.toList()
}