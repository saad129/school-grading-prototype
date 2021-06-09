package com.school.system.grading.datasource.impl

import com.school.system.grading.datasource.UserClassDataSource
import com.school.system.grading.datasource.entities.UserClassEntity
import com.school.system.grading.datasource.entities.UserClassEntity_
import com.school.system.grading.datasource.entities.UserEntity
import com.school.system.grading.datasource.entities.UserEntity_
import com.school.system.grading.datasource.extensions.withCriteriaBuilder
import com.school.system.grading.datasource.mapper.mapToUserClassEntity
import com.school.system.grading.datasource.mapper.mapToUserClassResponse
import com.school.system.grading.entity.*
import com.school.system.grading.entity.user.isAdmin
import com.school.system.grading.entity.userclass.response.UserClassResponse
import com.school.system.grading.entity.userclass.request.UserClassCreate
import com.school.system.grading.extensions.replaceSpaceWithDash
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import java.net.URI
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/7/21
 **/

@Repository
class UserClassDataSourceImpl(
        @PersistenceContext
        private val entityManager: EntityManager,
) : UserClassDataSource {

    @Transactional
    override suspend fun create(userClassCreate: UserClassCreate): ResponseEntity<Response<UserClassResponse>> {
        val result = entityManager.find(UserEntity::class.java,userClassCreate.id)
                ?: return ResponseEntity.ok().body(Response(
                        status = USER_NOT_FOUND,
                        message = "User not found"
                ))

        val classResult = entityManager.withCriteriaBuilder<UserClassEntity> { builder, query, root ->
            query.select(root).where(builder.like(builder.lower(root.get(UserClassEntity_.NAME)), "%${userClassCreate.className?.toLowerCase()?.trim()}%"))
        }.resultList

        if(classResult.isNotEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = CLASS_ALREADY_EXIST,
                    message = "Class already exists"
            ))
        }

        if(!result.role.isAdmin()) {
            return ResponseEntity.ok().body(Response(
                    status = UNAUTHORIZED_USER,
                    message = "User not authorized to create class"
            ))
        }

        val userClass = userClassCreate.mapToUserClassEntity()
        entityManager.persist(userClass)
        val location = URI.create(String.format("/class/%s", userClassCreate.className?.replaceSpaceWithDash()))
        return ResponseEntity.created(location).body(Response(
                status = CREATED,
                message = "Class is created",
                data = userClass.mapToUserClassResponse()
        ))
    }

    override suspend fun findAll(): ResponseEntity<Response<List<UserClassResponse>>> {
        val classResult = entityManager.withCriteriaBuilder<UserClassEntity> { builder, query, root ->
            query.select(root)
        }.resultList

        classResult.takeIf { it.isNotEmpty() }?.let {
            return ResponseEntity.ok().body(Response(
                    status = SUCCESS,
                    message = "Total classes",
                    data = it.mapToUserClassResponse()
            ))
        } ?: run {
            return ResponseEntity.ok().body(Response(
                    status = NO_CONTENT,
                    message = "No classes found"
            ))
        }
    }

    override suspend fun findById(id: Int): ResponseEntity<Response<UserClassResponse>> {
        val classResult = entityManager.find(UserClassEntity::class.java,id)

        classResult?.let {
            return ResponseEntity.ok().body(Response(
                    status = SUCCESS,
                    message = "Class found",
                    data = it.mapToUserClassResponse()
            ))
        } ?: run {
            return ResponseEntity.ok().body(Response(
                    status = NO_CONTENT,
                    message = "No class found"
            ))
        }
    }
}