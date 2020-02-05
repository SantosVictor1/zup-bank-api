package br.com.zup.bank.camunda.workflow

import br.com.zup.bank.dto.request.UserRequestDTO
import org.camunda.bpm.engine.RuntimeService
import org.springframework.stereotype.Service

@Service
class WorkflowManager (
    private val runtimeService: RuntimeService
){
    fun start(userRequestDTO: UserRequestDTO) {
        val variables: MutableMap<String, Any> = mutableMapOf()
        variables["name"] = userRequestDTO.name
        variables["email"] = userRequestDTO.email
        variables["cpf"] = userRequestDTO.cpf

        runtimeService.startProcessInstanceByKey("userRegister", variables)
    }
}