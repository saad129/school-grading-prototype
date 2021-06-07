package com.school.system.grading.datasource.impl

import com.school.system.grading.datasource.UserDataSource
import com.school.system.grading.datasource.mapper.mapToUserEntity
import com.school.system.grading.datasource.mapper.mapToUserResponse
import com.school.system.grading.entity.*
import com.school.system.grading.datasource.entities.UserEntity
import com.school.system.grading.datasource.entities.UserEntity_
import com.school.system.grading.datasource.extensions.withCriteriaBuilder
import com.school.system.grading.entity.user.request.UserLogin
import com.school.system.grading.entity.user.request.UserUpdate
import com.school.system.grading.entity.user.request.Users
import com.school.system.grading.entity.user.response.UserResponse
import com.school.system.grading.extensions.generateNewToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import java.net.URI
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Repository
class UserDataSourceImpl(
        @PersistenceContext
        private val entityManager: EntityManager,
        @Autowired
        private val passwordEncoder: PasswordEncoder
) : UserDataSource {

    override fun findAll(): ResponseEntity<Response<List<UserResponse>>> {
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

    override fun findById(id: Int): ResponseEntity<Response<UserResponse>> {
        val result = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            query.select(root).where(builder.equal(root.get<Int>(UserEntity_.id), id))
        }.resultList

        return if(result.isNotEmpty()) {
            ResponseEntity.ok(Response(
                    status = SUCCESS,
                    message = "Users found",
                    data = result.first().mapToUserResponse()
            ))
        } else {
            ResponseEntity.ok(Response(
                    status = USER_NOT_FOUND,
                    message = "No users created "
            ))
        }
    }


    @Transactional
    override fun insert(users: Users): ResponseEntity<Response<UserResponse>> {
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
    override fun login(userLogin: UserLogin): ResponseEntity<Response<UserResponse>> {
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
    override fun update(userUpdate: UserUpdate): ResponseEntity<Response<UserResponse>> {
        val result = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            query.select(root).where(builder.equal(root.get<Int>(UserEntity_.id), userUpdate.id))
        }.resultList
         result.takeIf { it.isNotEmpty() }?.first()?.apply {
            this.firstName = userUpdate.firstName
            this.lastName = userUpdate.lastName
            this.password = if(!passwordEncoder.matches(userUpdate.password,this.password)) passwordEncoder.encode(userUpdate.password) else this.password
            this.username = userUpdate.username.replace(" ",".")

            entityManager.merge(this)
            return ResponseEntity.ok(Response(
                    status = SUCCESS,
                    message = "User profile updated",
                    data = this.mapToUserResponse()
            ))
        } ?:run {
            return ResponseEntity.ok(Response(
                    status = USER_NOT_FOUND,
                    message = "User profile unable to update",
            ))
        }

        return ResponseEntity.ok(Response(
                status = USER_NOT_FOUND,
                message = "User profile unable to update",
        ))
    }

}