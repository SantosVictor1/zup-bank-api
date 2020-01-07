package br.com.zup.bank.service.impl

import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.*
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.exception.ResourceNotFoundException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.ActivityRepository
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.IActivityService
import br.com.zup.bank.service.IUserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by Victor Santos on 27/12/2019
 */
@Service
@Transactional
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