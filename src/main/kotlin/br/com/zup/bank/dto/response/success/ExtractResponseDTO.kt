package br.com.zup.bank.dto.response.success

import br.com.zup.bank.enums.Operation
import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

/**
 * Created by Victor Santos on 27/12/2019
 */
data class ExtractResponseDTO(
    val extract: MutableList<ExtractDataDTO>,
    val pagination: PaginationResponseDTO
)