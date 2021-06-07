package com.school.system.grading.datasource.impl

import com.school.system.grading.datasource.UserClassDataSource
import com.school.system.grading.datasource.entities.UserEntity
import com.school.system.grading.datasource.entities.UserEntity_
import com.school.system.grading.datasource.extensions.withCriteriaBuilder
import com.school.system.grading.datasource.mapper.mapToUserClassEntity
import com.school.system.grading.datasource.mapper.mapToUserClassResponse
import com.school.system.grading.entity.*
import com.school.system.grading.entity.user.UserRoles
import com.school.system.grading.entity.userclass.response.UserClassCreateResponse
import com.school.system.grading.entity.userclass.request.UserClassCreate
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
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
    override fun create(userClassCreate: UserClassCreate): ResponseEntity<Response<UserClassCreateResponse>> {
        val result = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            query.select(root).where(builder.equal(root.get<Int>(UserEntity_.id), userClassCreate.id))
        }.resultList

        return if (result.isNotEmpty()) {
            val userEntity = result.first()
            if(userEntity.role == UserRoles.ADMIN.role) {
                val userCreate = userClassCreate.mapToUserClassEntity()
                entityManager.persist(userCreate)
                val location = URI.create(String.format("/class/%s", userClassCreate.className))
                ResponseEntity.created(location).body(Response(
                        status = CREATED,
                        message = "Class is created",
                        data = userCreate.mapToUserClassResponse()
                ))
            } else {
                ResponseEntity.ok().body(Response(
                        status = UNAUTHORIZED_USER,
                        message = "User not authorized to create class"
                ))
            }

        } else {
            ResponseEntity.ok().body(Response(
                    status = USER_NOT_FOUND,
                    message = "User not found"
            ))
        }
    }
}