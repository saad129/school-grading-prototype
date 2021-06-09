package com.school.system.grading.repository

import com.school.system.grading.entity.Response
import com.school.system.grading.entity.userclass.response.UserClassResponse
import com.school.system.grading.entity.userclass.request.UserClassCreate
import org.springframework.http.ResponseEntity

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/7/21
 **/
interface UserClassRepository {
    suspend fun createClass(userClassCreate: UserClassCreate): ResponseEntity<Response<UserClassResponse>>

    suspend fun getAllClasses(): ResponseEntity<Response<List<UserClassResponse>>>

    suspend fun getOneClass(id: Int): ResponseEntity<Response<UserClassResponse>>
}