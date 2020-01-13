package br.com.zup.bank.model

import br.com.zup.bank.dto.response.success.AccountResponseDTO
import javax.persistence.*

/**
 * Created by Victor Santos on 26/12/2019
 */
@Entity
@Table(name = "account")
class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accountId")
    var id: Long? = null,

    @Column(name = "balance", nullable = false)
    var balance: Double = 0.0,

    @Column(name = "accLimit", nullable = false)
    var limit: Double = 1000.0,

    @Column(name = "number", unique = true, nullable = false)
    var accountNumber: String,

    @Column(name = "isActive", nullable = false)
    var isActive: Boolean,

    @OneToOne
    @JoinColumn
    var user: User? = null
) {
    companion object {
        fun toEntity(accountResponseDTO: AccountResponseDTO, user: User): Account {
            return Account(
                accountResponseDTO.id,
                accountResponseDTO.balance,
                accountResponseDTO.limit,
                accountResponseDTO.accountNumber,
                accountResponseDTO.isActive,
                user
            )
        }
    }
}