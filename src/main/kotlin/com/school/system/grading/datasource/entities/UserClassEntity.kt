package com.school.system.grading.datasource.entities

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
        @Column(unique=true)
        var name: String
)