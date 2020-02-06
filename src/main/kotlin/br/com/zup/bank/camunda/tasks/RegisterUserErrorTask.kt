package br.com.zup.bank.camunda.tasks

import br.com.zup.bank.service.IWaitListService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Service

@Service
class RegisterUserErrorTask(
    private val waitListService: IWaitListService
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val cpf = execution.getVariable("cpf").toString()

        waitListService.disapproveRegister(cpf)
    }
}