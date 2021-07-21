package com.school.system.grading.datasource.mapper

import com.school.system.grading.datasource.entities.TestGradeEntity
import com.school.system.grading.entity.grade.response.TestGradeResponse
import com.school.system.grading.entity.tests.response.StudentTestResponse
import javax.persistence.Tuple

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 7/4/21
 **/

fun TestGradeEntity.mapToGradeResponse(): TestGradeResponse {
    return TestGradeResponse(
            id = this.id,
            studentId = this.students?.id,
            studentName = "${this.students?.firstName} ${this.students?.lastName}",
            testId = this.tests?.id,
            marks = this.marks?.toPlainString()
    )
}

fun List<TestGradeEntity>.mapToGradeResponse(): List<TestGradeResponse> {
    val list = mutableListOf<TestGradeResponse>()
    this.forEach {
        list.add(it.mapToGradeResponse())
    }
    return list
}

fun List<Tuple>.mapTupleToGradeResponse(): List<TestGradeResponse> {
    val list = mutableListOf<TestGradeResponse>()
    this.forEach {
        list.add(it.get(1,TestGradeEntity::class.java).mapToGradeResponse())
    }
    return list
}
fun List<TestGradeEntity>.mapToStudentTestResponse(): List<StudentTestResponse> {
    val list = mutableListOf<StudentTestResponse>()
    this.forEach {
        list.add(StudentTestResponse(
                id = it.id,
                testName = it.tests?.name,
                marks = it.marks?.toPlainString()
        ))
    }

    return list
}
