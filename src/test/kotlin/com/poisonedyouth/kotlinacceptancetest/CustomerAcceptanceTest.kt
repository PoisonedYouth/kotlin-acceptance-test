package com.poisonedyouth.kotlinacceptancetest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.File

@RealDatabaseTest
@AutoConfigureMockMvc
class CustomerAcceptanceTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `scenario save customer is successful`() {
        // given

        // when
        val result = mockMvc.perform(
            post("/api/v1/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(File("src/test/resources/payload.json").readText())
        )
            .andExpect(status().isCreated)
            .andReturn()

        // then
        assertThat(result.response.contentAsString).matches("\\{\"customerId\":[\\d]{5}}")
    }
}