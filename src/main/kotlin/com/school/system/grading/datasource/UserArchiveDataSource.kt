package com.school.system.grading.datasource

import com.school.system.grading.datasource.entities.UserEntity
import com.school.system.grading.entity.AppResponseEntity
import com.school.system.grading.entity.userclass.request.UserAssignClassRequest

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/27/21
 **/
interface UserArchiveDataSource {
    suspend fun findAll()

    suspend fun findByStudentId(id: Int)

    suspend fun findByTeacherId(id: Int)

    suspend fun findBySubjectId(id: Int)

    suspend fun archive(ids: List<UserEntity>?): AppResponseEntity<MutableList<Int?>>

    suspend fun archiveSubjects(ids: List<UserEntity>?): AppResponseEntity<List<Int?>>

    suspend fun archiveTests(ids: List<Int?>): AppResponseEntity<Unit>
}