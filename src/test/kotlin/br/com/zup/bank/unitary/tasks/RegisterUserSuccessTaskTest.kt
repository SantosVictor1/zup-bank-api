package br.com.zup.bank.unitary.tasks

import br.com.zup.bank.camunda.tasks.RegisterUserSuccessTask
import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.service.IBlacklistService
import br.com.zup.bank.service.IUserService
import br.com.zup.bank.service.IWaitListService
import com.nhaarman.mockito_kotlin.argumentCaptor
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class RegisterUserSuccessTaskTest {
    private val userService: IUserService = Mockito.mock((IUserService::class.java))
    private val waitListService: IWaitListService = Mockito.mock(IWaitListService::class.java)
    private val blacklistService: IBlacklistService = Mockito.mock((IBlacklistService::class.java))
    private val registerUserSuccessTask = RegisterUserSuccessTask(
        userService, waitListService, blacklistService
    )

    @Test
    fun testSuccess() {
        val cpf = "02160795607"
        val email = "victor@gmail.com"
        val name = "Victor Santos"
        val userRequestDTO = UserRequestDTO(name, cpf, email)
        val execution = Mockito.mock(DelegateExecution::class.java)
        val map = mutableMapOf<String, Any>()
        map["cpf"] = cpf
        map["name"] = name
        map["email"] = email

        Mockito.doNothing().`when`(userService).saveUser(userRequestDTO)
        Mockito.doNothing().`when`(blacklistService).removeFromList(cpf)
        Mockito.doNothing().`when`(waitListService).removeFromList(cpf)
        Mockito.`when`(execution.getVariable("cpf")).thenReturn(cpf)
        Mockito.`when`(execution.getVariable("email")).thenReturn(email)
        Mockito.`when`(execution.getVariable("name")).thenReturn(name)

        registerUserSuccessTask.execute(execution)

        val argument = argumentCaptor<UserRequestDTO>()

        Mockito.verify(userService, Mockito.times(1)).saveUser(argument.capture())
        Mockito.verify(blacklistService, Mockito.times(1)).removeFromList(cpf)
        Mockito.verify(waitListService, Mockito.times(1)).removeFromList(cpf)

        Assert.assertThat(argument.firstValue.cpf, CoreMatchers.`is`(userRequestDTO.cpf))
        Assert.assertThat(argument.firstValue.email, CoreMatchers.`is`(userRequestDTO.email))
        Assert.assertThat(argument.firstValue.name, CoreMatchers.`is`(userRequestDTO.name))
    }
}