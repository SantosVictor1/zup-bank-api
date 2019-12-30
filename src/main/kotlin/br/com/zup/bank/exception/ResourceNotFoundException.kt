package br.com.zup.bank.exception

/**
 * Created by Victor Santos on 30/12/2019
 */
class ResourceNotFoundException(
    val errors: MutableList<String>?
) : RuntimeException()