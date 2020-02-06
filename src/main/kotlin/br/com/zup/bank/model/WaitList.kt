package br.com.zup.bank.model

import br.com.zup.bank.enums.Status
import javax.persistence.*

@Entity
@Table(name = "waitlist")
data class WaitList(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waitlist_id")
    val id: Long? = null,

    @Column(name = "user_cpf", unique = true, nullable = false)
    val cpf: String,

    @Enumerated(value = EnumType.STRING)
    var status: Status = Status.IN_PROCESS,

    @Column(name = "message")
    var message: String = ""
) {
    companion object {
        fun toEntity(cpf: String): WaitList = WaitList(cpf = cpf)
    }
}