package br.com.zup.bank.model

import javax.persistence.*

/**
 * Created by Victor Santos on 26/12/2019
 */
@Entity
@Table(name = "account")
data class Account (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accountId")
    var id: Long? = null,

    @Column(name = "balance")
    var balance: Double? = 0.0,

    @Column(name = "accLimit")
    var limit: Double? = 1000.0,

    @Column(name = "number", unique = true)
    var accountNumber: String? = null,

    @OneToOne()
    @JoinColumn()
    var user: User? = null
)