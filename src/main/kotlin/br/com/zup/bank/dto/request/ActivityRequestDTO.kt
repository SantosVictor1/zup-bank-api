package br.com.zup.bank.dto.request

import br.com.zup.bank.enums.Operation
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.validator.constraints.br.CPF
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * Created by Victor Santos on 27/12/2019
 */
class ActivityRequestDTO(
    cpf: String,
    accNumber: String,
    value: Double
) {
    @CPF(message = "cpf.invalid")
    @NotBlank(message = "cpf.required")
    var cpf: String = cpf

    @NotBlank(message = "account.number.required")
    @Size(message = "account.number.size.invalid", min = 10, max = 10)
    var accNumber: String = accNumber

    var value: Double = value

    @JsonIgnore
    lateinit var operation: Operation
}