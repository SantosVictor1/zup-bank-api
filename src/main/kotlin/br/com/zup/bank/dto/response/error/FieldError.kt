package br.com.zup.bank.dto.response.error

/**
 * Created by Victor Santos on 08/01/2020
 */
class FieldError (
    val errorCode: String,
    val field: String,
    val messsage: String
)