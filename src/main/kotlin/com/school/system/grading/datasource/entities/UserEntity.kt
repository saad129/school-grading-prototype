package com.school.system.grading.datasource.entities

import javax.persistence.*

@Entity
@Table(name = "users")
data class UserEntity(
        @Id
        @SequenceGenerator(name = "User_Gen", initialValue = 1001)
        @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "User_Gen")
        var id: Int? = null,
        var token: String? = null,
        var firstName: String,
        var lastName: String,
        var username: String,
        var password: String,
        var role: Int,
        var expiredAt: Long? = null,
): BaseEntity()