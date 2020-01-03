package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.dto.response.success.NewTransferResponseDTO
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.service.ITransferService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

/**
 * Created by Victor Santos on 30/12/2019
 */
@RestController
@RequestMapping("/api/transfer")
class TransferController {
    @Autowired
    private lateinit var transferService: ITransferService

    @ApiOperation(value = "Realiza uma trasnferência")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = NewTransferResponseDTO::class),
        ApiResponse(code = 400, message = "Algum dado é inválido", response = ErrorResponse::class)
    )
    @PostMapping
    fun newTransfer(
        @RequestBody @Valid transferDTO: TransferRequestDTO,
        result: BindingResult
    ): ResponseEntity<NewTransferResponseDTO> {
        if (result.hasErrors()) {
            badRequest(result.allErrors)
        }

        return ResponseEntity.ok(transferService.newTransfer(transferDTO))
    }

    private fun badRequest(objectErrors: MutableList<ObjectError>) {
        val errors = mutableListOf<String>()

        objectErrors.forEach {
            errors.add(it.defaultMessage.toString())
        }

        throw BankException(400, errors)
    }
}