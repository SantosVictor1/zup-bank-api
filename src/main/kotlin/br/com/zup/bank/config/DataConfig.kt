package br.com.zup.bank.config

import br.com.zup.bank.model.Account
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Created by Victor Santos on 03/01/2020
 */
@Configuration
class DataConfig {
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Bean
    fun databaseInitializer(userRepository: UserRepository, accountRepository: AccountRepository) = ApplicationRunner {
        val user = userRepository.save(
            User
            (null,
            "Victor Santos",
            "02160795607",
            "victor@gmail.com",
            true
            )
        )

        accountRepository.save(Account(accountNumber = "5146789132", user = user, isActive = true))
    }
}