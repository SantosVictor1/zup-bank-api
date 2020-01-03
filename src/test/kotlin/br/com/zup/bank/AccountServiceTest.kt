package br.com.zup.bank

import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.response.success.AccountResponseDTO
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.exception.ResourceNotFoundException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.impl.AccountServiceImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

/**
 * Created by Victor Santos on 26/12/2019
 */
@RunWith(SpringRunner::class)
class AccountServiceTest {
    @InjectMocks
    private lateinit var accountService: AccountServiceImpl
    @Mock
    private lateinit var accountRepository: AccountRepository
    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var accResponse: AccountResponseDTO
    private lateinit var acc: Account
    private lateinit var user: User
    private lateinit var accRequestDTO: AccountRequestDTO

    @Before
    fun setObjects() {
        accResponse = AccountResponseDTO(1, 1000.0, 0.0, "0138424688", true, null)
        user = User(2, "Victor", "02160795607", "victor@gmail.com", true)
        acc = Account(1, 1000.0, 0.0, "7278424688", true, user)
        accRequestDTO = AccountRequestDTO()
        accRequestDTO.cpf = "02160795607"
    }

    @Test(expected = ResourceNotFoundException::class)
    fun userDontExistsTest() {
        Mockito.`when`(userRepository.findByCpf(user.cpf)).thenReturn(Optional.empty())

        accountService.createAccount(accRequestDTO)
    }

    @Test
    fun userExistsTest() {
        Mockito.`when`(userRepository.findByCpf(user.cpf)).thenReturn(Optional.of(user))
        Mockito.`when`(accountRepository.existsAccountByUserCpf(user.cpf!!)).thenReturn(false)
        Mockito.`when`(accountRepository.save(Mockito.any(Account::class.java))).thenReturn(acc)

        accountService.createAccount(accRequestDTO)

        Mockito.verify(accountRepository, Mockito.times(1)).existsAccountByUserCpf(user.cpf!!)
        Mockito.verify(accountRepository, Mockito.times(1)).save(Mockito.any(Account::class.java))
        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf)
    }

    @Test(expected = BankException::class)
    fun existAccountWithCpf() {
        Mockito.`when`(userRepository.findByCpf(user.cpf)).thenReturn(Optional.of(user))
        Mockito.`when`(accountRepository.existsAccountByUserCpf(user.cpf!!)).thenReturn(true)

        accountService.createAccount(accRequestDTO)
    }

    @Test
    fun notExistAccountWithCpf() {
        Mockito.`when`(userRepository.findByCpf(user.cpf)).thenReturn(Optional.of(user))
        Mockito.`when`(accountRepository.existsAccountByUserCpf(user.cpf!!)).thenReturn(false)
        Mockito.`when`(accountRepository.save(Mockito.any(Account::class.java))).thenReturn(acc)

        accountService.createAccount(accRequestDTO)

        Mockito.verify(accountRepository, Mockito.times(1)).existsAccountByUserCpf(user.cpf!!)
        Mockito.verify(accountRepository, Mockito.times(1)).save(Mockito.any(Account::class.java))
        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf)
    }

    @Test
    fun getAllTest() {
        Mockito.`when`(accountRepository.findAll()).thenReturn(mutableListOf(acc))

        accountService.getAll()

        Mockito.verify(accountRepository, Mockito.times(1)).findAll()
    }

    @Test(expected = ResourceNotFoundException::class)
    fun getByIdWithError() {
        Mockito.`when`(accountRepository.findById(acc.id!!)).thenReturn(Optional.empty())

        accountService.getById(acc.id!!)
    }

    @Test
    fun getByIdWithSuccess() {
        Mockito.`when`(accountRepository.findById(acc.id!!)).thenReturn(Optional.of(acc))

        accountService.getById(acc.id!!)

        Mockito.verify(accountRepository, Mockito.times(1)).findById(acc.id!!)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun getByCpfWithError() {
        Mockito.`when`(accountRepository.findByUserCpf(user.cpf!!)).thenReturn(Optional.empty())

        accountService.getByCpf(user.cpf!!)
    }

    @Test
    fun getByCpfWithSuccess() {
        Mockito.`when`(accountRepository.findByUserCpf(user.cpf!!)).thenReturn(Optional.of(acc))

        accountService.getByCpf(user.cpf!!)

        Mockito.verify(accountRepository, Mockito.times(1)).findByUserCpf(user.cpf!!)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun getByAccountNumberWithError() {
        Mockito.`when`(accountRepository.findByAccountNumber(acc.accountNumber!!)).thenReturn(Optional.empty())

        accountService.getByAccountNumber(acc.accountNumber!!)
    }

    @Test
    fun getByAccountNumberWithSuccess() {
        Mockito.`when`(accountRepository.findByAccountNumber(acc.accountNumber!!)).thenReturn(Optional.of(acc))

        accountService.getByAccountNumber(acc.accountNumber!!)

        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumber(acc.accountNumber!!)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun getAccountBalanceWithErrorTest() {
        Mockito.`when`(accountRepository.findByAccountNumber(acc.accountNumber!!)).thenReturn(Optional.empty())

        accountService.getAccountBalance(acc.accountNumber!!)
    }

    @Test
    fun getAccountBalanceWithErrorSuccess() {
        Mockito.`when`(accountRepository.findByAccountNumber(acc.accountNumber!!)).thenReturn(Optional.of(acc))

        accountService.getAccountBalance(acc.accountNumber!!)

        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumber(acc.accountNumber!!)
    }
}