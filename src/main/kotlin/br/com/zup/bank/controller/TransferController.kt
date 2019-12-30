package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.dto.response.error.ErrorSupport
import br.com.zup.bank.model.Transfer
import br.com.zup.bank.service.ITransferService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * Created by Victor Santos on 30/12/2019
 */
@RestController
@RequestMapping("/api/transfer")
class TransferController {
    @Autowired
    private lateinit var transferService: ITransferService

    @PostMapping
    fun newTransfer(@RequestBody @Valid transferDTO: TransferRequestDTO, result: BindingResult): ResponseEntity<Any> {
        if (result.hasErrors()) {
            val errors = mutableListOf<ErrorSupport>()
            result.allErrors.forEach {
                errors.add(ErrorSupport(it.defaultMessage.toString()))
            }
            return ResponseEntity(ErrorResponse(400, errors), HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity.ok(transferService.newTransfer(transferDTO))
    }

    @GetMapping
    fun getAll(): ResponseEntity<MutableList<Transfer>> {
        return ResponseEntity.ok(transferService.getAllTransfer())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Transfer> {
        return ResponseEntity.ok(transferService.getById(id))
    }
}