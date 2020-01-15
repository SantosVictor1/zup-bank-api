package br.com.zup.bank.dto.response.success

import br.com.zup.bank.enums.Status

data class StatusResponseDTO(
    val transferId: Long,
    val transferStatus: Status
)