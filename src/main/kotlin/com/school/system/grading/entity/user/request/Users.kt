package com.school.system.grading.entity.user.request


data class Users(
        var firstName:String?= null,
        var lastName:String?= null,
        var username: String?= null,
        var password: String?= null,
        var role: Int?= null
)