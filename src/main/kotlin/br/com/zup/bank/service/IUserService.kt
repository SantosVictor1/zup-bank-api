package br.com.zup.bank.service

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.enums.Status

/**
 * Created by Victor Santos on 26/12/2019
 */
interface IUserService {

    fun saveUser(userRequestDTO: UserRequestDTO): UserResponseDTO

    fun registerUser(userRequestDTO: UserRequestDTO, id: Long)

    fun getAll(): MutableList<UserResponseDTO>

    fun getById(id: Long): UserResponseDTO

    fun getByCpf(cpf: String, isActive: Boolean): UserResponseDTO

    fun deactivateUser(cpf: String)

    fun reactivateUser(cpf: String): UserResponseDTO

    fun validateFields(userRequestDTO: UserRequestDTO)
}