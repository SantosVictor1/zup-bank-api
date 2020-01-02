package br.com.zup.bank

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.exception.ResourceNotFoundException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.impl.UserServiceImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

/**
 * Created by Victor Santos on 23/12/2019
 */
@RunWith(SpringRunner::class)
class UserServiceTest {
    @InjectMocks
    private lateinit var userService: UserServiceImpl
    @Mock
    private lateinit var userRepository: UserRepository
    @Mock
    private lateinit var accountRepository: AccountRepository
    private lateinit var user: User
    private lateinit var userRequestDTO: UserRequestDTO
    private lateinit var acc: Account

    @Before
    fun createUser() {
        user = User(
            id = 1,
            name = "Victor",
            cpf = "02160795607",
            email = "teste@gmail.com",
            isActive = true
        )
        userRequestDTO = UserRequestDTO()
        userRequestDTO.cpf = "02160795607"
        userRequestDTO.email = "teste@gmail.com"
        userRequestDTO.name = "Victor"

        acc = Account(id = 1, accountNumber = "1234567891", user = user, isActive = true)

    }

    @Test(expected = BankException::class)
    fun existsByCpfTest() {
        Mockito.`when`(userRepository.existsByCpf(userRequestDTO.cpf)).thenReturn(true)
        userService.createUser(userRequestDTO)

        Mockito.verify(userRepository, Mockito.times(1)).existsByCpf(userRequestDTO.cpf)
    }

    @Test
    fun notExistsByCpfTest() {
        Mockito.`when`(userRepository.existsByCpf(userRequestDTO.cpf)).thenReturn(false)
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(user)
        userService.createUser(userRequestDTO)

        Mockito.verify(userRepository, Mockito.times(1)).existsByCpf(userRequestDTO.cpf)
    }

    @Test(expected = BankException::class)
    fun existsByEmailTest() {
        Mockito.`when`(userRepository.existsByEmail(userRequestDTO.email)).thenReturn(true)
        userService.createUser(userRequestDTO)

        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(userRequestDTO.email)
    }

    @Test
    fun notExistsByEmailTest() {
        Mockito.`when`(userRepository.existsByEmail(userRequestDTO.email)).thenReturn(false)
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(user)
        userService.createUser(userRequestDTO)

        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(userRequestDTO.email)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun getByIdWithErrorTest() {
        Mockito.`when`(userRepository.findById(user.id!!)).thenReturn(Optional.empty())
        userService.getById(user.id!!)

        Mockito.verify(userRepository, Mockito.times(1)).findById(user.id!!)
    }

    @Test
    fun getByIdWithSuccessTest() {
        Mockito.`when`(userRepository.findById(user.id!!)).thenReturn(Optional.of(user))
        userService.getById(user.id!!)

        Mockito.verify(userRepository, Mockito.times(1)).findById(user.id!!)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun deleteWithErrorTest() {
        Mockito.`when`(userRepository.findByCpf(user.cpf!!)).thenReturn(Optional.empty())
        Mockito.`when`(accountRepository.findByUserCpf(user.cpf!!)).thenReturn(Optional.empty())
        userService.deactivateUser(user.cpf!!)

        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf!!)
        Mockito.verify(userRepository, Mockito.times(1)).save(user)
        Mockito.verify(accountRepository, Mockito.times(1)).findByUserCpf(user.cpf!!)
        Mockito.verify(accountRepository, Mockito.times(1)).save(acc)
    }

    @Test
    fun deleteWithSuccessTest() {
        Mockito.`when`(userRepository.findByCpf(user.cpf!!)).thenReturn(Optional.of(user))
        Mockito.`when`(userRepository.save(user)).thenReturn(user)
        Mockito.`when`(accountRepository.findByUserCpf(user.cpf!!)).thenReturn(Optional.of(acc))
        Mockito.`when`(accountRepository.save(acc)).thenReturn(acc)

        userService.deactivateUser(user.cpf!!)

        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf!!)
        Mockito.verify(userRepository, Mockito.times(1)).save(user)
        Mockito.verify(accountRepository, Mockito.times(1)).findByUserCpf(user.cpf!!)
        Mockito.verify(accountRepository, Mockito.times(1)).save(acc)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun reactivateWithErrorTest() {
        Mockito.`when`(userRepository.findByCpfAndIsActiveFalse(user.cpf!!)).thenReturn(Optional.empty())
        Mockito.`when`(accountRepository.findByUserCpfAndIsActiveFalse(user.cpf!!)).thenReturn(Optional.empty())
        userService.reactivateUser(user.cpf!!)

        Mockito.verify(userRepository, Mockito.times(1)).findByCpfAndIsActiveFalse(user.cpf!!)
        Mockito.verify(userRepository, Mockito.times(1)).save(user)
        Mockito.verify(accountRepository, Mockito.times(1)).findByUserCpfAndIsActiveFalse(user.cpf!!)
        Mockito.verify(accountRepository, Mockito.times(1)).save(acc)
    }

    @Test
    fun reactivateWithSuccessTest() {
        Mockito.`when`(userRepository.findByCpfAndIsActiveFalse(user.cpf!!)).thenReturn(Optional.of(user))
        Mockito.`when`(accountRepository.findByUserCpfAndIsActiveFalse(user.cpf!!)).thenReturn(Optional.of(acc))
        userService.reactivateUser(user.cpf!!)

        Mockito.verify(userRepository, Mockito.times(1)).findByCpfAndIsActiveFalse(user.cpf!!)
        Mockito.verify(userRepository, Mockito.times(1)).save(user)
        Mockito.verify(accountRepository, Mockito.times(1)).findByUserCpfAndIsActiveFalse(user.cpf!!)
        Mockito.verify(accountRepository, Mockito.times(1)).save(acc)
    }

    @Test
    fun getAllTest() {
        Mockito.`when`(userRepository.findAll()).thenReturn(mutableListOf(user))

        userService.getAll()

        Mockito.verify(userRepository, Mockito.times(1)).findAll()
    }
}