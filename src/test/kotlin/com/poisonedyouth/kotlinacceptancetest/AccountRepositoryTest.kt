package com.poisonedyouth.kotlinacceptancetest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@RealDatabaseTest
class AccountRepositoryTest {

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Test
    fun `save account is possible`() {
        // given
        val account = Account(
            number = 1234,
            balance = 200
        )

        // when
        val actual = accountRepository.save(account)

        // then
        assertThat(accountRepository.findById(actual.id).get()).isEqualTo(account)
    }

    @Test
    fun `findById returns matching account`() {
        // given
        val account1 = Account(1, 1234, 200)
        accountRepository.save(account1)

        val account2 = Account(2, 1235, -150)
        accountRepository.save(account2)

        // when
        val actual = accountRepository.findById(account1.id).get()

        // then
        assertThat(actual).isEqualTo(account1)
    }

}