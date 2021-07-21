package com.school.system.grading.repository.impl

import com.school.system.grading.datasource.UserSubjectDataSource
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.subject.request.ArchiveSubjectRequest
import com.school.system.grading.entity.subject.request.RemoveSubjectRequest
import com.school.system.grading.entity.subject.request.UserSubjectRequest
import com.school.system.grading.entity.subject.request.UserSubjectUpdateRequest
import com.school.system.grading.entity.subject.response.ArchivedSubjectResponse
import com.school.system.grading.entity.subject.response.StudentAverageSubjectResponse
import com.school.system.grading.entity.subject.response.UserSubjectResponse
import com.school.system.grading.repository.UserSubjectRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/
@Repository
class UserSubjectRepositoryImpl(
        private val userSubjectDataSource: UserSubjectDataSource
): UserSubjectRepository {

    override suspend fun getSubjectsCount(): ResponseEntity<Response<Long>> {
        return userSubjectDataSource.count()
    }

    override suspend fun createSubject(userSubjectRequest: UserSubjectRequest): ResponseEntity<Response<UserSubjectResponse>> {
        return userSubjectDataSource.create(userSubjectRequest)
    }

    override suspend fun updateSubjectById(updateSubjectRequest: UserSubjectUpdateRequest): ResponseEntity<Response<UserSubjectResponse>> {
        return userSubjectDataSource.update(updateSubjectRequest)
    }

    override suspend fun getAllSubjects(): ResponseEntity<Response<List<UserSubjectResponse>>> {
        return userSubjectDataSource.findAll()
    }

    override suspend fun getAllArchivedSubjects(): ResponseEntity<Response<List<ArchivedSubjectResponse>>> {
        return userSubjectDataSource.findAllArchived()
    }

    override suspend fun getSubjectsByTeacherId(id: Int): ResponseEntity<Response<List<UserSubjectResponse>>> {
        return userSubjectDataSource.findByTeacherId(id)
    }

    override suspend fun getOneSubject(id: Int): ResponseEntity<Response<UserSubjectResponse>> {
        return userSubjectDataSource.findById(id)
    }

    override suspend fun getSubjectByClassId(id: Int): ResponseEntity<Response<List<UserSubjectResponse>>> {
        return userSubjectDataSource.findByClassId(id)
    }

    override suspend fun deleteSubjectsById(removeSubjectRequest: RemoveSubjectRequest): ResponseEntity<Response<Unit>>{
        return userSubjectDataSource.deleteByIds(removeSubjectRequest)
    }

    override suspend fun archiveOneSubject(archiveSubjectRequest: ArchiveSubjectRequest): ResponseEntity<Response<Unit>> {
        return userSubjectDataSource.archive(archiveSubjectRequest)
    }

    override suspend fun getResultsOfSubjectByUserId(id: Int): ResponseEntity<Response<List<StudentAverageSubjectResponse>>> {
        return userSubjectDataSource.getAverageSubjectResult(id)
    }


}