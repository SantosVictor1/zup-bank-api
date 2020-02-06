package br.com.zup.bank.exception

class MultipleRegisterBankException(
    errorCode: String,
    field: String,
    objectName: String
) : BankException(errorCode, field, objectName)