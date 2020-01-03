package br.com.zup.bank.dto.request

import org.hibernate.validator.constraints.br.CPF
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * Created by Victor Santos on 23/12/2019
 */
class UserRequestDTO {
    @NotBlank(message = "Nome obrigatório")
    @Size(min = 3, max = 80, message = "Nome deve ter entre 3 e 80 caracteres")
    var name: String? = null

    @NotBlank(message = "CPF obrigatório")
    @CPF(message = "CPF inválido")
    var cpf: String? = null

    @NotBlank(message = "Email obrigatório")
    @Email(message = "Email inválido")
    var email: String? = null
//
//    @NotBlank(message = "Senha obrigatória")
//    @Size(min = 8, message = "Senha deve ser maior que 8 caracteres")
//    val password: String
}