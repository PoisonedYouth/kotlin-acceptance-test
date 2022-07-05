package com.poisonedyouth.kotlinacceptancetest

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import java.time.LocalDate

@Entity
data class Customer(
    @Id
    @GeneratedValue
    var id: Long = 0,
    val firstName: String,
    val lastName: String,
    val birthdate: LocalDate,
    val email: String,
    @OneToOne
    val address: Address,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = EAGER)
    @JoinColumn(name = "addresss_id")
    val accounts: Set<Account> = emptySet()
)