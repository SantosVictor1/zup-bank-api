package br.com.zup.bank.exception

/**
 * Created by Victor Santos on 08/01/2020
 */
class InvalidResourceBankException(
    errorCode: String,
    field: String,
    objectName: String
) : BankException(errorCode, field, objectName)