package br.com.zup.bank.exception

/**
 * Created by Victor Santos on 23/12/2019
 */
class BankException(
    val httpStatus: Int,
    val errors: MutableList<String>
) : RuntimeException()