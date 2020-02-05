package br.com.zup.bank.unitary.service

import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.*
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.InvalidResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.IActivityService
import br.com.zup.bank.service.impl.AccountServiceImpl
import com.nhaarman.mockito_kotlin.any
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.springframework.data.domain.PageRequest
import java.util.*

/**
 * Created by Victor Santos on 26/12/2019
 */
class AccountServiceTest {
    private val activityService = Mockito.mock(IActivityService::class.java)
    private val accountRepository = Mockito.mock(AccountRepository::class.java)
    private val userRepository = Mockito.mock(UserRepository::class.java)

    private val accountService: AccountServiceImpl = AccountServiceImpl(
        accountRepository,
        userRepository,
        activityService
    )
    private lateinit var account: Account
    private lateinit var user: User
    private lateinit var accountRequestDTO: AccountRequestDTO
    private lateinit var accountResponseDTO: AccountResponseDTO
    private lateinit var userAccountResponseDTO: UserAccountResponseDTO
    private lateinit var activityRequestDTO: ActivityRequestDTO
    private lateinit var activityResponseDTO: ActivityResponseDTO
    private lateinit var activity: Activity

//    @Before
//    fun setObjects() {
//        user = User(2, "Victor", "02160795607", "victor@gmail.com", true)
//        account = Account(1, 0.0, 1000.0, "7278424688", true, user)
//        accountRequestDTO = AccountRequestDTO("02160795607")
//        userAccountResponseDTO = UserAccountResponseDTO(user.name, user.cpf, user.isActive)
//        accountResponseDTO = AccountResponseDTO(
//            1,
//            1000.0,
//            0.0,
//            "7278424688",
//            true,
//            userAccountResponseDTO)
//
//        activityRequestDTO = ActivityRequestDTO(user.cpf, account.accountNumber, 100.0)
//        activity = Activity(id = 1, value = 100.0, operation = Operation.DEPOSIT, account = account)
//        activityResponseDTO = ActivityResponseDTO(account.balance, account.accountNumber, activity.activityDate, activity.operation.toString())
//    }
//
//    @Test
//    fun userDontExistsTest() {
//        Mockito.`when`(userRepository.findByCpf(user.cpf, true)).thenReturn(null)
//
//        val exception = assertThrows<ResourceNotFoundBankException>{ accountService.createAccount(accountRequestDTO) }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("User"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("cpf"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("user.not.found"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf, true)
//    }
//
//    @Test
//    fun userExistsTest() {
//        Mockito.`when`(userRepository.findByCpf(user.cpf, true)).thenReturn(user)
//        Mockito.`when`(accountRepository.existsAccountByUserCpf(user.cpf)).thenReturn(false)
//        Mockito.`when`(accountRepository.save(Mockito.any(Account::class.java))).thenReturn(account)
//
//        val response = accountService.createAccount(accountRequestDTO)
//
//        Assert.assertEquals(accountResponseDTO.id, response.id)
//        Assert.assertThat(accountResponseDTO.balance, CoreMatchers.`is`(response.balance))
//        Assert.assertThat(accountResponseDTO.limit, CoreMatchers.`is`(response.limit))
//        Assert.assertEquals(accountResponseDTO.user, response.user)
//        Assert.assertEquals(accountResponseDTO.isActive, response.isActive)
//
//        Mockito.verify(accountRepository, Mockito.times(1)).existsAccountByUserCpf(user.cpf)
//        Mockito.verify(accountRepository, Mockito.times(1)).save(Mockito.any(Account::class.java))
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf, true)
//    }
//
//    @Test
//    fun existAccountWithCpf() {
//        Mockito.`when`(userRepository.findByCpf(user.cpf, true)).thenReturn(user)
//        Mockito.`when`(accountRepository.existsAccountByUserCpf(user.cpf)).thenReturn(true)
//
//        val exception = assertThrows<DuplicatedResourceBankException> { accountService.createAccount(accountRequestDTO) }
//
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("double.account.not.allowed"))
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("Account"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`(""))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf, true)
//        Mockito.verify(accountRepository, Mockito.times(1)).existsAccountByUserCpf(user.cpf)
//    }
//
//    @Test
//    fun notExistAccountWithCpf() {
//        Mockito.`when`(userRepository.findByCpf(user.cpf, true)).thenReturn(user)
//        Mockito.`when`(accountRepository.existsAccountByUserCpf(user.cpf)).thenReturn(false)
//        Mockito.`when`(accountRepository.save(Mockito.any(Account::class.java))).thenReturn(account)
//
//        val response = accountService.createAccount(accountRequestDTO)
//
//        Assert.assertEquals(accountResponseDTO.id, response.id)
//        Assert.assertThat(accountResponseDTO.balance, CoreMatchers.`is`(response.balance))
//        Assert.assertThat(accountResponseDTO.limit, CoreMatchers.`is`(response.limit))
//        Assert.assertEquals(accountResponseDTO.user, response.user)
//        Assert.assertEquals(accountResponseDTO.isActive, response.isActive)
//
//        Mockito.verify(accountRepository, Mockito.times(1)).existsAccountByUserCpf(user.cpf)
//        Mockito.verify(accountRepository, Mockito.times(1)).save(Mockito.any(Account::class.java))
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf, true)
//    }
//
//    @Test
//    fun getAllTest() {
//        Mockito.`when`(accountRepository.findAll()).thenReturn(mutableListOf(account))
//
//        val response = accountService.getAll()
//
//        Assert.assertEquals(response, mutableListOf(accountResponseDTO))
//
//        Mockito.verify(accountRepository, Mockito.times(1)).findAll()
//    }
//
//    @Test
//    fun getByIdWithError() {
//        Mockito.`when`(accountRepository.findById(account.id!!)).thenReturn(Optional.empty())
//
//        val exception = assertThrows<ResourceNotFoundBankException> { accountService.getById(account.id!!) }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("Account"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("id"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("account.not.found"))
//
//        Mockito.verify(accountRepository, Mockito.times(1)).findById(account.id!!)
//    }
//
//    @Test
//    fun getByIdWithSuccess() {
//        Mockito.`when`(accountRepository.findById(account.id!!)).thenReturn(Optional.of(account))
//
//        val response = accountService.getById(account.id!!)
//
//        Assert.assertEquals(response, accountResponseDTO)
//
//        Mockito.verify(accountRepository, Mockito.times(1)).findById(account.id!!)
//    }
//
//    @Test
//    fun getByCpfWithError() {
//        Mockito.`when`(accountRepository.findByAccountNumberOrUserCpf("", user.cpf)).thenReturn(null)
//
//        val exception = assertThrows<ResourceNotFoundBankException> { accountService.getByAccountNumberOrCpf("", user.cpf) }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("Account"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("accountNumber"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("account.not.found"))
//
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf("", user.cpf)
//    }
//
//    @Test
//    fun getByCpfWithSuccess() {
//        Mockito.`when`(accountRepository.findByAccountNumberOrUserCpf("", user.cpf)).thenReturn(account)
//
//        val response = accountService.getByAccountNumberOrCpf("", user.cpf)
//
//        Assert.assertEquals(response, accountResponseDTO)
//
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf("", user.cpf)
//    }
//
//    @Test
//    fun getByAccountNumberWithError() {
//        Mockito.`when`(accountRepository.findByAccountNumberOrUserCpf(account.accountNumber, "")).thenReturn(null)
//
//        val exception = assertThrows<ResourceNotFoundBankException> { accountService.getByAccountNumberOrCpf(account.accountNumber, "") }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("Account"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("accountNumber"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("account.not.found"))
//
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(account.accountNumber, "")
//    }
//
//    @Test
//    fun getByAccountNumberWithSuccess() {
//        Mockito.`when`(accountRepository.findByAccountNumberOrUserCpf(account.accountNumber, "")).thenReturn(account)
//
//        val response = accountService.getByAccountNumberOrCpf(account.accountNumber, "")
//
//        Assert.assertEquals(response, accountResponseDTO)
//
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(account.accountNumber, "")
//    }
//
//    @Test
//    fun getAccountBalanceWithErrorTest() {
//        Mockito.`when`(accountRepository.findByAccountNumberAndIsActiveTrue(account.accountNumber)).thenReturn(null)
//
//        val exception = assertThrows<ResourceNotFoundBankException> { accountService.getAccountBalance(account.accountNumber) }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("Account"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("accountNumber"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("account.not.found"))
//
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberAndIsActiveTrue(account.accountNumber)
//    }
//
//    @Test
//    fun getAccountBalanceWithErrorSuccess() {
//        val accountBalance = AccountBalanceDTO("7278424688", 0.0)
//        Mockito.`when`(accountRepository.findByAccountNumberAndIsActiveTrue(account.accountNumber)).thenReturn(account)
//
//        val response = accountService.getAccountBalance(account.accountNumber)
//
//        Assert.assertEquals(response, accountBalance)
//
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberAndIsActiveTrue(account.accountNumber)
//    }
//
//    @Test
//    fun deactivateAccountWithSuccess() {
//        Mockito.`when`(accountRepository.findByUserCpf(user.cpf)).thenReturn(account)
//        Mockito.`when`(accountRepository.save(account)).thenReturn(account)
//
//        accountService.deactivateAccount(user.cpf)
//
//        Assert.assertThat(account.isActive, CoreMatchers.`is`(false))
//
//        Mockito.verify(accountRepository, Mockito.times(1)).findByUserCpf(user.cpf)
//        Mockito.verify(accountRepository, Mockito.times(1)).save(account)
//    }
//
//    @Test
//    fun reactivateAccountWithSuccess() {
//        Mockito.`when`(accountRepository.findByUserCpf(user.cpf)).thenReturn(account)
//        Mockito.`when`(accountRepository.save(account)).thenReturn(account)
//
//        accountService.reactivateAccount(user.cpf)
//
//        Assert.assertThat(account.isActive, CoreMatchers.`is`(true))
//
//        Mockito.verify(accountRepository, Mockito.times(1)).findByUserCpf(user.cpf)
//        Mockito.verify(accountRepository, Mockito.times(1)).save(account)
//    }
//
//    @Test
//    fun depositWithUserNotFound() {
//        Mockito.`when`(userRepository.findByCpf(user.cpf, true)).thenReturn(null)
//
//        val exception = assertThrows<ResourceNotFoundBankException> { accountService.deposit(activityRequestDTO) }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("User"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("cpf"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("user.not.found"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf, true)
//    }
//
//    @Test
//    fun depositWithAccountNotFound() {
//        Mockito.`when`(userRepository.findByCpf(activityRequestDTO.cpf, true)).thenReturn(user)
//        Mockito.`when`(accountRepository.findByAccountNumberOrUserCpf(activityRequestDTO.accNumber,"")).thenReturn(null)
//
//        val exception = assertThrows<ResourceNotFoundBankException> { accountService.deposit(activityRequestDTO) }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("Account"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("accountNumber"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("account.not.found"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(activityRequestDTO.cpf, true)
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")
//    }
//
//    @Test
//    fun depositWithUserCpfAndAccountUserCpfDifferent() {
//        user.cpf = "12345678912"
//        Mockito.`when`(userRepository.findByCpf(activityRequestDTO.cpf, true)).thenReturn(user)
//        Mockito.`when`(accountRepository.findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")).thenReturn(account)
//
//        val exception = assertThrows<InvalidResourceBankException> { accountService.deposit(activityRequestDTO) }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("ActivityRequestDTO"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("cpf"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("cpf.invalid"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(activityRequestDTO.cpf, true)
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")
//    }
//
//    @Test
//    fun depositWithValueSmallerThanZero() {
//        activityRequestDTO.value = -50.0
//        user.cpf = activityRequestDTO.cpf
//
//        Mockito.`when`(userRepository.findByCpf(activityRequestDTO.cpf, true)).thenReturn(user)
//        Mockito.`when`(accountRepository.findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")).thenReturn(account)
//
//        val exception = assertThrows<InvalidResourceBankException> { accountService.deposit(activityRequestDTO) }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("ActivityRequestDTO"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("value"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("value.invalid"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(activityRequestDTO.cpf, true)
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")
//    }
//
//    @Test
//    fun depositWithSuccess() {
//        user.cpf = activityRequestDTO.cpf
//        activityRequestDTO.operation = Operation.DEPOSIT
//        activityResponseDTO.balance = 100.0
//
//        Mockito.`when`(userRepository.findByCpf(activityRequestDTO.cpf, true)).thenReturn(user)
//        Mockito.`when`(accountRepository.findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")).thenReturn(account)
//        Mockito.`when`(accountRepository.save(account)).thenReturn(account)
//        Mockito.`when`(activityService.createActivity(any(), any())).thenAnswer { activityResponseDTO }
//
//        val response = accountService.deposit(activityRequestDTO)
//        val balance = activityRequestDTO.value + account.balance
//
//        Assert.assertEquals(response.accNumber, activityRequestDTO.accNumber)
//        Assert.assertEquals(response.balance, balance)
//        Assert.assertEquals(response.operation, activityRequestDTO.operation.toString())
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(activityRequestDTO.cpf, true)
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")
//        Mockito.verify(accountRepository, Mockito.times(1)).save(Mockito.any(Account::class.java))
//    }
//
//    @Test
//    fun withdrawWithUserNotFound() {
//        Mockito.`when`(userRepository.findByCpf(user.cpf, true)).thenReturn(null)
//
//        val exception = assertThrows<ResourceNotFoundBankException> { accountService.withdraw(activityRequestDTO) }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("User"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("cpf"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("user.not.found"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf, true)
//    }
//
//    @Test
//    fun withdrawWithAccountNotFound() {
//        Mockito.`when`(userRepository.findByCpf(activityRequestDTO.cpf, true)).thenReturn(user)
//        Mockito.`when`(accountRepository.findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")).thenReturn(null)
//
//        val exception = assertThrows<ResourceNotFoundBankException> { accountService.withdraw(activityRequestDTO) }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("Account"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("accountNumber"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("account.not.found"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(activityRequestDTO.cpf, true)
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")
//    }
//
//    @Test
//    fun withdrawWithUserCpfAndAccountUserCpfDifferent() {
//        Mockito.`when`(userRepository.findByCpf(activityRequestDTO.cpf, true)).thenReturn(user)
//        Mockito.`when`(accountRepository.findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")).thenReturn(account)
//
//        val exception = assertThrows<InvalidResourceBankException> { accountService.withdraw(activityRequestDTO) }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("Account"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("balance"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("negative.balance"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(activityRequestDTO.cpf, true)
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")
//    }
//
//    @Test
//    fun withdrawWithValueSmallerThanZero() {
//        activityRequestDTO.value = -50.0
//        user.cpf = activityRequestDTO.cpf
//
//        Mockito.`when`(userRepository.findByCpf(activityRequestDTO.cpf, true)).thenReturn(user)
//        Mockito.`when`(accountRepository.findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")).thenReturn(account)
//
//        val exception = assertThrows<InvalidResourceBankException> { accountService.withdraw(activityRequestDTO) }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("ActivityRequestDTO"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("value"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("value.invalid"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(activityRequestDTO.cpf, true)
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")
//    }
//
//    @Test
//    fun withdrawWithBalanceBelowZero() {
//        activityRequestDTO.value = 300.0
//        user.cpf = activityRequestDTO.cpf
//
//        Mockito.`when`(userRepository.findByCpf(activityRequestDTO.cpf, true)).thenReturn(user)
//        Mockito.`when`(accountRepository.findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")).thenReturn(account)
//
//        val exception = assertThrows<InvalidResourceBankException> { accountService.withdraw(activityRequestDTO) }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("Account"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("balance"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("negative.balance"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(activityRequestDTO.cpf, true)
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")
//    }
//
//    @Test
//    fun withdrawWithSuccess() {
//        user.cpf = activityRequestDTO.cpf
//        activityRequestDTO.operation = Operation.WITHDRAW
//        activity.operation = Operation.WITHDRAW
//        account.balance = 200.0
//        activityResponseDTO.operation = Operation.WITHDRAW.toString()
//        activityResponseDTO.balance = 100.0
//
//        Mockito.`when`(userRepository.findByCpf(activityRequestDTO.cpf, true)).thenReturn(user)
//        Mockito.`when`(accountRepository.findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")).thenReturn(account)
//        Mockito.`when`(accountRepository.save(account)).thenReturn(account)
//        Mockito.`when`(activityService.createActivity(any(), any())).thenAnswer { activityResponseDTO }
//
//        val response = accountService.withdraw(activityRequestDTO)
//        val balance = account.balance - activityRequestDTO.value
//
//        Assert.assertEquals(response.accNumber, activityRequestDTO.accNumber)
//        Assert.assertEquals(response.balance, balance)
//        Assert.assertEquals(response.operation, activityRequestDTO.operation.toString())
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(activityRequestDTO.cpf, true)
//        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(activityRequestDTO.accNumber, "")
//        Mockito.verify(accountRepository, Mockito.times(1)).save(Mockito.any(Account::class.java))
//    }
//
//    @Test
//    fun extractWithAccountNotFound() {
//        Mockito.`when`(accountRepository.existsAccountByAccountNumber(account.accountNumber)).thenReturn(false)
//
//        val exception = assertThrows<ResourceNotFoundBankException> { accountService.extract(account.accountNumber, 0, 10) }
//
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("Account"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("accountNumber"))
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("account.not.found"))
//
//        Mockito.verify(accountRepository, Mockito.times(1)).existsAccountByAccountNumber(account.accountNumber)
//    }
//
//    @Test
//    fun extractWithSuccess() {
//        val paginationDTO = PaginationResponseDTO(0, 10)
//        val pageRequest = PageRequest.of(0, 10)
//        val extractResponseDTO = ExtractResponseDTO(mutableListOf(), paginationDTO)
//
//        Mockito.`when`(accountRepository.existsAccountByAccountNumber(account.accountNumber)).thenReturn(true)
//        Mockito.`when`(activityService.extract(account.accountNumber, pageRequest)).thenReturn(extractResponseDTO)
//
//        val response = accountService.extract(account.accountNumber, 0, 10)
//
//        Assert.assertEquals(response, ExtractResponseDTO(mutableListOf(), paginationDTO))
//
//        Mockito.verify(accountRepository, Mockito.times(1)).existsAccountByAccountNumber(account.accountNumber)
//        Mockito.verify(activityService, Mockito.times(1)).extract(account.accountNumber, pageRequest)
//    }
}