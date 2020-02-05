package br.com.zup.bank.service.impl

import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.enums.Status
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

    override fun createUser(userRequestDTO: UserRequestDTO): UserResponseDTO {
        validateFields(userRequestDTO)

        var user: User = User.toEntity(userRequestDTO, Status.IN_PROCESS)
        user = userRepository.save(user)

        return UserResponseDTO.toDto(user)
    }

    override fun getAll(): MutableList<UserResponseDTO> {
        var userResponseDTOList: MutableList<UserResponseDTO> = mutableListOf<UserResponseDTO>()
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
        var user = User.toEntity(getByCpf(cpf, true))

        user.isActive = false
        accountService.deactivateAccount(cpf)

        userRepository.save(user)
    }

    @Transactional
    override fun reactivateUser(cpf: String): UserResponseDTO {
        var user = User.toEntity(getByCpf(cpf, false))

        user.isActive = true

        accountService.reactivateAccount(cpf)
        userRepository.save(user)

        return UserResponseDTO.toDto(user)
    }

    private fun validateFields(user: UserRequestDTO) {
        if (existsByCpf(user.cpf)) {
            duplicatedResourceException(
                BankErrorCode.BANK016.code,
                UserRequestDTO::cpf.name,
                UserRequestDTO::class.simpleName!!
            )
        }

        if (existsByEmail(user.email)) {
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