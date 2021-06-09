package com.school.system.grading.service

import com.school.system.grading.entity.FIELD_MISSING
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.subject.request.UserSubject
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

    suspend fun getOneSubject(id: Int) = userSubjectRepository.getOneSubject(id)

    suspend fun createSubject(userSubject: UserSubject): ResponseEntity<Response<UserSubjectResponse>> {
        if(userSubject.subjectName.isEmpty()) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }
        return userSubjectRepository.createSubject(userSubject)
    }
}