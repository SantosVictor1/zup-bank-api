package br.com.zup.bank.service.impl

import br.com.zup.bank.dto.response.success.AccountResponseDTO
import br.com.zup.bank.dto.response.success.UserAccountResponseDTO
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.service.IAccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by Victor Santos on 26/12/2019
 */
@Service
class AccountServiceImpl : IAccountService {
    @Autowired
    private lateinit var accountRepository: AccountRepository

    override fun createAccount(user: User): Account {
        lateinit var acc: Account

        findAccountByUser(user.cpf!!)

        val accountNumber = createAccountNumber()
        acc = Account(accountNumber = accountNumber, user = user)

        return accountRepository.save(acc)
    }

    override fun getAll(): MutableList<Account> {
        return accountRepository.findAll()
    }

    override fun getById(id: Long): Account {
        val account = accountRepository.findById(id)

        if (!account.isPresent) {
            throw BankException(404, "Conta não encontrada")
        }

        return account.get()
    }

    override fun getByCpf(cpf: String): Account {
        val account = accountRepository.findByUserCpf(cpf)

        if (!account.isPresent) {
            throw BankException(404, "Conta não encontrada")
        }

        return account.get()
    }

    override fun getByAccountNumber(accNumber: String): Account {
        val account = accountRepository.findByAccountNumber(accNumber)

        if (!account.isPresent) {
            throw BankException(404, "Conta não encontrada")
        }

        return account.get()
    }

    override fun getAccountDTO(account: Account): AccountResponseDTO {
        val userAccResponse = UserAccountResponseDTO(account.user?.name, account.user?.cpf)

        return AccountResponseDTO(account.id, account.limit, account.balance, account.accountNumber, userAccResponse)
    }

    private fun findAccountByUser(cpf: String) {
        if (accountRepository.existsAccountByUserCpf(cpf)) {
            throw BankException(400, "Usuário já possui uma conta em seu nome")
        }
    }

    private fun createAccountNumber(): String {
        val numbers = arrayOf("0","1","2","3","4","5","6","7","8","9")
        var accNumber = ""

        for (number in numbers) {
            val j = (Math.random()*numbers.size).toInt()
            accNumber += numbers[j]
        }

        return accNumber
    }
}