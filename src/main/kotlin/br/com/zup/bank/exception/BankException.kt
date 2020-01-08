package br.com.zup.bank.exception

/**
 * Created by Victor Santos on 08/01/2020
 */
open class BankException(
    val errorCode: String,
    val field: String,
    val objectName: String
) : RuntimeException()