package br.com.zup.bank.service.impl

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by Victor Santos on 23/12/2019
 */
@Service
class UserServiceImpl : IUserService {
    @Autowired
    private lateinit var userRepository: UserRepository

    override fun createUser(userRequestDTO: UserRequestDTO): UserResponseDTO {
        validateFields(userRequestDTO)

        var user: User = setUser(userRequestDTO)
        user = userRepository.save(user)

        return getUserDTO(user)
    }

    override fun getAll(): MutableList<UserResponseDTO> {
        var userResponseDTOList: MutableList<UserResponseDTO> = mutableListOf<UserResponseDTO>()
        val response = userRepository.findAll()

        response.forEach {
            userResponseDTOList.add(getUserDTO(it))
        }

        return userResponseDTOList
    }

    override fun getById(id: Long): UserResponseDTO {
        val user = userRepository.findById(id)

        if (!user.isPresent) {
            throw BankException(404, "Usuário não encontrado")
        }

        return getUserDTO(user.get())
    }

    override fun deleteById(id: Long) {
        val user = userRepository.findById(id)

        if (!user.isPresent) {
            throw BankException(404, "Usuário não encontrado")
        }

        userRepository.deleteById(id)
    }

    private fun setUser(userRequestDTO: UserRequestDTO): User {
        return User(null, userRequestDTO.name, userRequestDTO.cpf, userRequestDTO.email)
    }

    private fun validateFields(user: UserRequestDTO) {
        if (existsByCpf(user.cpf!!)) {
            throw BankException(400, "CPF já cadastrado")
        }

        if (existsByEmail(user.email!!)) {
            throw BankException(400, "Email já cadastrado")
        }
    }

    private fun existsByCpf(cpf: String): Boolean {
        return userRepository.existsByCpf(cpf)
    }

    private fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    private fun getUserDTO(user: User): UserResponseDTO {
        return UserResponseDTO(user.id!!, user.name!!, user.cpf!!, user.email!!)
    }
}