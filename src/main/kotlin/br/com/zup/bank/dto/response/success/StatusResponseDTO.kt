package br.com.zup.bank.dto.response.success

import br.com.zup.bank.enums.Status
import br.com.zup.bank.model.Transfer

data class StatusResponseDTO(
    val id: Long,
    val status: Status,
    val message: String
) {
    companion object {
        fun toDto(transfer: Transfer, message: String = "") = StatusResponseDTO(transfer.id!!, transfer.transferStatus, message)
    }
}