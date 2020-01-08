package br.com.zup.bank.exception

import java.lang.RuntimeException

/**
 * Created by Victor Santos on 08/01/2020
 */
class DuplicatedResourceException(
    val errorCode: String,
    val field: String,
    val objectName: String
) : RuntimeException()