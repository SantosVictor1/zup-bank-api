package br.com.zup.bank.model

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.enums.Status
import java.util.*
import javax.persistence.*

/**
 * Created by Victor Santos on 30/12/2019
 */
@Entity
@Table(name = "transfer")
data class Transfer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transferId")
    val id: Long?,

    @ManyToOne
    @JoinColumn(name = "originAccountId")
    val originAccount: Account,

    @ManyToOne
    @JoinColumn(name = "destinyAccountId")
    val destinyAccount: Account,

    @Column(name = "value", nullable = false)
    val value: Double?,

    @Enumerated(value = EnumType.STRING)
    var transferStatus: Status,

    @Column(name = "errorCode", nullable = true)
    var errorCode: String? = "",

    @Column(name = "date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    val transferDate: Date = Date()
) {
    companion object {
        fun toEntity(transferRequestDTO: TransferRequestDTO, originAccount: Account, destinyAccount: Account): Transfer {
            return Transfer(
                transferRequestDTO.transferId,
                originAccount,
                destinyAccount,
                transferRequestDTO.transferValue,
                Status.IN_PROCESS,
                ""
            )
        }
    }
}