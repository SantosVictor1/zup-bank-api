package br.com.zup.bank.service

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.exception.BankException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by Victor Santos on 23/12/2019
 */
@Service
class UserService {
    @Autowired
    private lateinit var userRepository: UserRepository

    fun createUser(user: User): User {
        validateFields(user)

        return userRepository.save(user)
    }

    fun getAll(): List<User> {
        return userRepository.findAll()
    }

    fun getById(id: Long): UserResponseDTO {
        val user = userRepository.findById(id)

        if (!user.isPresent) {
            throw BankException(404, "Usuário não encontrado")
        }

        return UserResponseDTO(user.get().id!!, user.get().name!!, user.get().cpf!!, user.get().email!!)
    }

    fun deleteById(id: Long) {
        val user = userRepository.findById(id)

        if (!user.isPresent) {
            throw BankException(404, "Usuário não encontrado")
        }

        userRepository.deleteById(id)
    }

    fun findByCpf(cpf: String): User {
        val user = userRepository.findByCpf(cpf)
        if (!user.isPresent) {
            throw BankException(404, "Usuário não encontrado")
        }

        return user.get()
    }

    fun setUser(userRequestDTO: UserRequestDTO): User {
        return User(null, userRequestDTO.name, userRequestDTO.cpf, userRequestDTO.email)
    }

    private fun validateFields(user: User) {
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
}