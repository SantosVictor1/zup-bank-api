package br.com.zup.bank.service

import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.ActivityResponseDTO
import br.com.zup.bank.model.Activity

/**
 * Created by Victor Santos on 27/12/2019
 */
interface IActivityService {

    fun doDeposit(activityRequestDTO: ActivityRequestDTO): ActivityResponseDTO
}