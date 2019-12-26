package br.com.zup.bank.service.exception

/**
 * Created by Victor Santos on 23/12/2019
 */
class BankException(
    val httpStatus: Int,
    val errorMessage: String
) : RuntimeException()