package br.com.zup.bank.model

import br.com.zup.bank.dto.request.BlacklistRequestDTO
import javax.persistence.*

@Entity
@Table(name = "blacklist")
data class Blacklist(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blacklist_id")
    val id: Long? = null,

    @Column(name = "user_cpf", nullable = false, unique = true)
    val cpf: String
) {
    companion object {
        fun toEntity(blacklistRequestDTO: BlacklistRequestDTO): Blacklist = Blacklist(cpf = blacklistRequestDTO.cpf)
    }
}