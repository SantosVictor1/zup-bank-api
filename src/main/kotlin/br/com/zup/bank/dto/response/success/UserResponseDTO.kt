package br.com.zup.bank.dto.response.success

import br.com.zup.bank.model.User

/**
 * Created by Victor Santos on 23/12/2019
 */
data class UserResponseDTO(
    val id: Long,
    val name: String,
    val cpf: String,
    val email: String,
    val isActive: Boolean
) {
    companion object {
        fun toResponseDto(user: User): UserResponseDTO {
            return UserResponseDTO(user.id!!, user.name, user.cpf, user.email, user.isActive)
        }
    }
}