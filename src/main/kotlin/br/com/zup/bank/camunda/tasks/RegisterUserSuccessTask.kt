package br.com.zup.bank.camunda.tasks

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.enums.Status
import br.com.zup.bank.service.IUserService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Service

@Service
class RegisterUserSuccessTask(
    private val userService: IUserService
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val name = execution.getVariable("name").toString()
        val email = execution.getVariable("email").toString()
        val cpf = execution.getVariable("cpf").toString()
        val id = execution.getVariable("userId")

        val userRequestDTO = UserRequestDTO(name, cpf, email)

        userService.registerUser(userRequestDTO, id as Long, Status.COMPLETED)
    }
}