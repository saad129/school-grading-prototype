package com.school.system.grading.datasource.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.school.system.grading.datasource.entities.common.BaseEntity
import org.apache.commons.lang3.builder.ToStringExclude
import java.io.Serializable
import javax.persistence.*

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/7/21
 **/

@Entity
@Table(name = "users_class")
data class UserClassEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,
        @Column(unique = true)
        var name: String,
        @OneToMany(mappedBy = "classes",cascade = [CascadeType.ALL])
        var userSubjectEntity: List<UserSubjectEntity>? = null
): BaseEntity() {

        fun addClassToSubject(userSubjectEntity: UserSubjectEntity) {
                val list = this.userSubjectEntity?.toMutableList()
                list?.add(userSubjectEntity)
                this.userSubjectEntity = list?.toList()
                userSubjectEntity.classes = this
        }

        override fun toString(): String {
                return "UserClass [id= $id, name= $name]"
        }
}