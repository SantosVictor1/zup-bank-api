package br.com.zup.bank.dto.request

import org.hibernate.validator.constraints.br.CPF
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * Created by Victor Santos on 30/12/2019
 */
data class TransferRequestDTO(
    @field:[NotBlank(message = "origin.account.required")]
    @field:[Size(min = 10, max = 10, message = "account.number.size.invalid")]
    var originAccount: String,

    @field:[NotBlank(message = "destiny.account.required")]
    @field:[Size(min = 10, max = 10, message = "account.number.size.invalid")]
    var destinyAccount: String,

    @field:[NotBlank(message = "cpf.required")]
    @field:[CPF(message = "cpf.invalid")]
    var recipientsCpf: String,

    var transferValue: Double,

    var transferId: Long?
)