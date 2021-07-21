package com.school.system.grading.datasource.impl

import com.school.system.grading.datasource.UserClassDataSource
import com.school.system.grading.datasource.UserSubjectDataSource
import com.school.system.grading.datasource.entities.*
import com.school.system.grading.datasource.extensions.withCountCriteriaBuilder
import com.school.system.grading.datasource.extensions.withCriteriaBuilder
import com.school.system.grading.datasource.extensions.withJoinCriteriaBuilder
import com.school.system.grading.datasource.mapper.mapToUserClassEntity
import com.school.system.grading.datasource.mapper.mapToUserClassResponse
import com.school.system.grading.datasource.mapper.mapTupleToUserClassResponse
import com.school.system.grading.entity.*
import com.school.system.grading.entity.subject.SubjectState
import com.school.system.grading.entity.subject.isNotArchived
import com.school.system.grading.entity.subject.request.ArchiveSubjectRequest
import com.school.system.grading.entity.user.UserRoles
import com.school.system.grading.entity.user.isAdmin
import com.school.system.grading.entity.userclass.request.*
import com.school.system.grading.entity.userclass.response.UserClassResponse
import com.school.system.grading.extensions.replaceSpaceWithDash
import org.hibernate.Session
import org.springframework.beans.factory.annotation.Autowired
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
        @Autowired
        private val userSubjectDataSource: UserSubjectDataSource
) : UserClassDataSource {

    override suspend fun count(): ResponseEntity<Response<Long>> {
        val classCount = entityManager.withCountCriteriaBuilder<UserClassEntity> { builder, query, root ->
            query.select(builder.count(root))
        }

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Total classes Count",
                data = classCount
        ))
    }

    @Transactional
    override suspend fun create(userClassCreateRequest: UserClassCreateRequest): ResponseEntity<Response<UserClassResponse>> {
        val result = entityManager.find(UserEntity::class.java, userClassCreateRequest.id)
                ?: return ResponseEntity.ok().body(Response(
                        status = USER_NOT_FOUND,
                        message = "User not found"
                ))

        val classResult = entityManager.withCriteriaBuilder<UserClassEntity> { builder, query, root ->
            query.select(root).where(builder.like(builder.lower(root.get(UserClassEntity_.NAME)), "%${userClassCreateRequest.className?.toLowerCase()?.trim()}%"))
        }.resultList

        if (classResult.isNotEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = CLASS_ALREADY_EXIST,
                    message = "Class already exists"
            ))
        }

        if (!result.role.isAdmin()) {
            return ResponseEntity.ok().body(Response(
                    status = UNAUTHORIZED_USER,
                    message = "User not authorized to create class"
            ))
        }

        val userClass = userClassCreateRequest.mapToUserClassEntity()
        entityManager.persist(userClass)
        val location = URI.create(String.format("/class/%s", userClassCreateRequest.className?.replaceSpaceWithDash()))
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
        val classResult = entityManager.find(UserClassEntity::class.java, id)

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

    @Transactional
    override suspend fun deleteById(userDeleteClassRequest: UserDeleteClassRequest): ResponseEntity<Response<Unit>> {
        entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            val predicateForId = builder.equal(root.get(UserEntity_.id),userDeleteClassRequest.userId)
            val predicateForRole = builder.equal(root.get(UserEntity_.role),UserRoles.ADMIN.role)
            val predicateCondition = builder.and(predicateForId,predicateForRole)
            query.select(root).where(predicateCondition)
        }.resultList.firstOrNull()
                ?: return ResponseEntity.ok().body(Response(
                        status = USER_NOT_FOUND,
                        message = "Admin not logged in"
                ))

        entityManager.remove(entityManager.find(UserClassEntity::class.java,userDeleteClassRequest.classId))
        println("class deleted")
        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Class deleted!"
        ))
    }



    @Transactional
    override suspend fun archiveSubjectsAndTests(deleteClassRequest: UserDeleteClassRequest): AppResponseEntity<Unit> {
        val classResult = entityManager.withJoinCriteriaBuilder<UserClassEntity,UserSubjectEntity>(UserClassEntity_.userSubjectEntity) {
            builder, query, root,join ->
            val predicateForClassId = builder.equal(root.get(UserClassEntity_.id),deleteClassRequest.classId)
            query.where(predicateForClassId)
        }.resultList

        if(classResult.isNullOrEmpty()) {
            return AppResponseEntity.ERROR
        }

        classResult.forEach {
            val subject = it.get(1,UserSubjectEntity::class.java)
            if(subject != null && !subject.userTest.isNullOrEmpty()) {
                subject.archived = SubjectState.ARCHIVED.value
                subject.classes = null
                subject.removeTeacherFromArchiveSubject()
            } else if( subject != null && subject.userTest.isNullOrEmpty()) {
                println("init removing subjects..!")
                val tupleResult = entityManager.withJoinCriteriaBuilder<UserEntity,UserSubjectEntity>(UserEntity_.studentSubjects) {
                    builder, query, root,join ->
                    val predicateForClassId = builder.equal(join.get(UserSubjectEntity_.id),subject.id)
                    query.where(predicateForClassId)
                }.resultList
                if(!tupleResult.isNullOrEmpty()) {
                    println("result found for subjects..!")
                    tupleResult.forEach {
                        val student = it.get(0,UserEntity::class.java)
                        student.removeSubjectFromStudent(subject)
                        println("clear subjects from student..!")
                    }
                }
                println("removing subject ..!")
                entityManager.remove(subject)
            }
        }

        return AppResponseEntity.SUCCESS(Unit)
    }

    @Transactional
    override suspend fun deAssignedStudents(deleteClassRequest: UserDeleteClassRequest): AppResponseEntity<Unit> {
        val studentResult = entityManager.withJoinCriteriaBuilder<UserEntity,UserClassEntity>(UserEntity_.studentClass) {
            builder, query, root,join ->
            val predicateForClassId = builder.equal(join.get(UserClassEntity_.id),deleteClassRequest.classId)
            val predicateForRole = builder.equal(root.get(UserEntity_.role),UserRoles.STUDENT.role)
            query.where(builder.and(predicateForClassId,predicateForRole))
        }.resultList

        if(!studentResult.isNullOrEmpty()) {
            studentResult.forEach {
                val student = it.get(0,UserEntity::class.java)
                student.studentClass = null
                entityManager.merge(student)
            }
        }
        return AppResponseEntity.SUCCESS(Unit)
    }

    override suspend fun findAllValidClasses(): ResponseEntity<Response<List<UserClassResponse>>> {
        val classResult = entityManager.withJoinCriteriaBuilder<UserClassEntity,UserSubjectEntity>(UserClassEntity_.userSubjectEntity){
            builder, query, root,join ->
            val predicateForSubject = builder.isNotEmpty(root.get(UserClassEntity_.userSubjectEntity))
            val predicateForSubjectState = builder.equal(join.get(UserSubjectEntity_.archived),SubjectState.UNARCHIVED.value)
            val condition = builder.and(predicateForSubject,predicateForSubjectState)
            query.where(condition)
        }.resultList

        classResult.takeIf { it.isNotEmpty() }?.let {
            return ResponseEntity.ok().body(Response(
                    status = SUCCESS,
                    message = "Total classes",
                    data = it.mapTupleToUserClassResponse()
            ))
        } ?: run {
            return ResponseEntity.ok().body(Response(
                    status = NO_CONTENT,
                    message = "No classes found"
            ))
        }
    }

    @Transactional
    override suspend fun update(userClassUpdateRequest: UserClassUpdateRequest): ResponseEntity<Response<UserClassResponse>> {
       entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
           val predicateForId = builder.equal(root.get(UserEntity_.id),userClassUpdateRequest.id)
           val predicateForRole = builder.equal(root.get(UserEntity_.role),UserRoles.ADMIN.role)
           val predicateCondition = builder.and(predicateForId,predicateForRole)
           query.select(root).where(predicateCondition)
       }.resultList.firstOrNull()
               ?: return ResponseEntity.ok().body(Response(
                       status = USER_NOT_FOUND,
                       message = "Admin not logged in"
               ))

        val classResult = entityManager.find(UserClassEntity::class.java, userClassUpdateRequest.classId)
                ?: return ResponseEntity.ok().body(Response(
                        status = NO_CONTENT,
                        message = "No class found"
                ))

        classResult.name = userClassUpdateRequest.className?.trim()!!
        entityManager.merge(classResult)

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Class updated",
                data = classResult.mapToUserClassResponse()
        ))
    }

    @Transactional
    override suspend fun assignClass(userAssignClassRequest: UserAssignClassRequest): ResponseEntity<Response<Unit>> {
        entityManager.find(UserEntity::class.java,userAssignClassRequest.adminId).takeIf {
                    it.role == UserRoles.ADMIN.role
                } ?: return ResponseEntity.ok().body(Response(
                        status = USER_NOT_FOUND,
                        message = "User is not admin."
                ))

        val userResult = entityManager.unwrap(Session::class.java).byMultipleIds(UserEntity::class.java)
                .multiLoad(userAssignClassRequest.studentIds)
                .filterNotNull()
                .filter { it.role == UserRoles.STUDENT.role }

        if(userResult.isEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = USER_NOT_FOUND,
                    message = "Students not found."
            ))
        }

        val classResult = entityManager.find(UserClassEntity::class.java,userAssignClassRequest.classId)
                ?: return ResponseEntity.ok().body(Response(
                        status = CLASS_NOT_FOUND,
                        message = "Class not found"
                ))

        val subjectsForClass = classResult.userSubjectEntity?.filter { it.archived.isNotArchived() }

        if(subjectsForClass.isNullOrEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = SUBJECT_NOT_FOUND,
                    message = "Subjects not found. Add subjects first"
            ))
        }

        userResult.forEach { user ->
            user.studentClass = classResult
            val updatedUser = user.addStudentToClassSubjects(subjectsForClass)
            entityManager.merge(updatedUser)
        }

        removePreviousAssignedSubjectsFromClass(userAssignClassRequest.studentIds,classId = userAssignClassRequest.classId)

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Students assigned to class and respective subjects"
        ))
    }

    override suspend fun deAssignClass(userDeAssignRequest: UserDeAssignRequest): ResponseEntity<Response<Unit>> {
        val studentResult = entityManager.find(UserEntity::class.java,userDeAssignRequest.userId)
                ?: return ResponseEntity.ok().body(Response(
                        status = USER_NOT_FOUND,
                        message = "Students not found."
                ))

        studentResult.studentSubjects
                ?.filter { it.archived == SubjectState.UNARCHIVED.value }
                ?.filter { it.classes?.id == studentResult.studentClass?.id }
                ?.forEach {
                    studentResult.removeSubjectFromStudent(it)
                }

        studentResult.studentClass = null
        entityManager.merge(studentResult)
        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Student deAssigned from class."
        ))
    }

    override suspend fun checkIfStudentsAlreadyAssigned(userAssignClassRequest: UserAssignClassRequest): AppResponseEntity<List<UserEntity>> {
        val userResult = entityManager.unwrap(Session::class.java).byMultipleIds(UserEntity::class.java)
                .multiLoad(userAssignClassRequest.studentIds)
                .filterNotNull()
                .filter { it.role == UserRoles.STUDENT.role }
                .filter { it.studentClass != null && it.studentClass?.id == userAssignClassRequest.classId }

        if(userResult.isNullOrEmpty()) {
            return AppResponseEntity.ERROR
        }

        return AppResponseEntity.SUCCESS(data = userResult)
    }

    @Transactional
    override suspend fun removePreviousAssignedSubjectsFromClass(users: List<UserEntity>?) {
        users?.forEach {
            it.removeSubjectsFromStudent()
            println("class removed ${it.studentSubjects}")
        }
    }

    @Transactional
    override suspend fun removePreviousAssignedSubjectsFromClass(userId: List<Int>?, classId: Int?) {
        val userResult = entityManager.unwrap(Session::class.java).byMultipleIds(UserEntity::class.java)
                .multiLoad(userId)
                .filterNotNull()
                .filter { it.role == UserRoles.STUDENT.role }
                .filter { it.studentClass != null }

        if(!userResult.isNullOrEmpty()) {
            val subjectResult = entityManager.withJoinCriteriaBuilder<UserClassEntity,UserSubjectEntity>(UserClassEntity_.userSubjectEntity) {
                builder, query, root, join ->
                val predicateForId = builder.equal(root.get(UserClassEntity_.id),classId)
                val predicateForSubjectState = builder.equal(join.get(UserSubjectEntity_.archived),SubjectState.UNARCHIVED.value)
                val condition = builder.and(predicateForId,predicateForSubjectState)
                query.where(condition)
            }.resultList
            if(!subjectResult.isNullOrEmpty()) {
                userResult.forEach { user ->
                    user.studentSubjects?.filter {
                        it.id !in subjectResult.map {
                            it.get(1,UserSubjectEntity::class.java).id
                        } && it.archived == SubjectState.UNARCHIVED.value
                    }?.map {
                        user.removeSubjectFromStudent(it)
                    }

                }
            }
        }
    }
}