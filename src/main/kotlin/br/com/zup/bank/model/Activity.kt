package br.com.zup.bank.model

import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.enums.Operation
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
    val id: Long? = null,

    @Column(name = "activityDate", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    val activityDate: Date = Date(),

    @Column(name = "value", nullable = false)
    var value: Double,

    @Enumerated(EnumType.STRING)
    var operation: Operation,

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "accountId")
    val account: Account
) {
    companion object {
        fun toEntity(acc: Account, activityDTO: ActivityRequestDTO): Activity {
            return Activity(
                id = null,
                value = activityDTO.value,
                operation = activityDTO.operation,
                account = acc
            )
        }
    }
}