package com.school.system.grading.datasource.impl

import com.school.system.grading.datasource.UserSubjectDataSource
import com.school.system.grading.datasource.entities.*
import com.school.system.grading.datasource.extensions.withCountCriteriaBuilder
import com.school.system.grading.datasource.extensions.withCriteriaBuilder
import com.school.system.grading.datasource.extensions.withJoinCriteriaBuilder
import com.school.system.grading.datasource.mapper.*
import com.school.system.grading.entity.*
import com.school.system.grading.entity.subject.SubjectState
import com.school.system.grading.entity.subject.isNotArchived
import com.school.system.grading.entity.subject.request.ArchiveSubjectRequest
import com.school.system.grading.entity.subject.request.RemoveSubjectRequest
import com.school.system.grading.entity.subject.request.UserSubjectRequest
import com.school.system.grading.entity.subject.request.UserSubjectUpdateRequest
import com.school.system.grading.entity.subject.response.ArchivedSubjectResponse
import com.school.system.grading.entity.subject.response.StudentAverageSubjectResponse
import com.school.system.grading.entity.subject.response.UserSubjectResponse
import com.school.system.grading.entity.user.UserRoles
import com.school.system.grading.entity.user.isTeacher
import com.school.system.grading.extensions.round
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
) : UserSubjectDataSource {

    override suspend fun count(): ResponseEntity<Response<Long>> {
        val subjectCount = entityManager.withCountCriteriaBuilder<UserSubjectEntity> { builder, query, root ->
            query.select(builder.count(root))
        }

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Total subjects Count",
                data = subjectCount
        ))
    }

    override suspend fun findAll(): ResponseEntity<Response<List<UserSubjectResponse>>> {
        val result = entityManager.withCriteriaBuilder<UserSubjectEntity> { builder, query, root ->
            val predicateForState = builder.equal(root.get(UserSubjectEntity_.archived), SubjectState.UNARCHIVED.value)
            query.select(root).where(predicateForState)
        }.resultList

        if (result.isNullOrEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = NO_CONTENT,
                    message = "Subjects not found"
            ))
        }

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Total subjects",
                data = result.mapToUserResponse()
        ))
    }

    override suspend fun findAllArchived(): ResponseEntity<Response<List<ArchivedSubjectResponse>>> {
        val result = entityManager.withCriteriaBuilder<UserSubjectEntity> { builder, query, root ->
            val predicateForState = builder.equal(root.get(UserSubjectEntity_.archived), SubjectState.ARCHIVED.value)
            query.select(root).where(predicateForState)
        }.resultList

        if (result.isNullOrEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = NO_CONTENT,
                    message = "Subjects not found"
            ))
        }

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Total subjects",
                data = result.mapToSubjectArchivedResponse()
        ))
    }

    override suspend fun findByTeacherId(id: Int): ResponseEntity<Response<List<UserSubjectResponse>>> {
        val result = entityManager.withJoinCriteriaBuilder<UserEntity,UserSubjectEntity>(UserEntity_.teacherSubjectEntity) {
            builder, query, root,join ->
            val predicateForState = builder.equal(join.get(UserSubjectEntity_.archived), SubjectState.UNARCHIVED.value)
            val predicateForId = builder.equal(root.get(UserEntity_.id), id)
            val condition = builder.and(predicateForId,predicateForState)
            query.where(condition)
        }.resultList

        if (result.isNullOrEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = NO_CONTENT,
                    message = "Subjects not found"
            ))
        }

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Total subjects",
                data = result.mapToUserSubjectResponse()
        ))
    }

    override suspend fun findById(id: Int): ResponseEntity<Response<UserSubjectResponse>> {
        println("fetching by id $id")
        val result = entityManager.withCriteriaBuilder<UserSubjectEntity> { builder, query, root ->
            val predicateForId = builder.equal(root.get(UserSubjectEntity_.id), id)
            val predicateForState = builder.equal(root.get(UserSubjectEntity_.archived), SubjectState.UNARCHIVED.value)
            val predicateCondition = builder.and(predicateForId, predicateForState)
            query.select(root).where(predicateCondition)
        }.resultList.firstOrNull()
                ?: return ResponseEntity.ok().body(Response(
                        status = NO_CONTENT,
                        message = "Subject not found"
                ))

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Subject found",
                data = result.mapToUserResponse()
        ))
    }

    override suspend fun findByClassId(id: Int): ResponseEntity<Response<List<UserSubjectResponse>>> {
        val result = entityManager.withCriteriaBuilder<UserClassEntity> { builder, query, root ->
            val predicateForId = builder.equal(root.get(UserClassEntity_.id), id)
            query.select(root).where(predicateForId)
        }.resultList.firstOrNull()
                ?: return ResponseEntity.ok().body(Response(
                        status = NO_CONTENT,
                        message = "Invalid class"
                ))
        val unArchiveSubjects = result.userSubjectEntity?.filter { it.archived == SubjectState.UNARCHIVED.value }
        if (unArchiveSubjects.isNullOrEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = NO_CONTENT,
                    message = "Subjects not found for this class"
            ))
        }

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Subject found",
                data = unArchiveSubjects?.mapToUserResponse()
        ))
    }

    @Transactional
    override suspend fun update(updateSubjectRequest: UserSubjectUpdateRequest): ResponseEntity<Response<UserSubjectResponse>> {
        entityManager.find(UserEntity::class.java,updateSubjectRequest.userId)
                ?: return ResponseEntity.ok().body(Response(
                        status = USER_NOT_FOUND,
                        message = "User not found"
                ))
        val subjectResult = entityManager.withCriteriaBuilder<UserSubjectEntity> { builder, query, root ->
            val predicateForArchive = builder.equal(root.get(UserSubjectEntity_.archived),SubjectState.UNARCHIVED.value)
            val predicateForId = builder.equal(root.get(UserSubjectEntity_.id),updateSubjectRequest.subjectId)
            val condition = builder.and(predicateForArchive,predicateForId)
            query.select(root).where(condition)
        }.resultList.firstOrNull()
                ?: return ResponseEntity.ok().body(Response(
                        status = SUBJECT_NOT_FOUND,
                        message = "Subject not found"
                ))

        subjectResult.name = updateSubjectRequest.subjectName
        entityManager.merge(subjectResult)

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Subject updated",
                data = subjectResult.mapToUserResponse()
        ))
    }

    @Transactional
    override suspend fun create(userSubjectRequest: UserSubjectRequest): ResponseEntity<Response<UserSubjectResponse>> {
        entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            val predicateRole = builder.equal(root.get(UserEntity_.role), UserRoles.ADMIN.role)
            val predicateId = builder.equal(root.get(UserEntity_.id), userSubjectRequest.userId)
            val condition = builder.and(predicateId, predicateRole)
            query.select(root).where(condition)
        }.resultList.firstOrNull() ?: return ResponseEntity.ok(Response(
                status = ERROR,
                message = "Only admin can create subjects"
        ))

        val teacherResult = entityManager.find(UserEntity::class.java, userSubjectRequest.teacherId)
                ?: return ResponseEntity.ok(Response(
                        status = NO_CONTENT,
                        message = "No data found for teacher"
                ))

        if (!teacherResult.role.isTeacher()) {
            return ResponseEntity.ok(Response(
                    status = UNAUTHORIZED_USER,
                    message = "Only teacher can be assigned for subjects"
            ))
        }

        val classResult = entityManager.find(UserClassEntity::class.java, userSubjectRequest.classId)
                ?: return ResponseEntity.ok(Response(
                        status = NO_CONTENT,
                        message = "No data found for class."
                ))

        if (classResult.userSubjectEntity
                        ?.filter { it.archived == SubjectState.UNARCHIVED.value }
                        ?.any { it.name == userSubjectRequest.subjectName?.toLowerCase() }!!) {
            return ResponseEntity.ok().body(Response(
                    status = SUBJECT_ALREADY_EXIST,
                    message = "Subject already exists and has an assigned teacher"
            ))
        }

        val userSubjectEntity = userSubjectRequest.mapToUserSubjectEntity()
        teacherResult.addTeacherToSubject(userSubjectEntity)
        classResult.addClassToSubject(userSubjectEntity)
        entityManager.persist(userSubjectEntity)

        val location = URI.create(String.format("/subject/%s", userSubjectEntity.id))
        return ResponseEntity.created(location).body(Response(
                status = CREATED,
                message = "Subject created",
                data = userSubjectEntity.mapToUserResponse(classResult, teacherResult)
        ))
    }

    @Transactional
    override suspend fun deleteByIds(removeSubjectRequest: RemoveSubjectRequest): ResponseEntity<Response<Unit>> {
        entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            val predicateRole = builder.equal(root.get(UserEntity_.role), UserRoles.ADMIN.role)
            val predicateId = builder.equal(root.get(UserEntity_.id), removeSubjectRequest.userId)
            val condition = builder.and(predicateId, predicateRole)
            query.select(root).where(condition)
        }.resultList.firstOrNull() ?: return ResponseEntity.ok(Response(
                status = ERROR,
                message = "Only admin can archive subjects"
        ))

        var count = 0
        removeSubjectRequest.subjectIds?.forEach {
            val subject = entityManager.find(UserSubjectEntity::class.java,it)
            if(subject.archived.isNotArchived() && subject.userTest.isNullOrEmpty()) {
               val studentResult = entityManager.withJoinCriteriaBuilder<UserEntity,UserSubjectEntity>(UserEntity_.studentSubjects) {
                    builder, query, root,join ->
                    val predicateForRole = builder.equal(root.get(UserEntity_.role),UserRoles.STUDENT.role)
                    val predicateForSubjectId = builder.equal(join.get(UserSubjectEntity_.id),subject.id)
                    query.where(builder.and(predicateForRole,predicateForSubjectId))
                }.resultList
                if(!studentResult.isNullOrEmpty()) {
                    studentResult.forEach {
                        val student = it.get(0,UserEntity::class.java)
                        student.removeSubjectsFromStudent()
                        val studentResultForClass = entityManager.find(UserEntity::class.java,student.id)
                        if(studentResultForClass != null && studentResultForClass.studentSubjects.isNullOrEmpty()) {
                            studentResultForClass.studentClass = null
                        }
                    }
                }
                count += 1
                entityManager.remove(subject)
            }
        }

        if(count == 0) {
            return ResponseEntity.ok(Response(
                    status = SUBJECT_CANNOT_DELETE,
                    message = "Subject cannot be deleted. It can be archived"
            ))
        }

        return ResponseEntity.ok(Response(
                status = SUCCESS,
                message = "$count subjects deleted"
        ))
    }

    @Transactional
    override suspend fun archive(archiveSubjectRequest: ArchiveSubjectRequest): ResponseEntity<Response<Unit>> {
        entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            val predicateRole = builder.equal(root.get(UserEntity_.role), UserRoles.ADMIN.role)
            val predicateId = builder.equal(root.get(UserEntity_.id), archiveSubjectRequest.userId)
            val condition = builder.and(predicateId, predicateRole)
            query.select(root).where(condition)
        }.resultList.firstOrNull() ?: return ResponseEntity.ok(Response(
                status = ERROR,
                message = "Only admin can archive subjects"
        ))

        val subjectResult = entityManager.find(UserSubjectEntity::class.java,archiveSubjectRequest.subjectId)
                ?: return ResponseEntity.ok().body(Response(
                        status = SUBJECT_NOT_FOUND,
                        message = "Subjects not found!"
                ))

        if(subjectResult.userTest.isNullOrEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = SUBJECT_CANNOT_ARCHIVE,
                    message = "Subject unable to archive. It does not have tests. Please remove if you want to!"
            ))
        }
        subjectResult.archived = SubjectState.ARCHIVED.value
        entityManager.merge(subjectResult)
        subjectResult.classes = null
        subjectResult.removeTeacherFromArchiveSubject()
        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Subject is archived!"
        ))
    }

    override suspend fun getAverageSubjectResult(id: Int): ResponseEntity<Response<List<StudentAverageSubjectResponse>>> {
        val subjectResult = entityManager.withJoinCriteriaBuilder<UserEntity,UserSubjectEntity>(UserEntity_.studentSubjects) {
            builder, query, root, join ->
            val predicateForId = builder.equal(root.get(UserEntity_.id),id)
            query.where(predicateForId)
        }.resultList

        if(subjectResult.isNullOrEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = SUBJECT_NOT_FOUND,
                    message = "Subjects not found!"
            ))
        }

        val response = mutableListOf<StudentAverageSubjectResponse>()
        subjectResult.forEach {
            val subject = it.get(1,UserSubjectEntity::class.java)
            if(!subject.userTest.isNullOrEmpty()) {
                val averageGrade = mutableListOf<Double>()
                subject.userTest?.forEach {
                    val grade = it.tests?.filter {
                        it.students?.id == id
                    }?.mapNotNull {
                        it.marks?.toDouble()
                    }?.average()
                    if (grade != null) {
                        averageGrade.add(grade)
                    }
                }
                response.add(StudentAverageSubjectResponse(
                        id = subject?.id,
                        subjectName = subject?.name,
                        averageGrade = averageGrade.average().round().toString()
                ))
                println("next subject")

            } else {
                response.add(StudentAverageSubjectResponse(
                        id = subject.id,
                        subjectName = subject?.name,
                        averageGrade = "0.0"
                ))
            }
        }

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Subjects found!",
                data = response
        ))
    }

    override suspend fun test(userSubjectRequest: UserSubjectRequest): ResponseEntity<Response<UserSubjectResponse>> {
        val result = entityManager.createNativeQuery("SELECT * FROM users_class FULL OUTER JOIN users_subject ON users_class.id=users_subject.class_id").resultList
        val builder = entityManager.criteriaBuilder
        val query = builder.createTupleQuery()
        val root = query.from(entityManager.metamodel.entity(UserClassEntity::class.java))
        val join: ListJoin<UserClassEntity, UserSubjectEntity> = root.join(UserClassEntity_.userSubjectEntity, JoinType.LEFT)
        query.multiselect(root.get(UserClassEntity_.name), join.get(UserSubjectEntity_.id))
        join.on(builder.equal(root.get(UserClassEntity_.id), 1))
        val list: List<Tuple> = entityManager.createQuery(query).resultList
        list.forEach {
            println(it.get(0, String::class.java))
            println(it.get(1, String::class.java))
        }
        return ResponseEntity.ok(Response(
                status = UNAUTHORIZED_USER,
                message = "Only teacher can be assigned for subjects"
        ))
    }

    override suspend fun testsSubject() {
        val result = entityManager.withJoinCriteriaBuilder<UserTestEntity,TestGradeEntity>(UserTestEntity_.tests) {
            builder, query, root, join ->
            val testId = builder.equal(root.get(UserTestEntity_.id), 10)
            val condition = builder.and(testId)
            query.where(condition)
        }.resultList

        val student1Result = result.firstOrNull { it.get(1,TestGradeEntity::class.java).students?.id == 1001 }
        val student2Result = result.firstOrNull { it.get(1,TestGradeEntity::class.java).students?.id == 1003 }

        println("result user: ${student1Result?.get(0,UserTestEntity::class.java)}")
        println("result user: ${student1Result?.get(1,TestGradeEntity::class.java)?.marks}")

        println("result user: ${student2Result?.get(0,UserTestEntity::class.java)}")
        println("result user: ${student2Result?.get(1,TestGradeEntity::class.java)?.marks}")

        println("result : $result")
    }
}