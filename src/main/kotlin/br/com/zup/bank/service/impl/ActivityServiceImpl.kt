package br.com.zup.bank.service.impl

import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.ActivityResponseDTO
import br.com.zup.bank.dto.response.success.ExtractResponseDTO
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import br.com.zup.bank.repository.ActivityRepository
import br.com.zup.bank.service.IActivityService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

/**
 * Created by Victor Santos on 27/12/2019
 */
@Service
class ActivityServiceImpl(
    val activityRepository: ActivityRepository
) : IActivityService {
    override fun extract(accNumber: String, pageRequest: PageRequest): ExtractResponseDTO {
        val extracts = activityRepository.findAllByAccountAccountNumberOrderByActivityDateDesc(accNumber, pageRequest)

        return ExtractResponseDTO.toResponseDto(extracts, pageRequest.pageNumber, pageRequest.pageSize)
    }

    override fun createActivity(account: Account, activityDTO: ActivityRequestDTO): ActivityResponseDTO {
        var activity = Activity.toEntity(account, activityDTO)
        activity = activityRepository.save(activity)

        return ActivityResponseDTO.toResponseDto(account, activity)
    }
}