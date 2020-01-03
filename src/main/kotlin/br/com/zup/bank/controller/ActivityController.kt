package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.dto.response.success.ActivityResponseDTO
import br.com.zup.bank.dto.response.success.ExtractResponseDTO
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.service.IActivityService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
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

    @ApiOperation(value = "Realiza um depósito")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = ActivityResponseDTO::class),
        ApiResponse(code = 400, message = "Algum dado é inválido", response = ErrorResponse::class)
    )
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

    @ApiOperation(value = "Realiza um saque")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = ActivityResponseDTO::class),
        ApiResponse(code = 400, message = "Algum dado é inválido", response = ErrorResponse::class)
    )
    @PostMapping("/withdraw")
    fun withdraw(
        @RequestBody @Valid activityRequestDTO: ActivityRequestDTO,
        result: BindingResult
    ): ResponseEntity<ActivityResponseDTO> {
        if (result.hasErrors()) {
            badRequest(result.allErrors)
        }

        activityRequestDTO.operation = Operation.WITHDRAW
        return ResponseEntity.ok(activityService.operation(activityRequestDTO))
    }

    @ApiOperation(value = "Retorna o extrato de uma conta")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = ExtractResponseDTO::class),
        ApiResponse(code = 400, message = "Campo inválido", response = ErrorResponse::class)
    )
    @GetMapping("/extract")
    fun extract(
        @RequestParam(required = true)
        @ApiParam(value = "Número da conta", required = true, defaultValue = "")
        accNumber: String,
        @RequestParam(required = false, defaultValue = "0")
        @ApiParam(value = "Número da página")
        page: String,
        @RequestParam(required = false, defaultValue = "10")
        @ApiParam(value = "Tamanho da página")
        size: String
    ): ResponseEntity<ExtractResponseDTO> {
        return ResponseEntity.ok(activityService.extract(accNumber, page.toInt(), size.toInt()))
    }

    private fun badRequest(objectErrors: MutableList<ObjectError>) {
        val errors = mutableListOf<String>()

        objectErrors.forEach {
            errors.add(it.defaultMessage.toString())
        }

        throw BankException(400, errors)
    }
}