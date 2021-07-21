package com.school.system.grading.repository.impl

import com.school.system.grading.datasource.UserArchiveDataSource
import com.school.system.grading.datasource.entities.UserEntity
import com.school.system.grading.entity.AppResponseEntity
import com.school.system.grading.repository.UserArchiveRepository
import org.springframework.stereotype.Repository

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/27/21
 **/
@Repository
class UserArchiveRepositoryImpl(
       private val userArchiveDataSource: UserArchiveDataSource
): UserArchiveRepository {

    override suspend fun archiveUserData(ids: List<UserEntity>?): AppResponseEntity<MutableList<Int?>> {
        return userArchiveDataSource.archive(ids)
    }

    override suspend fun archiveSubjects(ids: List<UserEntity>?): AppResponseEntity<List<Int?>> {
        return userArchiveDataSource.archiveSubjects(ids)
    }

    override suspend fun archiveTests(ids: List<Int?>): AppResponseEntity<Unit> {
        return userArchiveDataSource.archiveTests(ids)
    }
}