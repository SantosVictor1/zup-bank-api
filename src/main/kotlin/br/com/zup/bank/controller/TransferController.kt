package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.error.ObjectErrorResponse
import br.com.zup.bank.dto.response.success.NewTransferResponseDTO
import br.com.zup.bank.service.ITransferService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
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
class TransferController(
    val transferService: ITransferService
) {
    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @ApiOperation(value = "Realiza uma trasnferência")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = NewTransferResponseDTO::class),
        ApiResponse(code = 400, message = "Algum dado é inválido", response = ObjectErrorResponse::class)
    )
    @PostMapping
    fun newTransfer(@RequestBody @Valid transferDTO: TransferRequestDTO): ResponseEntity<NewTransferResponseDTO> {
        return ResponseEntity.ok(transferService.newTransfer(transferDTO))
    }

    @PostMapping("/kafka")
    fun message(@RequestBody transferDTO: TransferRequestDTO) {
        kafkaTemplate.send("bank_api", Gson().toJson(transferDTO))
    }
}