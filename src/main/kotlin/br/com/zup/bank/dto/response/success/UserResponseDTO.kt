package br.com.zup.bank.dto.response.success

/**
 * Created by Victor Santos on 23/12/2019
 */
data class UserResponseDTO(
    val id: Long,
    val name: String,
    val cpf: String,
    val email: String,
    val isActive: Boolean
)