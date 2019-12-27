package br.com.zup.bank.dto.response.success

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

/**
 * Created by Victor Santos on 27/12/2019
 */
data class ActivityResponseDTO (
    val balance: Double?,
    val accNumber: String?,
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    val activityDate: Date?,
    val operation: String
)