package br.com.zup.bank.unitary.controller

import br.com.zup.bank.controller.UserController
import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.IAccountService
import br.com.zup.bank.service.IUserService
import br.com.zup.bank.service.impl.UserServiceImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql

/**
 * Created by Victor Santos on 10/01/2020
 */
@ActiveProfiles("test")
class UserControllerUnitaryTest {
    private val userService: IUserService = Mockito.mock(IUserService::class.java)
    private val userController: UserController = UserController(
        userService
    )
    private lateinit var userRequestDTO: UserRequestDTO
    private lateinit var userResponseDTO: UserResponseDTO

    @Before
    fun setMocks() {
        this.userRequestDTO = UserRequestDTO("Victor", "02160795607", "victor@gmail.com")
        this.userResponseDTO = UserResponseDTO(1, "Victor", "02160795607", "victor@gmail.com", true)
    }

    @Test(expected = DuplicatedResourceBankException::class)
    fun throwAnExceptionWithDuplicatedCpf() {
        Mockito.`when`(userService.createUser(this.userRequestDTO)).thenThrow(DuplicatedResourceBankException::class.java)

        userController.createUser(this.userRequestDTO)
    }

    @Test
    fun createAnUserWithSuccess() {
        Mockito.`when`(userService.createUser(this.userRequestDTO)).thenReturn(this.userResponseDTO)

        val response = userController.createUser(this.userRequestDTO)
        Assert.assertEquals(response.statusCodeValue, HttpStatus.CREATED.value())
        Assert.assertEquals(response.body, this.userResponseDTO)

        Mockito.verify(userService, Mockito.times(1)).createUser(this.userRequestDTO)
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun throwAnExceptionWhenNotFindUserByCpfToReactivate() {
        Mockito.`when`(userService.reactivateUser(this.userRequestDTO.cpf)).thenThrow(ResourceNotFoundBankException::class.java)

        userController.reactivateUser(this.userRequestDTO.cpf)
    }

    @Test
    fun reactivateUserWithSuccess() {
        Mockito.`when`(userService.reactivateUser(this.userRequestDTO.cpf)).thenReturn(this.userResponseDTO)

        val response = userController.reactivateUser(this.userRequestDTO.cpf)

        Assert.assertEquals(response.statusCodeValue, HttpStatus.OK.value())
        Assert.assertEquals(response.body, this.userResponseDTO)

        Mockito.verify(userService, Mockito.times(1)).reactivateUser(this.userRequestDTO.cpf)
    }

    @Test
    fun getAllUsersWithSuccess() {
        Mockito.`when`(userService.getAll()).thenReturn(mutableListOf(this.userResponseDTO))

        val response = userController.getAll()

        Assert.assertEquals(response.statusCodeValue, HttpStatus.OK.value())
        Assert.assertEquals(response.body, mutableListOf(this.userResponseDTO))

        Mockito.verify(userService, Mockito.times(1)).getAll()
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun throwAnExceptionWhenNotFindUserById() {
        Mockito.`when`(userService.getById(1)).thenThrow(ResourceNotFoundBankException::class.java)

        userController.getById(1)
    }

    @Test
    fun getAnUserByIdWithSuccess() {
        Mockito.`when`(userService.getById(1)).thenReturn(this.userResponseDTO)

        val response = userController.getById(1)

        Assert.assertEquals(response.statusCodeValue, HttpStatus.OK.value())
        Assert.assertEquals(response.body, this.userResponseDTO)

        Mockito.verify(userService, Mockito.times(1)).getById(1)
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun throwAnExceptionWhenNotFindUserByCpfToDeactivate() {
        Mockito.`when`(userService.deactivateUser(this.userRequestDTO.cpf)).thenThrow(ResourceNotFoundBankException::class.java)

        userController.deactivateUser(this.userRequestDTO.cpf)
    }

    @Test
    fun deactivateUserWithSuccess() {
        Mockito.doNothing().`when`(userService).deactivateUser(this.userRequestDTO.cpf)

        val response = userController.deactivateUser(this.userRequestDTO.cpf)

        Assert.assertEquals(response.statusCodeValue, HttpStatus.NO_CONTENT.value())
        Assert.assertEquals(response.body, null)

        Mockito.verify(userService, Mockito.times(1)).deactivateUser(this.userRequestDTO.cpf)
    }
}