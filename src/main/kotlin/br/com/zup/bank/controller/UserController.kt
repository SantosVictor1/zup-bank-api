package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.dto.response.error.ErrorSupport
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.service.IUserService
import br.com.zup.bank.service.impl.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
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
    fun createUser(@RequestBody @Valid userRequestDTO: UserRequestDTO, result: BindingResult): ResponseEntity<Any> {
        if (result.hasErrors()) {
            var errors = mutableListOf<ErrorSupport>()
            result.allErrors.forEach {
                errors.add(ErrorSupport(it.defaultMessage.toString()))
            }

            return ResponseEntity(ErrorResponse(400, errors), HttpStatus.BAD_REQUEST)
        }

        val user = userService.setUser(userRequestDTO)

        return ResponseEntity(userService.createUser(user), HttpStatus.CREATED)
    }

    @GetMapping
    fun getAll(): ResponseEntity<MutableList<UserResponseDTO>> {
        var userResponseDTOList: MutableList<UserResponseDTO> = mutableListOf<UserResponseDTO>()
        val response = userService.getAll()

        response.forEach { user ->
            var userResponse = UserResponseDTO(user.id!!, user.name!!, user.cpf!!, user.email!!)
            userResponseDTOList.add(userResponse)
        }

        return ResponseEntity.ok().body(userResponseDTOList)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<UserResponseDTO> {
        return ResponseEntity.ok().body(userService.getById(id))
    }

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long): ResponseEntity<Any> {
        userService.deleteById(id)

        return ResponseEntity.noContent().build()
    }
}