package com.poisonedyouth.kotlinacceptancetest

import com.poisonedyouth.kotlinacceptancetest.ApiResult.Failure
import com.poisonedyouth.kotlinacceptancetest.ApiResult.Success
import com.poisonedyouth.kotlinacceptancetest.ErrorCode.DUPLICATE_EMAIL
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerApplicationService(
    private val customerRepository: CustomerRepository,
    private val addressRepository: AddressRepository
) {


    @Transactional
    fun addNewCustomer(customer: Customer): ApiResult<Any> {
        if (customerRepository.existsCustomerByEmail(customer.email)) {
            return Failure(DUPLICATE_EMAIL, "For the email '${customer.email}' a customer already exists!")
        }

        val address = addressRepository.findAddressesByZipCode(customer.address.zipCode)
        val customerToPersist = if (address.isEmpty) {
            customer.copy(address = addressRepository.save(customer.address))
        } else {
            customer.copy(address = address.get())
        }

        return Success(customerRepository.save(customerToPersist).customerId)
    }
}

sealed class ApiResult<out T> {
    internal data class Failure(val errorCode: ErrorCode, val errorMessage: String) : ApiResult<Nothing>()
    internal data class Success<T>(val value: T) : ApiResult<T>()
}

enum class ErrorCode {
    DUPLICATE_EMAIL
}