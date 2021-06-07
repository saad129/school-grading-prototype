package com.school.system.grading.datasource.extensions

import javax.persistence.EntityManager
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

inline fun <reified T> EntityManager.withCriteriaBuilder(block:(builder: CriteriaBuilder, query: CriteriaQuery<T>, root: Root<T>) -> CriteriaQuery<T>): TypedQuery<T> {
    val builder = this.criteriaBuilder
    val criteriaQuery = builder.createQuery(T::class.java)
    val root = criteriaQuery.from(T::class.java)
    val query = block(builder,criteriaQuery,root)
    return this.createQuery(query)
}