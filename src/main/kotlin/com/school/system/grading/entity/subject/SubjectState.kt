package com.school.system.grading.entity.subject

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/28/21
 **/

enum class SubjectState(val value: Int){
    UNARCHIVED(0),ARCHIVED(1),
}


fun SubjectState.isArchived() = this.value == SubjectState.ARCHIVED.value

fun SubjectState.isNotArchived() = this.value == SubjectState.UNARCHIVED.value


fun Int?.isArchived() = this == SubjectState.ARCHIVED.value


fun Int?.isNotArchived() = this == SubjectState.UNARCHIVED.value