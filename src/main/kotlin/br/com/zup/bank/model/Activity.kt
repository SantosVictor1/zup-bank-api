package br.com.zup.bank.model

import br.com.zup.bank.enums.Operation
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonManagedReference
import java.util.*
import javax.persistence.*

/**
 * Created by Victor Santos on 27/12/2019
 */
@Entity
@Table(name = "activity")
data class Activity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activityId")
    var id: Long? = null,

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @Column(name = "activityDate")
    var activityDate: Date? = null,

    @Column(name = "value")
    var value: Double? = null,

    @Enumerated(EnumType.STRING)
    var operation: Operation,

    @JsonManagedReference
    @ManyToOne
    var account: Account? = null,

    @OneToOne
    var user: User? = null
)