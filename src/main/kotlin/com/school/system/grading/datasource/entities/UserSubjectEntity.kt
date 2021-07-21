package com.school.system.grading.datasource.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.school.system.grading.datasource.entities.common.BaseEntity
import java.io.Serializable
import javax.persistence.*

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/

@Entity
@Table(name = "users_subject")
data class UserSubjectEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,
        @Column(name = "subject_name")
        var name: String?,
        var archived: Int,
        @OneToMany(mappedBy = "subject",cascade = [CascadeType.ALL])
        var userTest: List<UserTestEntity>?= null,
        @JsonIgnore
        @ManyToOne
        var users: UserEntity?= null,
        @JsonIgnore
        @ManyToOne
        var classes: UserClassEntity?= null,
): BaseEntity() {

        fun addSubjectToTest(userTestEntity: UserTestEntity) {
                val list = this.userTest?.toMutableList()
                list?.add(userTestEntity)
                this.userTest = list?.toList()
                userTestEntity.subject = this
        }

        fun removeTeacherFromArchiveSubject() {
                this.users = null
                val list = this.userTest?.toMutableList()
                list?.forEach {
                     it.teacher = null
                }
                this.userTest = list?.toList()
        }

        override fun toString(): String {
                return "UserSubject [id= $id, name= $name]"
        }
}