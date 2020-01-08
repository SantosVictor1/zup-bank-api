package br.com.zup.bank.dto.request

import org.hibernate.validator.constraints.br.CPF
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * Created by Victor Santos on 30/12/2019
 */
class TransferRequestDTO(
    originAccount: String,
    destinyAccount: String,
    recipientsCpf: String,
    transferValue: Double
) {
    @NotBlank(message = "origin.account.required")
    @Size(min = 10, max = 10, message = "account.number.size.invalid")
    var originAccount: String = originAccount

    @NotBlank(message = "destiny.account.required")
    @Size(min = 10, max = 10, message = "account.number.size.invalid")
    var destinyAccount: String = destinyAccount

    @NotBlank(message = "cpf.required")
    @CPF(message = "cpf.invalid")
    var recipientsCpf: String = recipientsCpf

    var transferValue: Double = transferValue

    var date: Date = Date()
}