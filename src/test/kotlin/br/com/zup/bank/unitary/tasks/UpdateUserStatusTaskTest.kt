package br.com.zup.bank.unitary.tasks

import br.com.zup.bank.camunda.tasks.UpdateUserStatusTask
import br.com.zup.bank.service.IBlacklistService
import br.com.zup.bank.service.IWaitListService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.junit.Test
import org.mockito.Mockito

class UpdateUserStatusTaskTest {
    private val waitListService: IWaitListService = Mockito.mock(IWaitListService::class.java)
    private val blacklistService: IBlacklistService = Mockito.mock(IBlacklistService::class.java)
    private val updateUserStatusTask = UpdateUserStatusTask(waitListService, blacklistService)

    @Test
    fun testWhenCpfNotExistsInBlacklist() {
        val cpf = "02160795607"
        val execution = Mockito.mock(DelegateExecution::class.java)
        val map = mutableMapOf<String, Any>()
        map["cpf"] = cpf

        Mockito.`when`(execution.getVariable("cpf")).thenReturn(map["cpf"])
        Mockito.`when`(blacklistService.existsByCpf(cpf)).thenReturn(false)

        updateUserStatusTask.execute(execution)

        Mockito.verify(blacklistService, Mockito.times(1)).existsByCpf(cpf)
    }

    @Test
    fun testWhenCpfExistsInBlacklist() {
        val cpf = "02160795607"
        val execution = Mockito.mock(DelegateExecution::class.java)
        val map = mutableMapOf<String, Any>()
        map["cpf"] = cpf

        Mockito.`when`(execution.getVariable("cpf")).thenReturn(map["cpf"])
        Mockito.`when`(blacklistService.existsByCpf(cpf)).thenReturn(true)

        updateUserStatusTask.execute(execution)

        Mockito.verify(blacklistService, Mockito.times(1)).existsByCpf(cpf)
    }
}