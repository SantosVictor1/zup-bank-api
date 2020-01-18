package br.com.zup.bank.dto.response.success

import br.com.zup.bank.enums.Status
import br.com.zup.bank.model.Transfer

data class StatusResponseDTO(
    val transferId: Long,
    val transferStatus: Status,
    val message: String
) {
    companion object {
        fun toDto(transfer: Transfer, message: String = ""): StatusResponseDTO {
            return StatusResponseDTO(transfer.id!!, transfer.transferStatus, message)
        }
    }
}