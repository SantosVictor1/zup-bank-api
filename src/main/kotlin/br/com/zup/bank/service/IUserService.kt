package br.com.zup.bank.service

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.model.User

/**
 * Created by Victor Santos on 26/12/2019
 */
interface IUserService {

    fun createUser(user: User): User

    fun getAll(): List<User>

    fun getById(id: Long): UserResponseDTO

    fun deleteById(id: Long)

    fun findByCpf(cpf: String): User

    fun setUser(userRequestDTO: UserRequestDTO): User
}