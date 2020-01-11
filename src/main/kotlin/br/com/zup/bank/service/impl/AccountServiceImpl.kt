package br.com.zup.bank.service.impl

import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.AccountBalanceDTO
import br.com.zup.bank.dto.response.success.AccountResponseDTO
import br.com.zup.bank.dto.response.success.ActivityResponseDTO
import br.com.zup.bank.dto.response.success.ExtractResponseDTO
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.InvalidResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.IAccountService
import br.com.zup.bank.service.IActivityService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by Victor Santos on 26/12/2019
 */
@Service
class AccountServiceImpl(
    val accountRepository: AccountRepository,
    val userRepository: UserRepository,
    val activityService: IActivityService
) : IAccountService {
    override fun createAccount(accountRequestDTO: AccountRequestDTO): AccountResponseDTO {
        lateinit var acc: Account
        var user = getUser(accountRequestDTO.cpf)

        findAccountByUser(user.cpf)

        val accountNumber = createAccountNumber()
        acc = Account(accountNumber = accountNumber, user = user, isActive = true)
        acc = accountRepository.save(acc)

        return AccountResponseDTO.toResponseDto(acc)
    }

    override fun getAll(): MutableList<AccountResponseDTO> {
        val accounts = accountRepository.findAll()
        val accResponse = mutableListOf<AccountResponseDTO>()

        accounts.forEach {
            accResponse.add(AccountResponseDTO.toResponseDto(it))
        }

        return accResponse
    }

    override fun getById(id: Long): AccountResponseDTO {
        val account = accountRepository.findById(id)

        if (!account.isPresent) {
            resourceNotFoundException(BankErrorCode.BANK022.code, "", "Account")
        }

        return AccountResponseDTO.toResponseDto(account.get())
    }

    override fun getByAccountNumberOrCpf(accNumber: String, cpf: String): AccountResponseDTO {
        val account = accountRepository.findByAccountNumberOrUserCpf(cpf, accNumber)

        if (account == null) {
            resourceNotFoundException(BankErrorCode.BANK022.code, "", "Account")
        }

        return AccountResponseDTO.toResponseDto(account!!)
    }

    override fun getAccountBalance(accNumber: String): AccountBalanceDTO {
        val account = accountRepository.findByAccountNumberAndIsActiveTrue(accNumber)

        if (account == null) {
            resourceNotFoundException(BankErrorCode.BANK022.code, "", "Account")
        }

        return AccountBalanceDTO(accNumber, account?.balance)
    }

    override fun deactivateAccount(cpf: String) {
        var account = accountRepository.findByUserCpf(cpf)

        if (account != null) {
            account?.isActive = false
            accountRepository.save(account!!)
        }
    }

    override fun reactivateAccount(cpf: String) {
        var account = accountRepository.findByUserCpf(cpf)

        if (account != null) {
            account?.isActive = true
            accountRepository.save(account)
        }
    }

    @Transactional
    override fun deposit(activityRequestDTO: ActivityRequestDTO): ActivityResponseDTO {
        val user = getUser(activityRequestDTO.cpf)
        val account = Account.toEntity(getByAccountNumberOrCpf(activityRequestDTO.accNumber, ""), user)

        validateOperation(account, activityRequestDTO)

        account.balance += activityRequestDTO.value

        accountRepository.save(account)

        return activityService.createActivity(account, activityRequestDTO)
    }

    @Transactional
    override fun withdraw(activityRequestDTO: ActivityRequestDTO): ActivityResponseDTO {
        val user = getUser(activityRequestDTO.cpf)
        val account = Account.toEntity(getByAccountNumberOrCpf(activityRequestDTO.accNumber, ""), user)

        validateOperation(account, activityRequestDTO)

        account.balance -= activityRequestDTO.value

        if (account.balance < 0.0) {
            invalidResourceException(BankErrorCode.BANK024.code, "balance", "activityRequestDTO")
        }

        accountRepository.save(account)

        return activityService.createActivity(account, activityRequestDTO)
    }

    override fun extract(accNumber: String, page: Int, size: Int): ExtractResponseDTO {
        existsByNumber(accNumber)

        var pageRequest = PageRequest.of(page, size)

        return activityService.extract(accNumber, pageRequest)
    }

    private fun validateOperation(account: Account, activityRequestDTO: ActivityRequestDTO) {
        if (account.user?.cpf != activityRequestDTO.cpf) {
            invalidResourceException(BankErrorCode.BANK014.code, "cpf", "activityRequestDTO")
        }

        if (activityRequestDTO.value <= 0.0) {
            invalidResourceException(BankErrorCode.BANK040.code, "value", "activityRequestDTO")
        }
    }

    private fun existsByNumber(accNumber: String) {
        if (!accountRepository.existsAccountByAccountNumber(accNumber)) {
            resourceNotFoundException(BankErrorCode.BANK022.code, "", "Account")
        }
    }

    private fun getUser(cpf: String): User {
        val user = userRepository.findByCpf(cpf, true)

        if (user == null) {
            resourceNotFoundException(BankErrorCode.BANK018.code, "", "User")
        }

        return user!!
    }

    private fun findAccountByUser(cpf: String) {
        if (accountRepository.existsAccountByUserCpf(cpf)) {
            duplicatedResourceException(BankErrorCode.BANK023.code, "", "Account")
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

    private fun resourceNotFoundException(errorCode: String, field: String, objectName: String) {
        throw ResourceNotFoundBankException(errorCode, field, objectName)
    }

    private fun duplicatedResourceException(errorCode: String, field: String, objectName: String) {
        throw DuplicatedResourceBankException(errorCode, field, objectName)
    }

    private fun invalidResourceException(errorCode: String, field: String, objectName: String) {
        throw InvalidResourceBankException(errorCode, field, objectName)
    }
}