package com.school.system.grading.service

import com.school.system.grading.entity.FIELD_MISSING
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.subject.request.ArchiveSubjectRequest
import com.school.system.grading.entity.subject.request.RemoveSubjectRequest
import com.school.system.grading.entity.subject.request.UserSubjectRequest
import com.school.system.grading.entity.subject.request.UserSubjectUpdateRequest
import com.school.system.grading.entity.subject.response.UserSubjectResponse
import com.school.system.grading.repository.UserSubjectRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/
@Service
class UserSubjectService(
        private val userSubjectRepository: UserSubjectRepository
) {

    suspend fun getAllSubjects() = userSubjectRepository.getAllSubjects()

    suspend fun getAllArchivedSubjects() = userSubjectRepository.getAllArchivedSubjects()

    suspend fun getOneSubject(id: Int) = userSubjectRepository.getOneSubject(id)

    suspend fun getSubjectByClassId(id: Int) = userSubjectRepository.getSubjectByClassId(id)

    suspend fun getSubjectsByTeacherId(id: Int) = userSubjectRepository.getSubjectsByTeacherId(id)

    suspend fun getSubjectCount() = userSubjectRepository.getSubjectsCount()

    suspend fun updateSubject(userSubjectUpdateRequest: UserSubjectUpdateRequest): ResponseEntity<Response<UserSubjectResponse>> {
        if(userSubjectUpdateRequest.subjectName.isNullOrEmpty() || userSubjectUpdateRequest.subjectId == null
                || userSubjectUpdateRequest.userId == null) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }

        return userSubjectRepository.updateSubjectById(userSubjectUpdateRequest)
    }

    suspend fun createSubject(userSubjectRequest: UserSubjectRequest): ResponseEntity<Response<UserSubjectResponse>> {
        if(userSubjectRequest.subjectName.isNullOrEmpty() || userSubjectRequest.classId == null
                || userSubjectRequest.teacherId == null || userSubjectRequest.userId == null) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }
        return userSubjectRepository.createSubject(userSubjectRequest)
    }

    suspend fun archiveOneSubject(archiveSubjectRequest: ArchiveSubjectRequest): ResponseEntity<Response<Unit>> {
        if(archiveSubjectRequest.subjectId == null || archiveSubjectRequest.userId == null) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }

        return userSubjectRepository.archiveOneSubject(archiveSubjectRequest)
    }


    suspend fun deleteSubject(removeSubjectRequest: RemoveSubjectRequest): ResponseEntity<Response<Unit>> {
        if(removeSubjectRequest.subjectIds.isNullOrEmpty() || removeSubjectRequest.userId == null) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }

        return userSubjectRepository.deleteSubjectsById(removeSubjectRequest)
    }

    suspend fun getAverageResultBySubject(id: Int) = userSubjectRepository.getResultsOfSubjectByUserId(id)
}