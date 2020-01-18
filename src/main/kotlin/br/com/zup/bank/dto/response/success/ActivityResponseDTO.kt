package br.com.zup.bank.dto.response.success

import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

/**
 * Created by Victor Santos on 27/12/2019
 */
data class ActivityResponseDTO(
    var balance: Double?,
    val accNumber: String?,
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    val activityDate: Date?,
    var operation: String
) {
    companion object {
        fun toResponseDto(acc: Account, activity: Activity): ActivityResponseDTO {
            return ActivityResponseDTO(acc.balance, acc.accountNumber, activity.activityDate, activity.operation.toString())
        }
    }
}