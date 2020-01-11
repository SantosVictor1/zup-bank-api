package br.com.zup.bank.unitary.controller

import br.com.zup.bank.controller.AccountController
import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.*
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.InvalidResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.service.IAccountService
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import java.util.*

/**
 * Created by Victor Santos on 10/01/2020
 */
class AccountControllerUnitaryTest {
    private val accountService = Mockito.mock(IAccountService::class.java)
    private val accountController = AccountController(
        accountService
    )
    private lateinit var accountRequestDTO: AccountRequestDTO
    private lateinit var accountResponseDTO: AccountResponseDTO
    private lateinit var userAccountResponseDTO: UserAccountResponseDTO
    private lateinit var activityRequestDTO: ActivityRequestDTO
    private lateinit var activityResponseDTO: ActivityResponseDTO

    @Before
    fun setMocks() {
        userAccountResponseDTO = UserAccountResponseDTO("Victor", "02160795607", true)
        accountRequestDTO = AccountRequestDTO("02160795607")
        accountResponseDTO = AccountResponseDTO(1, 1000.0, 100.0, "4658132467", true, userAccountResponseDTO)

        activityRequestDTO = ActivityRequestDTO(userAccountResponseDTO.cpf!!, accountResponseDTO.accountNumber, 100.0)
        activityResponseDTO = ActivityResponseDTO(accountResponseDTO.balance + 100.0, accountResponseDTO.accountNumber, Date(), Operation.DEPOSIT.toString())
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun throwAnExceptionWhenNotFindUser() {
        Mockito.`when`(accountService.createAccount(accountRequestDTO)).thenThrow(ResourceNotFoundBankException::class.java)

        accountController.newAccount(accountRequestDTO)
    }

    @Test(expected = DuplicatedResourceBankException::class)
    fun throwAnExceptionWhenUserAlreadyHasAnAccount() {
        Mockito.`when`(accountService.createAccount(accountRequestDTO)).thenThrow(DuplicatedResourceBankException::class.java)

        accountController.newAccount(accountRequestDTO)
    }

    @Test
    fun createNewAccountWithSuccess() {
        Mockito.`when`(accountService.createAccount(accountRequestDTO)).thenReturn(accountResponseDTO)

        val response = accountController.newAccount(accountRequestDTO)

        Assert.assertEquals(response.statusCodeValue, HttpStatus.CREATED.value())
        Assert.assertEquals(response.body, accountResponseDTO)

        Mockito.verify(accountService, Mockito.times(1)).createAccount(accountRequestDTO)
    }

    @Test
    fun getAllAccountsWithSuccess() {
        Mockito.`when`(accountService.getAll()).thenReturn(mutableListOf(accountResponseDTO))

        val response = accountController.getAllAccounts()

        Assert.assertEquals(response.statusCodeValue, HttpStatus.OK.value())
        Assert.assertEquals(response.body, mutableListOf(accountResponseDTO))

        Mockito.verify(accountService, Mockito.times(1)).getAll()
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun throwAnExceptionWhenNotFindAnAccountById() {
        Mockito.`when`(accountService.getById(1)).thenThrow(ResourceNotFoundBankException::class.java)

        accountController.getById(1)
    }

    @Test
    fun getByIdWithSuccess() {
        Mockito.`when`(accountService.getById(1)).thenReturn(accountResponseDTO)

        val response = accountController.getById(1)

        Assert.assertEquals(response.statusCodeValue, HttpStatus.OK.value())
        Assert.assertEquals(response.body, accountResponseDTO)

        Mockito.verify(accountService, Mockito.times(1)).getById(1)
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun throwExceptionWhenNotFindAccountByUserCpf() {
        Mockito.`when`(accountService.getByAccountNumberOrCpf("", accountRequestDTO.cpf))
            .thenThrow(ResourceNotFoundBankException::class.java)

        accountController.getByAccountNumberOrCpf("", accountRequestDTO.cpf)
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun throwExceptionWhenNotFindAccountByAccountNumber() {
        Mockito.`when`(accountService.getByAccountNumberOrCpf(accountResponseDTO.accountNumber, ""))
            .thenThrow(ResourceNotFoundBankException::class.java)

        accountController.getByAccountNumberOrCpf(accountResponseDTO.accountNumber, "")
    }

    @Test
    fun getAccountByAccountNumberOrCpfWithSuccess() {
        Mockito.`when`(accountService.getByAccountNumberOrCpf(accountResponseDTO.accountNumber, accountRequestDTO.cpf))
            .thenReturn(accountResponseDTO)

        val response = accountController.getByAccountNumberOrCpf(accountResponseDTO.accountNumber, accountRequestDTO.cpf)

        Assert.assertEquals(response.statusCodeValue, HttpStatus.OK.value())
        Assert.assertEquals(response.body, accountResponseDTO)

        Mockito.verify(accountService, Mockito.times(1)).getByAccountNumberOrCpf(
            accountResponseDTO.accountNumber, accountRequestDTO.cpf
        )
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun throwAnExceptionWhenNotFindByAccountNumberInGetBalanceRequest() {
        Mockito.`when`(accountService.getAccountBalance(accountResponseDTO.accountNumber))
            .thenThrow(ResourceNotFoundBankException::class.java)

        accountController.getAccountBalance(accountResponseDTO.accountNumber)
    }

    @Test
    fun getAccountBalanceWithSuccess() {
        val accountBalanceDTO = AccountBalanceDTO(accountResponseDTO.accountNumber, 1000.0)

        Mockito.`when`(accountService.getAccountBalance(accountResponseDTO.accountNumber))
            .thenReturn(accountBalanceDTO)

        val response = accountController.getAccountBalance(accountResponseDTO.accountNumber)

        Assert.assertEquals(response.statusCodeValue, HttpStatus.OK.value())
        Assert.assertEquals(response.body, accountBalanceDTO)

        Mockito.verify(accountService, Mockito.times(1))
            .getAccountBalance(accountResponseDTO.accountNumber)
    }

    @Test(expected = InvalidResourceBankException::class)
    fun throwAnExceptionWhitInvalidResourcesToDeposit() {
        Mockito.`when`(accountService.deposit(activityRequestDTO)).thenThrow(InvalidResourceBankException::class.java)

        accountController.deposit(activityRequestDTO)
    }

    @Test
    fun makeADepositWithSuccess() {
        Mockito.`when`(accountService.deposit(activityRequestDTO)).thenReturn(activityResponseDTO)

        val response = accountController.deposit(activityRequestDTO)

        Assert.assertEquals(response.statusCodeValue, HttpStatus.OK.value())
        Assert.assertEquals(response.body, activityResponseDTO)

        Mockito.verify(accountService, Mockito.times(1)).deposit(activityRequestDTO)
    }

    @Test(expected = InvalidResourceBankException::class)
    fun throwAnExceptionWhitInvalidResourcesToWithdraw() {
        Mockito.`when`(accountService.withdraw(activityRequestDTO)).thenThrow(InvalidResourceBankException::class.java)

        accountController.withdraw(activityRequestDTO)
    }

    @Test
    fun makeAWithdrawWithSuccess() {
        activityResponseDTO = ActivityResponseDTO(100.0, accountResponseDTO.accountNumber, Date(), Operation.WITHDRAW.toString())

        Mockito.`when`(accountService.withdraw(activityRequestDTO)).thenReturn(activityResponseDTO)

        val response = accountController.withdraw(activityRequestDTO)

        Assert.assertEquals(response.statusCodeValue, HttpStatus.OK.value())
        Assert.assertEquals(response.body, activityResponseDTO)

        Mockito.verify(accountService, Mockito.times(1)).withdraw(activityRequestDTO)
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun throwAnExceptionWhenNotFindAccount() {
        Mockito.`when`(accountService.extract(activityRequestDTO.accNumber, 0, 10)).thenThrow(ResourceNotFoundBankException::class.java)

        accountController.extract(activityRequestDTO.accNumber, "0", "10")
    }

    @Test
    fun getExtractWithSuccess() {
        val extractResponseDTO: ExtractResponseDTO = ExtractResponseDTO(mutableListOf(ExtractDataDTO(Date(), 100.0, Operation.DEPOSIT)), PaginationResponseDTO(0, 10))
        Mockito.`when`(accountService.extract(activityRequestDTO.accNumber, 0, 10)).thenReturn(extractResponseDTO)

        val response = accountController.extract(activityRequestDTO.accNumber, "0", "10")

        Assert.assertEquals(response.statusCodeValue, HttpStatus.OK.value())
        Assert.assertEquals(response.body, extractResponseDTO)

        Mockito.verify(accountService, Mockito.times(1)).extract(activityRequestDTO.accNumber, 0, 10)
    }
}