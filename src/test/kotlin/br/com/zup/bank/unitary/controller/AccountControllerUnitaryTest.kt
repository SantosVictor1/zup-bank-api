package br.com.zup.bank.unitary.controller

import br.com.zup.bank.controller.AccountController
import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.AccountBalanceDTO
import br.com.zup.bank.dto.response.success.AccountResponseDTO
import br.com.zup.bank.dto.response.success.UserAccountResponseDTO
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.service.IAccountService
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus

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
    private lateinit var user: UserAccountResponseDTO
    private lateinit var activityRequestDTO: ActivityRequestDTO

    @Before
    fun setMocks() {
        user = UserAccountResponseDTO("Victor", "02160795607", true)
        accountRequestDTO = AccountRequestDTO("02160795607")
        accountResponseDTO = AccountResponseDTO(1, 1000.0, 0.0, "4658132467", true, user)

        activityRequestDTO = ActivityRequestDTO(user.cpf!!, accountResponseDTO.accountNumber, 100.0)
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

//    @Test(expected = ResourceNotFoundBankException::class.java)
//    fun throw
}