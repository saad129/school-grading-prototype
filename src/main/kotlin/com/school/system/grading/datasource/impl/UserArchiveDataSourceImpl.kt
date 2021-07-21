package com.school.system.grading.datasource.impl

import com.school.system.grading.datasource.UserArchiveDataSource
import com.school.system.grading.datasource.entities.*
import com.school.system.grading.datasource.mapper.mapToArchiveSubject
import com.school.system.grading.entity.AppResponseEntity
import com.school.system.grading.entity.subject.SubjectState
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/27/21
 **/

@Repository
class UserArchiveDataSourceImpl(
        @PersistenceContext
        private val entityManager: EntityManager
): UserArchiveDataSource {

    override suspend fun findAll() {
        TODO("Not yet implemented")
    }

    override suspend fun findByStudentId(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun findBySubjectId(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun findByTeacherId(id: Int) {
        TODO("Not yet implemented")
    }

    @Transactional
    override suspend fun archive(ids: List<UserEntity>?): AppResponseEntity<MutableList<Int?>> {
        val unArchivedSubject = mutableListOf<Int?>()
        ids?.forEach { user ->
            user.studentSubjects?.forEachIndexed { index,subject ->
                if(!subject.userTest.isNullOrEmpty()) {
                    subject.archived = SubjectState.ARCHIVED.value
                    entityManager.merge(subject)
                    subject.classes = null
                    subject.removeTeacherFromArchiveSubject()
                    println("data archived")
                } else {
                    unArchivedSubject.add(subject.id)
                }
            }
        }

        return AppResponseEntity.SUCCESS(unArchivedSubject.distinct().toMutableList())
    }

    @Transactional
    override suspend fun archiveSubjects(ids: List<UserEntity>?): AppResponseEntity<List<Int?>> {
        val archivedSubjectList = mutableSetOf<Int?>()
        ids?.forEach {user ->
            user.studentSubjects?.forEach { subject ->
                if(!subject.userTest.isNullOrEmpty()) {
                    val archiveSubjects = subject.mapToArchiveSubject(user)
                    entityManager.merge(archiveSubjects)
                    println("data archived")
                    archivedSubjectList.add(subject.id)
                }
            }
        }

        return AppResponseEntity.SUCCESS(archivedSubjectList.toList())
    }

    @Transactional
    override suspend fun archiveTests(ids: List<Int?>): AppResponseEntity<Unit> {
        TODO("Not yet implemented")
    }

}