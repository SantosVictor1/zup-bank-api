package br.com.zup.bank.model

import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.enums.Status
import javax.persistence.*

/**
 * Created by Victor Santos on 23/12/2019
 */
@Entity
@Table(name = "user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "cpf", unique = true, nullable = false)
    var cpf: String,

    @Column(name = "email", unique = true, nullable = false)
    var email: String,

    @Column(name = "isActive", nullable = false)
    var isActive: Boolean = true
) {
    companion object {
        fun toEntity(userRequestDTO: UserRequestDTO) = User(
            name = userRequestDTO.name,
            cpf = userRequestDTO.cpf,
            email = userRequestDTO.email,
            isActive = true
        )

        fun toEntity(userResponseDTO: UserResponseDTO) = User(
            userResponseDTO.id,
            userResponseDTO.name,
            userResponseDTO.cpf,
            userResponseDTO.email,
            userResponseDTO.isActive
        )
    }
}