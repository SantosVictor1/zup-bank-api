package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.service.IUserService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
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

    @ApiOperation(value = "Cadastra um usuário")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = Any::class),
        ApiResponse(code = 201, message = "Requisição feita com sucesso!", response = UserResponseDTO::class),
        ApiResponse(code = 400, message = "Algum dado é inválido", response = ErrorResponse::class)
    )
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

    @ApiOperation(value = "Reativa usuário")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = UserResponseDTO::class),
        ApiResponse(code = 404, message = "Usuário não encontrado", response = ErrorResponse::class)
    )
    @PatchMapping("/reactivate/{cpf}")
    fun reactivateUser(@PathVariable cpf: String): ResponseEntity<UserResponseDTO> {
        return ResponseEntity.ok(userService.reactivateUser(cpf))
    }

    @ApiOperation(value = "Retorna todos os usuários")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = UserResponseDTO::class, responseContainer = "List")
    )
    @GetMapping
    fun getAll(): ResponseEntity<MutableList<UserResponseDTO>> {
        return ResponseEntity.ok().body(userService.getAll())
    }

    @ApiOperation(value = "Retorna um usuário pelo id")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = UserResponseDTO::class),
        ApiResponse(code = 404, message = "Usuário não encontrado", response = ErrorResponse::class)
    )
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<UserResponseDTO> {
        return ResponseEntity.ok().body(userService.getById(id))
    }

    @ApiOperation(value = "Desativa um usuário pelo seu cpf")
    @ApiResponses(
        ApiResponse(code = 204, message = "Requisição feita com sucesso!"),
        ApiResponse(code = 404, message = "Usuário não encontrado", response = ErrorResponse::class)
    )
    @DeleteMapping("/{cpf}")
    fun deactivateUser(@PathVariable cpf: String): ResponseEntity<Any> {
        userService.deactivateUser(cpf)

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