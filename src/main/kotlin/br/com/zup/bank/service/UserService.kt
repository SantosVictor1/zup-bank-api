package br.com.zup.bank.service

import br.com.zup.bank.dto.request.UserRequest
import br.com.zup.bank.dto.response.success.UserResponse
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.exception.BankException
import br.com.zup.bank.util.CpfValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by Victor Santos on 23/12/2019
 */
@Service
class UserService {
    @Autowired
    private lateinit var userRepository: UserRepository

    fun createUser(user: User): UserResponse {
        validateUser(user)

        val userSaved = userRepository.save(user)
        return UserResponse(userSaved.id!!, userSaved.name!!, userSaved.cpf!!, userSaved.email!!)
    }

    fun getAll(): List<User> {
        return userRepository.findAll()
    }

    fun getById(id: Long): UserResponse {
        val user = userRepository.findById(id)

        if (!user.isPresent) {
            throw BankException(404, "Usuário não encontrado")
        }

        return UserResponse(user.get().id!!, user.get().name!!, user.get().cpf!!, user.get().email!!)
    }

    fun deleteById(id: Long) {
        val user = userRepository.findById(id)

        if (!user.isPresent) {
            throw BankException(404, "Usuário não encontrado")
        }

        userRepository.deleteById(id)
    }

    fun setUser(userRequest: UserRequest): User {
        return User(null, userRequest.name, userRequest.cpf, userRequest.email)
    }

    private fun validateUser(user: User) {
        var pattern: Pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$")
        var matcher: Matcher = pattern.matcher(user.email)

        if (existsByCpf(user.cpf!!)) {
            throw BankException(400, "CPF já cadastrado")
        }

        if (existsByEmail(user.email!!)) {
            throw BankException(400, "Email já cadastrado")
        }

        if (user.name.isNullOrEmpty()) {
            throw BankException(400, "Nome obrigatório")
        }

        if (user.name?.length!! < 3 || user.name?.length!! > 80) {
            throw BankException(400, "Nome deve ter entre 3 e 80 caracteres")
        }

        if (user.cpf.isNullOrEmpty() || !CpfValidator.validateCpf(user.cpf!!)) {
            throw BankException(400, "CPF inválido")
        }

        if (user.email.isNullOrEmpty()) {
            throw BankException(400, "Email obrigatório")
        }

        if (!matcher.matches()) {
            throw BankException(400, "Email inválido")
        }
    }

    private fun existsByCpf(cpf: String): Boolean {
        return userRepository.existsByCpf(cpf)
    }

    private fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }
}