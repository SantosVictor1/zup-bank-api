package br.com.zup.bank.dto.request

import org.hibernate.validator.constraints.br.CPF
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * Created by Victor Santos on 23/12/2019
 */
class UserRequestDTO(
    @field:[NotBlank(message = "name.required")]
    @field:[Size(min = 3, max = 80, message = "name.size.invalid")]
    var name: String,

    @field:[NotBlank(message = "cpf.required")]
    @field:[CPF(message = "cpf.invalid")]
    var cpf: String,

    @field:[NotBlank(message = "email.required")]
    @field:[Email(message = "email.invalid")]
    var email: String
)