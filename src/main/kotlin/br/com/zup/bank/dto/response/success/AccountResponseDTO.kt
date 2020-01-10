package br.com.zup.bank.dto.response.success

import br.com.zup.bank.model.Account

/**
 * Created by Victor Santos on 26/12/2019
 */
data class AccountResponseDTO(
    val id: Long?,
    val limit: Double,
    val balance: Double,
    val accountNumber: String,
    val isActive: Boolean,
    val user: UserAccountResponseDTO
) {
    companion object {
        fun toResponseDto(account: Account): AccountResponseDTO {
            val userAccResponse = UserAccountResponseDTO(account.user?.name, account.user?.cpf, account.user?.isActive)

            return AccountResponseDTO(
                account.id,
                account.limit,
                account.balance,
                account.accountNumber,
                account.isActive,
                userAccResponse
            )
        }
    }
}