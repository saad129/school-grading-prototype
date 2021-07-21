package com.school.system.grading.manager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 7/18/21
 **/

@Component
class EmailManagerImpl(
        @Autowired
        private val emailSender: JavaMailSender
): EmailManager {

        override fun sendEmail(to: String, subject: String, body: String) {
                val mimeMessage = emailSender.createMimeMessage()
                MimeMessageHelper(mimeMessage,"utf-8").apply {
                        this.setText(body,true)
                        this.setTo(to)
                        this.setSubject(subject)
                }
                emailSender.send(mimeMessage)
        }

}