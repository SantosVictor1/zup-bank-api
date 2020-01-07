package br.com.zup.bank.service

import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.ActivityResponseDTO
import br.com.zup.bank.dto.response.success.ExtractResponseDTO
import br.com.zup.bank.model.Account
import org.springframework.data.domain.PageRequest

/**
 * Created by Victor Santos on 27/12/2019
 */
interface IActivityService {

    fun extract(accNumber: String, pageRequest: PageRequest): ExtractResponseDTO

    fun createActivity(account: Account, activityDTO: ActivityRequestDTO): ActivityResponseDTO
}