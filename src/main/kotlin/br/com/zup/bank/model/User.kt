package br.com.zup.bank.model

import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * Created by Victor Santos on 23/12/2019
 */
@Entity
@Table(name = "user")
class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    var id: Long? = null,

    @NotBlank(message = "Nome obrigatório")
    @Size(min = 3, max = 80)
    @Column(name = "name")
    var name: String? = null,

    @NotBlank(message = "CPF obrigatório")
    @Column(name = "cpf", unique = true)
    var cpf: String? = null,

    @NotBlank(message = "Email obrigatório")
    @Email(message = "Email inválido")
    @Column(name = "email", unique = true)
    var email: String? = null
//
//    @NotBlank(message = "Senha obrigatória")
//    @Size(min = 10, message = "Senha deve ser maior que 10 caracteres")
//    @Column(name = "password")
//    var password: String? = null
)