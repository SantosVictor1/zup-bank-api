package br.com.zup.bank.exception

/**
 * Created by Victor Santos on 08/01/2020
 */
class InvalidResourceException(
    errorCode: String,
    field: String,
    objectName: String
) : Exception(errorCode, field, objectName)