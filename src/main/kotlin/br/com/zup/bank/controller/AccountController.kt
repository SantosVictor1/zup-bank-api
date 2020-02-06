package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.AccountBalanceDTO
import br.com.zup.bank.dto.response.success.AccountResponseDTO
import br.com.zup.bank.dto.response.success.ActivityResponseDTO
import br.com.zup.bank.dto.response.success.ExtractResponseDTO
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.service.IAccountService
import io.swagger.annotations.ApiParam
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * Created by Victor Santos on 26/12/2019
 */
@RestController
@RequestMapping("/account")
class AccountController(
    val accountService: IAccountService
) {
    @PostMapping
    fun newAccount(@RequestBody @Valid accountRequestDTO: AccountRequestDTO): ResponseEntity<AccountResponseDTO> {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(accountRequestDTO))
    }

    @GetMapping
    fun getAllAccounts(): ResponseEntity<MutableList<AccountResponseDTO>> {
        return ResponseEntity.ok(accountService.getAll())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<AccountResponseDTO> {
        return ResponseEntity.ok(accountService.getById(id))
    }

    @GetMapping("/data")
    fun getByAccountNumberOrCpf(
        @RequestParam @ApiParam(value = "Número da conta") accNumber: String,
        @RequestParam cpf: String
    ): ResponseEntity<AccountResponseDTO> {
        return ResponseEntity.ok(accountService.getByAccountNumberOrCpf(accNumber, cpf))
    }

    @GetMapping("/balance")
    fun getAccountBalance(
        @RequestParam(required = true) @ApiParam(value = "Número da conta", required = true) accNumber: String
    ): ResponseEntity<AccountBalanceDTO> {
        return ResponseEntity.ok(accountService.getAccountBalance(accNumber))
    }

    @PostMapping("/deposit")
    fun deposit(@RequestBody @Valid activityRequestDTO: ActivityRequestDTO): ResponseEntity<ActivityResponseDTO> {
        activityRequestDTO.operation = Operation.DEPOSIT
        return ResponseEntity.ok(accountService.deposit(activityRequestDTO))
    }

    @PostMapping("/withdraw")
    fun withdraw(@RequestBody @Valid activityRequestDTO: ActivityRequestDTO): ResponseEntity<ActivityResponseDTO> {
        activityRequestDTO.operation = Operation.WITHDRAW
        return ResponseEntity.ok(accountService.withdraw(activityRequestDTO))
    }

    @GetMapping("/extract")
    fun extract(
        @RequestParam(required = true)
        accNumber: String,
        @RequestParam(required = false, defaultValue = "0")
        page: String,
        @RequestParam(required = false, defaultValue = "10")
        size: String
    ): ResponseEntity<ExtractResponseDTO> {
        return ResponseEntity.ok(accountService.extract(accNumber, page.toInt(), size.toInt()))
    }
}