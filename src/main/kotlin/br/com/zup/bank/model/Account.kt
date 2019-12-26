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

    @Column(name = "balance", nullable = false)
    var balance: Double? = 0.0,

    @Column(name = "accLimit", nullable = false)
    var limit: Double? = 1000.0,

    @OneToOne()
    @JoinColumn(nullable = false)
    var user: User? = null
)