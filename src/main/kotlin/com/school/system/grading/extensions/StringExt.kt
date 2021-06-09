package com.school.system.grading.extensions

import org.apache.commons.text.WordUtils
import java.util.*

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/

fun String.generateAcronym(): String {
    val split = this.split(" ")
    return if(split.size > 1) {
        WordUtils.initials(this, ' ','.').toUpperCase()
    } else {
        this.toUpperCase()
    }

}

fun String.replaceSpaceWithDash() = this.replace(" ","_")

fun String.replaceSpaceWithDot() = this.replace(" ",".")
