package br.com.zup.bank.dto.response.success

import br.com.zup.bank.enums.Operation
import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

/**
 * Created by Victor Santos on 27/12/2019
 */
data class ExtractResponseDTO (
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    val date: Date?,
    val value: Double?,
    val operation: Operation?
)