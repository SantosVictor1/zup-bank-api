package br.com.zup.bank.dto.request

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by Victor Santos on 23/12/2019
 */
class UserRequest(
    @NotBlank(message = "Nome obrigatório")
    @Size(min = 3, max = 80)
    val name: String,

    @NotBlank(message = "CPF obrigatório")
    val cpf: String,

    @NotBlank(message = "Email obrigatório")
    @Email(message = "Email inválido")
    val email: String
//
//    @NotBlank(message = "Senha obrigatória")
//    @Size(min = 8, message = "Senha deve ser maior que 8 caracteres")
//    val password: String
)