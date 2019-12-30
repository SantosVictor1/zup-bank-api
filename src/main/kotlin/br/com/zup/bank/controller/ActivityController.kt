package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.dto.response.error.ErrorSupport
import br.com.zup.bank.dto.response.success.ActivityResponseDTO
import br.com.zup.bank.dto.response.success.ExtractResponseDTO
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.service.IActivityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * Created by Victor Santos on 27/12/2019
 */
@RestController
@RequestMapping("/api/activity")
class ActivityController {
    @Autowired
    private lateinit var activityService: IActivityService

    @PostMapping("/deposit")
    fun deposit(
        @RequestBody @Valid activityRequestDTO: ActivityRequestDTO,
        result: BindingResult
    ): ResponseEntity<ActivityResponseDTO> {
        if (result.hasErrors()) {
            badRequest(result.allErrors)
        }

        activityRequestDTO.operation = Operation.DEPOSIT
        return ResponseEntity.ok(activityService.operation(activityRequestDTO))
    }

    @PostMapping("/withdraw")
    fun withdraw(@RequestBody @Valid activityRequestDTO: ActivityRequestDTO, result: BindingResult): ResponseEntity<Any> {
        if (result.hasErrors()) {
            val errors = mutableListOf<ErrorSupport>()
            result.allErrors.forEach {
                errors.add(ErrorSupport(it.defaultMessage.toString()))
            }
            return ResponseEntity(ErrorResponse(400, errors), HttpStatus.BAD_REQUEST)
        }

        activityRequestDTO.operation = Operation.WITHDRAW
        return ResponseEntity.ok(activityService.operation(activityRequestDTO))
    }

    @GetMapping("/extract")
    fun extract(@RequestParam accNumber: String): ResponseEntity<MutableList<ExtractResponseDTO>> {
        return ResponseEntity.ok(activityService.extract(accNumber))
    }

    private fun badRequest(objectErrors: MutableList<ObjectError>) {
        val errors = mutableListOf<String>()

        objectErrors.forEach {
            errors.add(it.defaultMessage.toString())
        }

        throw BankException(400, errors)
    }
}