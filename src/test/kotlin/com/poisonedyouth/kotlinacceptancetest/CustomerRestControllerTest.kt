package com.poisonedyouth.kotlinacceptancetest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.io.File
import java.time.LocalDate

@RealDatabaseTest
@AutoConfigureMockMvc
class CustomerRestControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var addressRepository: AddressRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    private val mapper = jacksonObjectMapper()

    @Test
    fun `addCustomer returns error result for invalid birthdate`() {
        // given
        val addressDto = AddressDto(
            street = "Main Street",
            number = "13",
            zipCode = 123,
            city = "Los Angeles",
            country = "US"
        )

        val customerDto = CustomerDto(
            firstName = "John",
            lastName = "Doe",
            birthdate = "2000.5.10",
            email = "john.doe@mail.com",
            address = addressDto,
            accounts = setOf(
                AccountDto(
                    number = 12345,
                    balance = 200
                ),
                AccountDto(
                    number = 12346,
                    balance = -150
                )
            )
        )

        // when
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(customerDto))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()

        // then
        Assertions.assertThat(result.response.contentAsString)
            .isEqualTo("{\"errorCode\":\"INVALID_DATE\",\"errorMessage\":\"The birthdate '2000.5.10' is not in expected format (dd.MM.yyyy)!\"}")


    }

    @Test
    fun `addCustomer returns error result for invalid zipCode`() {
        // given
        val addressDto = AddressDto(
            street = "Main Street",
            number = "13",
            zipCode = 123,
            city = "Los Angeles",
            country = "US"
        )

        val customerDto = CustomerDto(
            firstName = "John",
            lastName = "Doe",
            birthdate = "05.10.2000",
            email = "john.doe@mail.com",
            address = addressDto,
            accounts = setOf(
                AccountDto(
                    number = 12345,
                    balance = 200
                ),
                AccountDto(
                    number = 12346,
                    balance = -150
                )
            )
        )

        // when
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(customerDto))
        )
            .andExpect(MockMvcResultMatchers.status().isInternalServerError)
            .andReturn()

        // then
        Assertions.assertThat(result.response.contentAsString)
            .isEqualTo("{\"errorCode\":\"GENERAL_ERROR\",\"errorMessage\":\"An unexpected error occurred (ZipCode must contain 5 digits!!\"}")
    }

    @Test
    fun `addCustomer returns error result for duplicate email`() {
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
        customerRepository.save(customer)

        val addressDto = AddressDto(
            street = "Main Street",
            number = "13",
            zipCode = 90001,
            city = "Los Angeles",
            country = "US"
        )

        val customerDto = CustomerDto(
            firstName = "John",
            lastName = "Doe",
            birthdate = "05.10.2000",
            email = "john.doe@mail.com",
            address = addressDto,
            accounts = setOf(
                AccountDto(
                    number = 12345,
                    balance = 200
                ),
                AccountDto(
                    number = 12346,
                    balance = -150
                )
            )
        )

        // when
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(customerDto))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()

        // then
        Assertions.assertThat(result.response.contentAsString)
            .isEqualTo("{\"errorCode\":\"DUPLICATE_EMAIL\",\"errorMessage\":\"For the email 'john.doe@mail.com' a customer already exists!\"}")

    }
}