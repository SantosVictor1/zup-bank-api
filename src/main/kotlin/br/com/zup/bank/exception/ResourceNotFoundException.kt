package br.com.zup.bank.exception

import java.lang.RuntimeException

/**
 * Created by Victor Santos on 30/12/2019
 */
class ResourceNotFoundException(
    val errors: MutableList<String>?
) : RuntimeException()