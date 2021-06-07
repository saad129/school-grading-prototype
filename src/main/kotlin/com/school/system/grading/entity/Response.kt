package com.school.system.grading.entity

class Response<T>(
        val status: Int,
        val message: String,
        val data: T? = null
)

//SUCCESS CODE
const val SUCCESS = 200
const val CREATED = 200


//ERROR CODE
const val USER_ALREADY_EXIST = 600
const val USER_NOT_FOUND = 601
const val INVALID_PASSWORD = 602
const val FIELD_MISSING = 603
const val CLASS_CREATED = 604
const val CLASS_ALREADY_EXIST = 605
const val UNAUTHORIZED_USER = 606