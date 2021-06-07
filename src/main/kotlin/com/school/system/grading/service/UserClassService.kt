package com.school.system.grading.service

import com.school.system.grading.entity.FIELD_MISSING
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.userclass.request.UserClassCreate
import com.school.system.grading.entity.userclass.response.UserClassCreateResponse
import com.school.system.grading.repository.UserClassRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/7/21
 **/

@Service
class UserClassService(
        private val userClassRepository: UserClassRepository
) {

    fun createClass(userClassCreate: UserClassCreate): ResponseEntity<Response<UserClassCreateResponse>> {
        if(userClassCreate.className.isNullOrEmpty() || userClassCreate.id == null) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }
        return userClassRepository.createClass(userClassCreate)
    }
}