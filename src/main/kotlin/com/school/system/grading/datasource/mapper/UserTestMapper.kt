package com.school.system.grading.datasource.mapper

import com.school.system.grading.datasource.entities.TestGradeEntity
import com.school.system.grading.datasource.entities.UserSubjectEntity
import com.school.system.grading.datasource.entities.UserTestEntity
import com.school.system.grading.entity.subject.SubjectState
import com.school.system.grading.entity.tests.request.TestCreateRequest
import com.school.system.grading.entity.tests.response.TestCreateResponse
import com.school.system.grading.entity.tests.response.TestResultUpdateResponse
import com.school.system.grading.entity.tests.response.TestsResponse
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/9/21
 **/

fun TestCreateRequest.mapToTestEntity(): UserTestEntity {
    return UserTestEntity(
            name = this.name!!.toLowerCase(),
            testDate = convertStringToTimeStamp(this.testDate)
    )
}

fun UserTestEntity.mapToTestResponse(): TestsResponse {
    return TestsResponse(
            id = id,
            testName = this.name,
            createdAt = convertTimestampToDate(this.testDate),
            subjectName = this.subject?.name
    )
}

fun convertTimestampToDate(timestamp: Long?): String? {
    return timestamp?.let {
        val date = Date(Timestamp(it).time)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateFormat.format(date)
    } ?:run {
        null
    }
}

fun convertStringToTimeStamp(requestedDate: String?): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val date = dateFormat.parse(requestedDate)
    return date.time
}

fun List<UserTestEntity>.mapToTestResponse(): List<TestsResponse> {
    val list = mutableListOf<TestsResponse>()
    this.forEach {
        if (it.subject?.archived == SubjectState.UNARCHIVED.value) {
            list.add(it.mapToTestResponse())
        }
    }
    return list.toList()
}

fun UserSubjectEntity.mapToTestResponse(): List<TestsResponse> {
    val list = mutableListOf<TestsResponse>()
    this.userTest?.forEach {
        list.add(it.mapToTestResponse())
    }
    return list.toList()
}


fun UserTestEntity.mapToTestCreateResponse(): TestCreateResponse {
    return TestCreateResponse(
            id = this.id,
            name = this.name
    )
}

fun UserTestEntity.mapToTestResultUpdateResponse(gradeEntity: TestGradeEntity): TestResultUpdateResponse {
    return TestResultUpdateResponse(
            id = this.id,
            testName = this.name,
            marks = gradeEntity.marks?.toPlainString()
    )
}
