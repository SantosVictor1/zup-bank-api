package br.com.zup.bank.model

import br.com.zup.bank.dto.request.BlacklistRequestDTO
import javax.persistence.*

@Entity
@Table(name = "blacklist")
data class Blacklist(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blacklist_id")
    val id: Long?,

    @Column(name = "user_cpf", nullable = false, unique = true)
    val cpf: String,

    @Column(name = "is_approved")
    val isApproved: Boolean = false
) {
    companion object {
        fun toEntity(blacklistRequestDTO: BlacklistRequestDTO): Blacklist = Blacklist(null, blacklistRequestDTO.cpf)
    }
}