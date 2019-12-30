package br.com.zup.bank

import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.exception.ResourceNotFoundException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.ActivityRepository
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.impl.ActivityServiceImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

/**
 * Created by Victor Santos on 30/12/2019
 */
@RunWith(SpringRunner::class)
class ActivityServiceTest {
    @InjectMocks
    private lateinit var activityService: ActivityServiceImpl
    @Mock
    private lateinit var activityRepository: ActivityRepository
    @Mock
    private lateinit var accountRepository: AccountRepository
    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var user: User
    private lateinit var account: Account
    private lateinit var activity: Activity
    private lateinit var activityDTO: ActivityRequestDTO

    @Before
    fun setObjects() {
        activityDTO = ActivityRequestDTO()
        activityDTO.accNumber = "6085506328"
        activityDTO.cpf = "50359879063"
        activityDTO.value = 100.0

        user = User(
            id = 1,
            name = "Victor",
            cpf = "50359879063",
            email = "teste@gmail.com",
            isActive = true
        )

        account = Account(1, 1000.0, 0.0, "7278424688", true, user)
        activity = Activity(1, Date(), 100.0, Operation.DEPOSIT, account, user)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun userNotExistsTest() {
        Mockito.`when`(userRepository.findByCpf(user.cpf)).thenReturn(Optional.empty())

        activityService.operation(activityDTO)

        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun accountNotExistsTest() {
        Mockito.`when`(userRepository.findByCpf(activityDTO.cpf)).thenReturn(Optional.of(user))
        Mockito.`when`(accountRepository.findByAccountNumber(activityDTO.accNumber!!)).thenReturn(Optional.empty())

        activityService.operation(activityDTO)

        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumber(activityDTO.accNumber!!)
        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(activityDTO.cpf)
    }

    @Test
    fun userExistsTest() {
        Mockito.`when`(userRepository.findByCpf(user.cpf)).thenReturn(Optional.of(user))
        Mockito.`when`(accountRepository.findByAccountNumber(activityDTO.accNumber!!)).thenReturn(Optional.of(account))
        Mockito.`when`(accountRepository.save(account)).thenReturn(account)
        Mockito.`when`(activityRepository.save(Mockito.any(Activity::class.java))).thenReturn(activity)

        activityDTO.operation = Operation.DEPOSIT

        activityService.operation(activityDTO)

        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf)
        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumber(activityDTO.accNumber!!)
        Mockito.verify(accountRepository, Mockito.times(1)).save(account)
        Mockito.verify(activityRepository, Mockito.times(1)).save(Mockito.any(Activity::class.java))
    }

    @Test(expected = BankException::class)
    fun withdrawWithError() {
        Mockito.`when`(userRepository.findByCpf(user.cpf)).thenReturn(Optional.of(user))
        Mockito.`when`(accountRepository.findByAccountNumber(activityDTO.accNumber!!)).thenReturn(Optional.of(account))
        Mockito.`when`(accountRepository.save(account)).thenReturn(account)
        Mockito.`when`(activityRepository.save(Mockito.any(Activity::class.java))).thenReturn(activity)

        activityDTO.operation = Operation.WITHDRAW
        activityDTO.value = 10000.0

        activityService.operation(activityDTO)

        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf)
        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumber(activityDTO.accNumber!!)
        Mockito.verify(accountRepository, Mockito.times(1)).save(account)
        Mockito.verify(activityRepository, Mockito.times(1)).save(Mockito.any(Activity::class.java))
    }

    @Test
    fun withdrawWithSuccess() {
        Mockito.`when`(userRepository.findByCpf(user.cpf)).thenReturn(Optional.of(user))
        Mockito.`when`(accountRepository.findByAccountNumber(activityDTO.accNumber!!)).thenReturn(Optional.of(account))
        Mockito.`when`(accountRepository.save(account)).thenReturn(account)
        Mockito.`when`(activityRepository.save(Mockito.any(Activity::class.java))).thenReturn(activity)

        activityDTO.operation = Operation.WITHDRAW

        activityService.operation(activityDTO)

        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf)
        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumber(activityDTO.accNumber!!)
        Mockito.verify(accountRepository, Mockito.times(1)).save(account)
        Mockito.verify(activityRepository, Mockito.times(1)).save(Mockito.any(Activity::class.java))
    }

    @Test
    fun extractWithSuccess() {
        Mockito.`when`(accountRepository.findByAccountNumber(activityDTO.accNumber!!)).thenReturn(Optional.of(account))
        Mockito.`when`(activityRepository.findAllByAccountAccountNumberOrderByActivityDateDesc(activityDTO.accNumber!!))
            .thenReturn(mutableListOf(activity))

        activityService.extract(activityDTO.accNumber!!)

        Mockito.verify(activityRepository, Mockito.times(1))
            .findAllByAccountAccountNumberOrderByActivityDateDesc(activityDTO.accNumber!!)
    }
}