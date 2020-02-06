package br.com.zup.bank.camunda.tasks

import br.com.zup.bank.service.IBlacklistService
import br.com.zup.bank.service.IWaitListService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Service

@Service
class UpdateUserStatusTask(
    private val waitListService: IWaitListService,
    private val blacklistService: IBlacklistService
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val cpf = execution.getVariable("cpf").toString()

        waitListService.saveOnWait(cpf)

        when {
            blacklistService.existsByCpf(cpf) -> execution.setVariable("existsInBlackList", true)
            else -> execution.setVariable("existsInBlackList", false)
        }
    }
}