package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.UserRequest
import br.com.zup.bank.dto.response.success.UserResponse
import br.com.zup.bank.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * Created by Victor Santos on 23/12/2019
 */
@RestController
@RequestMapping("/api/users")
class UserController {
    @Autowired
    private lateinit var userService: UserService

    @PostMapping
    fun createUser(@RequestBody @Valid userRequest: UserRequest): ResponseEntity<UserResponse> {
        val user = userService.setUser(userRequest)

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user))
    }

    @GetMapping
    fun getAll(): ResponseEntity<MutableList<UserResponse>> {
        var userResponseList: MutableList<UserResponse> = mutableListOf<UserResponse>()
        val response = userService.getAll()

        response.forEach { user ->
            var userResponse = UserResponse(user.id!!, user.name!!, user.cpf!!, user.email!!)
            userResponseList.add(userResponse)
        }

        return ResponseEntity.ok().body(userResponseList)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<UserResponse> {
        return ResponseEntity.ok().body(userService.getById(id))
    }

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long): ResponseEntity<Any> {
        userService.deleteById(id)

        return ResponseEntity.noContent().build()
    }
}