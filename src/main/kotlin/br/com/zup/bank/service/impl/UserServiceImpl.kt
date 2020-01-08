package br.com.zup.bank.service.impl

import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.exception.DuplicatedResourceException
import br.com.zup.bank.exception.ResourceNotFoundException
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

        var user: User = User.fromUserRequestToEntity(userRequestDTO)
        user = userRepository.save(user)

        return UserResponseDTO.toResponseDto(user)
    }

    override fun getAll(): MutableList<UserResponseDTO> {
        var userResponseDTOList: MutableList<UserResponseDTO> = mutableListOf<UserResponseDTO>()
        val response = userRepository.findAll()

        response.forEach {
            userResponseDTOList.add(UserResponseDTO.toResponseDto(it))
        }

        return userResponseDTOList
    }

    override fun getById(id: Long): UserResponseDTO {
        val user = userRepository.findById(id)

        if (!user.isPresent) {
            resourceNotFoundException(BankErrorCode.BANK018.code, "", "User")
        }

        return UserResponseDTO.toResponseDto(user.get())
    }

    override fun getByCpf(cpf: String): UserResponseDTO {
        val user = userRepository.findByCpf(cpf)

        if (!user.isPresent) {
            resourceNotFoundException(BankErrorCode.BANK018.code, "", "User")
        }

        return UserResponseDTO.toResponseDto(user.get())
    }

    @Transactional
    override fun deactivateUser(cpf: String) {
        var user = userRepository.findByCpf(cpf)

        if (!user.isPresent) {
            resourceNotFoundException(BankErrorCode.BANK018.code, "", "User")
        }

        user.get().isActive = false
        accountService.deactivateAccount(cpf)

        userRepository.save(user.get())
    }

    @Transactional
    override fun reactivateUser(cpf: String): UserResponseDTO {
        var user = userRepository.findByCpfAndIsActiveFalse(cpf)

        if (user == null) {
            resourceNotFoundException(BankErrorCode.BANK018.code, "", "User")
        }

        user?.isActive = true

        accountService.reactivateAccount(cpf)
        userRepository.save(user!!)

        return UserResponseDTO.toResponseDto(user)
    }

    private fun validateFields(user: UserRequestDTO) {
        if (existsByCpf(user.cpf)) {
            duplicatedResourceException(BankErrorCode.BANK016.code, "cpf", "UserRequestDTO")
        }

        if (existsByEmail(user.email)) {
            duplicatedResourceException(BankErrorCode.BANK017.code, "email", "UserRequestDTO")
        }
    }

    private fun existsByCpf(cpf: String): Boolean {
        return userRepository.existsByCpf(cpf)
    }

    private fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    private fun resourceNotFoundException(errorCode: String, field: String, objectName: String) {
        throw ResourceNotFoundException(errorCode, field, objectName)
    }

    private fun duplicatedResourceException(errorCode: String, field: String, objectName: String) {
        throw DuplicatedResourceException(errorCode, field, objectName)
    }
}