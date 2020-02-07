package br.com.zup.bank.unitary.tasks

import br.com.zup.bank.camunda.tasks.RegisterUserErrorTask
import br.com.zup.bank.service.IWaitListService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.junit.Test
import org.mockito.Mockito

class RegisterUserErrorTaskTest {
    private val waitListService: IWaitListService = Mockito.mock((IWaitListService::class.java))
    private val registerUserErrorTask = RegisterUserErrorTask(waitListService)

    @Test
    fun testSuccess() {
        val cpf = "02160795607"
        val execution = Mockito.mock(DelegateExecution::class.java)
        val map = mutableMapOf<String, Any>()
        map["cpf"] = cpf

        Mockito.`when`((execution.getVariable("cpf"))).thenReturn(cpf)
        Mockito.doNothing().`when`(waitListService).disapproveRegister(cpf)

        registerUserErrorTask.execute(execution)

        Mockito.verify(waitListService, Mockito.times(1)).disapproveRegister(cpf)
    }
}