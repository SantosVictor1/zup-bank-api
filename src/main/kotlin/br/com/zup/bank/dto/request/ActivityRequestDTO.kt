package br.com.zup.bank.dto.request

import br.com.zup.bank.enums.Operation
import org.hibernate.validator.constraints.br.CPF
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by Victor Santos on 27/12/2019
 */
class ActivityRequestDTO {
    @CPF
    @NotBlank(message = "CPF obrigatório")
    var cpf: String? = null

    @NotBlank(message = "Número de conta obrigatório")
    @Size(message = "Número de conta deve ter tamanho 10", min = 10, max = 10)
    var accNumber: String? = null

    @NotNull(message = "Valor obrigatório")
    var value: Double? = null

    var operation: Operation? = null
}