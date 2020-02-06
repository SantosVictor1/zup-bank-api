package br.com.zup.bank.controller

import br.com.zup.bank.camunda.workflow.WorkflowManager
import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.dto.response.success.UserStatusDTO
import br.com.zup.bank.service.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * Created by Victor Santos on 23/12/2019
 */
@RestController
@RequestMapping("/users")
class UserController(
    private val userService: IUserService,
    private val workflowManager: WorkflowManager
) {

    @PostMapping
    fun createUser(@RequestBody @Valid userRequestDTO: UserRequestDTO): ResponseEntity<UserStatusDTO> {
        return ResponseEntity.ok(workflowManager.start(userRequestDTO))
    }

    @PatchMapping("/reactivate")
    fun reactivateUser(@RequestParam cpf: String): ResponseEntity<UserResponseDTO> {
        return ResponseEntity.ok(userService.reactivateUser(cpf))
    }

    @GetMapping
    fun getAll(): ResponseEntity<MutableList<UserResponseDTO>> {
        return ResponseEntity.ok().body(userService.getAll())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<UserResponseDTO> {
        return ResponseEntity.ok().body(userService.getById(id))
    }

    @DeleteMapping("/deactivate")
    fun deactivateUser(@RequestParam cpf: String): ResponseEntity<Any> {
        userService.deactivateUser(cpf)

        return ResponseEntity.noContent().build()
    }
}