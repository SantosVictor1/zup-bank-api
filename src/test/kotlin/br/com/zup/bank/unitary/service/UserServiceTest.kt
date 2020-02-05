package br.com.zup.bank.unitary.service

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.IAccountService
import br.com.zup.bank.service.impl.UserServiceImpl
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*

/**
 * Created by Victor Santos on 23/12/2019
 */
class UserServiceTest {
    private val userRepository = Mockito.mock(UserRepository::class.java)
    private val accountService = Mockito.mock(IAccountService::class.java)
    private val userService: UserServiceImpl = UserServiceImpl(
        userRepository,
        accountService
    )
    private lateinit var user: User
    private lateinit var userRequestDTO: UserRequestDTO
    private lateinit var acc: Account
    private lateinit var userResponseDTO: UserResponseDTO

//    @Before
//    fun createUser() {
//        user = User(
//            id = 1,
//            name = "Victor",
//            cpf = "02160795607",
//            email = "teste@gmail.com",
//            isActive = true
//        )
//        userRequestDTO = UserRequestDTO("Victor", "02160795607", "teste@gmail.com")
//
//        userResponseDTO = UserResponseDTO(1, "Victor", "02160795607", "teste@gmail.com", true)
//
//        acc = Account(id = 1, accountNumber = "1234567891", user = user, isActive = true)
//
//    }
//
//    @Test
//    fun existsByCpfTest() {
//        Mockito.`when`(userRepository.existsByCpf(userRequestDTO.cpf)).thenReturn(true)
//
//        val exception = assertThrows<DuplicatedResourceBankException> { userService.createUser(userRequestDTO) }
//
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("cpf.duplicated"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("cpf"))
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("UserRequestDTO"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).existsByCpf(userRequestDTO.cpf)
//    }
//
//    @Test
//    fun notExistsByCpfTest() {
//        Mockito.`when`(userRepository.existsByCpf(userRequestDTO.cpf)).thenReturn(false)
//        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(user)
//
//        val userResponseDTO = userService.createUser(userRequestDTO)
//        Assert.assertEquals(this.userResponseDTO, userResponseDTO)
//
//        Mockito.verify(userRepository, Mockito.times(1)).existsByCpf(userRequestDTO.cpf)
//        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User::class.java))
//    }
//
//    @Test
//    fun existsByEmailTest() {
//        Mockito.`when`(userRepository.existsByEmail(userRequestDTO.email)).thenReturn(true)
//
//        val exception = assertThrows<DuplicatedResourceBankException> { userService.createUser(userRequestDTO) }
//
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("email.duplicated"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("email"))
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("UserRequestDTO"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(userRequestDTO.email)
//    }
//
//    @Test
//    fun notExistsByEmailTest() {
//        Mockito.`when`(userRepository.existsByEmail(userRequestDTO.email)).thenReturn(false)
//        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(user)
//
//        val userResponseDTO = userService.createUser(userRequestDTO)
//        Assert.assertEquals(this.userResponseDTO, userResponseDTO)
//
//        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(userRequestDTO.email)
//        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User::class.java))
//    }
//
//    @Test
//    fun getByIdWithErrorTest() {
//        Mockito.`when`(userRepository.findById(user.id!!)).thenReturn(Optional.empty())
//
//        val exception = assertThrows<ResourceNotFoundBankException> { userService.getById(user.id!!) }
//
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("user.not.found"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("id"))
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("User"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findById(user.id!!)
//    }
//
//    @Test
//    fun getByIdWithSuccessTest() {
//        Mockito.`when`(userRepository.findById(user.id!!)).thenReturn(Optional.of(user))
//
//        val userResponse = userService.getById(user.id!!)
//        Assert.assertEquals(this.userResponseDTO, userResponse)
//
//        Mockito.verify(userRepository, Mockito.times(1)).findById(user.id!!)
//    }
//
//    @Test
//    fun deleteWithErrorTest() {
//        Mockito.`when`(userRepository.findByCpf(user.cpf, true)).thenReturn(null)
//
//        val exception = assertThrows<ResourceNotFoundBankException> { userService.deactivateUser(user.cpf) }
//
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("user.not.found"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("cpf"))
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("User"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf, true)
//    }
//
//    @Test
//    fun deleteWithSuccessTest() {
//        user.isActive = false
//        Mockito.`when`(userRepository.findByCpf(user.cpf, true)).thenReturn(user)
//        Mockito.`when`(userRepository.save(user)).thenReturn(user)
//        Mockito.doNothing().`when`(accountService).deactivateAccount(user.cpf)
//
//        userService.deactivateUser(user.cpf)
//        Assert.assertThat(user.isActive, CoreMatchers.`is`(false))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf, true)
//        Mockito.verify(userRepository, Mockito.times(1)).save(user)
//        Mockito.verify(accountService, Mockito.times(1)).deactivateAccount(user.cpf)
//    }
//
//    @Test
//    fun reactivateWithErrorTest() {
//        Mockito.`when`(userRepository.findByCpf(user.cpf, false)).thenReturn(null)
//
//        val exception = assertThrows<ResourceNotFoundBankException> { userService.reactivateUser(user.cpf) }
//
//        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("user.not.found"))
//        Assert.assertThat(exception.field, CoreMatchers.`is`("cpf"))
//        Assert.assertThat(exception.objectName, CoreMatchers.`is`("User"))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf, false)
//    }
//
//    @Test
//    fun reactivateWithSuccessTest() {
//        Mockito.`when`(userRepository.findByCpf(user.cpf, false)).thenReturn(user)
//
//        val userResponse = userService.reactivateUser(user.cpf)
//        Assert.assertThat(this.userResponseDTO, CoreMatchers.`is`(userResponse))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(user.cpf, false)
//        Mockito.verify(userRepository, Mockito.times(1)).save(user)
//    }
//
//    @Test
//    fun getAllTest() {
//        Mockito.`when`(userRepository.findAll()).thenReturn(mutableListOf(user))
//
//        val users = userService.getAll()
//        Assert.assertEquals(users, mutableListOf(this.userResponseDTO))
//
//        Mockito.verify(userRepository, Mockito.times(1)).findAll()
//    }
}