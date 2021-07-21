package com.school.system.grading.datasource.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.school.system.grading.datasource.entities.common.BaseEntity
import java.io.Serializable
import javax.persistence.*

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/9/21
 **/

@Entity
@Table(name = "users_test")
data class UserTestEntity(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,
        @Column(name = "test_name")
        var name:String,
        @Column(name = "test_date")
        var testDate:Long?,
        @OneToMany(mappedBy = "tests",cascade = [CascadeType.ALL])
        var tests: MutableList<TestGradeEntity>?= null,
        @JsonIgnore
        @ManyToOne
        var teacher: UserEntity?= null,
        @JsonIgnore
        @ManyToOne
        var subject: UserSubjectEntity?= null
): BaseEntity(), Serializable {

        fun addMarksToTests(testGradeEntity: TestGradeEntity) {
                val list = this.tests
                list?.add(testGradeEntity)
                this.tests = list
                testGradeEntity.tests = this
        }

        override fun toString(): String {
                return "UserTest [id= $id, name= $name"
        }
}