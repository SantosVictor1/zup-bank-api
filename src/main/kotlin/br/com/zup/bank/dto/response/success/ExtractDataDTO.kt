package br.com.zup.bank.dto.response.success

import br.com.zup.bank.enums.Operation
import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

/**
 * Created by Victor Santos on 03/01/2020
 */
data class ExtractDataDTO(
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    val date: Date?,
    val value: Double?,
    val operation: Operation?
)