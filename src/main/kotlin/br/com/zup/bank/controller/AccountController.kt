package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.dto.response.error.ErrorSupport
import br.com.zup.bank.dto.response.success.AccountBalanceDTO
import br.com.zup.bank.dto.response.success.AccountResponseDTO
import br.com.zup.bank.service.IAccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * Created by Victor Santos on 26/12/2019
 */
@RestController
@RequestMapping("api/account")
class AccountController {

    @Autowired
    private lateinit var accountService: IAccountService

    @PostMapping
    fun newAccount(@RequestBody @Valid accountRequestDTO: AccountRequestDTO, result: BindingResult): ResponseEntity<Any> {
        if (result.hasErrors()) {
            val errors = mutableListOf<ErrorSupport>()
            result.allErrors.forEach {
                errors.add(ErrorSupport(it.defaultMessage.toString()))
            }
            return ResponseEntity(ErrorResponse(400, errors), HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity.ok(accountService.createAccount(accountRequestDTO))
    }

    @GetMapping
    fun getAllAccounts(): ResponseEntity<MutableList<AccountResponseDTO>> {
        return ResponseEntity.ok(accountService.getAll())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<AccountResponseDTO> {
        return ResponseEntity.ok(accountService.getById(id))
    }

    @GetMapping("/cpf/{cpf}")
    fun getByUserCpf(@PathVariable cpf: String): ResponseEntity<AccountResponseDTO> {
        return ResponseEntity.ok(accountService.getByCpf(cpf))
    }

    @GetMapping("/{accNumber}")
    fun getByAccountNumber(@PathVariable accNumber: String): ResponseEntity<AccountResponseDTO> {
        return ResponseEntity.ok(accountService.getByAccountNumber(accNumber))
    }

    @GetMapping("/balance/{accNumber}")
    fun getAccountBalance(@PathVariable accNumber: String): ResponseEntity<AccountBalanceDTO> {
        return ResponseEntity.ok(accountService.getAccountBalance(accNumber))
    }
}