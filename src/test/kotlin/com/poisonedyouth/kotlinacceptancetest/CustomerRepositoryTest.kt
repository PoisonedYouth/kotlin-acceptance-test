package com.poisonedyouth.kotlinacceptancetest

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.InvalidDataAccessApiUsageException
import java.time.LocalDate

@RealDatabaseTest
class CustomerRepositoryTest {

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var addressRepository: AddressRepository

    @Test
    fun `save Customer is possible`() {
        // given
        val address = Address(
            street = "Main Street",
            number = "13",
            zipCode = 90001,
            city = "Los Angeles",
            country = "USA"
        )
        addressRepository.save(address)

        val customer = Customer(
            firstName = "John",
            lastName = "Doe",
            birthdate = LocalDate.of(2001, 5, 10),
            email = "john.doe@mail.com",
            address = address,
            accounts = setOf(
                Account(
                    number = 12345,
                    balance = 200
                ),
                Account(
                    number = 12346,
                    balance = -150
                )
            )
        )

        // when
        val actual = customerRepository.save(customer)

        // then
        assertThat(customerRepository.findById(actual.id).get()).isEqualTo(customer)
    }

    @Test
    fun `save Customer without saved address fails`() {
        // given
        val address = Address(
            street = "Main Street",
            number = "13",
            zipCode = 90001,
            city = "Los Angeles",
            country = "USA"
        )

        val customer = Customer(
            firstName = "John",
            lastName = "Doe",
            birthdate = LocalDate.of(2001, 5, 10),
            email = "john.doe@mail.com",
            address = address,
            accounts = setOf(
                Account(
                    number = 12345,
                    balance = 200
                ),
                Account(
                    number = 12346,
                    balance = -150
                )
            )
        )

        // when + then
        assertThatThrownBy {
            customerRepository.save(customer)
        }.isInstanceOf(InvalidDataAccessApiUsageException::class.java)
    }

    @Test
    fun `findByFirstNameAndLastName returns matching customer`() {
        // given
        val address = Address(
            street = "Main Street",
            number = "13",
            zipCode = 90001,
            city = "Los Angeles",
            country = "USA"
        )
        addressRepository.save(address)

        val customer = Customer(
            firstName = "John",
            lastName = "Doe",
            birthdate = LocalDate.of(2001, 5, 10),
            email = "john.doe@mail.com",
            address = address,
            accounts = setOf(
                Account(
                    number = 12345,
                    balance = 200
                ),
                Account(
                    number = 12346,
                    balance = -150
                )
            )
        )
        customerRepository.save(customer)

        // when
        val actual = customerRepository.findByFirstNameAndLastName("John", "Doe")

        // then
        assertThat(actual.get()).isEqualTo(customer)
    }
}