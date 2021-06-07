package com.school.system.grading.extensions

import java.security.SecureRandom
import java.util.*

private val random = SecureRandom()
private val base64Encoder: Base64.Encoder = Base64.getUrlEncoder()

fun generateNewToken(): String {
    val randomBytes =  ByteArray(24)
    random.nextBytes(randomBytes)
    return base64Encoder.encodeToString(randomBytes)
}
