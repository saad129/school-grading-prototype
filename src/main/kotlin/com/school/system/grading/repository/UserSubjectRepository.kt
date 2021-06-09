package com.school.system.grading.repository

import com.school.system.grading.entity.Response
import com.school.system.grading.entity.subject.request.UserSubject
import com.school.system.grading.entity.subject.response.UserSubjectResponse
import org.springframework.http.ResponseEntity

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/

interface UserSubjectRepository {
    suspend fun getAllSubjects(): ResponseEntity<Response<List<UserSubjectResponse>>>

    suspend fun getOneSubject(id: Int): ResponseEntity<Response<UserSubjectResponse>>

    suspend fun createSubject(userSubject: UserSubject): ResponseEntity<Response<UserSubjectResponse>>
}