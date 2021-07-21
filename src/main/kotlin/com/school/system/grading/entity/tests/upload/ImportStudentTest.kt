package com.school.system.grading.entity.tests.upload

import com.opencsv.bean.CsvBindByPosition


data class ImportStudentTest(
        @CsvBindByPosition( position = 0)
        var id: Int? = null,
        @CsvBindByPosition( position = 1)
        var studentId: Int? = null,
        @CsvBindByPosition( position = 2)
        var studentName: String? = null,
        @CsvBindByPosition( position = 3)
        var studentMarks: String? = null
)