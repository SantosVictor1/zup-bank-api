package br.com.zup.bank.dto.request

import org.hibernate.validator.constraints.br.CPF
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * Created by Victor Santos on 23/12/2019
 */
class UserRequestDTO(
    name: String,
    cpf: String,
    email: String
) {
    @NotBlank(message = "name.required")
    @Size(min = 3, max = 80, message = "name.size.invalid")
    var name: String = name

    @NotBlank(message = "cpf.required")
    @CPF(message = "cpf.invalid")
    var cpf: String = cpf

    @NotBlank(message = "email.required")
    @Email(message = "email.required")
    var email: String? = email
}