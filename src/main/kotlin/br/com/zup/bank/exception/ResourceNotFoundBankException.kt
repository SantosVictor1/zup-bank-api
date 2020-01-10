package br.com.zup.bank.exception

/**
 * Created by Victor Santos on 30/12/2019
 */
class ResourceNotFoundBankException(
    errorCode: String,
    field: String,
    objectName: String
) : BankException(errorCode, field, objectName)