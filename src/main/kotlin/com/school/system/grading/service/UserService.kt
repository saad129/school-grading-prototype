package com.school.system.grading.service

import com.school.system.grading.datasource.mapper.isValidRole
import com.school.system.grading.datasource.mapper.mapRoleToInt
import com.school.system.grading.entity.*
import com.school.system.grading.entity.user.request.*
import com.school.system.grading.entity.user.response.SingleUserResponse
import com.school.system.grading.entity.user.response.StudentAverageResultResponse
import com.school.system.grading.entity.user.response.UserResponse
import com.school.system.grading.repository.UserRepository
import com.school.system.grading.repository.UserSubjectRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class UserService(
        private val userRepository: UserRepository,
        private val subjectRepository: UserSubjectRepository,
) {

    val EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+")

    suspend fun getAllUsers(): ResponseEntity<Response<List<UserResponse>>> = userRepository.fetchAllUsers()

    suspend fun getCountUsers() = userRepository.getCountUsers()

    suspend fun getAllAssignedStudents() = userRepository.fetchAssignedUsers()

    suspend fun getAllDeassignedStudents() = userRepository.fetchDeassignedUsers()

    suspend fun getAllStudentsAverageGrade(teacherId: Int): ResponseEntity<Response<List<StudentAverageResultResponse>>> {
        val response = subjectRepository.getSubjectsByTeacherId(teacherId)
        return when(val result = response.mapAsAppResponse()) {
            is AppResponseEntity.SUCCESS -> {
                userRepository.fetchStudentsAverageGrade(result.data)
            }
            is AppResponseEntity.ERROR -> {
                 ResponseEntity.ok().body(Response(
                        status = NO_CONTENT,
                        message = "Subjects not found"
                ))
            }
        }
    }

    suspend fun getUserById(id: Int): ResponseEntity<Response<UserResponse>> = userRepository.findUserById(id)

    suspend fun logoutUser(id: Int) = userRepository.logoutUser(id)

    suspend fun deleteUser(deleteRequest: UserDeleteRequest): ResponseEntity<Response<Unit>>  {
        if(deleteRequest.userId == deleteRequest.deleteId) {
            return ResponseEntity.ok(Response(
                    status = ERROR,
                    message = "Unable to delete your own account"
            ))
        }
        return userRepository.deleteUser(deleteRequest)
    }

    suspend fun getUsersByRole(role:String?): ResponseEntity<Response<List<UserResponse>>> {
        if(role.isNullOrEmpty() || !role.isValidRole()) {
            return ResponseEntity.ok(Response(
                    status = ROLE_NOT_VALID,
                    message = "Role not valid")
            )
        }

        return userRepository.findUsersByRole(role)
    }

    suspend fun createUser(usersRequest: UsersRequest): ResponseEntity<Response<UserResponse>> {
        if (usersRequest.firstName.isNullOrEmpty() || usersRequest.lastName.isNullOrEmpty() || usersRequest.password.isNullOrEmpty()
                || usersRequest.role.isNullOrEmpty() || usersRequest.username.isNullOrEmpty() ) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }
        if(usersRequest.role?.isValidRole() == false) {
            return ResponseEntity.ok(Response(
                    status = ROLE_NOT_VALID,
                    message = "User role not valid")
            )
        }

        if(!usersRequest.email.isValidEmail()) {
            return ResponseEntity.ok(Response(
                    status = EMAIL_NOT_VALID,
                    message = "Email address not valid")
            )
        }

        return userRepository.insertUser(usersRequest)

    }

    suspend fun loginUser(userLoginRequest: UserLoginRequest): ResponseEntity<Response<UserResponse>> {
        if(userLoginRequest.username.isNullOrEmpty() || userLoginRequest.password.isNullOrEmpty()) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }
        return userRepository.loginUser(userLoginRequest)
    }

    suspend fun updateUser(userUpdateRequest: UserUpdateRequest): ResponseEntity<Response<UserResponse>> {
        return if(userUpdateRequest.firstName.isNullOrEmpty() || userUpdateRequest.lastName.isNullOrEmpty() || userUpdateRequest.password.isNullOrEmpty() ||
                userUpdateRequest.username.isNullOrEmpty() || userUpdateRequest.id == null) {
            ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        } else {
            userRepository.updateUser(userUpdateRequest)
        }
    }

    suspend fun contactUser(contactUserRequest: ContactUserRequest): ResponseEntity<Response<Unit>>  {
        if(contactUserRequest.id == null || contactUserRequest.message.isNullOrEmpty()
                || contactUserRequest.subject.isNullOrEmpty() || contactUserRequest.to.isNullOrEmpty()) {
            return ResponseEntity.ok(Response(
                    status = FIELD_MISSING,
                    message = "Fields are missing")
            )
        }

        return userRepository.contactUser(contactUserRequest)
    }


    private fun String?.isValidEmail(): Boolean {
        if(this.isNullOrEmpty()) return false

        return EMAIL_ADDRESS_PATTERN.matcher(this).matches()
    }
}