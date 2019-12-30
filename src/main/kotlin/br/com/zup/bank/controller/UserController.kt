package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.dto.response.error.ErrorSupport
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.service.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * Created by Victor Santos on 23/12/2019
 */
@RestController
@RequestMapping("/api/users")
class UserController {
    @Autowired
    private lateinit var userService: IUserService

    @PostMapping
    fun createUser(
        @RequestBody @Valid userRequestDTO: UserRequestDTO,
        result: BindingResult
    ): ResponseEntity<UserResponseDTO> {
        if (result.hasErrors()) {
            badRequest(result.allErrors)
        }

        return ResponseEntity(userService.createUser(userRequestDTO), HttpStatus.CREATED)
    }

    @GetMapping
    fun getAll(): ResponseEntity<MutableList<UserResponseDTO>> {
        return ResponseEntity.ok().body(userService.getAll())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<UserResponseDTO> {
        return ResponseEntity.ok().body(userService.getById(id))
    }

    @DeleteMapping("/{cpf}")
    fun deleteById(@PathVariable cpf: String): ResponseEntity<Any> {
        userService.deleteUser(cpf)

        return ResponseEntity.noContent().build()
    }

    private fun badRequest(objectErrors: MutableList<ObjectError>) {
        val errors = mutableListOf<String>()

        objectErrors.forEach {
            errors.add(it.defaultMessage.toString())
        }

        throw BankException(400, errors)
    }
}