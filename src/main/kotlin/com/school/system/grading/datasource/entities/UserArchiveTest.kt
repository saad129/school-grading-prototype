package com.school.system.grading.datasource.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.school.system.grading.datasource.entities.common.BaseEntity
import java.math.BigDecimal
import javax.persistence.*

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/30/21
 **/
@Entity
@Table(name = "archive_tests")
data class UserArchiveTest(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,
        var testId: Int? = null,
        var testName: String? = null,
        var marks: BigDecimal? = null,
        @JsonIgnore
        @ManyToOne(cascade = [CascadeType.ALL])
        var subject: UserArchiveSubject?= null,
): BaseEntity()