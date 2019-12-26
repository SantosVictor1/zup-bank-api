package br.com.zup.bank

import br.com.zup.bank.model.User
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.UserService
import br.com.zup.bank.exception.BankException
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
    private lateinit var userService: UserService
    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var user: User

    @Before
    fun createUser() {
        user = User(
            id = 1,
            name = "Victor",
            cpf = "02160795607",
            email = "teste@gmail.com"
        )
    }

    @Test(expected = BankException::class)
    fun existsByCpfTest() {
        Mockito.`when`(userRepository.existsByCpf(user.cpf)).thenReturn(true)
        userService.createUser(user)

        Mockito.verify(userRepository, Mockito.times(1)).existsByCpf(user.cpf)
    }

    @Test
    fun notExistsByCpfTest() {
        Mockito.`when`(userRepository.existsByCpf(user.cpf)).thenReturn(false)
        Mockito.`when`(userRepository.save(user)).thenReturn(user)
        userService.createUser(user)

        Mockito.verify(userRepository, Mockito.times(1)).existsByCpf(user.cpf)
    }

    @Test(expected = BankException::class)
    fun existsByEmailTest() {
        Mockito.`when`(userRepository.existsByEmail(user.email)).thenReturn(true)
        userService.createUser(user)

        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(user.email)
    }

    @Test
    fun notExistsByEmailTest() {
        Mockito.`when`(userRepository.existsByEmail(user.email)).thenReturn(false)
        Mockito.`when`(userRepository.save(user)).thenReturn(user)
        userService.createUser(user)

        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(user.email)
    }

    @Test(expected = BankException::class)
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

    @Test(expected = BankException::class)
    fun deleteByIdWithErrorTest() {
        Mockito.`when`(userRepository.findById(user.id!!)).thenReturn(Optional.empty())
        userService.deleteById(user.id!!)

        Mockito.verify(userRepository, Mockito.times(1)).findById(user.id!!)
    }

    @Test
    fun deleteByIdWithSuccessTest() {
        Mockito.`when`(userRepository.findById(user.id!!)).thenReturn(Optional.of(user))
        userService.deleteById(user.id!!)

        Mockito.verify(userRepository, Mockito.times(1)).findById(user.id!!)
    }

    @Test
    fun getAllTest() {
        Mockito.`when`(userRepository.findAll()).thenReturn(mutableListOf(user))

        userService.getAll()

        Mockito.verify(userRepository, Mockito.times(1)).findAll()
    }
}