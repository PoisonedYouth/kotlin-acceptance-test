package com.poisonedyouth.kotlinacceptancetest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@RealDatabaseTest
class AddressRepositoryTest {

    @Autowired
    private lateinit var addressRepository: AddressRepository

    @Test
    fun `save address is possible`() {
        // given
        val address = Address(
            street = "Main Street",
            number = "13",
            zipCode = 90001,
            city = "Los Angeles",
            country = "USA"
        )

        // when
        val actual = addressRepository.save(address)

        // then
        assertThat(addressRepository.findById(actual.id).get()).isEqualTo(address)
    }

    @Test
    fun `findAddressesByCity returns matching address`() {
        // given
        val address1 = Address(
            street = "Main Street",
            number = "13",
            zipCode = 90001,
            city = "Los Angeles",
            country = "USA"
        )
        addressRepository.save(address1)

        val address2 = Address(
            street = "Hauptstrasse",
            number = "25A",
            zipCode = 10115,
            city = "Berlin",
            country = "De"
        )
        addressRepository.save(address2)

        // when
        val actual = addressRepository.findAddressesByCity("Berlin")

        // then
        assertThat(actual).containsOnly(address2)
    }

    @Test
    fun `findAddressesByCity returns empty list for no matching address`() {
        // given
        val address1 = Address(
            street = "Main Street",
            number = "13",
            zipCode = 90001,
            city = "Los Angeles",
            country = "USA"
        )
        addressRepository.save(address1)

        val address2 = Address(
            street = "Hauptstrasse",
            number = "25A",
            zipCode = 10115,
            city = "Berlin",
            country = "De"
        )
        addressRepository.save(address2)

        // when
        val actual = addressRepository.findAddressesByCity("Freiburg")

        // then
        assertThat(actual).isEmpty()
    }
}