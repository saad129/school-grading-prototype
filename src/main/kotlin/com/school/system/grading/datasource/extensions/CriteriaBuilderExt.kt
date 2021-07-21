package com.school.system.grading.datasource.extensions

import javax.persistence.EntityManager
import javax.persistence.Tuple
import javax.persistence.TypedQuery
import javax.persistence.criteria.*
import javax.persistence.metamodel.ListAttribute
import javax.persistence.metamodel.SetAttribute
import javax.persistence.metamodel.SingularAttribute

/**
 * @project Grading
 * @author Muhammad Saad Bhatti
 * Date: 6/4/2021
 */


inline fun <reified T> EntityManager.withCriteriaBuilder
        (block: (builder: CriteriaBuilder, query: CriteriaQuery<T>, root: Root<T>) -> CriteriaQuery<T>): TypedQuery<T> {
    val builder = this.criteriaBuilder
    val criteriaQuery = builder.createQuery(T::class.java)
    val root = criteriaQuery.from(T::class.java)
    val query = block(builder, criteriaQuery, root)
    return this.createQuery(query)
}

inline fun <reified T> EntityManager.withCountCriteriaBuilder
        (block: (builder: CriteriaBuilder, query: CriteriaQuery<Long>, root: Root<T>) -> CriteriaQuery<Long>): Long {
    val builder = this.criteriaBuilder
    val criteriaQuery = builder.createQuery(Long::class.java)
    val root = criteriaQuery.from(T::class.java)
    val query = block(builder, criteriaQuery, root)
    return this.createQuery(query).singleResult
}


inline fun <reified T, X> EntityManager.withJoinCriteriaBuilder(
        withJoin: SetAttribute<T, X>,
        joinType: JoinType = JoinType.LEFT,
        block: (builder: CriteriaBuilder, query: CriteriaQuery<Tuple>, root: Root<T>,join: SetJoin<T,X>) -> CriteriaQuery<Tuple>): TypedQuery<Tuple> {
    val builder = this.criteriaBuilder
    val criteriaQuery = builder.createTupleQuery()
    val root = criteriaQuery.from(this.metamodel.entity(T::class.java))
    val join: SetJoin<T, X> = root.join(withJoin, joinType)
    criteriaQuery.multiselect(root, join)
    val query = block(builder, criteriaQuery, root,join)
    return this.createQuery(query)
}

inline fun <reified T, X> EntityManager.withJoinCriteriaBuilder(
        withJoin: ListAttribute<T, X>,
        joinType: JoinType = JoinType.LEFT,
        block: (builder: CriteriaBuilder, query: CriteriaQuery<Tuple>, root: Root<T>,join: ListJoin<T,X>) -> CriteriaQuery<Tuple>): TypedQuery<Tuple> {
    val builder = this.criteriaBuilder
    val criteriaQuery = builder.createTupleQuery()
    val root = criteriaQuery.from(this.metamodel.entity(T::class.java))
    val join: ListJoin<T, X> = root.join(withJoin, joinType)
    criteriaQuery.multiselect(root, join)
    val query = block(builder, criteriaQuery, root,join)
    return this.createQuery(query)
}


inline fun <reified T, X> EntityManager.withJoinCriteriaBuilder(
        withJoin: SingularAttribute<T, X>,
        joinType: JoinType = JoinType.LEFT,
        block: (builder: CriteriaBuilder, query: CriteriaQuery<Tuple>, root: Root<T>,join: Join<T,X>) -> CriteriaQuery<Tuple>): TypedQuery<Tuple> {
    val builder = this.criteriaBuilder
    val criteriaQuery = builder.createTupleQuery()
    val root = criteriaQuery.from(this.metamodel.entity(T::class.java))
    val join: Join<T, X> = root.join(withJoin, joinType)
    criteriaQuery.multiselect(root, join)
    val query = block(builder, criteriaQuery, root,join)
    return this.createQuery(query)
}