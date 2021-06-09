package com.school.system.grading.datasource.entities

import javax.persistence.*

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/

@Entity
@Table(name = "users_subject")
class UserSubjectEntity(
        @Id
        @Column(name = "subject_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,
        @Column(unique = true,name = "subject_name")
        var name: String
): BaseEntity()