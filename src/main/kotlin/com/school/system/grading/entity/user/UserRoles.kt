package com.school.system.grading.entity.user

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/7/21
 **/
enum class UserRoles(val role: Int) {
    ADMIN(1),TEACHER(2),STUDENT(3)
}


fun Int?.isAdmin(): Boolean {
    return when(this){
        UserRoles.ADMIN.role -> true
        else -> false
    }
}

fun Int?.isTeacher(): Boolean {
    return when(this){
        UserRoles.TEACHER.role -> true
        else -> false
    }
}

fun Int?.isStudent(): Boolean {
    return when(this){
        UserRoles.STUDENT.role -> true
        else -> false
    }
}