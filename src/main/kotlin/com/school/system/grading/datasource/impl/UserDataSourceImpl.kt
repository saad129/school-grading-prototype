package com.school.system.grading.datasource.impl

import com.school.system.grading.datasource.UserDataSource
import com.school.system.grading.datasource.entities.*
import com.school.system.grading.datasource.mapper.mapToUserEntity
import com.school.system.grading.datasource.mapper.mapToUserResponse
import com.school.system.grading.entity.*
import com.school.system.grading.datasource.extensions.withCountCriteriaBuilder
import com.school.system.grading.datasource.extensions.withCriteriaBuilder
import com.school.system.grading.datasource.extensions.withJoinCriteriaBuilder
import com.school.system.grading.datasource.mapper.mapRoleToInt
import com.school.system.grading.datasource.mapper.mapToUsersCount
import com.school.system.grading.entity.subject.SubjectState
import com.school.system.grading.entity.subject.response.UserSubjectResponse
import com.school.system.grading.entity.user.UserRoles
import com.school.system.grading.entity.user.isAdmin
import com.school.system.grading.entity.user.isStudent
import com.school.system.grading.entity.user.isTeacher
import com.school.system.grading.entity.user.request.UserDeleteRequest
import com.school.system.grading.entity.user.request.UserLoginRequest
import com.school.system.grading.entity.user.request.UserUpdateRequest
import com.school.system.grading.entity.user.request.UsersRequest
import com.school.system.grading.entity.user.response.StudentAverageResultResponse
import com.school.system.grading.entity.user.response.UserCountResponse
import com.school.system.grading.entity.user.response.UserResponse
import com.school.system.grading.extensions.generateNewToken
import com.school.system.grading.extensions.replaceSpaceWithDot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import java.net.URI
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional
import kotlin.math.round

@Repository
class UserDataSourceImpl(
        @PersistenceContext
        private val entityManager: EntityManager,
        @Autowired
        private val passwordEncoder: PasswordEncoder
) : UserDataSource {

    override suspend fun count(): ResponseEntity<Response<UserCountResponse>> {
        val admins = entityManager.withCountCriteriaBuilder<UserEntity> { builder, query, root ->
            val predicateForRole = builder.equal(root.get(UserEntity_.role),UserRoles.ADMIN.role)
            query.select(builder.count(root)).where(predicateForRole)
        }

        val students = entityManager.withCountCriteriaBuilder<UserEntity> { builder, query, root ->
            val predicateForRole = builder.equal(root.get(UserEntity_.role),UserRoles.STUDENT.role)
            query.select(builder.count(root)).where(predicateForRole)
        }

        val teachers = entityManager.withCountCriteriaBuilder<UserEntity> { builder, query, root ->
            val predicateForRole = builder.equal(root.get(UserEntity_.role),UserRoles.TEACHER.role)
            query.select(builder.count(root)).where(predicateForRole)
        }

        return ResponseEntity.ok(Response(
                status = SUCCESS,
                message = "Users count",
                data = mapToUsersCount(admins,students,teachers)
        ))
    }

    override suspend fun findAll(): ResponseEntity<Response<List<UserResponse>>> {
        val result = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            query.select(root)
        }.resultList

        return if(result.isNotEmpty()) {
            ResponseEntity.ok(Response(
                    status = SUCCESS,
                    message = "Users found",
                    data = result.mapToUserResponse()
            ))
        } else {
            ResponseEntity.ok(Response(
                    status = USER_NOT_FOUND,
                    message = "No users created "
            ))
        }
    }

    override suspend fun fetchAllAverageGrades(subjects: List<UserSubjectResponse>?): ResponseEntity<Response<List<StudentAverageResultResponse>>> {
        val response = mutableListOf<StudentAverageResultResponse>()
        subjects?.forEach { subject ->
            println("subject id : ${subject.id}")
            val tupleUserResponse = entityManager.withJoinCriteriaBuilder<UserEntity,UserSubjectEntity>(UserEntity_.studentSubjects) {
                builder, query, root, join ->
                val predicateForId = builder.equal(join.get(UserSubjectEntity_.id),subject.id)
                query.where(predicateForId)
            }.resultList
            println("${tupleUserResponse.size}")
            if(!tupleUserResponse.isNullOrEmpty()) {
                tupleUserResponse.forEach {
                    val student = it.get(0,UserEntity::class.java)
                    val marks  = student.studentsGradeEntity?.mapNotNull{
                        it.marks?.toDouble() }?.average() ?: run { 0.0 }
                    println("student ${student.id} got marks $marks")
                    response.add(StudentAverageResultResponse(
                            id = student.id,
                            firstName = student.firstName,
                            lastName = student.lastName,
                            className = student.studentClass?.name,
                            averageGrade = marks.round().toString()
                    ))
                }
            }
        }

        if(response.isEmpty()) {
            return ResponseEntity.ok(Response(
                    status = USER_NOT_FOUND,
                    message = "No users found "
            ))
        }

        return ResponseEntity.ok(Response(
                status = SUCCESS,
                message = "All users",
                data = response
        ))
    }

    override suspend fun findAllAssignedStudents(): ResponseEntity<Response<List<UserResponse>>> {
        val result = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            val predicateRole = builder.equal(root.get<Int>(UserEntity_.ROLE),UserRoles.STUDENT.role)
            val predicateClass = builder.isNotNull(root.get(UserEntity_.studentClass))
            val condition = builder.and(predicateRole,predicateClass)
            query.select(root).where(condition)
        }.resultList

        return if(result.isNotEmpty()) {
            ResponseEntity.ok(Response(
                    status = SUCCESS,
                    message = "Students found",
                    data = result.mapToUserResponse()
            ))
        } else {
            ResponseEntity.ok(Response(
                    status = USER_NOT_FOUND,
                    message = "No users created "
            ))
        }
    }

    override suspend fun findAllDessignedStudents(): ResponseEntity<Response<List<UserResponse>>> {
        val result = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            val predicateRole = builder.equal(root.get<Int>(UserEntity_.ROLE),UserRoles.STUDENT.role)
            val predicateClass = builder.isNull(root.get(UserEntity_.studentClass))
            val condition = builder.and(predicateRole,predicateClass)
            query.select(root).where(condition)
        }.resultList

        return if(result.isNotEmpty()) {
            ResponseEntity.ok(Response(
                    status = SUCCESS,
                    message = "Students found",
                    data = result.mapToUserResponse()
            ))
        } else {
            ResponseEntity.ok(Response(
                    status = USER_NOT_FOUND,
                    message = "No users created "
            ))
        }
    }

    override suspend fun findById(id: Int): ResponseEntity<Response<UserResponse>> {
        val result = entityManager.find(UserEntity::class.java,id) ?:
                return ResponseEntity.ok(Response(
                        status = USER_NOT_FOUND,
                        message = "No users created"
                ))
            return ResponseEntity.ok(Response(
                    status = SUCCESS,
                    message = "User found",
                    data = result.mapToUserResponse()
            ))
    }

    override fun findByUsername(username: String): ResponseEntity<Response<UserResponse>> {
        val result = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            val predicateForUsername = builder.equal(root.get(UserEntity_.username),username)
            query.select(root).where(predicateForUsername)
        }.resultList.firstOrNull()
                ?:  return ResponseEntity.ok(Response(
                        status = USER_NOT_FOUND,
                        message = "User not found"
                ))

        return ResponseEntity.ok(Response(
                status = SUCCESS,
                message = "User found",
                data = result.mapToUserResponse()
        ))
    }

    override suspend fun findByRole(role: String): ResponseEntity<Response<List<UserResponse>>> {
        val result = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            query.select(root).where(builder.equal(root.get<Int>(UserEntity_.ROLE),role.mapRoleToInt()))
        }.resultList

        if(result.isNullOrEmpty()) {
            return ResponseEntity.ok(Response(
                    status = USER_NOT_FOUND,
                    message = "No users created for this role"
            ))
        }

        return ResponseEntity.ok(Response(
                status = SUCCESS,
                message = "Users found",
                data = result.mapToUserResponse()
        ))
    }


    @Transactional
    override suspend fun insert(usersRequest: UsersRequest): ResponseEntity<Response<UserResponse>> {
        val userEntity = usersRequest.mapToUserEntity(passwordEncoder.encode(usersRequest.password))

        val result = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            query.select(root).where(builder.equal(root.get<String>(UserEntity_.USERNAME), userEntity.username.toLowerCase()))
        }.resultList

        return if (result.isEmpty()) {
            entityManager.persist(userEntity)
            val location = URI.create(java.lang.String.format("/user/%s", userEntity.id))
            ResponseEntity.created(location).body(Response(
                    status = SUCCESS,
                    message = "User created",
                    data = userEntity.mapToUserResponse()
            ))
        } else {
            ResponseEntity.ok(Response(
                    status = USER_ALREADY_EXIST,
                    message = "User with this username already exist"
            ))
        }
    }

    @Transactional
    override suspend fun login(userLoginRequest: UserLoginRequest): ResponseEntity<Response<UserResponse>> {
        val result = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            query.select(root).where(builder.equal(root.get<String>(UserEntity_.USERNAME), userLoginRequest.username?.toLowerCase()))
        }.resultList
        return result.firstOrNull()?.let {
            if (passwordEncoder.matches(userLoginRequest.password, it.password)) {
                it.token = generateNewToken()
                entityManager.merge(it)
                ResponseEntity.ok(Response(
                        status = SUCCESS,
                        message = "Login success",
                        data = it.mapToUserResponse()
                ))
            } else {
                ResponseEntity.ok(Response(
                        status = INVALID_PASSWORD,
                        message = "Password not valid"
                ))
            }
        } ?: run {
            ResponseEntity.ok(Response(
                    status = USER_NOT_FOUND,
                    message = "User name invalid or does not exist"
            ))
        }
    }

    @Transactional
    override suspend fun update(userUpdateRequest: UserUpdateRequest): ResponseEntity<Response<UserResponse>> {
        val result = entityManager.find(UserEntity::class.java,userUpdateRequest.id)
                ?: return ResponseEntity.ok(Response(
                        status = USER_NOT_FOUND,
                        message = "User profile unable to update",
                ))

        val updatedResult = result.apply {
            this.firstName = userUpdateRequest.firstName.toString()
            this.lastName = userUpdateRequest.lastName.toString()
            this.password = if(!passwordEncoder.matches(userUpdateRequest.password,this.password)) passwordEncoder.encode(userUpdateRequest.password) else this.password
            this.username = userUpdateRequest.username?.replaceSpaceWithDot().toString()
        }

        entityManager.merge(updatedResult)
        return ResponseEntity.ok(Response(
                status = SUCCESS,
                message = "User profile updated",
                data = updatedResult.mapToUserResponse()
        ))
    }

    @Transactional
    override suspend fun delete(deleteRequest: UserDeleteRequest): ResponseEntity<Response<Unit>> {

        entityManager.find(UserEntity::class.java,deleteRequest.userId)
                ?: return ResponseEntity.ok(Response(
                        status = ACCOUNT_ALREADY_DELETED,
                        message = "Admin not found"
                ))

        val user = entityManager.find(UserEntity::class.java,deleteRequest.deleteId)
        if(user.role.isTeacher()) {
            if(user.studentSubjects?.any { it.archived == SubjectState.UNARCHIVED.value } == true) {
                return ResponseEntity.ok(Response(
                        status = TEACHER_NOT_DELETED,
                        message = "Teacher has active subjects. Cannot be deleted!"
                ))
            }
        }

        if(user.role.isStudent()) {
            if(!user.studentSubjects.isNullOrEmpty()) {
                user.removeSubjectsFromStudent()
            }
            println("removed student")
            user.studentClass = null
        }

        entityManager.remove(user)
        return ResponseEntity.ok(Response(
                status = SUCCESS,
                message = "User delete successfully"
        ))
    }

    @Transactional
    override suspend fun logout(id: Int): ResponseEntity<Response<Unit>> {
        val user = entityManager.find(UserEntity::class.java,id)
                ?: return ResponseEntity.ok(Response(
                        status = ERROR,
                        message = "User unable to logout"
                ))
        user.token = null
        entityManager.merge(user)

        return ResponseEntity.ok(Response(
                status = SUCCESS,
                message = "Logout successful"
        ))
    }

    fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()
}