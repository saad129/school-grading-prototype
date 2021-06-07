package com.school.system.grading.datasource

import com.school.system.grading.entity.Response
import com.school.system.grading.entity.userclass.response.UserClassCreateResponse
import com.school.system.grading.entity.userclass.request.UserClassCreate
import org.springframework.http.ResponseEntity

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/7/21
 **/
interface UserClassDataSource {
    fun create(userClassCreate: UserClassCreate): ResponseEntity<Response<UserClassCreateResponse>>
}