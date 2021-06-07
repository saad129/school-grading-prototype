package com.school.system.grading.datasource.entities

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@MappedSuperclass
abstract class BaseEntity {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    protected var createdAt: Long? = null

    @LastModifiedDate
    @Column(name = "updated_at")
    protected var updatedAt: Long? = null

    @PrePersist
    protected fun prePersist() {
        this.createdAt = Date().time
    }

    @PreUpdate
    protected fun preUpdate() {
        this.updatedAt = Date().time
    }
}