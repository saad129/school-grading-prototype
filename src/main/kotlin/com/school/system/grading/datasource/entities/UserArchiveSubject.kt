package com.school.system.grading.datasource.entities

import com.school.system.grading.datasource.entities.common.BaseEntity
import javax.persistence.*

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/27/21
 **/
@Entity
@Table(name = "archive_subject")
data class UserArchiveSubject(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,
        var studentId: Int? = null,
        var teacherId: Int? = null,
        var subjectId: Int? = null,
        var studentName: String? = null,
        var teacherName: String? = null,
        var subjectName: String? = null,
        @OneToMany(mappedBy = "subject",cascade = [CascadeType.ALL])
        var tests: MutableList<UserArchiveTest>?= null,
) : BaseEntity() {

        fun addArchiveTests(testArchive: UserArchiveTest?) {
                if(testArchive == null) return
                val list = this.tests
                list?.add(testArchive)
                this.tests = list
                testArchive.subject = this
                println("test archived")
        }
}
