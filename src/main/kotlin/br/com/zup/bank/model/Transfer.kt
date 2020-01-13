package br.com.zup.bank.model

import com.fasterxml.jackson.annotation.JsonBackReference
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

    @Column(name = "value", nullable = false)
    val value: Double?,

    @Column(name = "date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    val transferDate: Date?,

    @JsonBackReference
    @ManyToMany
    @JoinTable(name = "transfer_account",
        joinColumns = [JoinColumn(name = "transferId")],
        inverseJoinColumns = [JoinColumn(name = "accountId")]
    )
    var accounts: MutableList<Account>
)