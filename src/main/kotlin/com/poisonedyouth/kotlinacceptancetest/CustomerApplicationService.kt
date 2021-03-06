package com.poisonedyouth.kotlinacceptancetest

import com.poisonedyouth.kotlinacceptancetest.ApiResult.Failure
import com.poisonedyouth.kotlinacceptancetest.ApiResult.Success
import com.poisonedyouth.kotlinacceptancetest.ErrorCode.DUPLICATE_EMAIL
import com.poisonedyouth.kotlinacceptancetest.ErrorCode.GENERAL_ERROR
import com.poisonedyouth.kotlinacceptancetest.ErrorCode.INVALID_DATE
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class CustomerApplicationService(
    private val customerRepository: CustomerRepository,
    private val addressRepository: AddressRepository
) {


    @Transactional
    fun addNewCustomer(customerDto: CustomerDto): ApiResult<Any> {
        try {
            val customer = mapCustomerDtoToCustomer(customerDto)

            if (customerRepository.existsCustomerByEmail(customer.email)) {
                return Failure(DUPLICATE_EMAIL, "For the email '${customer.email}' a customer already exists!")
            }

            val address = addressRepository.findAddressByZipCode(customer.address.zipCode)
            val customerToPersist = if (address.isEmpty) {
                customer.copy(address = addressRepository.save(customer.address))
            } else {
                customer.copy(address = address.get())
            }

            return Success(customerRepository.save(customerToPersist).customerId)

        } catch (e: DateTimeParseException) {
            return Failure(
                INVALID_DATE,
                "The birthdate '${customerDto.birthdate}' is not in expected format (dd.MM.yyyy)!"
            )
        } catch (e: Exception) {
            return Failure(GENERAL_ERROR, "An unexpected error occurred (${e.message}!")
        }
    }

    private fun mapCustomerDtoToCustomer(customerDto: CustomerDto): Customer {
        return Customer(
            firstName = customerDto.firstName,
            lastName = customerDto.lastName,
            birthdate = LocalDate.parse(customerDto.birthdate, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            email = customerDto.email,
            address = Address(
                street = customerDto.address.street,
                number = customerDto.address.number,
                zipCode = customerDto.address.zipCode,
                city = customerDto.address.city,
                country = customerDto.address.country
            ),
            accounts = customerDto.accounts.map {
                Account(
                    number = it.number,
                    balance = it.balance
                )
            }.toSet()
        )
    }
}

sealed class ApiResult<out T> {
    internal data class Failure(val errorCode: ErrorCode, val errorMessage: String) : ApiResult<Nothing>()
    internal data class Success<T>(val value: T) : ApiResult<T>()
}

enum class ErrorCode {
    DUPLICATE_EMAIL,
    INVALID_DATE,
    GENERAL_ERROR,
}