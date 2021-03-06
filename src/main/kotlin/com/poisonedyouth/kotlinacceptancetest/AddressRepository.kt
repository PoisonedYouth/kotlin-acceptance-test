package com.poisonedyouth.kotlinacceptancetest

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation.REQUIRED
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional(propagation = REQUIRED)
interface AddressRepository : CrudRepository<Address, Long> {
    fun findAddressByZipCode(zipCode: Int): Optional<Address>
}