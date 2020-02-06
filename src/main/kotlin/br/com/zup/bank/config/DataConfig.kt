package br.com.zup.bank.config

import br.com.zup.bank.enums.Status
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * Created by Victor Santos on 03/01/2020
 */
@Configuration
@Profile("prod")
class DataConfig(
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository
) {
    @Bean
    fun databaseInitializer(userRepository: UserRepository, accountRepository: AccountRepository) = ApplicationRunner {
        if (!userRepository.existsByCpf(cpf = "02160795607")) {
            val user = userRepository.save(
                User(
                    name = "Victor Santos",
                    cpf = "02160795607",
                    email = "victor@gmail.com",
                    isActive = true
                )
            )

            accountRepository.save(Account(accountNumber = "5146789132", user = user, isActive = true, balance = 1000.0))
        }
    }
}