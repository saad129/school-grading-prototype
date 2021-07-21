package com.school.system.grading.entity.user.request


data class UsersRequest(
        var firstName:String?= null,
        var lastName:String?= null,
        var username: String?= null,
        var password: String?= null,
        var email: String? = null,
        var role: String? = null
)