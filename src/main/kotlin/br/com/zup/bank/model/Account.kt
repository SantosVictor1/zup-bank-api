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

    @Column(name = "balance")
    var balance: Double = 0.0,

    @Column(name = "accLimit")
    var limit: Double = 1000.0,

    @Column(name = "number", unique = true)
    var accountNumber: String,

    @Column(name = "isActive")
    var isActive: Boolean,

    @OneToOne
    @JoinColumn
    var user: User? = null
//
//    @JsonBackReference
//    @OneToMany(mappedBy = "account", cascade = [CascadeType.ALL])
//    var activities: MutableList<Activity>? = null,
//
//    @JsonManagedReference
//    @ManyToMany(mappedBy = "accounts")
//    var transfers: MutableList<Transfer>? = null
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