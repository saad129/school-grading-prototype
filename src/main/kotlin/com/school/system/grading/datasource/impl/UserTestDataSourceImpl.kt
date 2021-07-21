package com.school.system.grading.datasource.impl

import com.opencsv.bean.CsvToBeanBuilder
import com.school.system.grading.datasource.UserTestDataSource
import com.school.system.grading.datasource.entities.*
import com.school.system.grading.datasource.extensions.buildEmailMessage
import com.school.system.grading.datasource.extensions.withCriteriaBuilder
import com.school.system.grading.datasource.extensions.withJoinCriteriaBuilder
import com.school.system.grading.datasource.mapper.*
import com.school.system.grading.entity.*
import com.school.system.grading.entity.grade.response.TestGradeResponse
import com.school.system.grading.entity.subject.SubjectState
import com.school.system.grading.entity.subject.isArchived
import com.school.system.grading.entity.tests.request.TestCreateRequest
import com.school.system.grading.entity.tests.request.TestResultUpdateRequest
import com.school.system.grading.entity.tests.request.TestUpdateRequest
import com.school.system.grading.entity.tests.response.StudentTestResponse
import com.school.system.grading.entity.tests.response.TestCreateResponse
import com.school.system.grading.entity.tests.response.TestResultUpdateResponse
import com.school.system.grading.entity.tests.response.TestsResponse
import com.school.system.grading.entity.user.UserRoles
import com.school.system.grading.entity.user.isTeacher
import com.school.system.grading.manager.EmailManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.net.URI
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/9/21
 **/
@Repository
class UserTestDataSourceImpl(
        @PersistenceContext
        private val entityManager: EntityManager,
        private val emailManager: EmailManager
) : UserTestDataSource {


    override suspend fun findAll(): ResponseEntity<Response<List<TestsResponse>>> {
        val result = entityManager.withCriteriaBuilder<UserTestEntity> { builder, query, root ->
            query.select(root)
        }.resultList

        if (result.isNullOrEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = TEST_NOT_FOUND,
                    message = "No tests created yet"
            ))
        }

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "All tests",
                data = result.mapToTestResponse()
        ))
    }

    override suspend fun findBySubjectId(id: Int): ResponseEntity<Response<List<TestsResponse>>> {
        val subjectResult = entityManager.find(UserSubjectEntity::class.java, id)
                ?: return ResponseEntity.ok().body(Response(
                        status = SUBJECT_NOT_FOUND,
                        message = "Invalid subject"
                ))

        if (subjectResult.userTest.isNullOrEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = TEST_NOT_FOUND,
                    message = "No tests created for this subject"
            ))
        }

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "All tests",
                data = subjectResult.mapToTestResponse()
        ))
    }

    override suspend fun findByStudentId(id: Int): ResponseEntity<Response<List<TestsResponse>>> {
        val studentResult = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            val predicateForId = builder.equal(root.get(UserEntity_.id), id)
            val predicateForRole = builder.equal(root.get(UserEntity_.role), UserRoles.STUDENT.role)
            val predicateCondition = builder.and(predicateForId, predicateForRole)
            query.select(root).where(predicateCondition)
        }.resultList.firstOrNull()
                ?: return ResponseEntity.ok().body(Response(
                        status = USER_NOT_FOUND,
                        message = "Invalid student"
                ))

        /* if(studentResult.studentTestEntity.isNullOrEmpty()) {
             return ResponseEntity.ok().body(Response(
                     status = TEST_NOT_FOUND,
                     message = "No tests found for this student"
             ))
         }*/

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "All tests"
        ))

    }

    override suspend fun findTestsBySubjectId(userId: Int, subjectId: Int): ResponseEntity<Response<List<StudentTestResponse>>> {
        val testResult = entityManager.withJoinCriteriaBuilder<UserEntity,TestGradeEntity>(UserEntity_.studentsGradeEntity) {
            builder, query, root, join ->
            val predicateForId = builder.equal(root.get(UserEntity_.id),userId)
            query.where(predicateForId)
        }.resultList

        if(testResult.isNullOrEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = TEST_NOT_FOUND,
                    message = "No tests found for this student"
            ))
        }

        val result = testResult.map {
            it.get(1,TestGradeEntity::class.java)
        }.filter {
            it.tests?.subject?.id == subjectId
        }

        if(result.isNullOrEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = TEST_NOT_FOUND,
                    message = "No tests found for this student"
            ))
        }

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "All tests",
                data = result.mapToStudentTestResponse()
        ))
    }

    override suspend fun findByTeacherId(id: Int): ResponseEntity<Response<List<TestsResponse>>> {
        val teacherResult = entityManager.withCriteriaBuilder<UserEntity> { builder, query, root ->
            val predicateForId = builder.equal(root.get(UserEntity_.id), id)
            val predicateForRole = builder.equal(root.get(UserEntity_.role), UserRoles.TEACHER.role)
            val predicateCondition = builder.and(predicateForId, predicateForRole)
            query.select(root).where(predicateCondition)
        }.resultList.firstOrNull()
                ?: return ResponseEntity.ok().body(Response(
                        status = USER_NOT_FOUND,
                        message = "Invalid teacher"
                ))

        if (teacherResult.teacherTestEntity.isNullOrEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = TEST_NOT_FOUND,
                    message = "No tests found for this teacher"
            ))
        }

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "All tests",
                data = teacherResult.teacherTestEntity!!.mapToTestResponse()
        ))
    }

    override suspend fun findAllResultsById(id: Int): ResponseEntity<Response<List<TestGradeResponse>>> {
        val result = entityManager.withJoinCriteriaBuilder<UserTestEntity,TestGradeEntity>(UserTestEntity_.tests) {
            builder, query, root, join ->
            val predicateForId = builder.equal(root.get(UserTestEntity_.id),id)
            query.where(predicateForId)
        }.resultList

        if(result.isNullOrEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = TEST_NOT_FOUND,
                    message = "Test not found"
            ))
        }

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Test results found",
                data = result.mapTupleToGradeResponse()
        ))
    }

    override suspend fun findById(id: Int): ResponseEntity<Response<TestsResponse>> {
        val testResult = entityManager.find(UserTestEntity::class.java, id)
                ?: return ResponseEntity.ok().body(Response(
                        status = TEST_NOT_FOUND,
                        message = "Test not found"
                ))

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Test found",
                data = testResult.mapToTestResponse()
        ))
    }

    @Transactional
    override suspend fun update(testUpdateRequest: TestUpdateRequest): ResponseEntity<Response<TestsResponse>> {
        val testResult = entityManager.find(UserTestEntity::class.java,testUpdateRequest.testId)
                ?: return ResponseEntity.ok().body(Response(
                        status = TEST_NOT_FOUND,
                        message = "Test not found"
                ))

        if(testResult.subject?.archived == SubjectState.ARCHIVED.value) {
            return ResponseEntity.ok().body(Response(
                    status = SUBJECT_ARCHIVED,
                    message = "Subject archived. Cannot edit changes"
            ))
        }

        val currentDate = convertTimestampToDate(Date().time)
        val formattedDate = convertStringToTimeStamp(currentDate)
        if(Date(convertStringToTimeStamp(testUpdateRequest.testDate)).before(Date(formattedDate))) {
            return ResponseEntity.ok().body(Response(
                    status = DATE_INVALID,
                    message = "You have chosen wrong date!"
            ))
        }
        println(convertStringToTimeStamp(testUpdateRequest.testDate))
        testResult.name = testUpdateRequest.testName.toString()
        testResult.testDate = convertStringToTimeStamp(testUpdateRequest.testDate)
        entityManager.merge(testResult)
        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Test updated",
                data = testResult.mapToTestResponse()
        ))
    }

    @Transactional
    override suspend fun insert(testCreateRequest: TestCreateRequest): ResponseEntity<Response<TestCreateResponse>> {
        val tupleTeacherResult = entityManager.withJoinCriteriaBuilder<UserEntity,UserSubjectEntity>(UserEntity_.teacherSubjectEntity) {
            builder, query, root, join ->
            val predicateForId = builder.equal(root.get(UserEntity_.id),testCreateRequest.teacherId)
            val predicateForRole = builder.equal(root.get(UserEntity_.role),UserRoles.TEACHER.role)
            val predicateForSubjectId = builder.equal(join.get(UserSubjectEntity_.id),testCreateRequest.subjectId)
            query.where(builder.and(predicateForId,predicateForRole,predicateForSubjectId))
        }.resultList.firstOrNull()
                ?: return ResponseEntity.ok().body(Response(
                        status = USER_NOT_FOUND,
                        message = "No teacher found"
                ))

        val subjectResult = entityManager.withCriteriaBuilder<UserSubjectEntity> { builder, query, root ->
            val predicateForId = builder.equal(root.get(UserSubjectEntity_.id), testCreateRequest.subjectId)
            val predicateForArchive = builder.equal(root.get(UserSubjectEntity_.archived), SubjectState.UNARCHIVED.value)
            val predicateCondition = builder.and(predicateForId, predicateForArchive)
            query.select(root).where(predicateCondition)
        }.resultList.firstOrNull() ?: return ResponseEntity.ok().body(Response(
                status = SUBJECT_NOT_FOUND,
                message = "Subject archived/not exist"
        ))

        if (subjectResult.userTest?.any { it.name == testCreateRequest.name?.toLowerCase() } == true) {
            return ResponseEntity.ok().body(Response(
                    status = TEST_ALREADY_EXIST,
                    message = "Test already exist with this name"
            ))
        }

        val tupleResult = entityManager.withJoinCriteriaBuilder<UserEntity, UserSubjectEntity>(UserEntity_.studentSubjects) { builder, query, root, join ->
            val predicateForRole = builder.equal(root.get(UserEntity_.role), UserRoles.STUDENT.role)
            val predicateForSubject = builder.equal(join.get(UserSubjectEntity_.id), testCreateRequest.subjectId)
            val predicateCondition = builder.and(predicateForRole, predicateForSubject)
            query.where(predicateCondition)
        }.resultList

        if (tupleResult.isNullOrEmpty()) {
            return ResponseEntity.ok().body(Response(
                    status = USER_NOT_FOUND,
                    message = "Students not found in this subject"
            ))
        }
        val currentDate = convertTimestampToDate(Date().time)
        val formattedDate = convertStringToTimeStamp(currentDate)
        if(Date(convertStringToTimeStamp(testCreateRequest.testDate)).before(Date(formattedDate))) {
            return ResponseEntity.ok().body(Response(
                    status = DATE_INVALID,
                    message = "You have chosen wrong date!"
            ))
        }

        val teacherResult = tupleTeacherResult.get(0,UserEntity::class.java)
        val testEntity = testCreateRequest.mapToTestEntity()
        tupleResult.forEach {
            val gradeEntity = TestGradeEntity(marks = BigDecimal("0"))
            teacherResult.addTeacherToTests(testEntity)
            subjectResult.addSubjectToTest(testEntity)
            testEntity.addMarksToTests(gradeEntity)
            testEntity.testDate = convertStringToTimeStamp(testCreateRequest.testDate)
            entityManager.persist(testEntity)
            val studentEntity = it.get(0, UserEntity::class.java)
            studentEntity.addStudentsToGrade(gradeEntity)
            println("students id ${studentEntity.id}")
        }

        val location = URI.create(java.lang.String.format("/test/%s", testEntity.id))
        return ResponseEntity.created(location).body(Response(
                status = SUCCESS,
                message = "Test created",
                data = testEntity.mapToTestCreateResponse()
        ))
    }

    @Transactional
    override suspend fun updateResult(testResultUpdateRequest: TestResultUpdateRequest): ResponseEntity<Response<TestResultUpdateResponse>> {
        val gradeResult = entityManager.withJoinCriteriaBuilder<UserTestEntity, TestGradeEntity>(UserTestEntity_.tests) { builder, query, root, join ->
            val predicateForId = builder.equal(root.get(UserTestEntity_.id), testResultUpdateRequest.testId)
            val studentId = builder.equal(join.get(TestGradeEntity_.students).get(UserEntity_.id),testResultUpdateRequest.studentId)
            val condition = builder.and(predicateForId,studentId)
            query.where(condition)
        }.resultList.firstOrNull()
                ?: return ResponseEntity.ok().body(Response(
                    status = TEST_NOT_FOUND,
                    message = "Test not found for this student"
            ))

        println("test found for update result for ${testResultUpdateRequest.studentId}")
        val testEntity = gradeResult.get(0, UserTestEntity::class.java)
        val gradeEntity = gradeResult.get(1, TestGradeEntity::class.java)
        if (testEntity.subject?.archived?.isArchived() == true) {
            return ResponseEntity.ok().body(Response(
                    status = SUBJECT_ARCHIVED,
                    message = "Unable to made changes in archived subject"
            ))
        }
        println("subject not archived for ${testEntity.id}")
        gradeEntity.marks = BigDecimal(testResultUpdateRequest.marks)
        println(gradeEntity)
        entityManager.merge(gradeEntity)

        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Test result updated",
                data = testEntity.mapToTestResultUpdateResponse(gradeEntity)
        ))
    }

    @Transactional
    override suspend fun deleteById(id: Int): ResponseEntity<Response<Unit>> {
        val testResult = entityManager.find(UserTestEntity::class.java,id)
                ?: return ResponseEntity.ok().body(Response(
                        status = TEST_NOT_FOUND,
                        message = "Test not found"
                ))
        testResult.subject = null
        testResult.teacher = null
        testResult.tests?.forEach {
            it.students = null
            it.tests = null
        }
        entityManager.remove(testResult)
        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Test deleted"
        ))
    }

    override suspend fun notifyUsersById(ids: List<Int>): ResponseEntity<Response<Unit>> {
        ids.forEach {
            val gradeResult = entityManager.withCriteriaBuilder<TestGradeEntity> { builder, query, root ->
                val predicateForId = builder.equal(root.get(TestGradeEntity_.id),it)
                query.select(root).where(predicateForId)
            }.resultList.firstOrNull()

            gradeResult?.let {result ->
                val body = result.buildEmailMessage()
                val to = result.students?.email ?: ""
                if(to.isNotEmpty()) {
                    emailManager.sendEmail(
                            to =  to,
                            subject = "Result announced",
                            body = body
                    )
                }
            }
        }
        return ResponseEntity.ok().body(Response(
                status = SUCCESS,
                message = "Email has been sent"
        ))
    }

}