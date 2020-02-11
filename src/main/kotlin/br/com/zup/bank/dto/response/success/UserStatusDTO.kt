package br.com.zup.bank.dto.response.success

import br.com.zup.bank.enums.Status

data class UserStatusDTO(
    val cpf: String,
    val status: Status,
    val message: String
) {
    companion object {
        fun toDto(cpf: String, status: Status, message: String = ""): UserStatusDTO = UserStatusDTO(cpf, status, message)
    }
}