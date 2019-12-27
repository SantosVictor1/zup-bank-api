package br.com.zup.bank

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.impl.UserServiceImpl
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
    private lateinit var userService: UserServiceImpl
    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var user: User
    private lateinit var userRequestDTO: UserRequestDTO

    @Before
    fun createUser() {
        user = User(
            id = 1,
            name = "Victor",
            cpf = "02160795607",
            email = "teste@gmail.com"
        )
        userRequestDTO = UserRequestDTO()
        userRequestDTO.cpf = "02160795607"
        userRequestDTO.email = "teste@gmail.com"
        userRequestDTO.name = "Victor"

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