package com.school.system.grading.service

import com.school.system.grading.entity.*
import com.school.system.grading.entity.userclass.request.*
import com.school.system.grading.entity.userclass.response.UserClassResponse
import com.school.system.grading.repository.UserArchiveRepository
import com.school.system.grading.repository.UserClassRepository
import com.school.system.grading.repository.UserSubjectRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/7/21
 **/

@Service
class UserClassService(
        private val userClassRepository: UserClassRepository,
        private val userArchiveRepository: UserArchiveRepository,
        private val userSubjectRepository: UserSubjectRepository
) {

    suspend fun getAllClasses() = userClassRepository.getAllClasses()

    suspend fun getAllValidClasses() = userClassRepository.getAllValidClasses()

    suspend fun getClassesCount() = userClassRepository.getClassesCount()

    suspend fun getOneClass(id: Int) = userClassRepository.getOneClass(id)

    suspend fun updateClass(classUpdateRequest: UserClassUpdateRequest): ResponseEntity<Response<UserClassResponse>> {
        if(classUpdateRequest.id == null || classUpdateRequest.classId == null
                || classUpdateRequest.className.isNullOrEmpty()) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }

        return userClassRepository.updateClass(classUpdateRequest)
    }

    suspend fun deleteClass(userDeleteClassRequest: UserDeleteClassRequest): ResponseEntity<Response<Unit>> {
        if(userDeleteClassRequest.classId == null || userDeleteClassRequest.userId == null)  {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }
        userClassRepository.run {
            this.deAssignedStudentsByClass(userDeleteClassRequest)
            return when(this.archiveSubjectsByClass(userDeleteClassRequest)) {
                is AppResponseEntity.SUCCESS -> {
                    userClassRepository.deleteClass(userDeleteClassRequest)
                }

               is AppResponseEntity.ERROR -> {
                   ResponseEntity.ok().body(Response(
                           status = CLASS_NOT_FOUND,
                           message = "Class not found"
                   ))
               }
            }
        }
    }

    suspend fun createClass(userClassCreateRequest: UserClassCreateRequest): ResponseEntity<Response<UserClassResponse>> {
        if(userClassCreateRequest.className.isNullOrEmpty() || userClassCreateRequest.id == null) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }
        return userClassRepository.createClass(userClassCreateRequest)
    }

    suspend fun assignStudentsClass(userAssignClassRequest: UserAssignClassRequest): ResponseEntity<Response<Unit>> {
        if(userAssignClassRequest.studentIds.isNullOrEmpty() || userAssignClassRequest.classId == null
                || userAssignClassRequest.adminId == null) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }
        val userClass = userAssignClassRequest.copy(studentIds = userAssignClassRequest.studentIds.distinct())

         return when(val result = userClassRepository.checkIfStudentsAssigned(userClass)) {
            is AppResponseEntity.SUCCESS ->  {
                userClassRepository.let {
                    userArchiveRepository.archiveUserData(result.data)
                //    userClassRepository.removePreviousAssignedSubjectsFromClass(userClass.studentIds,classId = userClass.classId)
                    it.assignStudentsClass(userClass)
                }
            }

            is AppResponseEntity.ERROR -> {
                userClassRepository.assignStudentsClass(userClass)
            }
        }

    }


    suspend fun deAssignStudentClass(deAssignRequest: UserDeAssignRequest): ResponseEntity<Response<Unit>> {
        if(deAssignRequest.userId == null) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Student id is missing")
            )
        }

        return userClassRepository.deAssignStudentsClass(deAssignRequest)
    }
}