package br.com.zup.bank.dto.response.success

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

/**
 * Created by Victor Santos on 30/12/2019
 */
class NewTransferResponseDTO(
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    val transferDate: Date?,
    val message: String?
)