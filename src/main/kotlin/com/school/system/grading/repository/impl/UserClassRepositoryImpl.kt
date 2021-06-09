package com.school.system.grading.repository.impl

import com.school.system.grading.datasource.UserClassDataSource
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.userclass.response.UserClassResponse
import com.school.system.grading.entity.userclass.request.UserClassCreate
import com.school.system.grading.repository.UserClassRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/7/21
 **/
@Repository
class UserClassRepositoryImpl(
        private val userClassDataSource: UserClassDataSource
) : UserClassRepository {

    override suspend fun createClass(userClassCreate: UserClassCreate): ResponseEntity<Response<UserClassResponse>> {
        return userClassDataSource.create(userClassCreate)
    }

    override suspend fun getAllClasses(): ResponseEntity<Response<List<UserClassResponse>>> {
        return userClassDataSource.findAll()
    }

    override suspend fun getOneClass(id: Int): ResponseEntity<Response<UserClassResponse>> {
        return userClassDataSource.findById(id)
    }
}