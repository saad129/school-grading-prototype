package com.school.system.grading.repository

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
interface UserClassRepository {
    suspend fun getClassesCount(): ResponseEntity<Response<Long>>

    suspend fun createClass(userClassCreateRequest: UserClassCreateRequest): ResponseEntity<Response<UserClassResponse>>

    suspend fun getAllClasses(): ResponseEntity<Response<List<UserClassResponse>>>

    suspend fun getAllValidClasses(): ResponseEntity<Response<List<UserClassResponse>>>

    suspend fun getOneClass(id: Int): ResponseEntity<Response<UserClassResponse>>

    suspend fun deleteClass(userDeleteClassRequest: UserDeleteClassRequest): ResponseEntity<Response<Unit>>

    suspend fun updateClass(classUpdateRequest: UserClassUpdateRequest): ResponseEntity<Response<UserClassResponse>>

    suspend fun assignStudentsClass(userAssignClassRequest: UserAssignClassRequest): ResponseEntity<Response<Unit>>

    suspend fun deAssignStudentsClass(userDeAssignRequest: UserDeAssignRequest): ResponseEntity<Response<Unit>>

    suspend fun checkIfStudentsAssigned(userAssignClassRequest: UserAssignClassRequest): AppResponseEntity<List<UserEntity>>

    suspend fun removePreviousAssignedSubjectsFromClass(userId: List<Int>?,classId: Int?)

    suspend fun archiveSubjectsByClass(userDeleteClassRequest: UserDeleteClassRequest): AppResponseEntity<Unit>

    suspend fun deAssignedStudentsByClass(userDeleteClassRequest: UserDeleteClassRequest): AppResponseEntity<Unit>
}