package com.school.system.grading.datasource.mapper

import com.school.system.grading.datasource.entities.*

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/27/21
 **/

fun UserSubjectEntity.mapToArchiveSubject(userEntity: UserEntity): UserArchiveSubject {
    return UserArchiveSubject(
            studentId = userEntity.id,
            studentName = "${userEntity.firstName} ${userEntity.lastName}",
            teacherId = this.users?.id,
            teacherName =  "${this.users?.firstName} ${this.users?.lastName}",
            subjectId = this.id,
            subjectName = this.name
    )
}
