package br.com.zup.bank.camunda.tasks

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.service.IBlacklistService
import br.com.zup.bank.service.IUserService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Service

@Service
class UpdateUserStatusTask(
    private val userService: IUserService,
    private val blacklistService: IBlacklistService
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val name = execution.getVariable("name").toString()
        val email = execution.getVariable("email").toString()
        val cpf = execution.getVariable("cpf").toString()

        val userRequestDTO = UserRequestDTO(name, cpf, email)

        val userResponse = userService.createUser(userRequestDTO)
        execution.setVariable("id", userResponse.id)

        when {
            blacklistService.existsByCpf(cpf) -> execution.setVariable("existsInBlackList", true)
            else -> execution.setVariable("existsInBlackList", false)
        }
    }
}