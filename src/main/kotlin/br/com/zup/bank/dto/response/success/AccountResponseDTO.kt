package br.com.zup.bank.dto.response.success

/**
 * Created by Victor Santos on 26/12/2019
 */
data class AccountResponseDTO (
    val id: Long?,
    val limit: Double?,
    val balance: Double?,
    val accountNumber: String?,
    val user: UserAccountResponseDTO?
)