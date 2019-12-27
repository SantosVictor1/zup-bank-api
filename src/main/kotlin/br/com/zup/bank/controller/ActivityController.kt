package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.dto.response.error.ErrorSupport
import br.com.zup.bank.service.IActivityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

/**
 * Created by Victor Santos on 27/12/2019
 */
@RestController
@RequestMapping("/api/activity")
class ActivityController {
    @Autowired
    private lateinit var activityService: IActivityService

    @PostMapping
    fun doDeposit(@RequestBody @Valid activityRequestDTO: ActivityRequestDTO, result: BindingResult): ResponseEntity<Any> {
        if (result.hasErrors()) {
            val errors = mutableListOf<ErrorSupport>()
            result.allErrors.forEach {
                errors.add(ErrorSupport(it.defaultMessage.toString()))
            }
            return ResponseEntity(ErrorResponse(400, errors), HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity.ok(activityService.doDeposit(activityRequestDTO))
    }
}