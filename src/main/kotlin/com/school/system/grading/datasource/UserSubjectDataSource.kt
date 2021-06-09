package com.school.system.grading.datasource

import com.school.system.grading.entity.Response
import com.school.system.grading.entity.subject.request.UserSubject
import com.school.system.grading.entity.subject.response.UserSubjectResponse
import org.springframework.http.ResponseEntity

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/
interface UserSubjectDataSource {
    suspend fun create(userSubject: UserSubject): ResponseEntity<Response<UserSubjectResponse>>

    suspend fun findAll(): ResponseEntity<Response<List<UserSubjectResponse>>>

    suspend fun findById(id: Int): ResponseEntity<Response<UserSubjectResponse>>

    suspend fun test(userSubject: UserSubject): ResponseEntity<Response<UserSubjectResponse>>
}