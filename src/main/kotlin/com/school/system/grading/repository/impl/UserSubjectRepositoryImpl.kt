package com.school.system.grading.repository.impl

import com.school.system.grading.datasource.UserSubjectDataSource
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.subject.request.UserSubject
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

    override suspend fun createSubject(userSubject: UserSubject): ResponseEntity<Response<UserSubjectResponse>> {
        return userSubjectDataSource.create(userSubject)
    }

    override suspend fun getAllSubjects(): ResponseEntity<Response<List<UserSubjectResponse>>> {
        return userSubjectDataSource.findAll()
    }

    override suspend fun getOneSubject(id: Int): ResponseEntity<Response<UserSubjectResponse>> {
        return userSubjectDataSource.findById(id)
    }

}