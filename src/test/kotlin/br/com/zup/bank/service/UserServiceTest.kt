package br.com.zup.bank.service

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.exception.ResourceNotFoundException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.impl.UserServiceImpl
import org.junit.Assert
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
    private var userService: UserServiceImpl = UserServiceImpl(
        Mockito.mock(UserRepository::class.java),
        Mockito.mock(AccountRepository::class.java)
    )
    private lateinit var user: User
    private lateinit var userRequestDTO: UserRequestDTO
    private lateinit var acc: Account
    private lateinit var userResponseDTO: UserResponseDTO

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

        userResponseDTO = UserResponseDTO(1, "Victor", "02160795607", "teste@gmail.com", true)

        acc = Account(id = 1, accountNumber = "1234567891", user = user, isActive = true)

    }

    @Test(expected = BankException::class)
    fun existsByCpfTest() {
        Mockito.`when`(userService.userRepository.existsByCpf(userRequestDTO.cpf)).thenReturn(true)

        userService.createUser(userRequestDTO)
    }

    @Test
    fun notExistsByCpfTest() {
        Mockito.`when`(userService.userRepository.existsByCpf(userRequestDTO.cpf)).thenReturn(false)
        Mockito.`when`(userService.userRepository.save(Mockito.any(User::class.java))).thenReturn(user)

        val userResponseDTO = userService.createUser(userRequestDTO)
        Assert.assertEquals(this.userResponseDTO, userResponseDTO)

        Mockito.verify(
            userService.userRepository,
            Mockito.times(1)
        ).existsByCpf(userRequestDTO.cpf)
        Mockito.verify(
            userService.userRepository,
            Mockito.times(1)
        ).save(Mockito.any(User::class.java))
    }

    @Test(expected = BankException::class)
    fun existsByEmailTest() {
        Mockito.`when`(userService.userRepository.existsByEmail(userRequestDTO.email)).thenReturn(true)

        userService.createUser(userRequestDTO)
    }

    @Test
    fun notExistsByEmailTest() {
        Mockito.`when`(userService.userRepository.existsByEmail(userRequestDTO.email)).thenReturn(false)
        Mockito.`when`(userService.userRepository.save(Mockito.any(User::class.java))).thenReturn(user)

        val userResponseDTO = userService.createUser(userRequestDTO)
        Assert.assertEquals(this.userResponseDTO, userResponseDTO)

        Mockito.verify(userService.userRepository, Mockito.times(1)).existsByEmail(userRequestDTO.email)
        Mockito.verify(userService.userRepository, Mockito.times(1)).save(Mockito.any(User::class.java))
    }

    @Test(expected = ResourceNotFoundException::class)
    fun getByIdWithErrorTest() {
        Mockito.`when`(userService.userRepository.findById(user.id!!)).thenReturn(Optional.empty())

        userService.getById(user.id!!)
    }

    @Test
    fun getByIdWithSuccessTest() {
        Mockito.`when`(userService.userRepository.findById(user.id!!)).thenReturn(Optional.of(user))

        val userResponse = userService.getById(user.id!!)
        Assert.assertEquals(this.userResponseDTO, userResponse)

        Mockito.verify(userService.userRepository, Mockito.times(1)).findById(user.id!!)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun deleteWithErrorTest() {
        Mockito.`when`(userService.userRepository.findByCpf(user.cpf!!)).thenReturn(Optional.empty())
        Mockito.`when`(userService.accountRepository.findByAccountNumberOrUserCpf(user.cpf!!)).thenReturn(Optional.empty())

        userService.deactivateUser(user.cpf!!)
    }

    @Test
    fun deleteWithSuccessTest() {
        Mockito.`when`(userService.userRepository.findByCpf(user.cpf!!)).thenReturn(Optional.of(user))
        Mockito.`when`(userService.userRepository.save(user)).thenReturn(user)
        Mockito.`when`(userService.accountRepository.findByAccountNumberOrUserCpf(user.cpf!!)).thenReturn(Optional.of(acc))
        Mockito.`when`(userService.accountRepository.save(acc)).thenReturn(acc)

        userService.deactivateUser(user.cpf!!)
        Assert.assertTrue(user.isActive == false)
        Assert.assertTrue(acc.isActive == false)

        Mockito.verify(userService.userRepository, Mockito.times(1)).findByCpf(user.cpf!!)
        Mockito.verify(userService.userRepository, Mockito.times(1)).save(user)
        Mockito.verify(userService.accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(user.cpf!!)
        Mockito.verify(userService.accountRepository, Mockito.times(1)).save(acc)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun reactivateWithErrorTest() {
        Mockito.`when`(userService.userRepository.findByCpfAndIsActiveFalse(user.cpf!!)).thenReturn(Optional.empty())
        Mockito.`when`(userService.accountRepository.findByUserCpfAndIsActiveFalse(user.cpf!!)).thenReturn(Optional.empty())

        userService.reactivateUser(user.cpf!!)
    }

    @Test
    fun reactivateWithSuccessTest() {
        Mockito.`when`(userService.userRepository.findByCpfAndIsActiveFalse(user.cpf!!)).thenReturn(Optional.of(user))
        Mockito.`when`(userService.accountRepository.findByUserCpfAndIsActiveFalse(user.cpf!!)).thenReturn(Optional.of(acc))

        val userResponse = userService.reactivateUser(user.cpf!!)
        Assert.assertEquals(this.userResponseDTO, userResponse)

        Mockito.verify(userService.userRepository, Mockito.times(1)).findByCpfAndIsActiveFalse(user.cpf!!)
        Mockito.verify(userService.userRepository, Mockito.times(1)).save(user)
        Mockito.verify(userService.accountRepository, Mockito.times(1)).findByUserCpfAndIsActiveFalse(user.cpf!!)
        Mockito.verify(userService.accountRepository, Mockito.times(1)).save(acc)
    }

    @Test
    fun getAllTest() {
        Mockito.`when`(userService.userRepository.findAll()).thenReturn(mutableListOf(user))

        val users = userService.getAll()
        Assert.assertEquals(users, mutableListOf(this.userResponseDTO))

        Mockito.verify(userService.userRepository, Mockito.times(1)).findAll()
    }
}