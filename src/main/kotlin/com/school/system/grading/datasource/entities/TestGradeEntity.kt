package com.school.system.grading.datasource.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import java.math.BigDecimal
import javax.persistence.*

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/29/21
 **/
@Entity
@Table(name = "users_tests_grade")
data class TestGradeEntity(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,
        var marks: BigDecimal? = null,
        @JsonIgnore
        @ManyToOne(cascade = [CascadeType.ALL])
        var tests: UserTestEntity?= null,
        @JsonIgnore
        @ManyToOne(cascade = [CascadeType.ALL])
        var students: UserEntity?= null,
)