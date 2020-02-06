package br.com.zup.bank.service.impl

import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.IAccountService
import br.com.zup.bank.service.IUserService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

/**
 * Created by Victor Santos on 23/12/2019
 */
@Service
class UserServiceImpl(
    val userRepository: UserRepository,
    val accountService: IAccountService
) : IUserService {

    override fun saveUser(userRequestDTO: UserRequestDTO) {
        val user: User = User.toEntity(userRequestDTO)

        userRepository.save(user)
    }

    override fun getAll(): MutableList<UserResponseDTO> {
        val userResponseDTOList: MutableList<UserResponseDTO> = mutableListOf()
        val response = userRepository.findAll()

        response.forEach {
            userResponseDTOList.add(UserResponseDTO.toDto(it))
        }

        return userResponseDTOList
    }

    override fun getById(id: Long): UserResponseDTO {
        val user = userRepository.findById(id)

        if (!user.isPresent) {
            resourceNotFoundException(
                BankErrorCode.BANK018.code,
                User::id.name,
                User::class.simpleName!!
            )
        }

        return UserResponseDTO.toDto(user.get())
    }

    override fun getByCpf(cpf: String, isActive: Boolean): UserResponseDTO {
        val user = userRepository.findByCpf(cpf, isActive)

        if (user == null) {
            resourceNotFoundException(
                BankErrorCode.BANK018.code,
                User::cpf.name,
                User::class.simpleName!!
            )
        }

        return UserResponseDTO.toDto(user!!)
    }

    @Transactional
    override fun deactivateUser(cpf: String) {
        val user = User.toEntity(getByCpf(cpf, true))

        user.isActive = false
        accountService.deactivateAccount(cpf)

        userRepository.save(user)
    }

    @Transactional
    override fun reactivateUser(cpf: String): UserResponseDTO {
        val user = User.toEntity(getByCpf(cpf, false))

        user.isActive = true

        accountService.reactivateAccount(cpf)
        userRepository.save(user)

        return UserResponseDTO.toDto(user)
    }

    override fun validateFields(userRequestDTO: UserRequestDTO) {
        if (existsByCpf(userRequestDTO.cpf)) {
            duplicatedResourceException(
                BankErrorCode.BANK016.code,
                UserRequestDTO::cpf.name,
                UserRequestDTO::class.simpleName!!
            )
        }

        if (existsByEmail(userRequestDTO.email)) {
            duplicatedResourceException(
                BankErrorCode.BANK017.code,
                UserRequestDTO::email.name,
                UserRequestDTO::class.simpleName!!
            )
        }
    }

    private fun existsByCpf(cpf: String): Boolean {
        return userRepository.existsByCpf(cpf)
    }

    private fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    private fun resourceNotFoundException(errorCode: String, field: String, objectName: String) {
        throw ResourceNotFoundBankException(errorCode, field, objectName)
    }

    private fun duplicatedResourceException(errorCode: String, field: String, objectName: String) {
        throw DuplicatedResourceBankException(errorCode, field, objectName)
    }
}