package br.com.zup.bank.dto.response.success

import br.com.zup.bank.enums.Status

data class UserStatusDTO (
    val cpf: String,
    val status: Status
)