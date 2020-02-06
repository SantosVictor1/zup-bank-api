package br.com.zup.bank.camunda.tasks

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.service.IBlacklistService
import br.com.zup.bank.service.IUserService
import br.com.zup.bank.service.IWaitListService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUserSuccessTask(
    private val userService: IUserService,
    private val waitListService: IWaitListService,
    private val blacklistService: IBlacklistService
) : JavaDelegate {

    @Transactional
    override fun execute(execution: DelegateExecution) {
        val name = execution.getVariable("name").toString()
        val email = execution.getVariable("email").toString()
        val cpf = execution.getVariable("cpf").toString()

        val userRequestDTO = UserRequestDTO(name, cpf, email)

        userService.saveUser(userRequestDTO)
        waitListService.removeFromList(cpf)
        blacklistService.removeFromList(cpf)
    }
}