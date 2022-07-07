package com.poisonedyouth.kotlinacceptancetest

import com.poisonedyouth.kotlinacceptancetest.ApiResult.Failure
import com.poisonedyouth.kotlinacceptancetest.ApiResult.Success
import com.poisonedyouth.kotlinacceptancetest.ErrorCode.DUPLICATE_EMAIL
import com.poisonedyouth.kotlinacceptancetest.ErrorCode.GENERAL_ERROR
import com.poisonedyouth.kotlinacceptancetest.ErrorCode.INVALID_DATE
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerRestController(
    private val customerApplicationService: CustomerApplicationService
) {

    @PostMapping(
        "/api/v1/customer",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun addCustomer(@RequestBody customerDto: CustomerDto): ResponseEntity<out Any> {
        return customerApplicationService.addNewCustomer(customerDto).let { result ->
            when (result) {
                is Success -> handleSuccess(result)
                is Failure -> handleFailure(result)
            }

        }
    }

    private fun handleSuccess(result: Success<Any>): ResponseEntity<SuccessDto> {
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessDto(result.value))
    }

    private fun handleFailure(
        result: Failure
    ): ResponseEntity<ErrorDto> {

        val code = result.errorCode
        val status = when (code) {
            INVALID_DATE,
            DUPLICATE_EMAIL -> HttpStatus.BAD_REQUEST
            GENERAL_ERROR -> INTERNAL_SERVER_ERROR
        }
        val errorDto = ErrorDto(code.name, result.errorMessage)
        return ResponseEntity.status(status).body(errorDto)
    }
}

data class ErrorDto(val errorCode: String, val errorMessage: String)

data class SuccessDto(val customerId: Any)
