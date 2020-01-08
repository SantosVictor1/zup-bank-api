package br.com.zup.bank.dto.request

import org.hibernate.validator.constraints.br.CPF
import javax.validation.constraints.NotBlank

/**
 * Created by Victor Santos on 26/12/2019
 */
class AccountRequestDTO(
    cpf: String
) {
    @NotBlank(message = "cpf.required")
    @CPF(message = "cpf.invalid")
    var cpf: String = cpf
}