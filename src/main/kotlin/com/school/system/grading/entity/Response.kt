package com.school.system.grading.entity

import org.springframework.http.ResponseEntity

class Response<T>(
        val status: Int,
        val message: String,
        val data: T? = null
)

fun <T> ResponseEntity<Response<T>>.asSuccess(): Boolean {
    return this.body?.let {
        if(it.status == SUCCESS) return true
        else false
    } ?: run {
        false
    }
}

sealed class AppResponseEntity<out T> {
    class SUCCESS<T>(val data: T?): AppResponseEntity<T>()
    object ERROR : AppResponseEntity<Nothing>()
}

fun AppResponseEntity<*>.asSuccess(): Boolean = this is AppResponseEntity.SUCCESS<*>

fun <T> ResponseEntity<Response<T>>.mapAsAppResponse(): AppResponseEntity<T> {
    return if(this.body?.status == SUCCESS) {
        AppResponseEntity.SUCCESS(this.body?.data)
    } else {
        AppResponseEntity.ERROR
    }
}


//SUCCESS CODE
const val SUCCESS = 200
const val CREATED = 201
const val NO_CONTENT = 204
const val ERROR = 422
const val CLASS_CREATED = 604


//ERROR CODE
const val USER_ALREADY_EXIST = 600
const val USER_NOT_FOUND = 601
const val INVALID_PASSWORD = 602
const val FIELD_MISSING = 603
const val CLASS_ALREADY_EXIST = 605
const val CLASS_NOT_FOUND = 606
const val UNAUTHORIZED_USER = 607
const val SUBJECT_ALREADY_EXIST = 608
const val TEST_ALREADY_EXIST = 609
const val TEST_NOT_FOUND = 610
const val SUBJECT_NOT_FOUND = 611
const val MIXED_USERS = 612
const val ROLE_NOT_VALID = 613
const val SUBJECT_ARCHIVED = 614
const val ACCOUNT_ALREADY_DELETED = 615
const val TEACHER_NOT_DELETED = 616
const val INVALID_MARKS = 617
const val SUBJECT_CANNOT_ARCHIVE = 618
const val SUBJECT_CANNOT_DELETE = 619
const val UNABLE_TO_DELETE = 620
const val DATE_INVALID = 621
const val RESULT_FOUND = 622
const val ERROR_CSV_IMPORT = 623
const val EMAIL_NOT_VALID = 624
const val EMAIL_NOT_SEND = 625

