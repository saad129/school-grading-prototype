package com.school.system.grading.datasource.entities

import com.school.system.grading.datasource.entities.common.BaseEntity
import com.school.system.grading.entity.subject.SubjectState
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
@Table(name = "users")
data class UserEntity(
        @Id
        @SequenceGenerator(name = "User_Gen", initialValue = 1001)
        @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "User_Gen")
        var id: Int? = null,
        var token: String? = null,
        var firstName: String,
        var lastName: String,
        var username: String,
        var email: String?,
        var password: String,
        var role: Int,
        var expiredAt: Long? = null,
        @OneToMany(mappedBy = "users", cascade = [CascadeType.ALL])
        var teacherSubjectEntity: List<UserSubjectEntity>? = null,
        @OneToMany(mappedBy = "teacher", cascade = [CascadeType.ALL])
        var teacherTestEntity: List<UserTestEntity>? = null,
        @OneToMany(mappedBy = "students",cascade = [CascadeType.ALL])
        var studentsGradeEntity: MutableList<TestGradeEntity>? = null,
        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "class_id", referencedColumnName = "id")
        var studentClass: UserClassEntity? = null,
        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        @JoinColumn(name = "students_id", referencedColumnName = "id")
        var studentSubjects: MutableList<UserSubjectEntity>? = null,
) : BaseEntity() {

    fun addTeacherToSubject(userSubjectEntity: UserSubjectEntity) {
        val list = this.teacherSubjectEntity?.toMutableList()
        list?.add(userSubjectEntity)
        this.teacherSubjectEntity = list?.toList()
        userSubjectEntity.users = this
    }


    fun addStudentToClassSubjects(userSubjectEntity: List<UserSubjectEntity>): UserEntity {
        val list = this.studentSubjects?.toMutableList()
        userSubjectEntity.forEach {
            if(list?.contains(it) == false) {
                list.add(it)
            }
        }
        this.studentSubjects = list
        return this
    }

    fun removeSubjectsFromStudent() {
        val list = this.studentSubjects?.toMutableList()
        list?.clear()
        this.studentSubjects = list
    }

    fun removeSubjectFromStudent(subjectEntity: UserSubjectEntity): UserEntity {
        val list = this.studentSubjects?.toMutableList()
        list?.remove(subjectEntity)
        this.studentSubjects = list
        return this
    }

    fun addStudentsToGrade(testGradeEntity: TestGradeEntity): UserEntity {
        val list = this.studentsGradeEntity?.toMutableList()
        list?.add(testGradeEntity)
        this.studentsGradeEntity = list
        testGradeEntity.students = this
        return this
    }

    fun addTeacherToTests(userTestEntity: UserTestEntity) {
        val list = this.teacherTestEntity?.toMutableList()
        list?.add(userTestEntity)
        this.teacherTestEntity = list?.toList()
        userTestEntity.teacher = this
    }

    fun removeSubjectsFromTeacher() {
        this.teacherSubjectEntity?.forEach {
            it.users = null
        }
        val list = this.teacherSubjectEntity?.toMutableList()
        list?.clear()
        this.teacherSubjectEntity = list
    }

    fun removeTestsFromTeacher() {
        this.teacherTestEntity?.forEach {
            println("test: ${it.id}")
            it.teacher = null
        }
        val list = this.teacherTestEntity?.toMutableList()
        list?.clear()
        this.teacherTestEntity = list
    }

    override fun toString(): String {
        return "UserEntity [id= $id, token= $token, firstName= $firstName, lastName=$lastName" +
                " username= $username, password= $password, role= $role]"
    }
}