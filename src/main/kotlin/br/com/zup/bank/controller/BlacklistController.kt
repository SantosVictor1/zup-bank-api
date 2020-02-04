package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.BlacklistRequestDTO
import br.com.zup.bank.service.IBlacklistService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/blacklist")
class BlacklistController(
    private val blacklistService: IBlacklistService
) {
    @PostMapping
    fun saveNewCpf(
        @RequestBody @Valid blacklistRequestDTO: BlacklistRequestDTO
    ) = ResponseEntity(blacklistService.saveNewCpf(blacklistRequestDTO), HttpStatus.CREATED)

    @GetMapping
    fun getByCpf(@RequestParam cpf: String) = ResponseEntity(blacklistService.findByCpf(cpf), HttpStatus.OK)
}