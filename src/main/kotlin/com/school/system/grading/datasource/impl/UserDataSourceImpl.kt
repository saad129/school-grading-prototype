package com.school.system.grading.datasource.impl

import com.school.system.grading.datasource.UserDataSource
import com.school.system.grading.datasource.mapper.mapToUserEntity
import com.school.system.grading.datasource.mapper.mapToUserSubjectResponse
import com.school.system.grading.entity.*
import com.school.system.grading.datasource.entities.UserEntity
import com.school.system.grading.datasource.entities.UserEntity_
import com.school.system.grading.datasource.extensions.withCriteriaBuilder
import com.school.system.grading.entity.user.request.UserLogin
import com.school.system.grading.entity.user.request.UserUpdate
import com.school.system.grading.entity.user.request.Users
import com.school.system.grading.entity.user.response.UserResponse
import com.school.system.grading.extensions.generateNewToken
import com.school.system.grading.extensions.replaceSpaceWithDot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import java.net.URI
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional
import kotlin.coroutines.suspendCoroutine

@Repository
class UserDataSourceImpl(
        @PersistenceContext
        private val entityManager: EntityManager,
        @Autowired
        private val passwordEncoder: PasswordEncoder
) : UserDataSource {

    override suspend fun findAll(): ResponseEntity<Response<List<UserResponse>>> {
        val result = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            query.select(root)
        }.resultList

        return if(result.isNotEmpty()) {
            ResponseEntity.ok(Response(
                    status = SUCCESS,
                    message = "Users found",
                    data = result.mapToUserSubjectResponse()
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
                        message = "No users created "
                ))

            return ResponseEntity.ok(Response(
                    status = SUCCESS,
                    message = "Users found",
                    data = result.mapToUserSubjectResponse()
            ))
    }


    @Transactional
    override suspend fun insert(users: Users): ResponseEntity<Response<UserResponse>> {
        val userEntity = users.mapToUserEntity(passwordEncoder.encode(users.password))

        val result = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            query.select(root).where(builder.equal(root.get<String>(UserEntity_.USERNAME), userEntity.username.toLowerCase()))
        }.resultList

        return if (result.isEmpty()) {
            entityManager.persist(userEntity)
            val location = URI.create(java.lang.String.format("/user/%s", userEntity.id))
            ResponseEntity.created(location).body(Response(
                    status = SUCCESS,
                    message = "User created",
                    data = userEntity.mapToUserSubjectResponse()
            ))
        } else {
            ResponseEntity.ok(Response(
                    status = USER_ALREADY_EXIST,
                    message = "User with this username already exist"
            ))
        }
    }

    @Transactional
    override suspend fun login(userLogin: UserLogin): ResponseEntity<Response<UserResponse>> {
        val result = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            query.select(root).where(builder.equal(root.get<String>(UserEntity_.USERNAME), userLogin.username.toLowerCase()))
        }.resultList
        return result.firstOrNull()?.let {
            if (passwordEncoder.matches(userLogin.password, it.password)) {
                it.token = generateNewToken()
                entityManager.merge(it)
                ResponseEntity.ok(Response(
                        status = SUCCESS,
                        message = "Login success",
                        data = it.mapToUserSubjectResponse()
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
    override suspend fun update(userUpdate: UserUpdate): ResponseEntity<Response<UserResponse>> {
        val result = entityManager.find(UserEntity::class.java,userUpdate.id)
                ?: return ResponseEntity.ok(Response(
                        status = USER_NOT_FOUND,
                        message = "User profile unable to update",
                ))

        val updatedResult = result.apply {
            this.firstName = userUpdate.firstName
            this.lastName = userUpdate.lastName
            this.password = if(!passwordEncoder.matches(userUpdate.password,this.password)) passwordEncoder.encode(userUpdate.password) else this.password
            this.username = userUpdate.username.replaceSpaceWithDot()
        }

        entityManager.merge(updatedResult)
        return ResponseEntity.ok(Response(
                status = SUCCESS,
                message = "User profile updated",
                data = updatedResult.mapToUserSubjectResponse()
        ))
    }

}