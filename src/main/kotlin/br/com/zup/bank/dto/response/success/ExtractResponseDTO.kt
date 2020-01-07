package br.com.zup.bank.dto.response.success

/**
 * Created by Victor Santos on 27/12/2019
 */
data class ExtractResponseDTO(
    val extract: MutableList<ExtractDataDTO>,
    val pagination: PaginationResponseDTO
)