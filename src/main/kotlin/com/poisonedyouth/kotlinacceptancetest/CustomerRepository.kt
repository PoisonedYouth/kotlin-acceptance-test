package com.poisonedyouth.kotlinacceptancetest

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation.REQUIRED
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Repository
@Transactional(propagation = REQUIRED)
interface CustomerRepository: CrudRepository<Customer, Long> {
    fun findByFirstNameAndLastName(firstName: String, lastName: String): Optional<Customer>
    fun existsCustomerByEmail(email: String): Boolean
}