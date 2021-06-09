package com.school.system.grading.datasource

import com.school.system.grading.entity.Response
import com.school.system.grading.entity.userclass.response.UserClassResponse
import com.school.system.grading.entity.userclass.request.UserClassCreate
import org.springframework.http.ResponseEntity

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/7/21
 **/
interface UserClassDataSource {
    suspend fun findAll(): ResponseEntity<Response<List<UserClassResponse>>>

    suspend fun create(userClassCreate: UserClassCreate): ResponseEntity<Response<UserClassResponse>>

    suspend fun findById(id: Int): ResponseEntity<Response<UserClassResponse>>
}