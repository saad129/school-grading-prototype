package com.school.system.grading.repository.impl

import com.school.system.grading.datasource.UserDataSource
import com.school.system.grading.datasource.extensions.buildContactMessage
import com.school.system.grading.entity.*
import com.school.system.grading.entity.subject.response.UserSubjectResponse
import com.school.system.grading.entity.user.request.*
import com.school.system.grading.entity.user.response.StudentAverageResultResponse
import com.school.system.grading.entity.user.response.UserCountResponse
import com.school.system.grading.entity.user.response.UserResponse
import com.school.system.grading.manager.EmailManager
import com.school.system.grading.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
        private val userDataSource: UserDataSource,
        private val emailManager: EmailManager
): UserRepository {

    override suspend fun fetchAllUsers() = userDataSource.findAll()

    override suspend fun fetchStudentsAverageGrade(subjects: List<UserSubjectResponse>?): ResponseEntity<Response<List<StudentAverageResultResponse>>> {
        return userDataSource.fetchAllAverageGrades(subjects)
    }

    override suspend fun deleteUser(deleteRequest: UserDeleteRequest): ResponseEntity<Response<Unit>> {
        return userDataSource.delete(deleteRequest)
    }

    override suspend fun getCountUsers(): ResponseEntity<Response<UserCountResponse>> {
        return userDataSource.count()
    }

    override suspend fun logoutUser(id: Int): ResponseEntity<Response<Unit>> {
        return userDataSource.logout(id)
    }

    override suspend fun fetchAssignedUsers(): ResponseEntity<Response<List<UserResponse>>> {
        return userDataSource.findAllAssignedStudents()
    }

    override suspend fun fetchDeassignedUsers(): ResponseEntity<Response<List<UserResponse>>> {
        return userDataSource.findAllDessignedStudents()
    }

    override suspend fun findUserById(id: Int): ResponseEntity<Response<UserResponse>> {
        return userDataSource.findById(id)
    }

    override fun findUserByUsername(username: String): ResponseEntity<Response<UserResponse>> {
        return userDataSource.findByUsername(username)
    }

    override suspend fun findUsersByRole(role: String): ResponseEntity<Response<List<UserResponse>>> {
        return userDataSource.findByRole(role)
    }

    override suspend fun insertUser(usersRequest: UsersRequest): ResponseEntity<Response<UserResponse>> = userDataSource.insert(usersRequest)

    override suspend fun loginUser(userLoginRequest: UserLoginRequest): ResponseEntity<Response<UserResponse>> = userDataSource.login(userLoginRequest)

    override suspend fun updateUser(userUpdateRequest: UserUpdateRequest): ResponseEntity<Response<UserResponse>> {
        return userDataSource.update(userUpdateRequest)
    }

    override suspend fun contactUser(contactUserRequest: ContactUserRequest): ResponseEntity<Response<Unit>> {
        val user = userDataSource.findById(contactUserRequest.id!!)
        return if(user.asSuccess()) {
             try {
                val userResponse = user.body?.data!!
                val message = userResponse.buildContactMessage(contactUserRequest.message)
                emailManager.sendEmail(contactUserRequest.to!!,contactUserRequest.subject!!,message)
                ResponseEntity.ok(Response(
                        status = SUCCESS,
                        message = "Email sent successfully.")
                )
            }catch (e: Exception) {
                ResponseEntity.ok(Response(
                        status = EMAIL_NOT_SEND,
                        message = "Unable to send email. Please try again")
                )
            }

        } else {
             ResponseEntity.ok(Response(
                    status = EMAIL_NOT_SEND,
                    message = "Unable to send email. Please try again")
            )
        }
    }

}