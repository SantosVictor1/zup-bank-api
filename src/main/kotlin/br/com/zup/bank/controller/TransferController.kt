package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.StatusResponseDTO
import br.com.zup.bank.service.IKafkaService
import br.com.zup.bank.service.ITransferService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * Created by Victor Santos on 30/12/2019
 */
@RestController
@RequestMapping("/transfer")
class TransferController(
    val kafkaService: IKafkaService,
    val transferService: ITransferService
) {
    @PostMapping
    fun newTransfer(@RequestBody @Valid transferDTO: TransferRequestDTO): ResponseEntity<StatusResponseDTO> {
        return ResponseEntity.ok(kafkaService.newTransferRequest(transferDTO))
    }

    @GetMapping("/{id}")
    fun getTransferStatus(@PathVariable id: Long): ResponseEntity<StatusResponseDTO> {
        return ResponseEntity.ok(transferService.getTransferStatus(id))
    }
}