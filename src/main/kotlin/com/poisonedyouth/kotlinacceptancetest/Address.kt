package com.poisonedyouth.kotlinacceptancetest

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Address(
    @Id
    @GeneratedValue
    var id: Long = 0,
    val street: String,
    val number: String,
    val zipCode: Int,
    val city: String,
    val country: String
)