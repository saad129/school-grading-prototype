package com.school.system.grading.datasource.impl

import com.school.system.grading.datasource.UserSubjectDataSource
import com.school.system.grading.datasource.entities.*
import com.school.system.grading.datasource.extensions.withCriteriaBuilder
import com.school.system.grading.datasource.mapper.*
import com.school.system.grading.entity.*
import com.school.system.grading.entity.subject.request.UserSubject
import com.school.system.grading.entity.subject.response.UserSubjectResponse
import com.school.system.grading.entity.user.isTeacher
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import java.net.URI
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.Tuple
import javax.persistence.criteria.*
import javax.transaction.Transactional

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/

@Repository
class UserSubjectDataSourceImpl(
        @PersistenceContext
        private val entityManager: EntityManager,
): UserSubjectDataSource {

    override suspend fun findAll(): ResponseEntity<Response<List<UserSubjectResponse>>> {
        val result = entityManager.withCriteriaBuilder<UserSubjectEntity> { builder, query, root ->
            query.select(root)
        }.resultList

         result.takeIf { it.isNotEmpty() }?.let {
             return ResponseEntity.ok().body(Response(
                    status = SUCCESS,
                    message = "Total subjects",
                    data = it.mapToUserSubjectResponse()
            ))
        } ?:run {
             return ResponseEntity.ok().body(Response(
                    status = NO_CONTENT,
                    message = "Subjects not found"
            ))
        }
    }

    override suspend fun findById(id: Int): ResponseEntity<Response<UserSubjectResponse>> {
        val result = entityManager.find(UserSubjectEntity::class.java,id)
                ?: return ResponseEntity.ok().body(Response(
                        status = NO_CONTENT,
                        message = "Subjects not found"
                ))

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Subject found",
                data = result.mapToUserSubjectResponse()
        ))
    }

    @Transactional
    override suspend fun create(userSubject: UserSubject): ResponseEntity<Response<UserSubjectResponse>> {
        val userResult = entityManager.find(UserEntity::class.java,userSubject.teacherId)
                ?: return ResponseEntity.ok(Response(
                        status = NO_CONTENT,
                        message = "No data found for teacher"
                ))
        val classResult = entityManager.find(UserClassEntity::class.java,userSubject.classId)
                ?: return ResponseEntity.ok(Response(
                        status = NO_CONTENT,
                        message = "No data found for class."
                ))

        val subjectResult = entityManager.withCriteriaBuilder<UserSubjectEntity> { builder, query, root ->
            query.select(root).where(builder.equal(root.get<String>(UserSubjectEntity_.NAME), makeUniqueSubjectName(classResult.name,userSubject.subjectName)))
        }.resultList

        if(subjectResult.isNotEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = SUBJECT_ALREADY_EXIST,
                    message = "Subject already exists and has an assigned teacher"
            ))
        }


        if(!userResult.role.isTeacher()) {
            return ResponseEntity.ok(Response(
                    status = UNAUTHORIZED_USER,
                    message = "Only teacher can be assigned for subjects"
            ))
        }

        val classSet = userResult.userClassEntity?.toMutableSet()
        val subjectSet = userResult.userSubjectEntity?.toMutableSet()
        classSet?.add(classResult)
        val userSubjectEntity = userSubject.mapToUserSubjectEntity(classResult.name)
        subjectSet?.add(userSubjectEntity)
        userResult.userClassEntity = classSet
        userResult.userSubjectEntity = subjectSet
        val subjectSetForClass = classResult.userSubjectEntity?.toMutableSet()
        subjectSetForClass?.add(userSubjectEntity)
        classResult.userSubjectEntity  = subjectSetForClass
        entityManager.merge(userResult)
        entityManager.merge(classResult)


        val subjectResponse = userResult.userSubjectEntity?.maxByOrNull { it.id!! } ?:run {
            return ResponseEntity.unprocessableEntity().body(Response(
                    status = ERROR,
                    message = "Something went wrong. Try again"
            ))
        }

        val location = URI.create(String.format("/subject/%s", subjectResponse.id))
        return ResponseEntity.created(location).body(Response(
                status = CREATED,
                message = "Subject created",
                data = subjectResponse.mapToUserSubjectResponse()
        ))
    }

    override suspend fun test(userSubject: UserSubject): ResponseEntity<Response<UserSubjectResponse>> {
        val result = entityManager.createNativeQuery("SELECT * FROM users_class FULL OUTER JOIN users_subject ON users_class.id=users_subject.class_id").resultList
        val builder = entityManager.criteriaBuilder
        val query = builder.createTupleQuery()
        val root = query.from(entityManager.metamodel.entity(UserClassEntity::class.java))
        val join: SetJoin<UserClassEntity,UserSubjectEntity> = root.join(UserClassEntity_.userSubjectEntity,JoinType.LEFT)
        query.multiselect(root.get(UserClassEntity_.name),join.get(UserSubjectEntity_.id))
        join.on(builder.equal(root.get(UserClassEntity_.id),1))
        val list:List<Tuple> = entityManager.createQuery(query).resultList
        list.forEach {
            println(it.get(0,String::class.java))
            println(it.get(1,String::class.java))
        }
        return ResponseEntity.ok(Response(
                status = UNAUTHORIZED_USER,
                message = "Only teacher can be assigned for subjects"
        ))
    }
}