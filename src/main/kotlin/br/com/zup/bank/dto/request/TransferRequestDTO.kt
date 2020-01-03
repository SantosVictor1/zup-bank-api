package br.com.zup.bank.dto.request

import org.hibernate.validator.constraints.br.CPF
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * Created by Victor Santos on 30/12/2019
 */
class TransferRequestDTO {
    @NotBlank(message = "Conta de origem obrigatória")
    var originAccount: String? = null

    @NotBlank(message = "Conta de destino obrigatória")
    var destinyAccount: String? = null

    @NotBlank(message = "CPF do destinatário obrigatório")
    @CPF(message = "CPF inválido")
    var recipientsCpf: String? = null

    @NotNull(message = "Valor de transferência obrigatório")
    var transferValue: Double? = null

    var date: Date = Date()
}