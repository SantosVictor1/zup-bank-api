package br.com.zup.bank.dto.response.error

/**
 * Created by Victor Santos on 23/12/2019
 */
data class ErrorResponse(
    val httpStatus: Int,
    val errorMessage: String
)