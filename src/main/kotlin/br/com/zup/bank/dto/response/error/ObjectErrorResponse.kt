package br.com.zup.bank.dto.response.error

/**
 * Created by Victor Santos on 08/01/2020
 */
class ObjectErrorResponse(
    val statusHttp: Int,
    val objectName: String,
    val fields: MutableList<FieldError>
)