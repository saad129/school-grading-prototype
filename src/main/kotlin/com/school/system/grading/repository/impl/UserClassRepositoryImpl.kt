package com.school.system.grading.repository.impl

import com.school.system.grading.datasource.UserClassDataSource
import com.school.system.grading.datasource.entities.UserEntity
import com.school.system.grading.entity.AppResponseEntity
import com.school.system.grading.entity.Response
import com.school.system.grading.entity.userclass.request.*
import com.school.system.grading.entity.userclass.response.UserClassResponse
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

    override suspend fun getClassesCount(): ResponseEntity<Response<Long>> {
        return userClassDataSource.count()
    }

    override suspend fun createClass(userClassCreateRequest: UserClassCreateRequest): ResponseEntity<Response<UserClassResponse>> {
        return userClassDataSource.create(userClassCreateRequest)
    }

    override suspend fun getAllClasses(): ResponseEntity<Response<List<UserClassResponse>>> {
        return userClassDataSource.findAll()
    }

    override suspend fun getAllValidClasses(): ResponseEntity<Response<List<UserClassResponse>>> {
        return userClassDataSource.findAllValidClasses()
    }

    override suspend fun getOneClass(id: Int): ResponseEntity<Response<UserClassResponse>> {
        return userClassDataSource.findById(id)
    }

    override suspend fun deleteClass(userDeleteClassRequest: UserDeleteClassRequest): ResponseEntity<Response<Unit>> {
        return userClassDataSource.deleteById(userDeleteClassRequest)
    }

    override suspend fun updateClass(classUpdateRequest: UserClassUpdateRequest): ResponseEntity<Response<UserClassResponse>> {
        return userClassDataSource.update(classUpdateRequest)
    }

    override suspend fun  assignStudentsClass(userAssignClassRequest: UserAssignClassRequest): ResponseEntity<Response<Unit>> {
        return userClassDataSource.assignClass(userAssignClassRequest)
    }

    override suspend fun deAssignStudentsClass(userDeAssignRequest: UserDeAssignRequest): ResponseEntity<Response<Unit>> {
        return userClassDataSource.deAssignClass(userDeAssignRequest)
    }

    override suspend fun checkIfStudentsAssigned(userAssignClassRequest: UserAssignClassRequest): AppResponseEntity<List<UserEntity>> {
        return userClassDataSource.checkIfStudentsAlreadyAssigned(userAssignClassRequest)
    }

    override suspend fun removePreviousAssignedSubjectsFromClass(userId: List<Int>?, classId: Int?) {
        return userClassDataSource.removePreviousAssignedSubjectsFromClass(userId,classId)
    }

    override suspend fun archiveSubjectsByClass(userDeleteClassRequest: UserDeleteClassRequest): AppResponseEntity<Unit> {
        return userClassDataSource.archiveSubjectsAndTests(userDeleteClassRequest)
    }

    override suspend fun deAssignedStudentsByClass(userDeleteClassRequest: UserDeleteClassRequest): AppResponseEntity<Unit> {
        return userClassDataSource.deAssignedStudents(userDeleteClassRequest)
    }
}