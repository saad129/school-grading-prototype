package com.school.system.grading.repository

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

interface UserSubjectRepository {
    suspend fun getSubjectsCount(): ResponseEntity<Response<Long>>

    suspend fun getAllSubjects(): ResponseEntity<Response<List<UserSubjectResponse>>>

    suspend fun getAllArchivedSubjects(): ResponseEntity<Response<List<ArchivedSubjectResponse>>>

    suspend fun getSubjectsByTeacherId(id: Int): ResponseEntity<Response<List<UserSubjectResponse>>>

    suspend fun getOneSubject(id: Int): ResponseEntity<Response<UserSubjectResponse>>

    suspend fun getSubjectByClassId(id: Int): ResponseEntity<Response<List<UserSubjectResponse>>>

    suspend fun createSubject(userSubjectRequest: UserSubjectRequest): ResponseEntity<Response<UserSubjectResponse>>

    suspend fun updateSubjectById(updateSubjectUpdateRequest: UserSubjectUpdateRequest): ResponseEntity<Response<UserSubjectResponse>>

    suspend fun deleteSubjectsById(removeSubjectRequest: RemoveSubjectRequest): ResponseEntity<Response<Unit>>

    suspend fun archiveOneSubject(archiveSubjectRequest: ArchiveSubjectRequest): ResponseEntity<Response<Unit>>

    suspend fun getResultsOfSubjectByUserId(id: Int): ResponseEntity<Response<List<StudentAverageSubjectResponse>>>

}