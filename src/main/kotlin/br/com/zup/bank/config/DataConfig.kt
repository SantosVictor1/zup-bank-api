package br.com.zup.bank.config

import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Blacklist
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.BlacklistRepository
import br.com.zup.bank.repository.UserRepository
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
    private val accountRepository: AccountRepository,
    private val blacklistRepository: BlacklistRepository
) {
    @Bean
    fun databaseInitializer() = ApplicationRunner {
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

        if (!blacklistRepository.existsByCpf("31504974000")) {
            blacklistRepository.save(Blacklist(cpf = "31504974000"))
        }
    }
}