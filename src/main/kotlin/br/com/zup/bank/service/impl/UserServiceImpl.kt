package br.com.zup.bank.service.impl

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.exception.ResourceNotFoundException
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

/**
 * Created by Victor Santos on 23/12/2019
 */
@Service
class UserServiceImpl : IUserService {
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var accountRepository: AccountRepository

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
            resourceNotFoundException(mutableListOf("Usuário não encontrado"))
        }

        return getUserDTO(user.get())
    }

    @Transactional
    override fun deleteUser(cpf: String) {
        var user = userRepository.findByCpf(cpf)
        var account = accountRepository.findByUserCpf(cpf)

        if (!user.isPresent) {
            resourceNotFoundException(mutableListOf("Usuário não encontrado"))
        }

        user.get().isActive = false
        account.get().isActive = false

        userRepository.save(user.get())
        accountRepository.save(account.get())
    }

    private fun setUser(userRequestDTO: UserRequestDTO): User {
        return User(null, userRequestDTO.name, userRequestDTO.cpf, userRequestDTO.email, true)
    }

    private fun validateFields(user: UserRequestDTO) {
        var errors = mutableListOf<String>()

        if (existsByCpf(user.cpf!!)) {
            errors.add("CPF já cadastrado")
        }

        if (existsByEmail(user.email!!)) {
            errors.add("Email já cadastrado")
        }

        badRequestException(errors)
    }

    private fun existsByCpf(cpf: String): Boolean {
        return userRepository.existsByCpf(cpf)
    }

    private fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    private fun getUserDTO(user: User): UserResponseDTO {
        return UserResponseDTO(user.id!!, user.name!!, user.cpf!!, user.email!!, user.isActive!!)
    }

    private fun resourceNotFoundException(errors: MutableList<String>) {
        if (errors.size > 0) {
            throw ResourceNotFoundException(errors)
        }
    }

    private fun badRequestException(errors: MutableList<String>) {
        if (errors.size > 0) {
            throw BankException(400, errors)
        }
    }
}