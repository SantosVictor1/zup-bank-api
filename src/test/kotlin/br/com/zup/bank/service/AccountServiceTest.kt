package br.com.zup.bank.service

import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.*
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.exception.DuplicatedResourceException
import br.com.zup.bank.exception.InvalidResourceException
import br.com.zup.bank.exception.ResourceNotFoundException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.ActivityRepository
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.impl.AccountServiceImpl
import br.com.zup.bank.service.impl.ActivityServiceImpl
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.util.*

/**
 * Created by Victor Santos on 26/12/2019
 */
class AccountServiceTest {
    private val activityService: ActivityServiceImpl = ActivityServiceImpl(
        Mockito.mock(ActivityRepository::class.java)
    )

    private val accountService: AccountServiceImpl = AccountServiceImpl(
        Mockito.mock(AccountRepository::class.java),
        Mockito.mock(UserRepository::class.java),
        activityService
    )
    private lateinit var acc: Account
    private lateinit var user: User
    private lateinit var accRequestDTO: AccountRequestDTO
    private lateinit var accResponse: AccountResponseDTO
    private lateinit var userAccountResponseDTO: UserAccountResponseDTO
    private lateinit var activityDTO: ActivityRequestDTO
    private lateinit var activity: Activity

    @Before
    fun setObjects() {
        user = User(2, "Victor", "02160795607", "victor@gmail.com", true)
        acc = Account(1, 0.0, 1000.0, "7278424688", true, user)
        accRequestDTO = AccountRequestDTO("02160795607")
        userAccountResponseDTO = UserAccountResponseDTO(user.name, user.cpf, user.isActive)
        accResponse = AccountResponseDTO(
            1,
            1000.0,
            0.0,
            "7278424688",
            true,
            userAccountResponseDTO)

        activityDTO = ActivityRequestDTO("02160795607", "7278424688", 100.0)

        activity = Activity(id = 1, value = 100.0, operation = Operation.DEPOSIT, account = acc)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun userDontExistsTest() {
        Mockito.`when`(accountService.userRepository.findByCpf(user.cpf, false)).thenReturn(null)

        accountService.createAccount(accRequestDTO)
    }

    @Test
    fun userExistsTest() {
        Mockito.`when`(accountService.userRepository.findByCpf(user.cpf, true)).thenReturn(user)
        Mockito.`when`(accountService.accountRepository.existsAccountByUserCpf(user.cpf)).thenReturn(false)
        Mockito.`when`(accountService.accountRepository.save(Mockito.any(Account::class.java))).thenReturn(acc)

        val accountResponse = accountService.createAccount(accRequestDTO)

        Assert.assertEquals(accResponse.id, accountResponse.id)
        Assert.assertThat(accResponse.balance, CoreMatchers.`is`(accountResponse.balance))
        Assert.assertThat(accResponse.limit, CoreMatchers.`is`(accountResponse.limit))
        Assert.assertEquals(accResponse.user, accountResponse.user)
        Assert.assertEquals(accResponse.isActive, accountResponse.isActive)

        Mockito.verify(accountService.accountRepository, Mockito.times(1)).existsAccountByUserCpf(user.cpf)
        Mockito.verify(accountService.accountRepository, Mockito.times(1)).save(Mockito.any(Account::class.java))
        Mockito.verify(accountService.userRepository, Mockito.times(1)).findByCpf(user.cpf, true)
    }

    @Test(expected = DuplicatedResourceException::class)
    fun existAccountWithCpf() {
        Mockito.`when`(accountService.userRepository.findByCpf(user.cpf, true)).thenReturn(user)
        Mockito.`when`(accountService.accountRepository.existsAccountByUserCpf(user.cpf)).thenReturn(true)

        accountService.createAccount(accRequestDTO)
    }

    @Test
    fun notExistAccountWithCpf() {
        Mockito.`when`(accountService.userRepository.findByCpf(user.cpf, true)).thenReturn(user)
        Mockito.`when`(accountService.accountRepository.existsAccountByUserCpf(user.cpf)).thenReturn(false)
        Mockito.`when`(accountService.accountRepository.save(Mockito.any(Account::class.java))).thenReturn(acc)

        val accountResponse = accountService.createAccount(accRequestDTO)

        Assert.assertEquals(accResponse.id, accountResponse.id)
        Assert.assertThat(accResponse.balance, CoreMatchers.`is`(accountResponse.balance))
        Assert.assertThat(accResponse.limit, CoreMatchers.`is`(accountResponse.limit))
        Assert.assertEquals(accResponse.user, accountResponse.user)
        Assert.assertEquals(accResponse.isActive, accountResponse.isActive)

        Mockito.verify(accountService.accountRepository, Mockito.times(1)).existsAccountByUserCpf(user.cpf)
        Mockito.verify(accountService.accountRepository, Mockito.times(1)).save(Mockito.any(Account::class.java))
        Mockito.verify(accountService.userRepository, Mockito.times(1)).findByCpf(user.cpf, true)
    }

    @Test
    fun getAllTest() {
        Mockito.`when`(accountService.accountRepository.findAll()).thenReturn(mutableListOf(acc))

        val accounts = accountService.getAll()

        Assert.assertEquals(accounts, mutableListOf(accResponse))

        Mockito.verify(accountService.accountRepository, Mockito.times(1)).findAll()
    }

    @Test(expected = ResourceNotFoundException::class)
    fun getByIdWithError() {
        Mockito.`when`(accountService.accountRepository.findById(acc.id!!)).thenReturn(Optional.empty())

        accountService.getById(acc.id!!)
    }

    @Test
    fun getByIdWithSuccess() {
        Mockito.`when`(accountService.accountRepository.findById(acc.id!!)).thenReturn(Optional.of(acc))

        val accountResponse = accountService.getById(acc.id!!)

        Assert.assertEquals(accountResponse, accResponse)

        Mockito.verify(accountService.accountRepository, Mockito.times(1)).findById(acc.id!!)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun getByCpfWithError() {
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf(user.cpf, "")).thenReturn(Optional.empty())

        accountService.getByAccountNumberOrCpf("", user.cpf)
    }

    @Test
    fun getByCpfWithSuccess() {
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf(user.cpf, "")).thenReturn(Optional.of(acc))

        val accountResponse = accountService.getByAccountNumberOrCpf("", user.cpf)

        Assert.assertEquals(accountResponse, accResponse)

        Mockito.verify(accountService.accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(user.cpf, "")
    }

    @Test(expected = ResourceNotFoundException::class)
    fun getByAccountNumberWithError() {
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf("", acc.accountNumber)).thenReturn(Optional.empty())

        accountService.getByAccountNumberOrCpf("", acc.accountNumber)
    }

    @Test
    fun getByAccountNumberWithSuccess() {
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf("", acc.accountNumber)).thenReturn(Optional.of(acc))

        val accountResponse = accountService.getByAccountNumberOrCpf(acc.accountNumber, "")

        Assert.assertEquals(accountResponse, accResponse)

        Mockito.verify(accountService.accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf("", acc.accountNumber)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun getAccountBalanceWithErrorTest() {
        Mockito.`when`(accountService.accountRepository.findByAccountNumberAndIsActiveTrue(acc.accountNumber)).thenReturn(Optional.empty())

        accountService.getAccountBalance(acc.accountNumber)
    }

    @Test
    fun getAccountBalanceWithErrorSuccess() {
        val accountBalance = AccountBalanceDTO("7278424688", 0.0)
        Mockito.`when`(accountService.accountRepository.findByAccountNumberAndIsActiveTrue(acc.accountNumber)).thenReturn(Optional.of(acc))

        val accBalance = accountService.getAccountBalance(acc.accountNumber)

        Assert.assertEquals(accBalance, accountBalance)

        Mockito.verify(accountService.accountRepository, Mockito.times(1)).findByAccountNumberAndIsActiveTrue(acc.accountNumber)
    }

    @Test
    fun deactivateAccountWithSuccess() {
        Mockito.`when`(accountService.accountRepository.findByUserCpf(user.cpf)).thenReturn(Optional.of(acc))
        Mockito.`when`(accountService.accountRepository.save(acc)).thenReturn(acc)

        accountService.deactivateAccount(user.cpf)

        Assert.assertThat(acc.isActive, CoreMatchers.`is`(false))

        Mockito.verify(accountService.accountRepository, Mockito.times(1)).findByUserCpf(user.cpf)
        Mockito.verify(accountService.accountRepository, Mockito.times(1)).save(acc)
    }

    @Test
    fun reactivateAccountWithSuccess() {
        Mockito.`when`(accountService.accountRepository.findByUserCpf(user.cpf)).thenReturn(Optional.of(acc))
        Mockito.`when`(accountService.accountRepository.save(acc)).thenReturn(acc)

        accountService.reactivateAccount(user.cpf)

        Assert.assertThat(acc.isActive, CoreMatchers.`is`(true))

        Mockito.verify(accountService.accountRepository, Mockito.times(1)).findByUserCpf(user.cpf)
        Mockito.verify(accountService.accountRepository, Mockito.times(1)).save(acc)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun depositWithUserNotFound() {
        Mockito.`when`(accountService.userRepository.findByCpf(user.cpf, false)).thenReturn(null)

        accountService.deposit(activityDTO)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun depositWithAccountNotFound() {
        Mockito.`when`(accountService.userRepository.findByCpf(activityDTO.cpf, true)).thenReturn(user)
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf("", activityDTO.accNumber)).thenReturn(Optional.empty())

        accountService.deposit(activityDTO)
    }

    @Test(expected = InvalidResourceException::class)
    fun depositWithUserCpfAndAccountUserCpfDifferent() {
        user.cpf = "12345678912"
        Mockito.`when`(accountService.userRepository.findByCpf(activityDTO.cpf, true)).thenReturn(user)
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf("", activityDTO.accNumber)).thenReturn(Optional.of(acc))

        accountService.deposit(activityDTO)
    }

    @Test(expected = InvalidResourceException::class)
    fun depositWithValueSmallerThanZero() {
        activityDTO.value = -50.0
        user.cpf = activityDTO.cpf

        Mockito.`when`(accountService.userRepository.findByCpf(activityDTO.cpf, true)).thenReturn(user)
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf("", activityDTO.accNumber)).thenReturn(Optional.of(acc))

        accountService.deposit(activityDTO)
    }

    @Test
    fun depositWithSuccess() {
        user.cpf = activityDTO.cpf
        activityDTO.operation = Operation.DEPOSIT

        Mockito.`when`(accountService.userRepository.findByCpf(this.activityDTO.cpf, true)).thenReturn(user)
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf("", this.activityDTO.accNumber)).thenReturn(Optional.of(acc))
        Mockito.`when`(accountService.accountRepository.save(acc)).thenReturn(acc)
        Mockito.`when`(activityService.activityRepository.save(Mockito.any(Activity::class.java))).thenReturn(activity)

        val activityDTO = accountService.deposit(this.activityDTO)
        val balance = this.activityDTO.value + acc.balance

        Assert.assertEquals(activityDTO.accNumber, this.activityDTO.accNumber)
        Assert.assertEquals(activityDTO.balance, balance)
        Assert.assertEquals(activityDTO.operation, this.activityDTO.operation.toString())

        Mockito.verify(accountService.userRepository, Mockito.times(1)).findByCpf(this.activityDTO.cpf, true)
        Mockito.verify(accountService.accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf("", this.activityDTO.accNumber)
        Mockito.verify(accountService.accountRepository, Mockito.times(1)).save(Mockito.any(Account::class.java))
        Mockito.verify(activityService.activityRepository, Mockito.times(1)).save(Mockito.any(Activity::class.java))
    }

    @Test(expected = ResourceNotFoundException::class)
    fun withdrawWithUserNotFound() {
        Mockito.`when`(accountService.userRepository.findByCpf(user.cpf, false)).thenReturn(null)

        accountService.withdraw(activityDTO)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun withdrawWithAccountNotFound() {
        Mockito.`when`(accountService.userRepository.findByCpf(activityDTO.cpf, true)).thenReturn(user)
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf("", activityDTO.accNumber)).thenReturn(Optional.empty())

        accountService.withdraw(activityDTO)
    }

    @Test(expected = InvalidResourceException::class)
    fun withdrawWithUserCpfAndAccountUserCpfDifferent() {
        Mockito.`when`(accountService.userRepository.findByCpf(activityDTO.cpf, true)).thenReturn(user)
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf("", activityDTO.accNumber)).thenReturn(Optional.of(acc))

        accountService.withdraw(activityDTO)
    }

    @Test(expected = InvalidResourceException::class)
    fun withdrawWithValueSmallerThanZero() {
        activityDTO.value = -50.0
        user.cpf = activityDTO.cpf

        Mockito.`when`(accountService.userRepository.findByCpf(activityDTO.cpf, true)).thenReturn(user)
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf("", activityDTO.accNumber)).thenReturn(Optional.of(acc))

        accountService.withdraw(activityDTO)
    }

    @Test(expected = InvalidResourceException::class)
    fun withdrawWithBalanceBelowZero() {
        activityDTO.value = 300.0
        user.cpf = activityDTO.cpf

        Mockito.`when`(accountService.userRepository.findByCpf(activityDTO.cpf, true)).thenReturn(user)
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf("", activityDTO.accNumber)).thenReturn(Optional.of(acc))

        accountService.withdraw(activityDTO)
    }

    @Test
    fun withdrawWithSuccess() {
        user.cpf = activityDTO.cpf
        this.activityDTO.operation = Operation.WITHDRAW
        activity.operation = Operation.WITHDRAW
        acc.balance = 200.0

        Mockito.`when`(accountService.userRepository.findByCpf(this.activityDTO.cpf, true)).thenReturn(user)
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf("", this.activityDTO.accNumber)).thenReturn(Optional.of(acc))
        Mockito.`when`(accountService.accountRepository.save(acc)).thenReturn(acc)
        Mockito.`when`(activityService.activityRepository.save(Mockito.any(Activity::class.java))).thenReturn(activity)

        val activityDTO = accountService.withdraw(this.activityDTO)
        val balance = acc.balance - this.activityDTO.value

        Assert.assertEquals(activityDTO.accNumber, this.activityDTO.accNumber)
        Assert.assertEquals(activityDTO.balance, balance)
        Assert.assertEquals(activityDTO.operation, this.activityDTO.operation.toString())

        Mockito.verify(accountService.userRepository, Mockito.times(1)).findByCpf(this.activityDTO.cpf, true)
        Mockito.verify(accountService.accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf("", this.activityDTO.accNumber)
        Mockito.verify(accountService.accountRepository, Mockito.times(1)).save(Mockito.any(Account::class.java))
        Mockito.verify(activityService.activityRepository, Mockito.times(1)).save(Mockito.any(Activity::class.java))
    }

    @Test(expected = ResourceNotFoundException::class)
    fun extracWithAccountNotFound() {
        Mockito.`when`(accountService.accountRepository.existsAccountByAccountNumber(acc.accountNumber)).thenReturn(false)

        accountService.extract(acc.accountNumber, 0, 10)
    }

    @Test
    fun extractWithSuccess() {
        val paginationDTO = PaginationResponseDTO(0, 10)
        var pageRequest = PageRequest.of(0, 10)

        Mockito.`when`(accountService.accountRepository.existsAccountByAccountNumber(acc.accountNumber)).thenReturn(true)
        Mockito.`when`(activityService.activityRepository.findAllByAccountAccountNumberOrderByActivityDateDesc(acc.accountNumber, pageRequest))
            .thenReturn((Page.empty(pageRequest)))

        val extracts = accountService.extract(acc.accountNumber, 0, 10)

        Assert.assertEquals(extracts, ExtractResponseDTO(mutableListOf(), paginationDTO))

        Mockito.verify(accountService.accountRepository, Mockito.times(1))
            .existsAccountByAccountNumber(acc.accountNumber)
        Mockito.verify(activityService.activityRepository, Mockito.times(1))
            .findAllByAccountAccountNumberOrderByActivityDateDesc(activityDTO.accNumber, pageRequest)
    }
}