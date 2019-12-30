package br.com.zup.bank.service.impl

import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.response.success.AccountBalanceDTO
import br.com.zup.bank.dto.response.success.AccountResponseDTO
import br.com.zup.bank.dto.response.success.UserAccountResponseDTO
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.exception.ResourceNotFoundException
import br.com.zup.bank.model.Account
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.UserRepository
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
    @Autowired
    private lateinit var userRepository: UserRepository

    override fun createAccount(accountRequestDTO: AccountRequestDTO): AccountResponseDTO {
        lateinit var acc: Account
        var user = userRepository.findByCpf(accountRequestDTO.cpf)

        if (!user.isPresent) {
            resourceNotFound(mutableListOf("Usuário não encontrado"))
        }

        findAccountByUser(user.get().cpf!!)

        val accountNumber = createAccountNumber()
        acc = Account(accountNumber = accountNumber, user = user.get(), isActive = true)
        acc = accountRepository.save(acc)

        return getAccountDTO(acc)
    }

    override fun getAll(): MutableList<AccountResponseDTO> {
        val accounts = accountRepository.findAll()
        val accResponse = mutableListOf<AccountResponseDTO>()

        accounts.forEach {
            accResponse.add(getAccountDTO(it))
        }

        return accResponse
    }

    override fun getById(id: Long): AccountResponseDTO {
        val account = accountRepository.findById(id)

        if (!account.isPresent) {
            resourceNotFound(mutableListOf("Conta não encontrada"))
        }

        return getAccountDTO(account.get())
    }

    override fun getByCpf(cpf: String): AccountResponseDTO {
        val account = accountRepository.findByUserCpf(cpf)

        if (!account.isPresent) {
            resourceNotFound(mutableListOf("Conta não encontrada"))
        }

        return getAccountDTO(account.get())
    }

    override fun getByAccountNumber(accNumber: String): AccountResponseDTO {
        val account = accountRepository.findByAccountNumber(accNumber)

        if (!account.isPresent) {
            resourceNotFound(mutableListOf("Conta não encontrada"))
        }

        return getAccountDTO(account.get())
    }

    override fun getAccountBalance(accNumber: String): AccountBalanceDTO {
        val account = accountRepository.findByAccountNumber(accNumber)

        if (!account.isPresent) {
            resourceNotFound(mutableListOf("Conta não encontrada"))
        }

        return AccountBalanceDTO(accNumber, account.get().balance)
    }

    private fun getAccountDTO(account: Account): AccountResponseDTO {
        val userAccResponse = UserAccountResponseDTO(account.user?.name, account.user?.cpf, account.user?.isActive)

        return AccountResponseDTO(account.limit, account.balance, account.accountNumber, account.isActive, userAccResponse)
    }

    private fun findAccountByUser(cpf: String) {
        if (accountRepository.existsAccountByUserCpf(cpf)) {
            badRequest(mutableListOf("Usuário já possui uma conta em seu nome"))
        }
    }

    private fun createAccountNumber(): String {
        val numbers = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
        var accNumber = ""

        for (number in numbers) {
            val j = (Math.random() * numbers.size).toInt()
            accNumber += numbers[j]
        }

        return accNumber
    }

    private fun resourceNotFound(errors: MutableList<String>) {
        if (errors.size > 0) {
            throw ResourceNotFoundException(errors)
        }
    }

    private fun badRequest(errors: MutableList<String>) {
        if (errors.size > 0) {
            throw BankException(400, errors)
        }
    }
}