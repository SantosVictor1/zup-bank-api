package br.com.zup.bank.service

import br.com.zup.bank.dto.response.success.AccountResponseDTO
import br.com.zup.bank.dto.response.success.UserAccountResponseDTO
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.service.exception.BankException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by Victor Santos on 26/12/2019
 */
@Service
class AccountService {
    @Autowired
    private lateinit var accountRepository: AccountRepository

    fun createAccount(user: User): AccountResponseDTO {
        findAccountByUser(user.cpf!!)

        val acc = accountRepository.save(Account(user = user))
        val user = UserAccountResponseDTO(acc.user?.name, acc.user?.cpf)

        return AccountResponseDTO(acc.id, acc.limit, acc.balance, user)
    }

    fun getAll(): MutableList<Account> {
        return accountRepository.findAll()
    }

    fun getById(id: Long): Account {
        val account = accountRepository.findById(id)

        if (!account.isPresent) {
            throw BankException(404, "Conta não encontrada")
        }

        return account.get()
    }

    fun getByCpf(cpf: String): Account {
        val account = accountRepository.findByUserCpf(cpf)

        if (!account.isPresent) {
            throw BankException(404, "Conta não encontrada")
        }

        return account.get()
    }

    private fun findAccountByUser(cpf: String) {
        if (accountRepository.existsAccountByUserCpf(cpf)) {
            throw BankException(400, "Usuário já possui uma conta em seu nome")
        }
    }
}