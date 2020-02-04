package br.com.zup.bank.dto.response.success

import br.com.zup.bank.model.Blacklist

data class BlacklistResponseDTO(
    val id: Long,
    val cpf: String,
    val isApproved: Boolean
) {
    companion object {
        fun toDto(blacklist: Blacklist) = BlacklistResponseDTO(blacklist.id!!, blacklist.cpf, blacklist.isApproved)
    }
}