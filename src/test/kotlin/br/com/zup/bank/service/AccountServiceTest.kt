package br.com.zup.bank.service

import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.response.success.AccountBalanceDTO
import br.com.zup.bank.dto.response.success.AccountResponseDTO
import br.com.zup.bank.dto.response.success.UserAccountResponseDTO
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.exception.ResourceNotFoundException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.impl.AccountServiceImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

/**
 * Created by Victor Santos on 26/12/2019
 */
@RunWith(SpringRunner::class)
class AccountServiceTest {
    private val accountService: AccountServiceImpl = AccountServiceImpl(
        Mockito.mock(AccountRepository::class.java),
        Mockito.mock(UserRepository::class.java)
    )
    private lateinit var acc: Account
    private lateinit var user: User
    private lateinit var accRequestDTO: AccountRequestDTO
    private lateinit var accResponse: AccountResponseDTO
    private lateinit var userAccountResponseDTO: UserAccountResponseDTO

    @Before
    fun setObjects() {
        user = User(2, "Victor", "02160795607", "victor@gmail.com", true)
        acc = Account(1, 0.0, 1000.0, "7278424688", true, user)
        accRequestDTO = AccountRequestDTO()
        accRequestDTO.cpf = "02160795607"
        userAccountResponseDTO = UserAccountResponseDTO(user.name, user.cpf, user.isActive)
        accResponse = AccountResponseDTO(
            1,
            1000.0,
            0.0,
            "7278424688",
            true,
            userAccountResponseDTO)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun userDontExistsTest() {
        Mockito.`when`(accountService.userRepository.findByCpf(user.cpf)).thenReturn(Optional.empty())

        accountService.createAccount(accRequestDTO)
    }

    @Test
    fun userExistsTest() {
        Mockito.`when`(accountService.userRepository.findByCpf(user.cpf)).thenReturn(Optional.of(user))
        Mockito.`when`(accountService.accountRepository.existsAccountByUserCpf(user.cpf!!)).thenReturn(false)
        Mockito.`when`(accountService.accountRepository.save(Mockito.any(Account::class.java))).thenReturn(acc)

        val accountResponse = accountService.createAccount(accRequestDTO)

        Assert.assertEquals(accResponse.id, accountResponse.id)
        Assert.assertEquals(accResponse.balance, accountResponse.balance)
        Assert.assertEquals(accResponse.limit, accountResponse.limit)
        Assert.assertEquals(accResponse.user, accountResponse.user)
        Assert.assertEquals(accResponse.isActive, accountResponse.isActive)

        Mockito.verify(accountService.accountRepository, Mockito.times(1)).existsAccountByUserCpf(user.cpf!!)
        Mockito.verify(accountService.accountRepository, Mockito.times(1)).save(Mockito.any(Account::class.java))
        Mockito.verify(accountService.userRepository, Mockito.times(1)).findByCpf(user.cpf)
    }

    @Test(expected = BankException::class)
    fun existAccountWithCpf() {
        Mockito.`when`(accountService.userRepository.findByCpf(user.cpf)).thenReturn(Optional.of(user))
        Mockito.`when`(accountService.accountRepository.existsAccountByUserCpf(user.cpf!!)).thenReturn(true)

        accountService.createAccount(accRequestDTO)
    }

    @Test
    fun notExistAccountWithCpf() {
        Mockito.`when`(accountService.userRepository.findByCpf(user.cpf)).thenReturn(Optional.of(user))
        Mockito.`when`(accountService.accountRepository.existsAccountByUserCpf(user.cpf!!)).thenReturn(false)
        Mockito.`when`(accountService.accountRepository.save(Mockito.any(Account::class.java))).thenReturn(acc)

        val accountResponse = accountService.createAccount(accRequestDTO)

        Assert.assertEquals(accResponse.id, accountResponse.id)
        Assert.assertEquals(accResponse.balance, accountResponse.balance)
        Assert.assertEquals(accResponse.limit, accountResponse.limit)
        Assert.assertEquals(accResponse.user, accountResponse.user)
        Assert.assertEquals(accResponse.isActive, accountResponse.isActive)

        Mockito.verify(accountService.accountRepository, Mockito.times(1)).existsAccountByUserCpf(user.cpf!!)
        Mockito.verify(accountService.accountRepository, Mockito.times(1)).save(Mockito.any(Account::class.java))
        Mockito.verify(accountService.userRepository, Mockito.times(1)).findByCpf(user.cpf)
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
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf(user.cpf!!)).thenReturn(Optional.empty())

        accountService.getByAccountNumberOrCpf(user.cpf!!)
    }

    @Test
    fun getByCpfWithSuccess() {
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf(user.cpf!!)).thenReturn(Optional.of(acc))

        val accountResponse = accountService.getByAccountNumberOrCpf(user.cpf!!)

        Assert.assertEquals(accountResponse, accResponse)

        Mockito.verify(accountService.accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(user.cpf!!)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun getByAccountNumberWithError() {
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf(acc.accountNumber!!)).thenReturn(Optional.empty())

        accountService.getByAccountNumberOrCpf(acc.accountNumber!!)
    }

    @Test
    fun getByAccountNumberWithSuccess() {
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf(acc.accountNumber!!)).thenReturn(Optional.of(acc))

        val accountResponse = accountService.getByAccountNumberOrCpf(acc.accountNumber!!)

        Assert.assertEquals(accountResponse, accResponse)

        Mockito.verify(accountService.accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(acc.accountNumber!!)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun getAccountBalanceWithErrorTest() {
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf(acc.accountNumber!!)).thenReturn(Optional.empty())

        accountService.getAccountBalance(acc.accountNumber!!)
    }

    @Test
    fun getAccountBalanceWithErrorSuccess() {
        val accountBalance = AccountBalanceDTO("7278424688", 0.0)
        Mockito.`when`(accountService.accountRepository.findByAccountNumberOrUserCpf(acc.accountNumber!!)).thenReturn(Optional.of(acc))

        val accBalance = accountService.getAccountBalance(acc.accountNumber!!)

        Assert.assertEquals(accBalance, accountBalance)

        Mockito.verify(accountService.accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(acc.accountNumber!!)
    }
}