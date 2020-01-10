package br.com.zup.bank.dto.response.success

import br.com.zup.bank.model.Activity
import org.springframework.data.domain.Page

/**
 * Created by Victor Santos on 27/12/2019
 */
data class ExtractResponseDTO(
    val extract: MutableList<ExtractDataDTO>,
    val pagination: PaginationResponseDTO
) {
    companion object {
        fun toResponseDto(activities: Page<Activity>, page: Int, size: Int): ExtractResponseDTO {
            val pagination = PaginationResponseDTO(page, size)
            var extractDataDTO = mutableListOf<ExtractDataDTO>()

            activities.forEach {
                extractDataDTO.add(ExtractDataDTO(it.activityDate, it.value, it.operation))
            }

            return ExtractResponseDTO(extractDataDTO, pagination)
        }
    }
}