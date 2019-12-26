package br.com.zup.bank.model

import javax.persistence.*

/**
 * Created by Victor Santos on 26/12/2019
 */
@Entity
@Table(name = "userAccount")
data class Account (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accountId")
    var id: Long? = null,

    @Column(name = "balance", nullable = false)
    var balance: String? = null,

    @Column(name = "accLimit", nullable = false)
    var limit: String? = null,

    @OneToOne()
    @JoinColumn(nullable = false)
    var user: User? = null
)