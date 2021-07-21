package com.school.system.grading.repository

import com.school.system.grading.datasource.entities.UserEntity
import com.school.system.grading.entity.AppResponseEntity

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/27/21
 **/

interface UserArchiveRepository {
    suspend fun archiveUserData(ids: List<UserEntity>?): AppResponseEntity<MutableList<Int?>>

    suspend fun archiveSubjects(ids: List<UserEntity>?): AppResponseEntity<List<Int?>>

    suspend fun archiveTests(ids: List<Int?>): AppResponseEntity<Unit>
}