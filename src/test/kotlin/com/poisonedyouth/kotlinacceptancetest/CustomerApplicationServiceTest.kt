package com.poisonedyouth.kotlinacceptancetest

import com.poisonedyouth.kotlinacceptancetest.ApiResult.Failure
import com.poisonedyouth.kotlinacceptancetest.ApiResult.Success
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

@RealDatabaseTest
class CustomerApplicationServiceTest {

    @Autowired
    private lateinit var addressRepository: AddressRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var customerApplicationService: CustomerApplicationService

    @Test
    fun `addNewCustomer returns failure result for duplicate email`() {
        // given
        val email = "john.doe@mail.com"
        val customer = createCustomer(email)

        val customerDuplicate = Customer(
            firstName = "John",
            lastName = "Doe",
            birthdate = LocalDate.of(2001, 5, 10),
            email = "john.doe@mail.com",
            address = customer.address,
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
        val actual = customerApplicationService.addNewCustomer(customerDuplicate)

        // then
        assertThat(actual).isInstanceOf(Failure::class.java)
        assertThat((actual as Failure).errorCode).isEqualTo(ErrorCode.DUPLICATE_EMAIL)
        assertThat(actual.errorMessage).isEqualTo("For the email 'john.doe@mail.com' a customer already exists!")
    }

    @Test
    fun `addNewCustomer returns success result for unique email`() {
        // given
        val address = Address(
            street = "Main Street",
            number = "13",
            zipCode = 90001,
            city = "Los Angeles",
            country = "US"
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
        val actual = customerApplicationService.addNewCustomer(customer)

        // then
        assertThat(actual).isInstanceOf(Success::class.java)
        assertThat((actual as Success).value).isNotNull
    }

    @Test
    fun `addNewCustomer not persists address twice`() {
        // given
        val email = "john.doe@mail.com"
        createCustomer(email)

        val address = Address(
            street = "Main Street",
            number = "13",
            zipCode = 90001,
            city = "Los Angeles",
            country = "US"
        )
        val customer = Customer(
            firstName = "Max",
            lastName = "DeMarco",
            birthdate = LocalDate.of(2001, 5, 10),
            email = "max.demarco@mail.com",
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
        customerApplicationService.addNewCustomer(customer)

        // then
        assertThat(addressRepository.findAll()).hasSize(1)
    }


    private fun createCustomer(email: String): Customer {
        val address = Address(
            street = "Main Street",
            number = "13",
            zipCode = 90001,
            city = "Los Angeles",
            country = "US"
        )
        addressRepository.save(address)
        val customer = Customer(
            firstName = "John",
            lastName = "Doe",
            birthdate = LocalDate.of(2001, 5, 10),
            email = email,
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
        return customerRepository.save(customer)
    }
}