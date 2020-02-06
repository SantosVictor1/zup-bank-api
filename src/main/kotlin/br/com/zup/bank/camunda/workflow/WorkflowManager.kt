package br.com.zup.bank.camunda.workflow

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserStatusDTO
import br.com.zup.bank.enums.Status
import br.com.zup.bank.service.IUserService
import org.camunda.bpm.engine.RuntimeService
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Component
class WorkflowManager (
    private val runtimeService: RuntimeService,
    private val userService: IUserService
){
    fun start(userRequestDTO: UserRequestDTO): UserStatusDTO {
        val variables: MutableMap<String, Any> = mutableMapOf()
        variables["name"] = userRequestDTO.name
        variables["email"] = userRequestDTO.email
        variables["cpf"] = userRequestDTO.cpf

        userService.validateFields(userRequestDTO)

        runtimeService.startProcessInstanceByKey("userRegister", variables)

        return UserStatusDTO(userRequestDTO.cpf, Status.IN_PROCESS)
    }
}