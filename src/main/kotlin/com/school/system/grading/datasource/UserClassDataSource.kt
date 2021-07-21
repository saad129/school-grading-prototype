package com.school.system.grading.datasource

import com.school.system.grading.datasource.entities.UserEntity
import com.school.system.grading.entity.AppResponseEntity
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.userclass.request.*
import com.school.system.grading.entity.userclass.response.UserClassResponse
import org.springframework.http.ResponseEntity

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/7/21
 **/
interface UserClassDataSource {
    suspend fun count(): ResponseEntity<Response<Long>>

    suspend fun findAll(): ResponseEntity<Response<List<UserClassResponse>>>

    suspend fun findAllValidClasses(): ResponseEntity<Response<List<UserClassResponse>>>

    suspend fun create(userClassCreateRequest: UserClassCreateRequest): ResponseEntity<Response<UserClassResponse>>

    suspend fun findById(id: Int): ResponseEntity<Response<UserClassResponse>>

    suspend fun deleteById(userDeleteClassRequest: UserDeleteClassRequest): ResponseEntity<Response<Unit>>

    suspend fun update(userClassUpdateRequest: UserClassUpdateRequest): ResponseEntity<Response<UserClassResponse>>

    suspend fun assignClass(userAssignClassRequest: UserAssignClassRequest): ResponseEntity<Response<Unit>>

    suspend fun deAssignClass(userDeAssignRequest: UserDeAssignRequest): ResponseEntity<Response<Unit>>

    suspend fun checkIfStudentsAlreadyAssigned(userAssignClassRequest: UserAssignClassRequest): AppResponseEntity<List<UserEntity>>

    suspend fun removePreviousAssignedSubjectsFromClass(users: List<UserEntity>?)

    suspend fun removePreviousAssignedSubjectsFromClass(userId: List<Int>?, classId: Int?)

    suspend fun archiveSubjectsAndTests(deleteClassRequest: UserDeleteClassRequest): AppResponseEntity<Unit>

    suspend fun deAssignedStudents(deleteClassRequest: UserDeleteClassRequest): AppResponseEntity<Unit>
}