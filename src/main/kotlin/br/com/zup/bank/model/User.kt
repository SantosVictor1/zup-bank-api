package br.com.zup.bank.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

/**
 * Created by Victor Santos on 23/12/2019
 */
@Entity
@Table(name = "user")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "cpf", unique = true, nullable = false)
    var cpf: String? = null,

    @Column(name = "email", unique = true, nullable = false)
    var email: String? = null,

    @Column(name = "isActive")
    var isActive: Boolean?

//
//    @NotBlank(message = "Senha obrigatória")
//    @Size(min = 10, message = "Senha deve ser maior que 10 caracteres")
//    @Column(name = "password")
//    var password: String? = null
)