package com.school.system.grading.manager

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 7/18/21
 **/
interface EmailManager {
    fun sendEmail(to: String,subject: String,body:String)
}