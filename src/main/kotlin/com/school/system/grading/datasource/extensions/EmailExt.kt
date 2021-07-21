package com.school.system.grading.datasource.extensions

import com.school.system.grading.datasource.entities.TestGradeEntity
import com.school.system.grading.entity.user.response.UserResponse


fun TestGradeEntity.buildEmailMessage(): String {
    return "<h2 style=\"text-align:center;\">Hello ${students?.lastName} ${students?.firstName}!</h2>" +
            "<h4>Result Announcement for ${tests?.name}!</h4>" +
            "<br />" +
            "<p>Your grade has been announced. Please login your <a href=\"http://localhost:3000\">Portal</a> to see the result</p>"
}


fun UserResponse.buildContactMessage(message: String?): String {
    return message +
            "<br />" +
            "This message is sent by your ${this.role.toLowerCase()} ${this.firstName} ${this.lastName}"
}