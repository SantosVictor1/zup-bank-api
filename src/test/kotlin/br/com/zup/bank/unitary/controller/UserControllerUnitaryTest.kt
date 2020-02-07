package br.com.zup.bank.unitary.controller

import br.com.zup.bank.camunda.workflow.WorkflowManager
import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.controller.UserController
import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.dto.response.success.UserStatusDTO
import br.com.zup.bank.enums.Status
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.MultipleRegisterBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.service.IUserService
import br.com.zup.bank.service.IWaitListService
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.springframework.http.HttpStatus

/**
 * Created by Victor Santos on 10/01/2020
 */
class UserControllerUnitaryTest {
    private val userService: IUserService = Mockito.mock(IUserService::class.java)
    private val waitListService: IWaitListService = Mockito.mock(IWaitListService::class.java)
    private val workflowManager: WorkflowManager = Mockito.mock(WorkflowManager::class.java)
    private val userController: UserController = UserController(
        userService,
        waitListService,
        workflowManager
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
        Mockito.`when`(userService.validateFields(this.userRequestDTO)).thenThrow(DuplicatedResourceBankException::class.java)

        userController.createUser(this.userRequestDTO)
    }

    @Test(expected = MultipleRegisterBankException::class)
    fun throwAnExceptionWithProcessRunning() {
        Mockito.`when`(waitListService.validateRequest(this.userRequestDTO.cpf)).thenThrow(MultipleRegisterBankException::class.java)

        userController.createUser(this.userRequestDTO)
    }

    @Test
    fun createAnUserWithSuccess() {
        val userStatusDTO = UserStatusDTO(userRequestDTO.cpf, Status.IN_PROCESS, "")

        Mockito.doNothing().`when`(userService).validateFields(this.userRequestDTO)
        Mockito.doNothing().`when`(waitListService).validateRequest(this.userRequestDTO.cpf)
        Mockito.`when`(workflowManager.start(userRequestDTO)).thenReturn(userStatusDTO)

        val response = userController.createUser(this.userRequestDTO)
        Assert.assertEquals(response.statusCodeValue, HttpStatus.OK.value())
        Assert.assertEquals(response.body, userStatusDTO)

        Mockito.verify(userService, Mockito.times(1)).validateFields(this.userRequestDTO)
        Mockito.verify(waitListService, Mockito.times(1)).validateRequest(this.userRequestDTO.cpf)
        Mockito.verify(workflowManager, Mockito.times(1)).start(this.userRequestDTO)
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

    @Test
    fun getStatusCompleted() {
        val userStatusDTO = UserStatusDTO(this.userRequestDTO.cpf, Status.COMPLETED, BankErrorCode.BANK072.code)

        Mockito.`when`(waitListService.getStatus(this.userRequestDTO.cpf)).thenReturn(userStatusDTO)

        val response = userController.getUserStatus(this.userRequestDTO.cpf)

        Assert.assertThat(response.statusCode, CoreMatchers.`is`(HttpStatus.OK))
        Assert.assertThat(response.body, CoreMatchers.`is`(userStatusDTO))

        Mockito.verify(waitListService, Mockito.times(1)).getStatus(this.userRequestDTO.cpf)
    }

    @Test
    fun getStatusFailed() {
        val userStatusDTO = UserStatusDTO(this.userRequestDTO.cpf, Status.FAILED, BankErrorCode.BANK070.code)

        Mockito.`when`(waitListService.getStatus(this.userRequestDTO.cpf)).thenReturn(userStatusDTO)

        val response = userController.getUserStatus(this.userRequestDTO.cpf)

        Assert.assertThat(response.statusCode, CoreMatchers.`is`(HttpStatus.OK))
        Assert.assertThat(response.body, CoreMatchers.`is`(userStatusDTO))

        Mockito.verify(waitListService, Mockito.times(1)).getStatus(this.userRequestDTO.cpf)
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