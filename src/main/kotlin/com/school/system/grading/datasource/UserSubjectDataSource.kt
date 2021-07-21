package com.school.system.grading.datasource

import com.school.system.grading.entity.AppResponseEntity
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.subject.request.ArchiveSubjectRequest
import com.school.system.grading.entity.subject.request.RemoveSubjectRequest
import com.school.system.grading.entity.subject.request.UserSubjectRequest
import com.school.system.grading.entity.subject.request.UserSubjectUpdateRequest
import com.school.system.grading.entity.subject.response.ArchivedSubjectResponse
import com.school.system.grading.entity.subject.response.StudentAverageSubjectResponse
import com.school.system.grading.entity.subject.response.UserSubjectResponse
import org.springframework.http.ResponseEntity

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/
interface UserSubjectDataSource {
    suspend fun count(): ResponseEntity<Response<Long>>

    suspend fun create(userSubjectRequest: UserSubjectRequest): ResponseEntity<Response<UserSubjectResponse>>

    suspend fun findAll(): ResponseEntity<Response<List<UserSubjectResponse>>>

    suspend fun findAllArchived(): ResponseEntity<Response<List<ArchivedSubjectResponse>>>

    suspend fun findByTeacherId(id: Int): ResponseEntity<Response<List<UserSubjectResponse>>>

    suspend fun findById(id: Int): ResponseEntity<Response<UserSubjectResponse>>

    suspend fun findByClassId(id: Int): ResponseEntity<Response<List<UserSubjectResponse>>>

    suspend fun update(updateSubjectRequest: UserSubjectUpdateRequest): ResponseEntity<Response<UserSubjectResponse>>

    suspend fun deleteByIds(removeSubjectRequest: RemoveSubjectRequest): ResponseEntity<Response<Unit>>

    suspend fun archive(archiveSubjectRequest: ArchiveSubjectRequest): ResponseEntity<Response<Unit>>

    suspend fun getAverageSubjectResult(id: Int): ResponseEntity<Response<List<StudentAverageSubjectResponse>>>

    suspend fun test(userSubjectRequest: UserSubjectRequest): ResponseEntity<Response<UserSubjectResponse>>

    suspend fun testsSubject()
}