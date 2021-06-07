package com.school.system.grading.repository.impl

import com.school.system.grading.datasource.UserClassDataSource
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.userclass.response.UserClassCreateResponse
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

    override fun createClass(userClassCreate: UserClassCreate): ResponseEntity<Response<UserClassCreateResponse>> {
        return userClassDataSource.create(userClassCreate)
    }

}